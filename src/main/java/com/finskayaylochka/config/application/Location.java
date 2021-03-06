package com.finskayaylochka.config.application;

/**
 * @author Alexandr Stegnin
 */

public class Location {

  // Общие
  public static final String LOGIN = "/login";

  public static final String LOGOUT = "/logout";

  public static final String DEMO = "/demo";

  public static final String INVESTMENTS = "/investments";

  public static final String HOME = "/";

  public static final String WELCOME = "/welcome";

  public static final String WILD_CARD = "**";

  // Админ
  public static final String ADMIN = "/admin";

  public static final String CATALOGUE = "/catalogue";

  // Транзакции
  public static final String URL_TRANSACTIONS = "/transactions";

  public static final String URL_TRANSACTIONS_TX_ID = URL_TRANSACTIONS + "/{txId}";

  public static final String URL_TRANSACTIONS_ROLLBACK = URL_TRANSACTIONS + "/rollback";

  public static final String URL_TRANSACTIONS_CASH = URL_TRANSACTIONS + "/cash";

  // Объекты
  public static final String FACILITIES = "/facilities";

  public static final String FACILITIES_LIST = FACILITIES + "/list";

  public static final String FACILITIES_CREATE = FACILITIES + "/create";

  public static final String FACILITIES_UPDATE = FACILITIES + "/update";

  public static final String FACILITIES_DELETE = FACILITIES + "/delete";

  public static final String FACILITIES_EDIT = FACILITIES + "/edit/{id}";

  public static final String FACILITY_FIND = FACILITIES + "/find";

  public static final String FACILITIES_OPENED = FACILITIES + "/opened";


  // Помещения
  public static final String ROOMS = "/rooms";

  public static final String ROOMS_LIST = ROOMS + "/list";

  public static final String ROOMS_UPDATE = ROOMS + "/update";

  public static final String ROOMS_DELETE = ROOMS + "/delete";

  public static final String ROOMS_CREATE = ROOMS + "/create";

  public static final String ROOMS_FIND = ROOMS + "/find";

  // Подобъекты
  public static final String UNDER_FACILITIES = "/facilities/child";

  public static final String UNDER_FACILITIES_LIST = UNDER_FACILITIES + "/list";

  public static final String UNDER_FACILITIES_UPDATE = UNDER_FACILITIES + "/update";

  public static final String UNDER_FACILITIES_DELETE = UNDER_FACILITIES + "/delete";

  public static final String UNDER_FACILITIES_CREATE = UNDER_FACILITIES + "/create";

  public static final String UNDER_FACILITIES_FIND = UNDER_FACILITIES + "/find";

  // Источники денег
  public static final String CASH_SOURCES = "/cash-sources";

  public static final String CASH_SOURCES_LIST = CASH_SOURCES + "/list";

  public static final String CASH_SOURCES_UPDATE = CASH_SOURCES + "/update";

  public static final String CASH_SOURCES_DELETE = CASH_SOURCES + "/delete";

  public static final String CASH_SOURCES_CREATE = CASH_SOURCES + "/create";

  public static final String CASH_SOURCES_FIND = CASH_SOURCES + "/find";

  // Приложения к договорам инвесторов
  public static final String INVESTOR_ANNEXES = "/investor/annexes";

  public static final String INVESTOR_ANNEXES_UPLOAD = INVESTOR_ANNEXES + "/upload";

  public static final String INVESTOR_ANNEXES_DELETE = INVESTOR_ANNEXES + "/delete";

  public static final String INVESTOR_ANNEXES_DELETE_LIST = INVESTOR_ANNEXES_DELETE + "/list";

  // Токены приложений
  public static final String TOKENS = "/tokens";

  public static final String TOKENS_CREATE = TOKENS + "/create";

  public static final String TOKENS_UPDATE = TOKENS + "/update";

  public static final String TOKENS_DELETE = TOKENS + "/delete";

  public static final String TOKENS_FIND = TOKENS + "/find";

  // Битрикс
  public static final String BITRIX_MERGE = "/bitrix/merge";

  // Websocket
  public static final String[] WEBSOCKET_PATHS = {
      "/turn" + WILD_CARD, "/progress" + WILD_CARD, "/status" + WILD_CARD
  };

  // Деньги инвесторов
  public static final String MONEY = "/money";

  public static final String MONEY_LIST = MONEY + "/list";

  public static final String MONEY_CREATE = MONEY + "/create";

  public static final String MONEY_EDIT_ID = MONEY + "/edit/{id}";

  public static final String MONEY_CLOSE_ID = MONEY + "/close/{id}";

  public static final String MONEY_SAVE = MONEY + "/save";

  public static final String MONEY_DOUBLE = MONEY + "/double";

  public static final String MONEY_DOUBLE_ID = MONEY + "/double/{id}";

  public static final String MONEY_DELETE_LIST = MONEY + "/delete/list";

  public static final String MONEY_CASHING = MONEY + "/cashing";

  public static final String MONEY_CASHING_ALL = MONEY_CASHING + "/all";

  public static final String MONEY_GET = MONEY + "/get";

  public static final String MONEY_REINVEST_SAVE = MONEY + "/reinvest/save";

  public static final String MONEY_REINVEST_CASH_SAVE = MONEY + "/reinvest/cash/save";

  public static final String MONEY_DIVIDE = "/divide-cash";

  public static final String MONEY_DIVIDE_MULTIPLE = MONEY + "/divide-multiple";

  public static final String MONEY_CLOSE = MONEY + "/close";

  public static final String MONEY_UPDATE = MONEY + "/update";

  public static final String MONEY_CLOSE_RESALE = MONEY + "/close/resale";

  public static final String MONEY_CLOSE_CASHING_ONE = MONEY + "/close/cashing/one";

  public static final String MONEY_ACCEPT = MONEY + "/accept";

  public static final String MONEY_OPENED = MONEY + "/opened";

  public static final String MONEY_RE_BUY = MONEY + "/buy-share";


  // Детали новых денег
  public static final String NEW_CASH_DETAILS = "/new-cash-details";

  public static final String NEW_CASH_DETAILS_LIST = NEW_CASH_DETAILS + "/list";

  public static final String NEW_CASH_DETAILS_UPDATE = NEW_CASH_DETAILS + "/update";

  public static final String NEW_CASH_DETAILS_DELETE = NEW_CASH_DETAILS + "/delete";

  public static final String NEW_CASH_DETAILS_CREATE = NEW_CASH_DETAILS + "/create";

  public static final String NEW_CASH_DETAILS_FIND = NEW_CASH_DETAILS + "/find";

  // Виды закрытия
  public static final String TYPE_CLOSING = "/type-closing";

  public static final String TYPE_CLOSING_LIST = TYPE_CLOSING + "/list";

  public static final String TYPE_CLOSING_UPDATE = TYPE_CLOSING + "/update";

  public static final String TYPE_CLOSING_DELETE = TYPE_CLOSING + "/delete";

  public static final String TYPE_CLOSING_CREATE = TYPE_CLOSING + "/create";

  public static final String TYPE_CLOSING_FIND = TYPE_CLOSING + "/find";

  // Восстановление пароля
  public static final String FORGOT_PASSWORD = "/forgotPassword";

  public static final String SAVE_PASSWORD = "/savePassword";

  public static final String SW_JS = "/sw.js";

  public static final String RESET_PASSWORD = "/resetPassword";

  public static final String CHANGE_PASSWORD = "/changePassword";

  // Выплаты инвесторам по аренде/продаже
  public static final String PAYMENTS_URL = "/payments";

  public static final String SALE_PAYMENTS = PAYMENTS_URL + "/sale";

  public static final String SALE_PAYMENTS_UPLOAD = SALE_PAYMENTS + "/upload";

  public static final String SALE_PAYMENTS_DELETE_CHECKED = SALE_PAYMENTS + "/delete/checked";

  public static final String SALE_PAYMENTS_REINVEST = SALE_PAYMENTS + "/reinvest";

  public static final String SALE_PAYMENTS_DIVIDE = SALE_PAYMENTS + "/divide";

  // ПОЛЬЗОВАТЕЛИ
  public static final String USERS_URL = "/users";

  public static final String DEACTIVATE_USER = USERS_URL + "/deactivate";

  public static final String USERS_LIST = USERS_URL + "/list";

  public static final String USERS_CREATE = USERS_URL + "/create";

  public static final String USERS_SAVE = USERS_URL + "/save";

  public static final String USERS_DELETE = USERS_URL + "/delete";

  public static final String USERS_FIND_BY_ID = USERS_URL + "/find";

  //РОЛИ СИСТЕМЫ
  public static final String ROLES_URL = "/roles";

  public static final String ROLE_LIST = ROLES_URL + "/list";

  public static final String ROLE_CREATE = ROLES_URL + "/create";

  public static final String ROLE_FIND = ROLES_URL + "/find";

  public static final String ROLE_UPDATE = ROLES_URL + "/update";

  public static final String ROLE_DELETE = ROLES_URL + "/delete";

  //ТРАНЗАКЦИИ ПО СЧЕТАМ
  public static final String ACC_TRANSACTIONS = MONEY + "/transactions";

  public static final String ACC_TRANSACTIONS_DELETE = ACC_TRANSACTIONS + "/delete";

  public static final String ACC_TRANSACTIONS_POPUP = ACC_TRANSACTIONS + "/popup";

  //СВОБОДНЫЕ СРЕДСТВА КЛИЕНТОВ
  public static final String TRANSACTIONS_SUMMARY = ACC_TRANSACTIONS + "/summary";

  public static final String TRANSACTIONS_DETAILS = ACC_TRANSACTIONS + "/details";

  public static final String TRANSACTIONS_REINVEST = ACC_TRANSACTIONS + "/reinvest";

  public static final String TRANSACTIONS_BALANCE = ACC_TRANSACTIONS + "/balance";

  //МАРКЕТИНГОВОЕ ДЕРЕВО
  public static final String MARKETING_TREE = "/marketing-tree";

  public static final String MARKETING_TREE_UPDATE = MARKETING_TREE + "/update";

  //С КЕМ ЗАКЛЮЧЁН ДОГОВОР
  public static final String USER_AGREEMENTS = "/agreements";

  public static final String USER_AGREEMENTS_LIST = USER_AGREEMENTS + "/list";

  public static final String USER_AGREEMENTS_ID = USER_AGREEMENTS + "/{id}";

  public static final String USER_AGREEMENTS_CREATE = USER_AGREEMENTS + "/create";

  public static final String USER_AGREEMENTS_UPDATE = USER_AGREEMENTS + "/update";

  public static final String USER_AGREEMENTS_DELETE = USER_AGREEMENTS + "/delete";

  public static final String SEND_WELCOME = "/send/welcome";

  public static final String[] ADMIN_URLS = {
      ADMIN,
      CATALOGUE,
      URL_TRANSACTIONS + WILD_CARD,
      FACILITIES + WILD_CARD,
      ROOMS + WILD_CARD,
      UNDER_FACILITIES + WILD_CARD,
      CASH_SOURCES + WILD_CARD,
      INVESTOR_ANNEXES + WILD_CARD,
      HOME, WELCOME, INVESTMENTS,
      TOKENS + WILD_CARD,
      BITRIX_MERGE,
      MONEY + WILD_CARD,
      NEW_CASH_DETAILS + WILD_CARD,
      TYPE_CLOSING + WILD_CARD,
      MONEY_DIVIDE,
      MONEY_DIVIDE_MULTIPLE,
      MONEY + WILD_CARD,
      PAYMENTS_URL + WILD_CARD,
      USERS_URL + WILD_CARD,
      SEND_WELCOME
  };

  public static final String[] INVESTOR_URLS = {
      HOME, WELCOME, INVESTMENTS,
  };

  public static final String[] PERMIT_ALL_URLS = {
      HOME,
      FORGOT_PASSWORD,
      SAVE_PASSWORD,
      SW_JS, LOGIN, LOGOUT,
      RESET_PASSWORD,
      CHANGE_PASSWORD + WILD_CARD,
      "/agreement-personal-data" + WILD_CARD
  };
}
