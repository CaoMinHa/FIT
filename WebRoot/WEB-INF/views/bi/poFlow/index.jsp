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
            $("#QueryBtn").click(function(){
                $("#QDateTip").hide();
                var date=$("#QDate").val();
                if(date.length!=4){
                   layer.alert("請輸入正確年份！(please enter a proper year)");
                   return;
                }
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/poFlow/list",{date:date,tableName:"FIT_PO_Target_CPO_CD_DTL"},function(){$("#loading").fadeOut(1000); $("#modalBtn").show();});
            });
            $("#toTaskBtn").click(function () {
                // if($("#countNotUploadNumber").val()==="0"){
                    var year=$("#QDate").val();
                    var total=$("#total").val();
                    var obj={
                        year:year
                    }
                    console.log(total);
                    if(total==undefined||total=="0"){
                        layer.alert("请先查询cpo表格数据")
                    }else{
                        $.ajax({
                            type:"POST",
                            url:"${ctx}/bi/poTask/addCpo",
                            async:false,
                            dataType:"json",
                            data:obj,
                            success: function(data){
                                layer.alert(data.msg);
                                refresh();
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                layer.alert("<spring:message code='connect_fail'/>");
                            }
                        });
                    }
                // }else{
                //     layer.alert("\"SBU年度CD目標匯總表\"必須全部審核通過才能創建該任務！")
                //     return;
                // }
            })
        });
        var periodId;
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><spring:message code='poFlow'/></span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls">
                <ul style="float:left;">
                        <input id="QDate" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="請輸入年份" type="text">
                </ul>
                <button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
                <button id="toTaskBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit">新建任务</button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>


</body>
</html>
