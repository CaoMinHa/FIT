<%@page import="foxconn.fit.entity.base.EnumGenerateType" %>
<%@page import="foxconn.fit.util.SecurityUtils" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglibs.jsp" %>
<%
    String entity = SecurityUtils.getEntity();
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

        .table-condensed td {
            padding: 1px 5px !important;
        }
        .modal-backdrop {
            position: initial!important;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $("#ui-datepicker-div").remove();
            $("#Date,#QDate,#DateEnd,#QDateEnd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yy-MM',
                showButtonPanel: true,
                closeText: "<spring:message code='confirm'/>"
            });
            $("#QDate,#DateEnd,#QDateEnd").click(function () {
                periodId = $(this).attr("id");
                $(this).val("");
            });

            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function () {
                var month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
                var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
                $("#" + periodId).val(year + '-' + (parseInt(month) + 1));//给input赋值，其中要对月值加1才是实际的月份
                if ($("#" + periodId + "Tip").length > 0) {
                    $("#" + periodId + "Tip").hide();
                }
            });

            $("input[type='radio']").change(function () {
                if($(this).val()=='FIT_PO_CD_MONTH_DTL'){
                    $("#cdFormula").show();
                }else{
                    $("#cdFormula").hide();
                }
            });

            $("#QTableName").change(function () {
                $("#Content table tr").remove();
                $("#Fenye").remove();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegrationList/downloadCheck",
                    async: true,
                    dataType: "json",
                    data: {
                        tableName: $("#QTableName").val()
                    },
                    success: function (data) {
                        if (data.flag == "success") {
                            $("#Download").show();
                        } else {
                            $("#Download").hide();
                        }
                    }
                });
                if ($(this).val().length > 0) {
                    $("#" + $(this).attr("id") + "Tip").hide();
                }
                $("#Query input").val("");
                $("#Query select").val("");
                $("#NTD").hide();
                $("#QpoCenter").show();
                $("#Scenario").text("");
                $("#buVal").show();
                $("#priceControl").show();
                $("#founderVal").hide();
                switch ($("#QTableName").val()) {
                    //實際採購非價格CD匯總表
                    case "FIT_ACTUAL_PO_NPRICECD_DTL":
                        $("input[name='YYYY']").hide();
                        $("input[name='YYYYMM']").show();
                        $("#priceControl").hide();
                        $("#founderVal").show();
                        $("#buVal").hide();
                        break;
                    //採購CD手動匯總表
                    case "FIT_PO_BUDGET_CD_DTL":
                        $("input[name='YYYY']").hide();
                        $("input[name='YYYYMM']").show();
                        $("#Scenario").text("Scenario:Actual")
                        break;
                    //SBU年度CD目標匯總表
                    case "FIT_PO_SBU_YEAR_CD_SUM":
                        $("input[name='YYYY']").show();
                        $("input[name='YYYYMM']").hide();
                        $("#Scenario").text("Scenario:Budget");
                        break;
                    //採購CD目標by月展開表
                    case "FIT_PO_CD_MONTH_DTL":
                        $("input[name='YYYY']").show();
                        $("input[name='YYYYMM']").hide();
                        $("#QpoCenter").hide();
                        $("#QpoCenter").change();
                        $("#NTD").show();
                        break;
                }
            });

            $("#QueryBtn").click(function () {
                var tableName = $("#QTableName").val();
                if (tableName.length == 0) {
                    $("#QTableNameTip").show();
                    return;
                }
                var DateYear = $("#DateYear").val();
                var date = $("#QDate").val();
                var dateEnd = $("#QDateEnd").val();
                if(tableName=='FIT_PO_SBU_YEAR_CD_SUM'||tableName=='FIT_PO_CD_MONTH_DTL'){
                    var r = /^\+?[1-9][0-9]*$/;
                    if (DateYear.length!=4 || !r.test(DateYear)) {
                      layer.alert("請填寫正確的年份(Please fill in the correct year)");
                        return;
                    }
                }else{
                    if (date.length == 0) {
                        layer.alert("請選擇開始日期！(Please select a start date)");
                        return;
                    }
                    if (dateEnd.length == 0) {
                        layer.alert("請選擇結束日期！(Please select an end date)");
                        return;
                    }
                    if(date.substr(0,3)!=dateEnd.substr(0,3)){
                        layer.alert("請選擇同一年日期作爲查詢條件！(Please select the date of the same year)");
                        return;
                    }
                }
                var entity = $("#QpoCenter").val();
                var sbuVal = $("#sbuVal").val();
                $("#QTableNameTip").hide();
                $("#QpoCenterTip").hide();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/poIntegrationList/list", {
                    date: date,
                    dateEnd: dateEnd,
                    DateYear: DateYear,
                    tableName: tableName,
                    poCenter: entity,
                    sbuVal: sbuVal,
                    priceControl: $("#priceControl").val(),
                    commodity: $("#commodity").val(),
                    buVal: $("#buVal").val(),
                    founderVal: $("#founderVal").val(),
                    flag:$("#flag").val()
                }, function () {
                    $("#loading").fadeOut(1000);
                });
            });

            $('#deleteBtn').click(function () {
                var ids = $('input[type=checkbox]');
                var data = '';
                ids.each(function () {
                    //获取当前元素的勾选状态
                    if ($(this).prop("checked")) {
                        data = data + $(this).val() + ",";
                    }
                });
                if(data===""){
                    layer.alert("請勾選要刪除的數據！(Select the data to delete)");
                }else{
                    data = data.substring(0, data.length - 1);
                    console.log(data)
                    var tableName = $("#QTableName").val();
                    var obj={
                        id:data,
                        tableName: tableName
                    }
                    $.ajax({
                        type:"POST",
                        url:"${ctx}/bi/poIntegrationList/delete",
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
                //去最后的点
            })

            //selectCommdity
            $("#QpoCenter").change(function (e) {
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegrationList/selectCommdity",
                    async: false,
                    dataType: "json",
                    data: {
                        functionName: $(this).val()
                    },
                    success: function (data) {
                        $("#commdityTable").empty();
                        var commdityTr=0;
                        jQuery.each(data, function (key, values) {
                            $("#commdityTable").append("<tr style='border-top: 1px solid #dadada;height: 30px;font-weight:bold;'><td colspan='4'><input type='checkbox' onchange='checkedChild(this)' value='"+key+"'>"+key+"</td></tr>");
                            jQuery.each(values, function (i, item) {
                                if (i % 4 == 0) {
                                    commdityTr++;
                                    $("#commdityTable").append("<tr id='commdityTr"+commdityTr+"'></tr>");
                                }
                                $("#commdityTr"+commdityTr).append("<td height='25px' width='140px'> <input type='checkbox' class='userGroupVal "+key+"' value='" + item + "'>" + item + "</td>");
                            })
                        })
                    },
                    error: function () {
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            })
        });

        var periodId;

        $("#Download").click(function () {
            if ($("#QTableName").val().length == 0) {
                $("#QTableNameTip").show();
                return;
            }
            var tableName = $("#QTableName").val();
            var date = $("#QDate").val();
            var dateEnd = $("#QDateEnd").val();
            var DateYear = $("#DateYear").val();
            if(tableName=='FIT_PO_SBU_YEAR_CD_SUM'||tableName=='FIT_PO_CD_MONTH_DTL'){
                var r = /^\+?[1-9][0-9]*$/;
                if (DateYear.length!=4 || !r.test(DateYear)) {
                    layer.alert("請填寫正確的年份(Please fill in the correct year)");
                    return;
                }
            }else{
                if (date.length == 0) {
                    layer.alert("請選擇開始日期！(Please select a start date)");
                    return;
                }
                if (dateEnd.length == 0) {
                    layer.alert("請選擇結束日期！(Please select an end date)");
                    return;
                }
                if(date.substr(0,3)!=dateEnd.substr(0,3)){
                    layer.alert("請選擇同一年日期作爲查詢條件！(Please select the date of the same year)");
                    return;
                }
            }
            var entity = $("#QpoCenter").val();
            var sbuVal = $("#sbuVal").val();
            $("#loading").show();
            $.ajax({
                type: "POST",
                url: "${ctx}/bi/poIntegration/download",
                async: true,
                dataType: "json",
                data: {date: date,
                    dateEnd: dateEnd,
                    DateYear: DateYear,
                    tableNames: tableName,
                    poCenter: entity,
                    sbuVal: sbuVal,
                    priceControl:$("#priceControl").val(),
                    commodity:$("#commodity").val(),
                    buVal: $("#buVal").val(),
                    founderVal: $("#founderVal").val(),
                    flag:$("#flag").val()
                },
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

        $(document).ready(function(){
            if("${detailsTsak}"=="ok"){
                    $("#QTableName").val("FIT_PO_SBU_YEAR_CD_SUM");
                    $("#QTableName").change();
                    $("#DateYear").val("${DateYear}");
                    $("#QueryBtn").click();
            }
        })

        function affirmModal(v) {
            var valueUser = '';
            if(v=="c"){
                $(".userGroupVal:checked").each(function () {
                    valueUser += $(this).val() + ",";
                })
                $("#commodity").val(valueUser.substring(0, valueUser.length - 1));
            }else {
                $(".userGroupValSbu:checked").each(function () {
                    valueUser += $(this).val() + ",";
                })
                $("#sbuVal").val(valueUser.substring(0, valueUser.length - 1));
            }
        }

        function closeModal(v) {
            if(v=="c"){
                $("#myModal input[type='checkbox']").prop("checked", false);
            }else {
                $("#myModalSbu input[type='checkbox']").prop("checked", false);
            }
        }

        function  checkedChild(e){
            var a= $(e).val();
            if ( $(e).prop("checked") == true) {
                $("."+a).prop("checked", true);
            } else {
                $("."+a).prop("checked", false);
            }
        }
        function modelShow() {
            $('#myModal').modal('show');
        }
        function modelShowSbu() {
            $('#myModalSbu').modal('show');
        }

        function allCheck(e,v){
            debugger;
            if(v=="c"){
                if ($(e).prop("checked") == true) {
                    $("#myModal input[type='checkbox']").prop("checked", true);
                } else {
                    $("#myModal input[type='checkbox']").prop("checked", false);
                }
            }else{
                if ($(e).prop("checked") == true) {
                    $("#myModalSbu input[type='checkbox']").prop("checked", true);
                } else {
                    $("#myModalSbu input[type='checkbox']").prop("checked", false);
                }
            }
        }
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><spring:message code='poIntegrationList'/></span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div style="margin-top: 20px;">
                <ul style="float:left;margin-right:10px;">
                    <li>
                        <select id="QTableName" class="input-large" style="width:200px;margin-bottom:0;">
                            <option value=""><spring:message code='tableSelect'/></option>
                            <c:forEach items="${tableListSelect }" var="poTable">
                                <option value="${poTable.tableName }">${poTable.comments }</option>
                            </c:forEach>
                        </select>
                    </li>
                    <li style="height:20px;">
                        <span id="QTableNameTip" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <span id="Query">
                <span id="Scenario"></span>
                <input id="DateYear" name="YYYY" type="text"
                       style="width:80px;text-align:center;display: none;"
                       placeholder="<spring:message code='year'/>">
                <input id="QDate" name="YYYYMM"
                       style="width:80px;text-align:center;"
                       placeholder="<spring:message code='start_time'/>"
                       type="text" value="" readonly>
                <input id="QDateEnd" name="YYYYMM"
                       style="width:80px;text-align:center;"
                       type="text" value=""
                       placeholder="<spring:message code='end_time'/>"
                       readonly>
                <select id="flag" class="input-large" style="width:100px;">
                    <option value="">
                        <c:if test="${languageS eq 'zh_CN'}">審批狀態</c:if>
                        <c:if test="${languageS eq 'en_US'}">Approval status</c:if>
                        </option>
                    <option value="0"><c:if test="${languageS eq 'zh_CN'}">未提交</c:if>
                        <c:if test="${languageS eq 'en_US'}">Unsubmitted</c:if></td></option>
                    <option value="1"><spring:message code='praeiudicium'/></option>
                    <option value="2">
                        <c:if test="${languageS eq 'zh_CN'}">終審</c:if>
                        <c:if test="${languageS eq 'en_US'}">Final Judgment</c:if>
                        </option>
                    <option value="10">
                        <c:if test="${languageS eq 'zh_CN'}">審核</c:if>
                        <c:if test="${languageS eq 'en_US'}">Audit</c:if>
                        </option>
                    <option value="3">
                        <c:if test="${languageS eq 'zh_CN'}">完成</c:if>
                        <c:if test="${languageS eq 'en_US'}">Finish</c:if>
                    </option>
                </select>
                <select id="QpoCenter" name="QpoCenter" class="input-large"
                        style="width:120px;">
                    <option value=""><spring:message code='poCenter'/></option>
                    <c:forEach items="${commodityMap}" var="code">
                        <option value="${code.key}">${code.key}</option>
                    </c:forEach>
                </select>
                <input type="text" id="commodity" style="width: 120px;" data-toggle="modal"
                       ondblclick="modelShow()" placeholder="commodity">
                <input type="text" style="width: 100px;" id="buVal" value="${buVal}"
                       placeholder="BU">
                <input type="text" style="width: 100px;" id="sbuVal" value="${sbuVal}" data-toggle="modal"
                       ondblclick="modelShowSbu()" placeholder="SBU">
                <input type="text" style="width: 100px;display: none;" id="founderVal" value="${founderVal}"
                       placeholder="<spring:message code='founder'/>">
                <select id="priceControl" name="priceControl" class="input-large"
                        style="width:100px;display: none;">
                    <option value="">是否客指</option>
                    <option value="客指">客指</option>
                    <option value="非客指">非客指</option>
                </select>
                </span>
                <button id="QueryBtn" class="btn search-btn"
                        type="submit"><spring:message code='query'/></button>
                <c:if test="${hasKey eq '1'}">
                    <button id="deleteBtn" class="btn search-btn"
                            type="submit"><spring:message code='delete'/></button>
                </c:if>
                <button id="Download" class="btn search-btn" type="button">
                    <spring:message code='download'/></button>
            </div>
        </div>
        <div id="NTD" style="clear: both;margin-left: 20px;display: none;"><h5>單位：K NTD</h5></div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>


<div class="modal fade" id="myModal" style="display: none" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    commodity
                </h4>
                <span>全選 <input onclick="allCheck(this,'c')" type="checkbox"></span>
            </div>
            <div class="modal-body">
                <table id="commdityTable" border="0" cellpadding="0" cellspacing="1">
                    <c:forEach items="${commodityMap}" var="dataMap">
                    <tr style="border-top: 1px solid #dadada;height: 30px;font-weight:bold;">
                        <td colspan="4"><input type="checkbox" onchange="checkedChild(this)" value="${dataMap.key}">${dataMap.key}</td>
                    </tr>
                    <c:forEach items="${dataMap.value}" var="commodity" varStatus="status">
                    <c:if test="${status.index %4 eq 0}">
                    <tr>
                        </c:if>
                        <td width="140px">
                            <input type="checkbox" class="userGroupVal ${dataMap.key}" value="${commodity}">${commodity}
                        </td>
                        </c:forEach>
                        </c:forEach>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="closeModal('c')" class="btn btn-default" data-dismiss="modal"><spring:message
                        code="close"/>
                </button>
                <button type="button" onclick="affirmModal('c')" class="btn btn-primary" data-dismiss="modal"><spring:message
                        code="submit"/></button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="myModalSbu" style="display: none" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabelSbu">
                    SBU
                </h4>
                <span>全選 <input onclick="allCheck(this,'s')" type="checkbox"></span>
            </div>
            <div class="modal-body">
                <table id="sbuTable" border="0" cellpadding="0" cellspacing="1">
                    <c:forEach items="${sbuMap}" var="dataMap">
                    <tr style="border-top: 1px solid #dadada;height: 30px;font-weight:bold;">
                        <td colspan="4"><input type="checkbox" onchange="checkedChild(this)" value="${dataMap.key}">${dataMap.key}</td>
                    </tr>
                    <c:forEach items="${dataMap.value}" var="commodity" varStatus="status">
                    <c:if test="${status.index %4 eq 0}">
                    <tr>
                        </c:if>
                        <td width="140px">
                            <input type="checkbox" class="userGroupValSbu ${dataMap.key}" value="${commodity}">${commodity}
                        </td>
                        </c:forEach>
                        </c:forEach>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="closeModal('s')" class="btn btn-default" data-dismiss="modal"><spring:message
                        code="close"/>
                </button>
                <button type="button" onclick="affirmModal('s')" class="btn btn-primary" data-dismiss="modal"><spring:message
                        code="submit"/></button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
