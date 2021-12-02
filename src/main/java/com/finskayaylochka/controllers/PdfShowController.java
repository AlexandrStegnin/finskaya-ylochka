package com.finskayaylochka.controllers;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Alexandr Stegnin
 */
@Controller
public class PdfShowController {

  @SneakyThrows
  @GetMapping("/agreement-personal-data")
  public void getAgreementPersonalData(HttpServletResponse response) {

    String filePath = System.getProperty("catalina.home") + "/pdf-files/agreement/";
    String fileName = "personal-data-agreement.pdf";
    File file = new File(filePath + fileName);
    response.setContentType("application/pdf");
    try (InputStream inputStream = new FileInputStream(file)) {
      int nRead;
      while ((nRead = inputStream.read()) != -1) {
        response.getOutputStream().write(nRead);
      }
    }
  }

}
