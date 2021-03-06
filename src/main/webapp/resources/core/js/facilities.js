let Action = {
    CREATE: 'CREATE',
    UPDATE: 'UPDATE',
    FIND: 'FIND',
    DELETE: 'DELETE',
    properties: {
        CREATE: {
            url: 'create'
        },
        UPDATE: {
            url: 'update'
        },
        FIND: {
            url: 'find'
        },
        DELETE: {
            url: 'delete'
        }
    }
}

Object.freeze(Action)

let Facility = function () {}

Facility.prototype = {
    id: 0,
    name: '',
    fullName: '',
    city: '',
    build: function (id, name, fullName, city) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.city = city;
    }
}

let confirmForm;
let facilityForm;

jQuery(document).ready(function ($) {
    confirmForm = $('#confirm-form');
    facilityForm = $('#facility-modal-form');
    onCreateFacilityEvent()
    onUpdateFacilityEvent()
    onDeleteFacilityEvent()
    onAcceptFormEvent()
    $('#create').click(function (event) {
        showLoader();
        let action = $(this).data('action')
        if (action === 'create') {
            event.preventDefault();
            create();
        }
        closeLoader();
    })
});

function create() {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let facility = createFacility(null);

    $.ajax({
        type : "POST",
        contentType : "application/json;charset=utf-8",
        url : "create",
        data : JSON.stringify(facility),
        dataType : 'json',
        timeout : 100000,
        beforeSend: function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data) {
            showPopup(data.message, false);
            if (data.status === 200) {
                clearForm();
            }
        },
        error: function(request, status, error){
            console.log(request.responseText);
            console.log(status);
            console.log(error);
        },
        always: function() {
            enableButton(true);
            closeLoader();
        }
    });
}

function enableButton(flag) {
    $("a#delete").prop("disabled", flag);
}

function createFacility(facilityId) {
    let facility = new Facility();
    let name = $('#f_name').val();
    let fullName = $('#full_name').val();
    let city = $('#city').val();
    facility.build(facilityId, name, fullName, city);
    return facility;
}

function clearForm() {
    $('#f_name').val('');
    $('#full_name').val('');
    $('#city').val('');
}

/**
 * ???????????????? ?????????? ?????? ????????????????/???????????????????????????? ??????????????
 *
 * @param action {Action} ????????????????
 */
function showForm(action) {
    let title = ''
    let button = ''
    switch (action) {
        case Action.CREATE:
            title = '???????????????? ??????????????'
            button = '??????????????'
            break
        case Action.UPDATE:
            title = '???????????????????? ??????????????'
            button = '????????????????'
            break
    }
    facilityForm.find('#title').html(title)
    facilityForm.find('#accept').html(button)
    facilityForm.find('#accept').attr('data-action', action)
    facilityForm.modal('show')
}

/**
 * ???????????????? ???????????? ???? id
 *
 * @param facilityId
 */
function getFacility(facilityId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let facilityDTO = new Facility()
    facilityDTO.build(facilityId, null, null, null)

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: Action.properties[Action.FIND].url,
        data: JSON.stringify(facilityDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            showUpdateFacilityForm(data)
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true);
        }
    });
}

/**
 * ?????????????? ?????? ???????????????? ??????????????
 */
function onCreateFacilityEvent() {
    $(document).on('click', '#create-facility',function (e) {
        e.preventDefault()
        showForm(Action.CREATE)
    })
}

/**
 * ?????????????? ?????? ?????????????????? ??????????????
 */
function onUpdateFacilityEvent() {
    $(document).on('click', '#edit-facility',function (e) {
        e.preventDefault()
        let facilityId = $(this).attr('data-facility-id')
        getFacility(facilityId)
        showForm(Action.UPDATE)
    })
}

/**
 * ?????????????? ?????? ???????????????? ??????????????
 */
function onDeleteFacilityEvent() {
    $(document).on('click', '#delete-facility',function (e) {
        e.preventDefault()
        let facilityId = $(this).attr('data-facility-id')
        showConfirmForm('???????????????? ??????????????', '?????????????????????????? ???????????? ?????????????? ?????????????', facilityId, Action.DELETE)
    })
}

function onAcceptFormEvent() {
    facilityForm.find('#accept').on('click',function (e) {
        e.preventDefault()
        let facilityDTO = getFacilityDTO()
        if (check(facilityDTO)) {
            let action = facilityForm.find('#accept').attr('data-action')
            facilityForm.modal('hide')
            save(facilityDTO, action)
        }
    })
    acceptConfirm()
}

/**
 * ???????????????? ?????????? ?????? ?????????????????? ??????????????
 *
 * @param data
 */
function showUpdateFacilityForm(data) {
    let facilityDTO = new Facility()
    facilityDTO.build(data.id, data.name, data.fullName, data.city)
    facilityForm.find('#facility-id').val(facilityDTO.id)
    facilityForm.find('#edit').val(true)
    facilityForm.find('#name').val(facilityDTO.name)
    facilityForm.find('#full-name').val(facilityDTO.fullName)
    facilityForm.find('#city').val(facilityDTO.city)
    facilityForm.find('#action').attr("data-action", Action.UPDATE)
}

/**
 * ???????????????????? ?????????? ??????????????????????????
 *
 * @param title {String} ?????????????????? ??????????
 * @param message {String} ??????????????????
 * @param objectId {String} ?????????????????????????? ??????????????
 * @param action {Action} ????????????????
 */
function showConfirmForm(title, message, objectId, action) {
    confirmForm.find('#title').html(title)
    confirmForm.find('#message').html(message)
    confirmForm.find('#accept').attr('data-object-id', objectId)
    confirmForm.find('#accept').attr('data-action', action)
    confirmForm.modal('show')
}

/**
 * ?????????????? DTO ?? ??????????
 *
 * @return {Facility}
 */
function getFacilityDTO() {
    let facilityDTO = new Facility()
    let facilityId = facilityForm.find('#facility-id').val()
    let name = facilityForm.find('#name').val()
    let fullName = facilityForm.find('#full-name').val()
    let city = facilityForm.find('#city').val()
    facilityDTO.build(facilityId, name, fullName, city)
    return facilityDTO
}

/**
 * ?????????????????? ???????????????????? ?????????? ??????????
 *
 * @param facilityDTO {Facility} DTO ?????? ????????????????
 * @return {boolean} ?????????????????? ????????????????
 */
function check(facilityDTO) {
    let facilityNameError = facilityForm.find('#facilityNameError');
    if (facilityDTO.name.length === 0) {
        facilityNameError.addClass('d-block')
        return false
    } else {
        facilityNameError.removeClass('d-block')
    }
    let fullNameError = facilityForm.find('#fullNameError')
    if (facilityDTO.fullName.length === 0) {
        fullNameError.addClass('d-block')
        return false
    } else {
        fullNameError.removeClass('d-block')
    }
    return true
}

/**
 * ??????????????/???????????????? ????????????
 *
 * @param facilityDTO {Facility} DTO ??????????????
 * @param action {Action} ????????????????
 */
function save(facilityDTO, action) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    showLoader();

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: Action.properties[action].url,
        data: JSON.stringify(facilityDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            closeLoader();
            showPopup(data.message, false);
            window.setTimeout(function(){
                window.location.href = 'list'
            }, 3000);
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true);
        }
    });
}

/**
 * ?????????????????????????? ?? ??????????
 *
 */
function acceptConfirm() {
    confirmForm.find('#accept').on('click', function () {
        let facilityId = confirmForm.find('#accept').attr('data-object-id')
        confirmForm.modal('hide')
        deleteFacility(facilityId)
        $('#facilities-table').find('tr#' + facilityId).remove();
    })
}

/**
 * ?????????????? ????????????
 *
 * @param facilityId id ??????????????
 */
function deleteFacility(facilityId) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let roleDTO = {
        id: facilityId
    }

    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: Action.properties[Action.DELETE].url,
        data: JSON.stringify(roleDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            showPopup(data.message, false)
        },
        error: function (jqXHR) {
            closeLoader();
            showPopup(jqXHR.responseJSON, true)
        }
    });
}
