<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Список объектов</title>
    <sec:csrfMetaTags/>
    <link rel="stylesheet"
          href="<c:url value='https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css' />"/>
    <link rel="stylesheet"
          href="<c:url value='https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/css/bootstrap-select.min.css' />">
    <link href="<c:url value='https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.6.1/css/bootstrap4-toggle.min.css' />"
          rel="stylesheet">
    <link rel="shortcut icon" href="<c:url value='/resources/core/img/favicon/favicon.ico?v=eEY755nn99' />">
    <link href="<c:url value='/resources/core/css/ddk_loader.css' />" rel="stylesheet"/>
    <style type="text/css">
        .has-error {
            color: red;
            padding: 8px 0 0 8px;
            font-size: 12px;
        }
    </style>
</head>

<body>
<%@include file="header.jsp" %>
<div class="container-fluid">
    <div class="d-flex flex-row justify-content-between" style="margin: 10px;">
        <sec:authorize access="isFullyAuthenticated()">
            <sec:authorize access="hasRole('ADMIN')">
                <div style="padding: 5px; margin-left: auto">
                    <a href="<c:url value='/#' />" id="create-facility"
                       class="btn btn-success btn-md pull-right">Создать</a>
                </div>
            </sec:authorize>
        </sec:authorize>
    </div>
</div>
<div class="container-fluid">
    <table class="table table-striped w-auto table-hover table-sm" style="table-layout: fixed"
           id="facilities-table">
        <thead style="text-align: center">
        <tr>
            <th>ID</th>
            <th>Название</th>
            <th>Название (1C)</th>
            <th>Город</th>
            <sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
                <th>Действие</th>
            </sec:authorize>
        </tr>
        </thead>
        <tbody style="text-align: center">
        <c:forEach items="${facilities}" var="facility">
            <tr id="${facility.id}">
                <td>${facility.id}</td>
                <td>${facility.name}</td>
                <td>${facility.fullName}</td>
                <td>${facility.city}</td>
                <sec:authorize access="isFullyAuthenticated()">
                    <sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
                        <td style="text-align: center">
                            <div class="dropdown pull-right" style="margin-right: 10px">
                                <button type="button" data-toggle="dropdown"
                                        class="btn btn-success btn-sm dropdown-toggle pull-right"><span
                                        class="fas fa-cog"></span></button>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" id="edit-facility" data-facility-id="${facility.id}"
                                       href="<c:url value='#' />">Изменить</a>
                                    <a class="dropdown-item" id="delete-facility" href="<c:url value='#' />"
                                       data-facility-id="${facility.id}" style="color: red">Удалить</a>
                                </div>
                            </div>
                        </td>
                    </sec:authorize>
                </sec:authorize>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@include file="popup_modal.jsp" %>
<%@include file="ddk_loader.jsp" %>
<%@include file="confirm-form.jsp" %>
<%@include file="facility-form.jsp" %>

<script type="text/javascript"
        src="<c:url value='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js' />"></script>
<script type="text/javascript"
        src="<c:url value='https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js' />"></script>
<script type="text/javascript"
        src="<c:url value='https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js' />"></script>
<script src="<c:url value='https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/js/bootstrap-select.min.js' />"></script>
<script src="<c:url value='https://kit.fontawesome.com/2b84e2f58d.js' />" crossorigin="anonymous"></script>
<script src="<c:url value='https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.6.1/js/bootstrap4-toggle.min.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/core/js/progress.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/core/js/ddk_loader.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/core/js/jsFunctions.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/core/js/scripts.js' />"></script>
<script type="text/javascript" src="<c:url value='/resources/core/js/facilities.js' />"></script>
</body>
</html>
