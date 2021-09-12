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
  RoomService roomService;
  FacilityService facilityService;
  UnderFacilityService underFacilityService;
  RentPaymentService rentPaymentService;
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
    switch (type) {
      case RENT:
        response = uploadRent(sheet);
        break;
      case SALE:
        response = uploadSale(sheet);
        break;
    }
    return response;
  }

  /**
   * Загрузить excel файл с данными о выплатах инвесторам по аренде
   *
   * @param sheet лист excel файла
   * @return ответ об успешном/неудачном выполнении
   */
  private ApiResponse uploadRent(Sheet sheet) {
    List<Room> rooms = roomService.findAll();
    List<AppUser> users = appUserService.findAll();
    List<RentPayment> rentPaymentTmp = rentPaymentService.findAll();
    int cel = 0;
    Map<Long, AccountTransaction> userTransactions = new HashMap<>();
    for (Row row : sheet) {
      cel++;
      if (cel > 1) {
        if (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK) {
          Calendar calendar = Calendar.getInstance();
          try {
            calendar.setTime(FORMAT.parse(row.getCell(0).getDateCellValue().toString()));
          } catch (Exception ignored) {
          }

          java.time.LocalDate cal = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

          try {
            for (int i = 0; i < row.getLastCellNum(); i++) {
              row.getCell(i).setCellType(CellType.STRING);
            }
          } catch (Exception ignored) {
          }

          String lastName;
          lastName = row.getCell(4).getStringCellValue();
          AppUser user = users.stream().filter(u -> u.getProfile().getLastName().equalsIgnoreCase(lastName))
              .findFirst()
              .orElse(null);
          if (user == null) {
            return new ApiResponse("Не найден пользователь [" + lastName + "]", HttpStatus.PRECONDITION_FAILED.value());
          }

          String underFacilityName = row.getCell(2).getStringCellValue();
          if (underFacilityName == null || underFacilityName.isEmpty()) {
            return new ApiResponse("Не указан подобъект", HttpStatus.PRECONDITION_FAILED.value());
          }
          UnderFacility underFacility = underFacilityService.findByName(underFacilityName);
          if (underFacility == null) {
            return new ApiResponse("Не найден подобъект [" + underFacilityName + "]", HttpStatus.PRECONDITION_FAILED.value());
          }
          String facilityName = row.getCell(1).getStringCellValue();
          if (facilityName == null || facilityName.isEmpty()) {
            return new ApiResponse("Не указан объект", HttpStatus.PRECONDITION_FAILED.value());
          }
          Facility facility = facilityService.findByName(facilityName);
          if (facility == null) {
            return new ApiResponse("Не найден объект [" + facilityName + "]", HttpStatus.PRECONDITION_FAILED.value());
          }
          RentPayment rentPayment = new RentPayment();
          rentPayment.setDateReport(Date.from(cal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
          rentPayment.setFacility(facility);
          rentPayment.setUnderFacility(underFacility);
          rentPayment.setRoom(rooms.stream()
              .filter(r -> r.getName().equalsIgnoreCase(row.getCell(3).getStringCellValue()))
              .findFirst().orElse(null));

          rentPayment.setInvestor(user);
          rentPayment.setShareType(row.getCell(5).getStringCellValue());
          rentPayment.setGivenCash(Float.parseFloat(row.getCell(6).getStringCellValue()));
          rentPayment.setSumInUnderFacility(Float.parseFloat(row.getCell(7).getStringCellValue()));
          rentPayment.setShareForSvod(Float.parseFloat(row.getCell(8).getStringCellValue()));

          rentPayment.setShare(Float.parseFloat(row.getCell(9).getStringCellValue()));
          rentPayment.setTaxation(Float.parseFloat(row.getCell(10).getStringCellValue()));
          rentPayment.setCashing(Float.parseFloat(row.getCell(11).getStringCellValue()));
          rentPayment.setSumma(Float.parseFloat(row.getCell(13).getStringCellValue()));
          rentPayment.setOnInvestor(Float.parseFloat(row.getCell(14).getStringCellValue()));
          rentPayment.setAfterTax(Float.parseFloat(row.getCell(15).getStringCellValue()));
          rentPayment.setAfterDeductionEmptyFacility(Float.parseFloat(row.getCell(16).getStringCellValue()));
          rentPayment.setAfterCashing(Float.parseFloat(row.getCell(17).getStringCellValue()));

          rentPayment.setReInvest(row.getCell(18).getStringCellValue());

          Facility reFacility = null;
          String reFacilityName = row.getCell(19).getStringCellValue();
          if (reFacilityName != null && !reFacilityName.isEmpty()) {
            reFacility = facilityService.findByName(reFacilityName);
          }

          rentPayment.setReFacility(reFacility);

          List<RentPayment> flowsList = rentPaymentTmp.stream()
              .filter(flows -> (flows.getDateReport() != null && rentPayment.getDateReport() != null) &&
                  globalFunctions.getMonthInt(flows.getDateReport()) ==
                      globalFunctions.getMonthInt(rentPayment.getDateReport()) &&
                  globalFunctions.getYearInt(flows.getDateReport()) ==
                      globalFunctions.getYearInt(rentPayment.getDateReport()) &&
                  globalFunctions.getDayInt(flows.getDateReport()) ==
                      globalFunctions.getDayInt(rentPayment.getDateReport()))

              .filter(flows -> !Objects.equals(flows.getFacility(), null) &&
                  flows.getFacility().getId().equals(rentPayment.getFacility().getId()))

              .filter(flows -> !Objects.equals(rentPayment.getUnderFacility(), null) &&
                  flows.getUnderFacility().getId().equals(rentPayment.getUnderFacility().getId()))

              .filter(flows -> flows.getInvestor().getId().equals(rentPayment.getInvestor().getId()))

              .collect(Collectors.toList());

          if (flowsList.size() == 0) {
            rentPayment.setIsReinvest(1);
            AccountTransaction transaction = userTransactions.get(user.getId());
            if (transaction == null) {
              transaction = createRentTransaction(user, rentPayment);
            } else {
              transaction = updateRentTransaction(transaction, rentPayment);
            }
            userTransactions.put(user.getId(), transaction);
            rentPaymentService.create(rentPayment);
          }
        }
      }
    }
    return new ApiResponse("Загрузка файла с данными по аренде завершена");
  }

  /**
   * Загрузить excel файл с данными о выплатах инвесторам по продаже
   *
   * @param sheet лист excel файла
   * @return ответ об успешном/неудачном выполнении
   */
  private ApiResponse uploadSale(Sheet sheet) {
    if (!ExcelUtils.isCorrect(sheet)) {
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
      if (cel > 1) {
        if (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK) {
          AccountTransaction parentTransaction = new AccountTransaction();
          Calendar calendar = Calendar.getInstance();
          try {
            calendar.setTime(FORMAT.parse(row.getCell(4).getDateCellValue().toString()));
          } catch (Exception ex) {
            return new ApiResponse(String.format("Не удачная попытка конвертировать строку в дату. Строка %d, столбец 5", cel),
                HttpStatus.PRECONDITION_FAILED.value());
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
            return new ApiResponse(String.format("Неудачная попытка конвертировать строку в дату. Строка %d, столбец 9", cel),
                HttpStatus.PRECONDITION_FAILED.value());
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

          String lastName;
          lastName = row.getCell(1).getStringCellValue();
          if (Objects.isNull(lastName) || lastName.isEmpty()) {
            return new ApiResponse(String.format("Не указан инвестор! Строка %d, столбец 2", cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          AppUser user = users.stream()
              .filter(u -> Objects.nonNull(u.getProfile()))
              .filter(u -> Objects.nonNull(u.getProfile().getLastName()))
              .filter(u -> u.getProfile().getLastName().equalsIgnoreCase(lastName))
              .findFirst()
              .orElse(null);

          if (Objects.isNull(user)) {
            return new ApiResponse(String.format("Неудачная попытка найти пользователя \"%s\". Строка %d, столбец 2", lastName, cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          String facilityName = row.getCell(0).getStringCellValue();
          if (Objects.isNull(facilityName) || facilityName.isEmpty()) {
            return new ApiResponse(String.format("Не указан объект! Строка %d, столбец 1", cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          Facility facility = facilities.get(facilityName);
          if (Objects.isNull(facility)) {
            facility = facilityService.findByName(facilityName);
            if (Objects.isNull(facility)) {
              return new ApiResponse(String.format("Не указан или не верно указан объект \"%s\". Строка %d, столбец 1", facilityName, cel),
                  HttpStatus.PRECONDITION_FAILED.value());
            }
          }
          facilities.putIfAbsent(facilityName, facility);

          String share = row.getCell(2).getStringCellValue();
          if (Objects.isNull(share)) {
            return new ApiResponse(String.format("Не указана доля. Строка %d, столбец 3", cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }
          ShareType shareType = shareKinds
              .stream()
              .filter(type -> type.getTitle().equalsIgnoreCase(share))
              .findFirst()
              .orElse(null);

          if (Objects.isNull(shareType)) {
            return new ApiResponse(String.format("Не указана или не верно указана доля \"%s\". Строка %d, столбец 3", share, cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          String strCashInFacility = row.getCell(3).getStringCellValue();
          BigDecimal cashInFacility;
          try {
            cashInFacility = new BigDecimal(strCashInFacility);
          } catch (NumberFormatException ex) {
            return new ApiResponse(String.format("Ошибка преобразования суммы \"Вложено в объект\". Строка %d, столбец 4", cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          String strCashInUnderFacility = row.getCell(5).getStringCellValue();
          BigDecimal cashInUnderFacility;
          try {
            cashInUnderFacility = new BigDecimal(strCashInUnderFacility);
          } catch (NumberFormatException ex) {
            return new ApiResponse(String.format("Ошибка преобразования суммы \"Вложено в подобъект\". Строка %d, столбец 6", cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          String strProfitToReinvest = row.getCell(6).getStringCellValue();
          BigDecimal profitToReinvest;
          try {
            profitToReinvest = new BigDecimal(strProfitToReinvest);
          } catch (NumberFormatException ex) {
            return new ApiResponse(String.format("Ошибка преобразования суммы \"Сколько прибыли реинвест\". Строка %d, столбец 7", cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }

          String underFacilityName = row.getCell(7).getStringCellValue();
          if (Objects.isNull(underFacilityName) || underFacilityName.isEmpty()) {
            return new ApiResponse(String.format("Не указан или не верно указан подобъект \"%s\". Строка %d, столбец 8", underFacilityName, cel),
                HttpStatus.PRECONDITION_FAILED.value());
          }
          UnderFacility underFacility = underFacilities.get(underFacilityName);
          if (Objects.isNull(underFacility)) {
            underFacility = underFacilityService.findByName(underFacilityName);
            if (Objects.isNull(underFacility)) {
              return new ApiResponse(String.format("Не указан или не верно указан подобъект \"%s\". Строка %d, столбец 8", underFacilityName, cel),
                  HttpStatus.PRECONDITION_FAILED.value());
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
              .cashInUnderFacility(cashInUnderFacility)
              .profitToReInvest(profitToReinvest)
              .underFacility(underFacility)
              .dateSale(Date.from(calSale.atStartOfDay(ZoneId.systemDefault()).toInstant()))
              .build();

          List<SalePayment> flowsSaleList = salePayments.stream()
              .filter(flows -> globalFunctions.getMonthInt(flows.getDateSale()) ==
                  globalFunctions.getMonthInt(salePayment.getDateSale()) &&
                  globalFunctions.getYearInt(flows.getDateSale()) ==
                      globalFunctions.getYearInt(salePayment.getDateSale()) &&
                  globalFunctions.getDayInt(flows.getDateSale()) ==
                      globalFunctions.getDayInt(salePayment.getDateSale()))

              .filter(flows -> Objects.nonNull(flows.getFacility()) &&
                  flows.getFacility().getId().equals(salePayment.getFacility().getId()))

              .filter(flows -> Objects.nonNull(salePayment.getUnderFacility()) &&
                  flows.getUnderFacility().getId().equals(salePayment.getUnderFacility().getId()))

              .filter(flows -> flows.getInvestor().getId().equals(salePayment.getInvestor().getId()))

              .collect(Collectors.toList());

          if (flowsSaleList.size() == 0) {
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
   * Создать транзакцию по выплате (аренда)
   *
   * @param investor    инвестор
   * @param rentPayment сумма аренды
   */
  private AccountTransaction createRentTransaction(AppUser investor, RentPayment rentPayment) {
    Account owner = getAccount(investor.getId(), OwnerType.INVESTOR);
    Account payer = getAccount(rentPayment.getFacility().getId(), OwnerType.FACILITY);
    AccountTransaction transaction = new AccountTransaction(owner);
    transaction.setPayer(payer);
    transaction.setRecipient(owner);
    transaction.setOperationType(OperationType.DEBIT);
    transaction.setCashType(CashType.RENT_CASH);
    transaction.setCash(BigDecimal.valueOf(rentPayment.getAfterCashing()));
    accountTransactionService.create(transaction);
    rentPayment.setAccTxId(transaction.getId());
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
   * @param rentPayment выплата (аренда)
   */
  private AccountTransaction updateRentTransaction(AccountTransaction transaction, RentPayment rentPayment) {
    BigDecimal newCash = transaction.getCash().add(BigDecimal.valueOf(rentPayment.getAfterCashing()));
    rentPayment.setAccTxId(transaction.getId());
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
