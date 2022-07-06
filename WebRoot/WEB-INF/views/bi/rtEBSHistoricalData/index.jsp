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
        .table thead th {
            vertical-align: middle;
            border-right:1px solid #eee;
        }
        .table-condensed td {
            border-right:1px solid #eee;
        }
        .ui-datepicker-calendar, .ui-datepicker-current {
            display: none;
        }
        #field{
            font-size: 5px;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $("#Download").click(function () {
                $("#UploadTip").hide();
                var queryCondition=$("#QueryCondition").serialize();
                queryCondition = decodeURIComponent(queryCondition,true);
                $("#loading").show();
                var data = '';
                $('#field input[name=columnName]').each(function () {
                    //获取当前元素的勾选状态
                    if ($(this).prop("checked")) {
                        data = data + $(this).val() + ",";
                    }
                });
                if (data === "") {
                    layer.msg("請勾選要查询的字段！（Select the fields to be queried）");
                    return;
                } else {
                    data = data.substring(0, data.length - 1);
                }
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtEBSHistoricalData/download",
                    async: true,
                    dataType: "json",
                    data: {queryCondition:queryCondition,columns:data},
                    success: function (data) {
                        $("#loading").hide();
                        if (data.flag == "success") {
                            window.location.href = "${ctx}/static/download/" + data.fileName;
                        } else {
                            layer.alert(data.msg);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        $("#loading").hide();
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            });

            $("#QueryBtn").click(function () {
                var data = '';
                $('#field input[name=columnName]').each(function () {
                    //获取当前元素的勾选状态
                    if ($(this).prop("checked")) {
                        data = data + $(this).val() + ",";
                    }
                });
                if (data === "") {
                    layer.msg("請勾選要查询的字段！（Select the fields to be queried）");
                    return;
                } else {
                    data = data.substring(0, data.length - 1);
                }
                var queryCondition=$("#QueryCondition").serialize();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/rtEBSHistoricalData/list", {
                    queryCondition:decodeURIComponent(queryCondition),
                    columns:data
                }, function () {
                    $("#loading").fadeOut(1000);
                });
            });

            $("#ui-datepicker-div").remove();
            $("#Date,#QDate,#DateEnd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yyyy',
                showButtonPanel: true,
                closeText: "<spring:message code='confirm'/>"
            });

            $("#Date").click(function () {
                periodId = $(this).attr("id");
                $("#ui-datepicker-div .ui-datepicker-month option[value='"+month+"']").attr("selected",true);
                $("#ui-datepicker-div .ui-datepicker-year option[value='"+year+"']").attr("selected",true);
            });
            var month;
            var year;
            var periodId;
            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
                month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
                year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
                $("#"+periodId).val(year+'-'+(parseInt(month)+1));//给input赋值，其中要对月值加1才是实际的月份
                if($("#"+periodId+"Tip").length>0){
                    $("#"+periodId+"Tip").hide();
                }
            });

            $("#selectAll").click(function(){
                if ($("#selectAll").prop("checked") == true) {
                    $(":checkbox[name='columnName']").prop("checked", true);
                } else {
                    $(":checkbox[name='columnName']").prop("checked", false);
                }
            });
        })
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><c:if test="${languageS eq 'zh_CN'}">EBS營收歷史數據表</c:if><c:if test="${languageS eq 'en_US'}">EBS Historical Data</c:if></span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls" style="display:inline-block;vertical-align:top;width:100%;margin-left:-20px;">
                <form id="QueryCondition" style="float:left;margin:0;">
                    <c:forEach items="${queryList}" var="query">
                        <c:if test="${query.key=='YEAR'}">
                            <input id="Date" name='YEAR_MONTH'
                                   class='m-l-md' style='width:150px;'
                                   placeholder="<spring:message code='please_select'/>"
                                   type="text" value="" readonly>
                        </c:if>
                        <c:if test="${query.key!='YEAR'}">
                            <input name='${query.key}' class='m-l-md' style='width:150px;'
                                   placeholder='${query.val}' type='text' value=''>
                        </c:if>
                    </c:forEach>
                    <div style="margin-left:20px;float:right;">
                        <button id="QueryBtn" class="btn search-btn btn-warning m-l-md"
                                type="button"><spring:message code='query'/></button>
                        <button id="Download" class="btn search-btn" type="button">
                            <spring:message code='download'/></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="p-l-md" >
            <table id="field">
                <thead>
                <tr>
                    <td colspan="10">
                        <input type="checkbox" checked  id="selectAll">全選</input>
                    </td>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${columns }" var="column" varStatus="status">
                <c:if test="${status.index%10 eq 0}">
                <tr>
                    </c:if>
                    <td>
                        <input type="checkbox" name="columnName" style="height:10px; !important;" checked value="${column.columnName }">${column.comments }
                    </td>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>
