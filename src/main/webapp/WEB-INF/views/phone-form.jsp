<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal" tabindex="-1" role="dialog" id="phone-form">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="title">ДОБАВИТЬ ТЕЛЕФОН</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <label for="phone-number"></label>
                <input class="form-control input-sm" type="tel" id="phone-number" placeholder="+79998887766"
                       required
                       autocomplete="off"/>
            </div>
            <div class="modal-footer" data-action="" id="action">
                <button type="button" class="btn btn-primary" id="confirm-phone-action" data-object-id="" data-action="add">Добавить</button>
                <button type="button" class="btn btn-secondary" id="decline-phone-action">Отменить</button>
            </div>
        </div>
    </div>
</div>
