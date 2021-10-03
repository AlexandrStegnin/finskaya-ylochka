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
            console.log(takeMoneyDTO)
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
    if (takeMoneyDTO.investorId.length === 0) {
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
    let sumError = takeMoneyForm.find('#sumError');
    if (takeMoneyDTO.sum.length === 0) {
        sumError.addClass('d-block')
        return false
    } else {
        sumError.removeClass('d-block')
    }
    return true
}
