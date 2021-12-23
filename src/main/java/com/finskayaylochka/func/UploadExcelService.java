package com.finskayaylochka.func;

import com.finskayaylochka.config.exception.EntityNotFoundException;
import com.finskayaylochka.model.*;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.enums.*;
import com.finskayaylochka.repository.MoneyRepository;
import com.finskayaylochka.repository.TypeClosingRepository;
import com.finskayaylochka.service.*;
import com.finskayaylochka.util.ExcelUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для загрузки Excel файлов
 *
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UploadExcelService {

  static SimpleDateFormat FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);

  static SimpleDateFormat DDMMYYYY_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

  AppUserService appUserService;
  FacilityService facilityService;
  UnderFacilityService underFacilityService;
  SalePaymentService salePaymentService;
  GlobalFunctions globalFunctions;
  AccountService accountService;
  AccountTransactionService accountTransactionService;
  MoneyRepository moneyRepository;
  TypeClosingRepository typeClosingRepository;

  public ApiResponse upload(MultipartHttpServletRequest request, UploadType type) {
    Iterator<String> itr = request.getFileNames();
    List<MultipartFile> multipartFiles = new ArrayList<>(0);
    while (itr.hasNext()) {
      multipartFiles.add(request.getFile(itr.next()));
    }
    MultipartFile file = multipartFiles.get(0);
    return upload(file, request, type);
  }

  /**
   * Загрузить excel файл с данными по продаже/аренде
   *
   * @param file    файл с клиента
   * @param request запрос
   * @param type    вид загружаемого файла
   * @return ответ об успешном/неудачном выполнении операции
   */
  public ApiResponse upload(MultipartFile file, HttpServletRequest request, UploadType type) {
    ApiResponse response = new ApiResponse();
    InputStream fileInputStream;
    Workbook workbook;
    Sheet sheet;
    try {
      fileInputStream = new BufferedInputStream(file.getInputStream());
      workbook = ExcelUtils.getWorkbook(fileInputStream, file.getOriginalFilename());
      sheet = workbook.getSheetAt(0);
    } catch (IOException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }
    HttpSession session = request.getSession(true);
    session.setMaxInactiveInterval(30 * 60);
    if (type == UploadType.SALE) {
      response = uploadSale(sheet);
    }
    return response;
  }

  /**
   * Загрузить excel файл с данными о выплатах инвесторам по продаже
   *
   * @param sheet лист excel файла
   * @return ответ об успешном/неудачном выполнении
   */
  private ApiResponse uploadSale(Sheet sheet) {
    if (ExcelUtils.isIncorrect(sheet)) {
      return new ApiResponse("Проверьте кол-во столбцов в файле. Должно быть 10", HttpStatus.BAD_REQUEST.value());
    }
    List<AppUser> users = appUserService.getInvestors();
    List<SalePayment> salePayments = salePaymentService.findAll();
    int cel = 0;

    List<ShareType> shareKinds = Arrays.asList(ShareType.values());
    Map<String, Facility> facilities = new HashMap<>();
    Map<String, UnderFacility> underFacilities = new HashMap<>();
    Map<Long, AccountTransaction> userTransactions = new HashMap<>();
    Map<Long, List<Long>> investorsUnderFacilities = new HashMap<>();
    LocalDate reportDate = null;
    for (Row row : sheet) {
      cel++;
      if (cel > 1 && (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK)) {
        AccountTransaction parentTransaction = new AccountTransaction();
        Calendar calendar = Calendar.getInstance();
        try {
          calendar.setTime(FORMAT.parse(row.getCell(4).getDateCellValue().toString()));
        } catch (Exception ex) {
          return ApiResponse.build422Response(String.format("Не удачная попытка конвертировать строку в дату. Строка %d, столбец 5", cel));
        }

        java.time.LocalDate cal = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Calendar dateSale = Calendar.getInstance();
        try {
          Cell cell = row.getCell(8);
          switch (cell.getCellTypeEnum()) {
            case NUMERIC:
              dateSale.setTime(FORMAT.parse(cell.getDateCellValue().toString()));
              break;
            case STRING:
              dateSale.setTime(DDMMYYYY_FORMAT.parse(cell.getStringCellValue()));
              break;
          }
        } catch (Exception ex) {
          return ApiResponse.build422Response(String.format("Неудачная попытка конвертировать строку в дату. Строка %d, столбец 9", cel));
        }

        java.time.LocalDate calSale = dateSale.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String realDateGivenStr;
        Date realDateGiven = null;
        Cell cell = row.getCell(9);
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
          case STRING:
            realDateGivenStr = cell.getStringCellValue();
            try {
              realDateGiven = FORMAT.parse(realDateGivenStr);
            } catch (Exception ignored) {
            }
            break;
          case NUMERIC:
            realDateGiven = cell.getDateCellValue();
            break;
        }

        try {
          row.cellIterator().forEachRemaining(c -> c.setCellType(CellType.STRING));
        } catch (Exception ignored) {
        }

        String login = row.getCell(1).getStringCellValue();
        if (Objects.isNull(login) || login.isEmpty()) {
          return ApiResponse.build422Response(String.format("Не указан инвестор! Строка %d, столбец 2", cel));
        }

        AppUser user = users.stream()
            .filter(u -> u.getLogin().equalsIgnoreCase(login))
            .findFirst()
            .orElse(null);

        if (Objects.isNull(user)) {
          return ApiResponse.build422Response(String.format("Неудачная попытка найти пользователя \"%s\". Строка %d, столбец 2", login, cel));
        }

        String facilityName = row.getCell(0).getStringCellValue();
        if (Objects.isNull(facilityName) || facilityName.isEmpty()) {
          return ApiResponse.build422Response(String.format("Не указан объект! Строка %d, столбец 1", cel));
        }

        Facility facility = facilities.get(facilityName);
        if (Objects.isNull(facility)) {
          facility = facilityService.findByName(facilityName);
          if (Objects.isNull(facility)) {
            return ApiResponse.build422Response(String.format("Не указан или не верно указан объект \"%s\". Строка %d, столбец 1", facilityName, cel));
          }
        }
        facilities.putIfAbsent(facilityName, facility);

        String share = row.getCell(2).getStringCellValue();
        if (Objects.isNull(share)) {
          return ApiResponse.build422Response(String.format("Не указана доля. Строка %d, столбец 3", cel));
        }
        ShareType shareType = shareKinds
            .stream()
            .filter(type -> type.getTitle().equalsIgnoreCase(share))
            .findFirst()
            .orElse(null);

        if (Objects.isNull(shareType)) {
          return ApiResponse.build422Response(String.format("Не указана или не верно указана доля \"%s\". Строка %d, столбец 3", share, cel));
        }

        String strCashInFacility = row.getCell(3).getStringCellValue();
        BigDecimal cashInFacility;
        try {
          cashInFacility = new BigDecimal(strCashInFacility);
        } catch (NumberFormatException ex) {
          return ApiResponse.build422Response(String.format("Ошибка преобразования суммы \"Вложено в объект\". Строка %d, столбец 4", cel));
        }

        String strCashInUnderFacility = row.getCell(5).getStringCellValue();
        BigDecimal cashInUnderFacility;
        try {
          cashInUnderFacility = new BigDecimal(strCashInUnderFacility);
        } catch (NumberFormatException ex) {
          return ApiResponse.build422Response(String.format("Ошибка преобразования суммы \"Вложено в подобъект\". Строка %d, столбец 6", cel));
        }

        String strProfitToReinvest = row.getCell(6).getStringCellValue();
        BigDecimal profitToReinvest;
        try {
          profitToReinvest = new BigDecimal(strProfitToReinvest).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
          return ApiResponse.build422Response(String.format("Ошибка преобразования суммы \"Сколько прибыли реинвест\". Строка %d, столбец 7", cel));
        }

        String underFacilityName = row.getCell(7).getStringCellValue();
        if (Objects.isNull(underFacilityName) || underFacilityName.isEmpty()) {
          return ApiResponse.build422Response(String.format("Не указан или не верно указан подобъект \"%s\". Строка %d, столбец 8", underFacilityName, cel));
        }
        UnderFacility underFacility = underFacilities.get(underFacilityName);
        if (Objects.isNull(underFacility)) {
          underFacility = underFacilityService.findByName(underFacilityName);
          if (Objects.isNull(underFacility)) {
            return ApiResponse.build422Response(String.format("Не указан или не верно указан подобъект \"%s\". Строка %d, столбец 8", underFacilityName, cel));
          }
        }
        underFacilities.putIfAbsent(underFacilityName, underFacility);

        SalePayment salePayment = SalePayment.builder()
            .realDateGiven(realDateGiven)
            .facility(facility)
            .investor(user)
            .shareType(shareType)
            .cashInFacility(cashInFacility)
            .dateGiven(Date.from(cal.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .cashInUnderFacility(cashInUnderFacility.setScale(2, RoundingMode.CEILING))
            .profitToReInvest(profitToReinvest.setScale(2, RoundingMode.CEILING))
            .underFacility(underFacility)
            .dateSale(Date.from(calSale.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .build();

        List<SalePayment> flowsSaleList = salePayments.stream()
            .filter(flows -> flows.getDateSale().equals(salePayment.getDateSale()))
            .filter(flows -> Objects.nonNull(flows.getFacility()))
            .filter(flows -> Objects.equals(flows.getFacility().getId(), salePayment.getFacility().getId()))
            .filter(flows -> Objects.nonNull(salePayment.getUnderFacility()))
            .filter(flows -> Objects.equals(flows.getUnderFacility().getId(), salePayment.getUnderFacility().getId()))
            .filter(flows -> Objects.equals(flows.getInvestor().getId(), salePayment.getInvestor().getId()))
            .collect(Collectors.toList());

        if (flowsSaleList.isEmpty()) {
          salePayment.setIsReinvest(1);
          AccountTransaction transaction = userTransactions.get(user.getId());
          if (Objects.isNull(transaction)) {
            transaction = createSaleTransaction(user, salePayment, parentTransaction);
          } else {
            transaction = updateSaleTransaction(transaction, salePayment);
          }
          userTransactions.put(user.getId(), transaction);
          reportDate = calSale;
          if (investorsUnderFacilities.containsKey(user.getId())) {
            List<Long> ufIds = investorsUnderFacilities.get(user.getId());
            if (Objects.isNull(ufIds)) {
              ufIds = new ArrayList<>();
            }
            ufIds.add(underFacility.getId());
          } else {
            List<Long> ufIds = new ArrayList<>();
            ufIds.add(underFacility.getId());
            investorsUnderFacilities.put(user.getId(), ufIds);
          }
          salePaymentService.create(salePayment);
        }
        closeOpenedMonies(investorsUnderFacilities, reportDate, parentTransaction);
      }
    }
    return new ApiResponse("Загрузка файла с данными о продаже завершена");
  }

  /**
   * Закрыть суммы, которые были инвестированы в проданный подобъект
   *
   * @param investorsUnderFacilities список пользователей и их подобъектов
   * @param reportDate               дата продажи
   */
  private void closeOpenedMonies(Map<Long, List<Long>> investorsUnderFacilities, LocalDate reportDate, AccountTransaction parent) {
    TypeClosing typeClosing = typeClosingRepository.findByName("Продажа");
    if (Objects.isNull(typeClosing)) {
      throw new EntityNotFoundException("Не найден вид закрытия [Продажа]");
    }
    if (Objects.isNull(reportDate)) {
      reportDate = LocalDate.now();
    }
    Date dateClosing = Date.from(reportDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    investorsUnderFacilities.forEach((k, v) -> v.forEach(ufId -> {
      List<Money> monies = moneyRepository.getOpenedMonies(ufId, k);
      monies.forEach(money -> {
        money.setDateClosing(dateClosing);
        money.setTypeClosing(typeClosing);
        money.setIsReinvest(1);
      });
      moveMoniesToAccount(monies, parent);
    }));
  }

  /**
   * Переместить суммы на счёт инвестора
   *
   * @param closedMonies список денег
   */
  private void moveMoniesToAccount(List<Money> closedMonies, AccountTransaction parent) {
    if (closedMonies.isEmpty()) {
      return;
    }
    Money money = closedMonies.get(0);
    AccountTransaction transaction = createMoneyTransaction(money, parent);
    closedMonies.remove(money);
    closedMonies.forEach(m -> updateMoneyTransaction(transaction, m));
    closedMonies.add(money);
    accountTransactionService.create(transaction);
    moneyRepository.save(closedMonies);
  }

  /**
   * Получить счёт по параметрам
   *
   * @param ownerId   id владельца
   * @param ownerType вид владельца
   * @return найденный счёт
   */
  private Account getAccount(Long ownerId, OwnerType ownerType) {
    Account account = accountService.findByOwnerId(ownerId, ownerType);
    if (Objects.isNull(account)) {
      throw new EntityNotFoundException("Не найден счёт с id [" + ownerId + "]");
    }
    return account;
  }

  /**
   * Создать транзакцию по выплате (продажа)
   *
   * @param investor    инвестор
   * @param salePayment сумма продажи
   */
  private AccountTransaction createSaleTransaction(AppUser investor, SalePayment salePayment, AccountTransaction parent) {
    Account owner = getAccount(investor.getId(), OwnerType.INVESTOR);
    Account payer = getAccount(salePayment.getUnderFacility().getId(), OwnerType.UNDER_FACILITY);
    if (Objects.isNull(parent.getOwner())) {
      parent.setOwner(payer);
      parent.setOperationType(OperationType.DEBIT);
      parent.setCashType(CashType.SALE_CASH);
      accountTransactionService.create(parent);
    }
    AccountTransaction transaction = new AccountTransaction(owner);
    transaction.setPayer(payer);
    transaction.setRecipient(owner);
    transaction.setOperationType(OperationType.DEBIT);
    transaction.setCashType(CashType.SALE_CASH);
    transaction.setCash(salePayment.getProfitToReInvest());
    transaction.setParent(parent);
    accountTransactionService.create(transaction);
    salePayment.setAccTxId(transaction.getId());
    return transaction;
  }

  /**
   * Создать транзакцию по деньгам инвесторов
   *
   * @param money сумма инвестора
   */
  private AccountTransaction createMoneyTransaction(Money money, AccountTransaction parent) {
    Account owner = getAccount(money.getInvestor().getId(), OwnerType.INVESTOR);
    Account payer = getAccount(money.getUnderFacility().getId(), OwnerType.UNDER_FACILITY);
    AccountTransaction transaction = new AccountTransaction(owner);
    transaction.setPayer(payer);
    transaction.setRecipient(owner);
    transaction.setOperationType(OperationType.DEBIT);
    transaction.setCashType(CashType.INVESTMENT_BODY);
    transaction.setCash(money.getGivenCash());
    transaction.setParent(parent);
    money.setTransaction(transaction);
    return transaction;
  }

  /**
   * Обновить транзакцию
   *
   * @param transaction транзакция
   * @param salePayment выплата (продажа)
   */
  private AccountTransaction updateSaleTransaction(AccountTransaction transaction, SalePayment salePayment) {
    BigDecimal newCash = transaction.getCash().add(salePayment.getProfitToReInvest());
    salePayment.setAccTxId(transaction.getId());
    return accountTransactionService.updateCash(transaction, newCash);
  }

  /**
   * Обновить транзакцию
   *
   * @param transaction транзакция
   * @param money       сумма инвестиций
   */
  private void updateMoneyTransaction(AccountTransaction transaction, Money money) {
    money.setTransaction(transaction);
    transaction.setCash(transaction.getCash().add(money.getGivenCash()));
  }

}
