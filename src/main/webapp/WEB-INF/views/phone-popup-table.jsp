<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="xlink" uri="http://jakarta.apache.org/taglibs/standard/scriptfree" %>

<div class="modal fade" id="phones-popup-table" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-responsive" style="font-weight: 600; color: #11325b;" id="header">
                    ТЕЛЕФОНЫ</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
                    <span aria-hidden="true" class="text-responsive">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                    <table aria-describedby="phones table" id="phones-table" class="table table-striped table-hover">
                        <thead style="text-align: center">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">НОМЕР</th>
                            <th scope="col">ДЕЙСТВИЕ</th>
                        </tr>
                        </thead>
                        <tbody class="text-responsive" style="text-align: center">

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
