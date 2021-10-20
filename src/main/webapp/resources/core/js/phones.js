let PhoneDTO = function () {
    // initialization
}

PhoneDTO.prototype = {
    id: null,
    number: null,
    appUserId: null,
    build: function (id, number, appUserId) {
        this.id = id;
        this.number = number;
        this.appUserId = appUserId;
    }
}

let phonesPopup
let confirmDeletePhone
let phoneForm

jQuery(document).ready(function ($) {
    phonesPopup = $('#phones-popup-table')
    confirmDeletePhone = $('#confirm-delete-phone-form')
    phoneForm = $('#phone-form')
    onShowPhonesClick()
    onDeletePhoneClick()
    onDeclineConfirm()
    onAcceptConfirm()
    onAddPhoneClick()
    onDeclineAddPhone()
    onAcceptPhoneFormClick()
    onEditPhoneClick()
})

function onShowPhonesClick() {
    $('.show-phones').on('click', function (e) {
        e.preventDefault()
        let userId = $(this).data('user-id')
        getUserPhones(userId)
        closeLoader()
    })
}

function getUserPhones(userId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    showLoader();

    let user = {
        id: userId
    }

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: 'phones',
        data: JSON.stringify(user),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            buildPhonesTable(data)
        },
        error: function (e) {
            closeLoader();
        },
        always: function () {
            closeLoader()
        }
    });
}

/**
 * @param phones {PhoneDTO[]}
 */
function buildPhonesTable(phones) {
    let phonesTable = $('#phones-table')
    let tableBody = phonesTable.find('tbody');
    tableBody.empty();
    $.each(phones, function (ind, phone) {
        let row = createRow(phone);
        tableBody.append(row);
    })
    phonesPopup.modal('show');
}

/**
 * @param phoneDTO {PhoneDTO} DTO телефона
 */
function createRow(phoneDTO) {
    return $('<tr>').append(
        $('<td>').text(phoneDTO.id),
        $('<td>').text(phoneDTO.number),
        $('<td>').append(createEditButton(phoneDTO.id, phoneDTO.number)).append(createDeleteButton(phoneDTO.id))
    );
}

function createDeleteButton(phoneId) {
    return $('' +
        '<button style="margin-left: 5px" type="button" class="btn btn-sm btn-danger delete-phone" data-toggle="tooltip" ' +
        'data-placement="center" title="Удалить телефон" data-id="' + phoneId +'">' +
        '<em class="fas fa-trash"></em>' +
        '</button>')
}

function createEditButton(phoneId, phoneNumber) {
    return $('' +
        '<button type="button" class="btn btn-sm btn-warning edit-phone" data-toggle="tooltip" ' +
        'data-placement="center" title="Редактировать телефон" data-id="' + phoneId +'" ' +
        'data-phone-number="' + phoneNumber + '">' +
        '<em class="fas fa-pencil-alt"></em>' +
        '</button>')
}

function onDeletePhoneClick() {
    $(document).on('click', '.delete-phone',function (e) {
        e.preventDefault()
        let phoneId = $(this).data('id')
        phonesPopup.modal('hide')
        showConfirmForm(phoneId)
    })
}

function showConfirmForm(phoneId) {
    confirmDeletePhone.find('#title').text('УДАЛЕНИЕ')
    confirmDeletePhone.find('#message').text('ВЫ ХОТИТЕ УДАЛИТЬ ТЕЛЕФОН?')
    confirmDeletePhone.find('#accept').attr('data-object-id', phoneId)
    confirmDeletePhone.modal('show')
}

function onDeclineConfirm() {
    $('#decline').on('click', function (e) {
        e.preventDefault()
        confirmDeletePhone.modal('hide')
        phonesPopup.modal('show')
    })
}

function onAcceptConfirm() {
    $(document).on('click','#accept', function (e) {
        e.preventDefault()
        let phoneId = $(this).data('object-id')
        deletePhone(phoneId)
        closeLoader()
    })
}

function deletePhone(phoneId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    confirmDeletePhone.modal('hide')
    showLoader();

    let phoneDTO = {
        id: phoneId
    }

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: 'phones/delete',
        data: JSON.stringify(phoneDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            showPopup(data.message, false)
        },
        error: function (e) {
            closeLoader();
            showPopup(e.responseJSON, true)
        },
        always: function () {
            closeLoader()
        }
    });
}

function onAddPhoneClick() {
    $(document).on('click', '.add-phone', function (e) {
        e.preventDefault()
        let userId = $(this).data('user-id')
        showAddPhoneForm('ДОБАВИТЬ ТЕЛЕФОН', 'Добавить', userId)
    })
}

function onDeclineAddPhone() {
    $('#decline-phone-action').on('click', function (e) {
        e.preventDefault()
        let action = phoneForm.find('#confirm-phone-action').data('action')
        if (action === 'edit') {
            phonesPopup.modal('show')
        }
        phoneForm.modal('hide')
    })
}

function onAcceptPhoneFormClick() {
    $('#confirm-phone-action').on('click', function (e) {
        e.preventDefault()
        let action = $(this).data('action')
        let number = phoneForm.find('#phone-number').val()
        if (checkPhone(number)) {
            let userId = $(this).data('object-id')
            savePhone(number, userId, action)
        }
        closeLoader()
    })
}

function checkPhone(number) {
    let regexp = new RegExp('\\+[7][0-9]{10}')
    if (!regexp.test(number)) {
        showPopup('УКАЖИТЕ ТЕЛЕФОН В ВЕРНОМ ФОРМАТЕ', true)
        return false
    }
    return true
}

function savePhone(number, objectId, action) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    if (action === 'add') {
        phoneForm.find('#phone-number').val('')
    }
    phoneForm.modal('hide')
    phonesPopup.modal('hide')
    showLoader();

    let phoneDTO = getPhoneDTO(number, objectId, action)

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: 'phones/' + action,
        data: JSON.stringify(phoneDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            showPopup(data.message, false)
        },
        error: function (e) {
            closeLoader();
            showPopup(e.responseJSON, true)
        },
        always: function () {
            closeLoader()
        }
    });
}

function onEditPhoneClick() {
    $(document).on('click', '.edit-phone', function (e) {
        e.preventDefault()
        let phoneId = $(this).data('id')
        let phoneNumber = $(this).data('phone-number')
        phonesPopup.modal('hide')
        showEditPhoneForm('ИЗМЕНИТЬ ТЕЛЕФОН', 'Изменить', phoneId, phoneNumber)
    })
}

function showPhoneForm(title, buttonText, dataId, phoneNumber, action) {
    phoneForm.find('#confirm-phone-action').attr('data-object-id', dataId)
    phoneForm.find('#title').text(title)
    phoneForm.find('#confirm-phone-action').text(buttonText)
    phoneForm.find('#phone-number').val(phoneNumber)
    phoneForm.find('#confirm-phone-action').attr('data-action', action)
    phoneForm.modal('show')
}

function showEditPhoneForm(title, buttonText, phoneId, phoneNumber) {
    showPhoneForm(title, buttonText, phoneId, phoneNumber, 'edit')
}

function showAddPhoneForm(title, buttonText, userId) {
    showPhoneForm(title, buttonText, userId, '', 'add')
}

function getPhoneDTO(number, objectId, action) {
    if (action === 'add') {
        return {
            number: number,
            appUserId: objectId
        }
    } else {
        return {
            number: number,
            id: objectId
        }
    }
}
