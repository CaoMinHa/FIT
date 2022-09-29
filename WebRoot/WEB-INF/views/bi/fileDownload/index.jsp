<%@page import="foxconn.fit.entity.base.EnumGenerateType" %>
<%@page import="foxconn.fit.util.SecurityUtils" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <style type="text/css">
        .search-btn {
            height: 40px;
            margin-left: 10px;
            color: #ffffff;
            background-image: linear-gradient(to bottom, #fbb450, #f89406);
            background-color: #f89406 !important;
        }

        .ui-datepicker-calendar, .ui-datepicker-current {
            display: none;
        }

        .ui-datepicker-close {
            float: none !important;
        }

        .ui-datepicker-buttonpane {
            text-align: center;
        }

        .table thead th {
            vertical-align: middle;
        }

        .table-condensed td{
            padding: 7px 10px;
        }
        .modal-backdrop {
            position: initial!important;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $("#fileDownload a").click(function () {
                window.location.href = "${ctx}/static/file/"+$(this).text();
            });
        })
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span>
                    <c:if test="${languageS eq 'zh_CN'}">文件下載</c:if>
					<c:if test="${languageS eq 'en_US'}">File Download</c:if>
                </span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md">
            <br>
            <c:forEach items="${fileNameList}" var="fileName" varStatus="status">
                <div id="fileDownload">
                        <a href="####">${fileName}<a>
                </div>
                <br>
            </c:forEach>
        </div>
    </div>
</div>

</body>
</html>
