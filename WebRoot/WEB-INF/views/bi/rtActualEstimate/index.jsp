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
    </style>
    <script type="text/javascript">
        $(function () {
            $("#Download").click(function () {
                $("#UploadTip").hide();
                var queryCondition=$("#QueryCondition").serialize();
                queryCondition = decodeURIComponent(queryCondition,true);
                console.log(queryCondition+"查询条件");
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtActualEstimate/download",
                    async: true,
                    dataType: "json",
                    data: {queryCondition:queryCondition,saleSorg:$("#SALESORG").val()},
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
                if(!$("#DateEnd").val()&&!$("#Date").val()){
                    layer.msg("請選擇時間區間！(Please select a time period！)");
                    return;
                }
                var queryCondition=$("#QueryCondition").serialize();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/rtActualEstimate/list", {
                    queryCondition:decodeURIComponent(queryCondition)
                    ,saleSorg:$("#SALESORG").val()
                }, function () {
                    $("#loading").fadeOut(1000);
                });
            });

            $("#ui-datepicker-div").remove();
            $("#Date,#DateEnd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yyyy',
                showButtonPanel: true,
                closeText: "<spring:message code='confirm'/>"
            });

            $("#Date").click(function () {
                periodId = $(this).attr("id");
                var val=$(this).val();
                var month=parseInt(val.substr(val.lastIndexOf("_")))-1;
                $("#ui-datepicker-div .ui-datepicker-month option[value='"+month+"']").attr("selected",true);
                $("#ui-datepicker-div .ui-datepicker-year option[value='"+val.substring(0,4)+"']").attr("selected",true);
            });
            $("#DateEnd").click(function () {
                periodId = $(this).attr("id");
                var val=$(this).val();
                var month=parseInt(val.substr(val.lastIndexOf("_")))-1;
                $("#ui-datepicker-div .ui-datepicker-month option[value='"+month+"']").attr("selected",true);
                $("#ui-datepicker-div .ui-datepicker-year option[value='"+val.substring(0,4)+"']").attr("selected",true);
            });
            var periodId;
            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
                var month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
                var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
                $("#"+periodId).val(year+'-'+(parseInt(month)+1));//给input赋值，其中要对月值加1才是实际的月份
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
                    <c:if test="${languageS eq 'zh_CN'}">實際+預估報表</c:if>
                    <c:if test="${languageS eq 'en_US'}">Actual Estimated Report</c:if>
                </span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls" style="display:inline-block;vertical-align:top;width:100%;margin-left:-20px;">
                <form id="QueryCondition" style="float:left;margin:0;">
                    <c:forEach items="${queryList}" var="query">
                        <c:if test="${query.key=='YEAR_MONTH'}">
                            <input id="Date" name='${query.key}'
                                   class='m-l-md' style='width:150px;'
                                   placeholder="<spring:message code='start_time'/>"
                                   type="text" value="" readonly>
                            <input id="DateEnd" name='YEAR_MONTHEND'
                                   class='m-l-md' style='width:150px;'
                                   placeholder="<spring:message code='end_time'/>"
                                   type="text" value="" readonly>

                        </c:if>
                        <c:if test="${query.key=='SALES_ORG'}">
                            <input id='SALESORG' class='m-l-md' style='width:150px;'
                                   placeholder='${query.val}' type='text' value=''>
                        </c:if>
                        <c:if test="${query.key!='YEAR_MONTH'}">
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
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>
