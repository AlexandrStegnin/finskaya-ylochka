package com.finskayaylochka.service;

import com.finskayaylochka.config.exception.ApiException;
import com.finskayaylochka.config.exception.FileUploadException;
import com.finskayaylochka.config.property.NextcloudProperty;
import com.finskayaylochka.config.property.TelebotProperty;
import com.finskayaylochka.model.UsersAnnexToContracts;
import com.finskayaylochka.model.supporting.dto.PhoneDTO;
import com.finskayaylochka.repository.UserAnnexToContractsRepository;
import com.finskayaylochka.util.FileUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.aarboard.nextcloud.api.NextcloudConnector;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SendFilesService {

  UserAnnexToContractsRepository userAnnexToContractsRepository;
  NextcloudProperty nextcloudProperty;
  TelebotProperty telebotProperty;
  NextcloudConnector connector;
  PhoneService phoneService;

  public void sendFiles() {
    Pageable pageable = new PageRequest(0, 1);
    List<UsersAnnexToContracts> unsentAnnexes = userAnnexToContractsRepository.fetchFirstUnsent(pageable);
    if (unsentAnnexes.size() > 1) {
      throw FileUploadException.build400Exception("Unsent annexes must be 1");
    } else if (unsentAnnexes.isEmpty()) {
      log.info("Unsent annexes not found");
      return;
    }
    UsersAnnexToContracts unsentAnnex = unsentAnnexes.get(0);
    String attachmentName = unsentAnnex.getAnnex().getAnnexName();
    String remotePath = FileUtils.resolveRemotePath(nextcloudProperty.getRemoteFolder(), attachmentName);
    Path tempPath = null;
    File tempFile = null;
    try {
      tempPath = Files.createTempFile("tmp", "." + FilenameUtils.getExtension(attachmentName));
      connector.downloadFile(remotePath, tempPath.getParent().toString());
      tempFile = new File(tempPath.getParent().toString() + File.separator + attachmentName);
      if (!tempFile.exists()) {
        log.error("Failed to get file");
        throw FileUploadException.build400Exception("Filed to get file");
      }
      sendFilesToTelebot(tempFile, attachmentName, unsentAnnex.getUserId());
      unsentAnnex.setSentAt(new Date());
      userAnnexToContractsRepository.save(unsentAnnex);
      deleteTempFiles(tempPath, tempFile);
    } catch (Exception e) {
      log.error("Error send file {}", e.getLocalizedMessage());
      deleteTempFiles(tempPath, tempFile);
      throw FileUploadException.build400Exception(e.getLocalizedMessage());
    }
  }

  private void deleteTempFiles(Path tempPath, File tempFile) {
    if (Objects.nonNull(tempPath)) {
      try {
        Files.delete(tempPath);
      } catch (IOException ex) {
        log.error("Error delete file {}", tempPath);
      }
    }
    if (Objects.nonNull(tempFile)) {
      try {
        Files.delete(tempFile.toPath());
      } catch (IOException ex) {
        log.error("Error delete file {}", tempFile.getName());
      }
    }
  }

  private void sendFilesToTelebot(File file, String filename, Long investorId) {

    List<PhoneDTO> phoneList = phoneService.getUserPhones(investorId);

    String phones = String.join(", ", phoneList.stream()
        .map(PhoneDTO::getNumber)
        .collect(Collectors.toList())
        .toArray(new String[phoneList.size()]));

    RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("phones", phones)
        .addFormDataPart("file", filename,
            RequestBody.create(file, okhttp3.MediaType.parse(MediaType.IMAGE_JPEG_VALUE)))
        .build();

    Request request = new Request.Builder()
        .url(telebotProperty.getUrl())
        .method("POST", body)
        .addHeader(telebotProperty.getHeaderName(), telebotProperty.getToken())
        .build();

    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      Response response = client.newCall(request).execute();
      try (ResponseBody responseBody = response.body()) {
        if (!response.isSuccessful()) {
          log.error("Send file to telebot failed {}", response);
          HttpStatus status;
          if (response.code() == 425) {
            status = HttpStatus.LOCKED;
          } else {
            status = HttpStatus.valueOf(response.code());
          }
          throw new ApiException("Send file to telebot failed", status);
        }
        String message = Objects.nonNull(responseBody) ? responseBody.string() : response.message();
        log.info("Telebot response {}", message);
      }
    } catch (IOException e) {
      log.error("Send file to telebot failed {}", e.getLocalizedMessage());
    } catch (HttpClientErrorException e) {
      log.error("Failed to send file to telebot {}", e.getResponseBodyAsString());
    }
  }

}
