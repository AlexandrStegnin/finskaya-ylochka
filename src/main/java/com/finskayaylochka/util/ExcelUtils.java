package com.finskayaylochka.util;

import lombok.experimental.UtilityClass;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Вспомогательный класс для загрузки excel файлов
 *
 * @author Alexandr Stegnin
 */
@UtilityClass
public class ExcelUtils {

  public static Workbook getWorkbook(InputStream inputStream, String excelPath) throws IOException {
    Workbook workbook = null;
    if (excelPath.endsWith("xlsx")) {
      workbook = new XSSFWorkbook(inputStream);
    } else if (excelPath.endsWith("xls")) {
      workbook = new HSSFWorkbook(inputStream);
    }
    return workbook;
  }

  public static boolean isCorrect(Sheet sheet) {
    int colCount = 0;
    for (Row row : sheet) {
      for (Cell cell : row) {
        if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
          colCount++;
        }
      }
      break;
    }
    return colCount == 10;
  }

  public static boolean isIncorrect(Sheet sheet) {
    return !isCorrect(sheet);
  }

}
