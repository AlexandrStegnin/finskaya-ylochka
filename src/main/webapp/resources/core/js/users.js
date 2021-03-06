let OperationEnum = {
    SAVE: 'SAVE',
    UPDATE: 'UPDATE',
    DELETE: 'DELETE',
    DEACTIVATE: 'DEACTIVATE',
    properties: {
        SAVE: {
            url: 'save'
        },
        UPDATE: {
            url: 'save'
        },
        DELETE: {
            url: 'delete'
        },
        DEACTIVATE: {
            url: 'deactivate'
        }
    }
}

Object.freeze(OperationEnum)

let UserDTO = function () {}

let RoleDTO = function () {}

let UserProfileDTO = function () {}

RoleDTO.prototype = {
    id: 0,
    humanized: '',
    build: function (id, humanized) {
        this.id = id;
        this.humanized = humanized;
    }
}

UserDTO.prototype = {
    id: 0,
    login: '',
    facilities: [],
    role: null,
    partnerId: null,
    profile: null,
    kin: null,
    phones: null,
    build: function (id, login, role, partnerId, kin, phone) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.partnerId = partnerId;
        this.kin = kin;
        this.phones = []
        this.phones[0] = {
            number: phone
        }
    },
    buildPartner: function (id, login) {
        this.id = id;
        this.login = login;
    }
}

UserProfileDTO.prototype = {
    id: 0,
    lastName: '',
    firstName: '',
    patronymic: '',
    email: '',
    masterInvestorId: 0,
    type: '',
    build: function (id, lastName, firstName, patronymic, email, masterInvestorId, type) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.email = email;
        this.masterInvestorId = masterInvestorId;
        this.type = type;
    }
}

let confirmForm

jQuery(document).ready(function ($) {
    confirmForm = $('#confirm-form');
    onCloseUserFormClick()
    onPhoneFieldFocusOut()
    let userForm = $('#user-form-modal');

    $(document).on('click', 'a[name*="page_"]', function (e) {
        e.preventDefault();
        $('#pageNumber').val(parseInt($(this).attr('id')) - 1);
        let pageSize = 100;
        if ($('#all').prop('checked')) pageSize = 1;
        $('#pageSize').val(pageSize);
        $('#search-form').submit();
    });

    $('#create-user').on('click', function (e) {
        e.preventDefault()
        userForm.find('#title').html('Создание пользователя')
        userForm.find('#create-user').html('Создать')
        userForm.modal('show')
    })

    $('.edit-user').on('click', function (e) {
        e.preventDefault()
        userForm.find('#title').html('Обновить пользователя')
        userForm.find('#create-user').html('Обновить')
        userForm.find('#create-user').attr('data-action', OperationEnum.UPDATE)
        let userId = $(this).attr('data-user-id')
        getUser(userId)
        closeLoader()
    })

    userForm.find('#create-user').on('click', function () {
        let userDTO = getUserDTO()
        if (checkUserDTO(userDTO)) {
            saveUser(userDTO)
            closeLoader()
        }
    })

    $('#inactive').on('change', function () {
        $('#deactivated').val(!$(this).prop('checked'))
        $('#search-form').submit()
    })

    $('#confirm').on('change', function () {
        $('#confirmed').val($(this).prop('checked'))
        $('#search-form').submit()
    })

    $('#all').on('change', function () {
        $('#allRows').val($(this).prop('checked'))
        $('#search-form').submit()
    })

    $('.deactivate').on('click', function (e) {
        e.preventDefault()
        let userId = $(this).data('user-id')
        showConfirmForm('Деактивация пользователя', 'Действительно хотите деактивировать пользователя?', userId, OperationEnum.DEACTIVATE)
    })

    let isValid = {
        'login': function () {
            let loginErr = $('#loginErr');
            let login = $('#login');
            let rus = new RegExp(".*?[А-Яа-я $\/].*?");
            if (login.val().length < 4 || login.val() === '' || login.val().length > 16) {
                isValid.errors = true;
                loginErr.html('Имя пользователя должно быть от 4 до 16 символов').show();
            } else if (rus.test(login.val())) {
                isValid.errors = true;
                loginErr.html('Имя пользователя может содержать только буквы латинского алфавита, ' +
                    'цифры, знак подчёркивания (_) и точку (.)').show();
            } else {
                isValid.errors = false;
                loginErr.html('').hide();
            }
        },

        'readAnnexes': function () {
            let errUnread = $('#errUnread');
            isValid.errors = !errUnread.css('display', 'block');
        },

        'email': function () {
            let emailErr = $('#emailErr');
            let email = $('#email');
            let emailValid = new RegExp("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$");
            if (!emailValid.test(email.val())) {
                isValid.errors = true;
                emailErr.html('Введите Email в формате mymail@example.ru').show();
            } else {
                emailErr.html('').hide();
                isValid.errors = false;
            }
        },
        'sendIt': function () {
            if (!isValid.errors) {
                prepareUserSave();
            }
        }
    };

    $('#send').click(function (event) {
        event.preventDefault();
        let isEdit = $('#edit').val();
        isValid.errors = false;
        if (isEdit === true) {
            isValid.login();
            isValid.readAnnexes();
        }
        isValid.sendIt();
        return false;
    });

    $('#login').blur(isValid.login);
    $('#email').blur(isValid.email);

    $('a#delete-user').click(function (event) {
        event.preventDefault();
        let userId = $(this).attr('data-user-id');
        showConfirmForm('Удаление пользователя', 'Действительно хотите удалить пользователя?', userId, OperationEnum.DELETE)
    });

    confirmForm.find('#accept').on('click', function () {
        let userId = confirmForm.find('#accept').attr('data-object-id')
        let action = confirmForm.find('#accept').attr('data-action')
        confirmForm.modal('hide')
        switch (action) {
            case OperationEnum.DELETE:
                deleteUser(userId)
                $('#users-table').find('tr#' + userId).remove();
                break
            case OperationEnum.DEACTIVATE:
                deactivate(userId)
                break
        }
    })

});

function onCloseUserFormClick() {
    $('#close-user-form').on('click', function (e) {
        clearUserForm()
    })
}

/**
 * Проверить правильность заполнения формы
 *
 * @param userDTO {UserDTO}
 * @return {boolean}
 */
function checkUserDTO(userDTO) {
    let rus = new RegExp(".*?[А-Яа-я $\/].*?")
    let loginError = $('#loginError')

    if (userDTO.login.length < 4 || userDTO.login.length > 16) {
        loginError.html('Имя пользователя должно быть от 4 до 16 символов')
        loginError.addClass('d-block')
        return false
    } else if (rus.test(userDTO.login)) {
        loginError.html('Имя пользователя может содержать только буквы латинского алфавита, ' +
            'цифры, знак подчёркивания (_) и точку (.)')
        loginError.addClass('d-block')
        return false
    } else {
        loginError.removeClass('d-block')
    }

    let phoneErr = $('#phoneError')
    let phoneValid = new RegExp("\\+[0-9]{11}$")
    if (!phoneValid.test(userDTO.phones[0].number)) {
        phoneErr.html('Введите телефон в формате +79998887766')
        phoneErr.addClass('d-block')
        return false
    } else {
        phoneErr.removeClass('d-block')
    }
    let rolesError = $('#rolesError')
    if (userDTO.role.id === '0') {
        rolesError.addClass('d-block')
        return false
    } else {
        rolesError.removeClass('d-block')
    }
    return true
}

/**
 * Создать DTO пользователя
 *
 * @returns {UserDTO}
 */
function getUserDTO() {
    let login = $('#user-login').val()
    let role = getRole();
    let partner = getPartner();
    let partnerId = null
    if (partner !== null) {
        partnerId = partner.id
    }
    let kin = $('#kins').val();
    let userId = $('#id').val();
    let phone = $('#phone').val()

    let userDTO = new UserDTO()
    userDTO.build(userId, login, role, partnerId, kin, phone)
    userDTO.profile = createProfile(userId, $('#lastName').val(), $('#firstName').val(), $('#patronymic').val(), $('#email').val(),
        $('#master-investor').val(), $('#user_type').val());

    return userDTO
}

/**
 * Подготовить пользователя к сохранению
 *
 */
function prepareUserSave() {
    let userDTO = getUserDTO()
    saveUser(userDTO);
    closeLoader()
}

/**
 * Получить список ролей
 *
 * @returns {RoleDTO}
 */
function getRole() {
    let roleDTO = new RoleDTO();
    let roleOption = $('#roles').find('option:selected');
    roleDTO.build(roleOption.val(), roleOption.text());
    return roleDTO;
}

/**
 * Получить партнёра, если выбран канал продаж
 *
 * @returns {UserDTO}
 */
function getPartner() {
    let saleChanel = $('#saleChanel');
    let partnerId = saleChanel.find(':selected').val()
    if (typeof partnerId === 'undefined' || partnerId === '0') {
        return null
    }
    let partnerDTO = new UserDTO();
    let partnerLogin = saleChanel.find(':selected').text()
    partnerDTO.buildPartner(partnerId, partnerLogin);
    if (partnerDTO.partnerId === 0) {
        partnerDTO.partnerId = null;
    }
    return partnerDTO;
}

/**
 * Создать профиль для DTO пользователя
 *
 * @param userId id пользователя
 * @param lastName Фамилия
 * @param firstName Имя
 * @param patronymic Отчество
 * @param email адрес эл почты
 * @param masterInvestorId id основного инвестора
 * @param userType тип инвестора
 * @returns {UserProfileDTO}
 */
function createProfile(userId, lastName, firstName, patronymic, email, masterInvestorId, userType) {
    let profile = new UserProfileDTO();
    if (lastName.length === 0) {
        lastName = null;
    }
    if (firstName.length === 0) {
        firstName = null;
    }
    if (patronymic.length === 0) {
        patronymic = null;
    }
    if (masterInvestorId.length === 0) {
        masterInvestorId = null;
    }
    if (userType.length === 0) {
        userType = null
    }
    profile.build(userId, lastName, firstName, patronymic, email, masterInvestorId, userType);
    return profile;
}

/**
 * Сохранить пользователя
 *
 * @param user {UserDTO}
 */
function saveUser(user) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    showLoader();

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: OperationEnum.properties[OperationEnum.SAVE].url,
        data: JSON.stringify(user),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            if (data.status === 412) {
                $('#user-form-modal').find('#loginError').html(data.error).addClass('d-block')
            } else {
                $('#user-form-modal').modal('hide')
                showPopup(data.message, false);
                clearUserForm()
            }
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true);
        },
        always: function () {
            closeLoader()
        }
    });
}

/**
 * Удалить пользователя
 *
 * @param userId id пользователя
 */
function deleteUser(userId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    showLoader();

    let userDTO = {
        id: userId
    }

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: OperationEnum.properties[OperationEnum.DELETE].url,
        data: JSON.stringify(userDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            showPopup(data.message, false)
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true)
        }
    });
}

/**
 * Деактивировать пользователя
 *
 * @param userId id пользователя
 */
function deactivate(userId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    showLoader();

    let userDTO = {
        id: userId
    }

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: OperationEnum.properties[OperationEnum.DEACTIVATE].url,
        data: JSON.stringify(userDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            showPopup(data.message, false)
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true)
        }
    });
}

function enableSearchButton(flag) {
    $("#btn-search").prop("disabled", flag);
}

/**
 * Очистить форму пользователей от данных
 */
function clearUserForm() {
    let userModalForm = $('#user-form-modal')
    userModalForm.find('#lastName').val('')
    userModalForm.find('#firstName').val('')
    userModalForm.find('#patronymic').val('')
    userModalForm.find('#user-login').val('')
    userModalForm.find('#email').val('')
    userModalForm.find('#phone').val('')
    userModalForm.find('#saleChanel').prop('selectedIndex', -1)
    userModalForm.find('#saleChanel').selectpicker('refresh')
}

/**
 * Отобразить форму подтверждения
 *
 * @param title {String} заголовок формы
 * @param message {String} сообщение
 * @param objectId {String} идентификатор объекта
 * @param action {OperationEnum} действие
 */
function showConfirmForm(title, message, objectId, action) {
    confirmForm.find('#title').html(title)
    confirmForm.find('#message').html(message)
    confirmForm.find('#accept').attr('data-object-id', objectId)
    confirmForm.find('#accept').attr('data-action', action)
    confirmForm.modal('show')
}

/**
 * Получить пользователя по id
 *
 * @param userId id пользователя
 */
function getUser(userId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    showLoader();
    let userDTO = {
        id: userId
    }

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: "find",
        data: JSON.stringify(userDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            showUpdateUserForm(data)
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true)
        },
        always: function () {
            closeLoader()
        }
    });
}

/**
 * Показать форму для изменения пользователя
 *
 * @param data
 */
function showUpdateUserForm(data) {
    let userDTO = new UserDTO()
    let phone = data.phones.length > 0 ? data.phones[0].number : null
    userDTO.build(data.id, data.login, data.role, data.partnerId, data.kin, phone)
    userDTO.profile = data.profile
    let userForm = $('#user-form-modal')
    userForm.find('#id').val(userDTO.id)
    userForm.find('#edit').val(true)
    userForm.find('#user-login').val(userDTO.login)
    userForm.find('#email').val(userDTO.profile.email)
    bindRoles(userDTO.role)
    bindPartner(userDTO.partnerId)
    bindKin(userDTO.kin)
    bindType(userDTO.profile.type)
    bindPhone(userDTO.phones[0].number)
    userForm.find('#lastName').val(userDTO.profile.lastName)
    userForm.find('#firstName').val(userDTO.profile.firstName)
    userForm.find('#patronymic').val(userDTO.profile.patronymic)
    userForm.find('#action').attr("data-action", OperationEnum.UPDATE)
    userForm.modal('show')
}

/**
 * Преобразовать список ролей пользователя в выделенные элементы выпадающего списка
 *
 * @param role {RoleDTO}
 */
function bindRoles(role) {
    let userForm = $('#user-form-modal');
    $.each(userForm.find('#roles option'), function (ind, el) {
        if (el.value === (role.id + '')) {
            $(el).prop('selected', true)
        }
    })
    userForm.find('#roles').selectpicker('refresh')
}

/**
 * Преобразовать id партнёра в выделенный элемент выпадающего списка
 *
 * @param partnerId id партнёра
 */
function bindPartner(partnerId) {
    let userForm = $('#user-form-modal');
    $.each(userForm.find('#saleChanel option'), function (ind, el) {
        if (el.value === (partnerId + '')) {
            $(el).prop('selected', true)
        }
    })
    userForm.find('#saleChanel').selectpicker('refresh')
}

/**
 * Преобразовать степень родства в выделенный элемент выпадающего списка
 *
 * @param kin
 */
function bindKin(kin) {
    let userForm = $('#user-form-modal');
    $.each(userForm.find('#kins option'), function (ind, el) {
        if (el.value === (kin + '')) {
            $(el).prop('selected', true)
        }
    })
    userForm.find('#kins').selectpicker('refresh')
}

/**
 * Преобразовать тип инвестора в выделенный элемент выпадающего списка
 *
 * @param type
 */
function bindType(type) {
    let userForm = $('#user-form-modal');
    $.each(userForm.find('#user_type option'), function (ind, el) {
        if (el.value === (type + '')) {
            $(el).prop('selected', true)
        }
    })
    userForm.find('#user_type').selectpicker('refresh')
}

/**
 * Преобразовать телефон в выделенный элемент выпадающего списка
 *
 * @param phone
 */
function bindPhone(phone) {
    let userForm = $('#user-form-modal');
    userForm.find('#phone').val(phone)
}

function onPhoneFieldFocusOut() {
    $('#phone').on('focusout', function (e) {
        let phone = '+' + $(this).val().replace(/\D/g,'')
        $('#user-form-modal').find('#phone').val(phone)
    })
}
