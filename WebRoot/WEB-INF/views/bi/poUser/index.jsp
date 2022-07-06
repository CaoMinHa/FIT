<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
    String entity=SecurityUtils.getEntity();
    request.setAttribute("entity", entity);


%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <style type="text/css">
        .search-btn{
            height:40px;
            margin-left:10px;
            color:#ffffff;
            background-image: linear-gradient(to bottom, #fbb450, #f89406);
            background-color: #f89406 !important;
        }
        .ui-datepicker select.ui-datepicker-month{
            display: none;
        }
        .ui-datepicker-calendar,.ui-datepicker-current{
            display:none;
        }
        .ui-datepicker-close{float:none !important;}
        .ui-datepicker-buttonpane{text-align: center;}
        .table thead th{vertical-align: middle;}
        .table-condensed td{padding:7px 10px;}
    </style>
    <script type="text/javascript">
        $(function() {
            $("#Query").click(function(){
                var name=$("#name").val();
                var username=$("#username").val();
                $("#loading").show();
                $("#Content").load("${ctx}/bi/poUser/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
                    orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),name:name,username:username},function(){$("#loading").fadeOut(1000);});
            }).click();
        });

        var periodId;
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span>用戶模塊</span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls">
                <ul style="float:left;">
                    <li>
                        <input id="name" style="float:left;width:140px;text-align:center;margin-bottom:0;margin-right:15px;" placeholder="請輸入查詢賬號" type="text">
                        <input id="username" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="請輸入查詢姓名" type="text">
                    </li>
                    <li style="float:left; height:70px;">
                        <span id="QTableNameTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <button id="Query" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>

    </div>
</div>
</body>
</html>
