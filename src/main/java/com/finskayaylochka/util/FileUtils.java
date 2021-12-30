package com.finskayaylochka.util;

import com.finskayaylochka.config.exception.FileUploadException;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Alexandr Stegnin
 */
@UtilityClass
public class FileUtils {

  public static String resolveSubFolder(String attachmentName) {
    String subFolder = attachmentName.split("_")[1].replaceAll("[^0-9]", "");
    if (subFolder.length() != 8) {
      throw FileUploadException.build400Exception("Проверьте название файла (маска 000_01.01.2021)");
    }
    return subFolder + File.separator;
  }

  public static String resolveRemotePath(String remoteFolder, String attachmentName) {
    String subFolder = resolveSubFolder(attachmentName);
    String targetFolder = remoteFolder + subFolder;
    return targetFolder + attachmentName;
  }

  public static String resolveFilenameExtension(String filename) {
    return "." + FilenameUtils.getExtension(filename);
  }

}
