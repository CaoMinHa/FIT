<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
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
        }
        .ui-datepicker-buttonpane{display:none;}
        .table thead th{vertical-align: middle;}
        .table-condensed td{padding:5px 3px;}
    </style>
    <script type="text/javascript">
        $(function() {
            $("#ui-datepicker-div").remove();
            $("#Date,#DateEnd").datepicker({
                dateFormat: 'yy-m-dd',
                showButtonPanel: true,
                changeYear:true,
                changeMonth:true
            });


            $("#Query").click(function () {
                debugger
                if($("#Date").val()){
                    if(!$("#DateEnd").val()){
                        layer.alert("請選擇開始日期");
                        return;
                    }else{
                        var date1 = new Date($("#Date").val());
                        var date2 = new Date($("#DateEnd").val());
                        if(date1>date2){
                            layer.alert("開始日期需小於結束日期");
                            return;
                        }
                    }
                }
                if($("#DateEnd").val()){
                    if(!$("#Date").val()){
                        layer.alert("請選擇結束日期");
                        return;
                    }
                }
                $("#loading").show();

                $("#Content").load("${ctx}/bi/poEmailLog/list",
                    {pageSize:15,title:$("#title").val(),name:$("#name").val(),
                        date:$("#Date").val(),dateEnd:$("#DateEnd").val()},
                    function(){$("#loading").fadeOut(1000);});
            }).click();
        });

        $('#backBtn').click(function () {
            $("#name").show();
            $("#title").show();
            $("#DateEnd").show();
            $("#Date").show();
            $("#Query").show();
            $("#Query").click();
        })
        var periodId;
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><spring:message code='poEmailLog'/></span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls" id="task">
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <input id="name" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="請輸入用户名" type="text">
                    </li>
                </ul>
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <input id="title" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="請輸入邮件标题" type="text">
                    </li>
                </ul>
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <input id="Date" name="date" style="z-index: 9999;background-color: #ffffff;
                               width:140px;text-align:center;"
                               placeholder="結束日期"
                               type="text" value="" readonly>
                        <input id="DateEnd" name="dateEnd" style="z-index: 9999;background-color: #ffffff;
                               width:140px;text-align:center;"
                               placeholder="開始日期"
                               type="text" value="" readonly>
                    </li>
                </ul>
                <button id="Query" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
            </div>
            <div style="height:55px;!important;" class="controls" id="audit">
                <button  id="backBtn" class="btn search-btn btn-primary" style="background-image: linear-gradient(to bottom, #aad83e, #aad83e);background-color: #aad83e;" type="submit">返回</button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
        <div id="modal-audit" style="display:none;">
            <form id="taskForm" class="form-horizontal">
                <input id="id" style="display: none;"/>
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <div class="pull-left">
                        <i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
                        名称：<input id="taskName2" style="height: 30px !important;" type="text" datatype="s3-30" readonly/>
                    </div>
                </div>
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <div class="pull-left">
                        <i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
                        審核：<select id="flag" name="flag" style="width:100px" datatype="*" nullmsg="<spring:message code='please_select'/>">
                        <option value="0">通過</option>
                        <option value="1">退回</option>
                    </select>
                    </div>
                </div>
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <div class="pull-left" style="margin-left: 40px">
                        备注：<input name="remark" style="height: 30px !important;" type="text" datatype="s3-30" nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
