let takeMoneyForm;

jQuery(document).ready(function ($) {
    takeMoneyForm = $('#take-money-form-modal')
    subscribeFormChange()
    onTakeMoneyButtonClick()
    onTakeMoneyFormSubmit()
    onTakeAllMoneyButtonClick()
})

function onTakeMoneyButtonClick() {
    $('#take-money').on('click', function (event) {
        event.preventDefault()
        takeMoneyForm.modal('show')
    })
}

function onTakeMoneyFormSubmit() {
    $('#take-money-button').on('click', function (event) {
        event.preventDefault()
        let takeMoneyDTO = buildTakeMoneyDTO()
        if (checkTakeMoneyDTO(takeMoneyDTO)) {
            takeMoney(takeMoneyDTO)
        }
    })
}

function buildTakeMoneyDTO() {
    return {
        investorId: $('#investor').val(),
        sum: $('#sum').val(),
        date: $('#date').val(),
        commission: $('#commission').val(),
        commissionNoMore: $('#commissionNoMore').val()
    }
}

function checkTakeMoneyDTO(takeMoneyDTO) {
    let investorError = takeMoneyForm.find('#investorError');
    if (takeMoneyDTO.investorId.length === 0 || takeMoneyDTO.investorId === '0') {
        investorError.addClass('d-block')
        return false
    } else {
        investorError.removeClass('d-block')
    }
    let dateError = takeMoneyForm.find('#dateError');
    if (takeMoneyDTO.date.length === 0) {
        dateError.addClass('d-block')
        return false
    } else {
        dateError.removeClass('d-block')
    }
    let commissionError = takeMoneyForm.find('#commissionError');
    if (takeMoneyDTO.commission.length === 0) {
        commissionError.addClass('d-block')
        return false
    } else {
        commissionError.removeClass('d-block')
    }
    if (takeMoneyDTO.commission >= 100) {
        commissionError.addClass('d-block')
        commissionError.text('Комиссия не может быть больше или равна 100%')
        return false
    } else {
        commissionError.removeClass('d-block')
    }
    let sumError = takeMoneyForm.find('#sumError');
    if (takeMoneyDTO.sum.length === 0 || takeMoneyDTO.sum === '0') {
        sumError.addClass('d-block')
        sumError.text('Необходимо указать сумму')
        return false
    } else {
        sumError.removeClass('d-block')
    }
    return true
}

function takeMoney(takeMoneyDTO) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    showLoader()
    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: "/take-money",
        data: JSON.stringify(takeMoneyDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        }
    })
        .done(function (data) {
            takeMoneyForm.modal('hide')
            showPopup(data.message, false);
            $('#btn-search').click()
        })
        .fail(function (jqXHR) {
            takeMoneyForm.modal('hide')
            showPopup(jqXHR.responseJSON, true);
        })
        .always(function () {
            closeLoader()
        });
}

function onTakeAllMoneyButtonClick() {
    $('#take-all-money').on('click', function (event) {
        event.preventDefault()
        let takeMoneyDTO = buildTakeMoneyDTO()
        if (checkTakeMoneyDTO(takeMoneyDTO)) {
            takeAllMoney(takeMoneyDTO)
        }
    })
}

function subscribeFormChange() {
    let validate = {
        'cashingAll': function () {
            validate.errors = false
            let investorsIds = [];
            $.map(takeMoneyForm.find('#investor').find('option:selected'), function (el) {
                investorsIds.push(el.value)
            })
            let cash = takeMoneyForm.find('#sum').val();
            let dateCashing = takeMoneyForm.find('#date').val();
            let commission = takeMoneyForm.find('#commission').val();
            if (investorsIds.length === 0 || cash.length === 0 || dateCashing.length === 0 || commission.length === 0) {
                validate.errors = true;
                takeMoneyForm.find('#take-all-money').addClass('d-none');
            } else {
                takeMoneyForm.find('#take-all-money').removeClass('d-none');
            }
        }
    };
    takeMoneyForm.find('#investor').on('change', function () {
        validate.cashingAll()
    })
    takeMoneyForm.find('#sum').on('input', function () {
        validate.cashingAll()
    })
    takeMoneyForm.find('#date').on('input', function () {
        validate.cashingAll()
    })
    takeMoneyForm.find('#commission').on('input', function () {
        validate.cashingAll()
    })
}

function takeAllMoney(takeMoneyDTO) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    showLoader()
    $.ajax({
        type: "POST",
        contentType: "application/json;charset=utf-8",
        url: "/take-all-money",
        data: JSON.stringify(takeMoneyDTO),
        dataType: 'json',
        timeout: 100000,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        }
    })
        .done(function (data) {
            takeMoneyForm.modal('hide')
            showPopup(data.message, false);
            $('#btn-search').click()
        })
        .fail(function (jqXHR) {
            takeMoneyForm.modal('hide')
            showPopup(jqXHR.responseJSON, true);
        })
        .always(function () {
            closeLoader()
        });
}
