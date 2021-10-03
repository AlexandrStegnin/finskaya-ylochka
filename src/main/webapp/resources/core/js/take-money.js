let takeMoneyForm;

jQuery(document).ready(function ($) {
    takeMoneyForm = $('#take-money-form-modal')
    onTakeMoneyButtonClick()
    onTakeMoneyFormSubmit()
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
            showPopup(data.message);
            $('#btn-search').click()
        })
        .fail(function (jqXHR) {
            $('#content').addClass('bg-warning')
            showPopup(jqXHR.responseText);
        })
        .always(function () {
            closeLoader()
        });
}
