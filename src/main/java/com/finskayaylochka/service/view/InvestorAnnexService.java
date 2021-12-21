package com.finskayaylochka.service.view;

import com.finskayaylochka.config.SecurityUtils;
import com.finskayaylochka.config.exception.FileUploadException;
import com.finskayaylochka.config.exception.UsernameNotFoundException;
import com.finskayaylochka.config.exception.UsernameParseException;
import com.finskayaylochka.config.property.NextcloudProperty;
import com.finskayaylochka.model.AnnexToContracts;
import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.UsersAnnexToContracts;
import com.finskayaylochka.model.supporting.filters.InvestorAnnexFilter;
import com.finskayaylochka.model.supporting.view.InvestorAnnex;
import com.finskayaylochka.repository.view.InvestorAnnexRepository;
import com.finskayaylochka.service.AppUserService;
import com.finskayaylochka.service.UsersAnnexToContractsService;
import com.finskayaylochka.specifications.InvestorAnnexSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aarboard.nextcloud.api.NextcloudConnector;
import org.aarboard.nextcloud.api.filesharing.SharePermissions;
import org.aarboard.nextcloud.api.filesharing.ShareType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvestorAnnexService {

  AppUserService userService;
  InvestorAnnexRepository annexRepository;
  InvestorAnnexSpecification specification;
  UsersAnnexToContractsService usersAnnexToContractsService;
  NextcloudConnector connector;
  NextcloudProperty nextcloudProperty;

  public List<InvestorAnnex> findAll() {
    return annexRepository.findAll();
  }

  public List<InvestorAnnex> findAll(InvestorAnnexFilter filters, Pageable pageable) {
    return annexRepository.findAll(specification.getFilter(filters), pageable).getContent();
  }

  public List<String> getInvestors() {
    List<InvestorAnnex> investorAnnexes = annexRepository.findAll();
    List<String> investors = new ArrayList<>();
    investors.add("Выберите инвестора");
    investors.addAll(investorAnnexes
        .stream()
        .map(InvestorAnnex::getInvestor)
        .distinct()
        .sorted()
        .collect(Collectors.toList()));
    return investors;
  }

  public String uploadFiles(MultipartHttpServletRequest request) throws IOException {
    Iterator<String> itr = request.getFileNames();
    List<MultipartFile> multipartFiles = new ArrayList<>(0);
    while (itr.hasNext()) {
      multipartFiles.add(request.getFile(itr.next()));
    }
    return uploadFiles(multipartFiles);
  }

  public String uploadFiles(List<MultipartFile> files) throws IOException {
    AppUser currentUser = userService.findByLogin(SecurityUtils.getUsername());
    for (MultipartFile uploadedFile : files) {
      checkFile(uploadedFile);
      Path path = Files.createTempFile("temp-", ".pdf");
      File file = path.toFile();
      uploadedFile.transferTo(file);
      AppUser investor = getInvestor(uploadedFile.getOriginalFilename());

      AnnexToContracts annex = new AnnexToContracts();
      annex.setAnnexName(uploadedFile.getOriginalFilename());
      annex.setDateLoad(new Date());
      annex.setLoadedBy(currentUser.getId());

      UsersAnnexToContracts usersAnnexToContracts = new UsersAnnexToContracts();
      usersAnnexToContracts.setAnnex(annex);
      usersAnnexToContracts.setUserId(investor.getId());
      usersAnnexToContracts.setAnnexRead(0);
      usersAnnexToContracts.setDateRead(null);
      usersAnnexToContractsService.create(usersAnnexToContracts);

      uploadFileToNextcloud(file, uploadedFile);
      Files.delete(path);
    }
    return "Файлы успешно загружены";
  }

  private void checkFile(MultipartFile uploadedFile) {
    if (uploadedFile.isEmpty()) {
      String message = String.format("Файл %s пустой", uploadedFile.getOriginalFilename());
      log.error(message);
      throw FileUploadException.build400Exception(message);
    }
    if (!uploadedFile.getOriginalFilename().endsWith(".pdf")) {
      String message = String.format("Файл %s должен быть в формате .PDF", uploadedFile.getOriginalFilename());
      log.error(message);
      throw FileUploadException.build400Exception(message);
    }
  }

  private void uploadFileToNextcloud(File file, MultipartFile uploadedFile) {
    try {
      String remoteFolder = nextcloudProperty.getRemoteFolder();
      connector.uploadFile(file, remoteFolder + uploadedFile.getOriginalFilename());
      SharePermissions permissions = new SharePermissions(SharePermissions.SingleRight.READ);
      connector.doShare(remoteFolder + uploadedFile.getOriginalFilename(),
          ShareType.PUBLIC_LINK, "", false, null, permissions);
    } catch (Exception e) {
      throw FileUploadException.build400Exception(e.getMessage());
    }
  }

  private AppUser getInvestor(String fileName) {
    String investorCode = fileName.substring(0, 3);
    if (StringUtils.isBlank(investorCode) || !StringUtils.isNumeric(investorCode)) {
      throw UsernameParseException.build400Exception(String.format("Ошибка получения логина инвестора из имени файла %s", fileName));
    }
    String login = "investor".concat(investorCode);
    AppUser investor = userService.findByLogin(login);
    if (Objects.isNull(investor)) {
      throw UsernameNotFoundException.build404Exception(String.format("Пользователь с логином %s не найден", login));
    }
    return investor;
  }

  public void delete(BigInteger id) {
    usersAnnexToContractsService.deleteById(id);
  }

  public void deleteList(List<BigInteger> ids) {
    String remoteFolder = nextcloudProperty.getRemoteFolder();
    ids.forEach(id -> {
      UsersAnnexToContracts annex = usersAnnexToContractsService.findById(id);
      connector.removeFile(remoteFolder + annex.getAnnex().getAnnexName());
      delete(id);
    });
  }

}
