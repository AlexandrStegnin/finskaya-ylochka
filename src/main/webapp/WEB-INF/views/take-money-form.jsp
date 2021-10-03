<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal" tabindex="-1" role="dialog" id="take-money-form-modal">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="title">Вывод денег</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="container">
                    <form:form method="POST" modelAttribute="takeMoneyDTO" class="form-horizontal"
                               style="margin: 10px 0 10px 0" id="take-money-form">

                        <div class="form-group row" id="investorRow">
                            <label class="col-sm-3 offset-sm-2 col-form-label-sm" for="investor">Инвестор:</label>
                            <div class="col-sm-5">
                                <form:select path="investorId" id="investor" items="${investors}" multiple="false"
                                             itemValue="id" itemLabel="login"
                                             class="form-control form-control-sm selectpicker"
                                             data-size="10" data-live-search="true" data-none-selected-text="Выберите инвестора"/>
                                <div class="has-error d-none" id="investorError">
                                    Необходимо выбрать инвестора
                                </div>
                            </div>
                        </div>

                        <div class="form-group row" id="dateCashingRow">
                            <label class="col-sm-3 offset-sm-2 col-form-label-sm" for="date">Дата вывода:</label>
                            <div class="col-sm-5">
                                <form:input type="date" path="date" id="date"
                                            class="form-control form-control-sm"/>
                                <div class="has-error d-none" id="dateError">
                                    Необходимо выбрать дату вывода
                                </div>
                            </div>
                        </div>

                        <div class="form-group row" id="commissionRow">
                            <label class="col-sm-3 offset-sm-2 col-form-label-sm" for="commission">Комиссия (%):</label>
                            <div class="col-sm-5">
                                <form:input type="number" path="commission" id="commission" class="form-control form-control-sm"
                                            min="0.00" max="100" step="any"/>
                                <div class="has-error d-none" id="commissionError">
                                    Необходимо указать комиссию в (%)
                                </div>
                            </div>
                        </div>

                        <div class="form-group row" id="commissionNoMoreRow">
                            <label class="col-sm-3 offset-sm-2 col-form-label-sm" for="commissionNoMore">Но не более:</label>
                            <div class="col-sm-5">
                                <form:input type="number" path="commissionNoMore" id="commissionNoMore"
                                            class="form-control form-control-sm"
                                            min="0.00" step="any"/>
                            </div>
                        </div>

                        <div class="form-group row" id="cashRow">
                            <label class="col-sm-3 offset-sm-2 col-form-label-sm" for="sum">Сумма:</label>
                            <div class="col-sm-5">
                                <form:input type="number" path="sum" id="sum" class="form-control form-control-sm"
                                            min="0.0" step="any"/>
                                <div id="sumError" class="has-error d-none">
                                </div>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
            <div class="modal-footer" data-action="" id="action">
                <button type="button" class="btn btn-danger d-none" id="take-all-money">Вывести всё</button>
                <button type="button" class="btn btn-primary" id="take-money-button">Вывести</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
            </div>
        </div>
    </div>
</div>
