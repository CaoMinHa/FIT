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
            $("#Date,#QDate,#DateEnd,#QDateEnd").click(function () {
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
                if($(this).val()=='FIT_PO_CD_MONTH_DTL'){
                    $("#QpoCenter").hide();
                    $("#NTD").show();
                }else{
                    $("#QpoCenter").show();
                    $("#NTD").hide();
                }
                if($(this).val()=='FIT_PO_SBU_YEAR_CD_SUM'||$(this).val()=='FIT_PO_CD_MONTH_DTL'){
                    $("ul[name='YYYY']").show();
                    $("#QDateEnd").val("");
                    $("#QDate").val("");
                    $("ul[name='YYYYMM']").hide();
                }else{
                    $("ul[name='YYYY']").hide();
                    $("ul[name='YYYYMM']").show();
                    $("#DateYear").val("");
                }
            });

            $("#QueryBtn").click(function () {
                $("#QDateTip").hide();
                $("#QDateTipEnd").hide();
                $("#DateYearTip").hide();
                var flag = true;
                var tableName = $("#QTableName").val();
                var DateYear = $("#DateYear").val();
                var date = $("#QDate").val();
                var dateEnd = $("#QDateEnd").val();
                if(tableName=='FIT_PO_SBU_YEAR_CD_SUM'||tableName=='FIT_PO_CD_MONTH_DTL'){
                    var r = /^\+?[1-9][0-9]*$/;
                    if (DateYear.length!=4 || !r.test(DateYear)) {
                        $("#DateYearTip").show();
                        flag = false;
                    }
                }else{
                    if (date.length == 0) {
                        $("#QDateTip").show();
                        flag = false;
                    }
                    if (dateEnd.length == 0) {
                        $("#QDateTipEnd").show();
                        flag = false;
                    }
                    if(date.substr(0,3)!=dateEnd.substr(0,3)){
                        layer.msg("請選擇同一年日期作爲查詢條件！(Please select the date of the same year)");
                        flag = false;
                    }
                }
                if (tableName.length == 0) {
                    $("#QTableNameTip").show();
                    flag = false;
                }
                var entity = $("#QpoCenter").val();
                var sbuVal = $("#sbuVal").val();
                if (!flag) {
                    return;
                }
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
                    commodity:$("#commodity").val()
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
                    layer.msg("請勾選要刪除的數據！(Select the data to delete)");
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
                    type:"POST",
                    url:"${ctx}/bi/poIntegrationList/selectCommdity",
                    async:false,
                    dataType:"json",
                    data:{
                        functionName:$(this).val()
                    },
                    success: function(data){
                       $("#commdityTable").empty();
                        $("#commdityTable").append("<tbody id='commdityTbody'></tbody>")
                        jQuery.each(data, function(i,item){
                            if(i%4===0){
                                $("#commdityTbody").append("<tr>");
                            }
                            $("#commdityTbody").append("<td height='25px' width='140px'> <input type='checkbox' class='userGroupVal' value='"+item+"'>"+item+"</td>");
                        })
                    },
                    error: function() {
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            })
        });


        $("input[name=tableNamesOut]").change(function(){
            if($("input[name=tableNamesOut]:checked").val()=="FIT_ACTUAL_PO_NPRICECD_DTL"||
                $("input[name=tableNamesOut]:checked").val()=="FIT_PO_Target_CPO_CD_DTL"||
                $("input[name=tableNamesOut]:checked").val()=="FIT_PO_BUDGET_CD_DTL"||
                $("input[name=tableNamesOut]:checked").val()=="BIDEV.V_PO_PRICE_DIFF_SUM"||
                $("input[name=tableNamesOut]:checked").val()=="BIDEV.V_PO_PRICE_DIFF"||
                $("input[name=tableNamesOut]:checked").val()=="FIT_PO_SBU_YEAR_CD_SUM"||
                $("input[name=tableNamesOut]:checked").val()=="FIT_PO_CD_MONTH_DOWN"
            ){
                $("#poCenter").show();
            }else{
                $("#poCenter").hide();
                $("#poCenter").val("");
            }
        })

        var periodId;
        $(document).ready(function(){
            if("${detailsTsak}"=="ok"){
                setTimeout(function () {
                    $("#QTableName").val("FIT_PO_SBU_YEAR_CD_SUM");
                    $("ul[name='YYYYMM']").hide();
                    $("ul[name='YYYY']").show();
                    $("#QueryBtn").click();
                },1000)
            }
        })

        $("#affirmBut").click(function () {
            var valueUser='';
            $(".userGroupVal:checked").each(function () {
                valueUser+=$(this).val()+",";
            })
            $("#commodity").val(valueUser.substring(0,valueUser.length-1));
        })
        $("#closeBut").click(function () {
            $(".userGroupVal:checked").prop("checked",false);
            $("#commodity").val();
        })
        $("#allCheck").click(function(){
            if ($("#allCheck").prop("checked") == true) {
                $(".userGroupVal").prop("checked", true);
            } else {
                $(".userGroupVal").prop("checked", false);
            }
        });


        $("#Download").click(function () {
            $("#QDateTip").hide();
            $("#QDateTipEnd").hide();
            $("#DateYearTip").hide();
            var flag = true;
            var tableName = $("#QTableName").val();
            var date = $("#QDate").val();
            var dateEnd = $("#QDateEnd").val();
            var DateYear = $("#DateYear").val();
            if(tableName!='FIT_PO_SBU_YEAR_CD_SUM'&&tableName!='FIT_PO_CD_MONTH_DTL'){
                if(date.length!=0||dateEnd.length!=0){
                    if(date.substr(0,3)!=dateEnd.substr(0,3)){
                        layer.msg("請選擇同一年日期作爲查詢條件！(Please select the date of the same year)");
                        flag = false;
                    }
                }
            }
            if ($("#QTableName").val().length == 0) {
                $("#QTableNameTip").show();
                flag = false;
            }
            var entity = $("#QpoCenter").val();
            var sbuVal = $("#sbuVal").val();
            if (!flag) {
                return;
            }
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
                    commodity:$("#commodity").val()
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
            <div class="controls" style="margin-top: 15px;margin-left: 20px;">
                <ul style="float:left;margin-right:20px;">
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
                <ul style="float:left;display: none;margin-left:-10px;" name="YYYY">
                    <li>
                        <input id="DateYear" style="float:left;width:140px;text-align:center;margin-bottom:0;"
                               placeholder="請填寫年份"
                               type="text" value="${DateYear}">
                    </li>
                    <li style="height:20px;">
                        <span id="DateYearTip" style="display:none;"
                              class="Validform_checktip Validform_wrong">請填寫正確的年份(Please fill in the correct year)</span>
                    </li>
                </ul>
                <ul style="float:left;margin-left:-10px;" name="YYYYMM">
                    <li>
                        <input id="QDate" style="float:left;width:140px;text-align:center;margin-bottom:0;"
                               placeholder="<spring:message code='start_time'/>"
                               type="text" value="" readonly>
                    </li>
                    <li style="height:20px;">
                        <span id="QDateTip" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <ul style="float:left;" name="YYYYMM">
                    <li>
                        <input id="QDateEnd" style="float:left;width:140px;text-align:center;margin-bottom:0;"
                               placeholder="<spring:message code='end_time'/>"
                               type="text" value="" readonly>
                    </li>
                    <li style="height:20px;">
                        <span id="QDateTipEnd" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <ul style="float:left;margin-left:10px;">
                    <li>
                        <select id="QpoCenter" name="QpoCenter" class="input-large" style="width:140px;">
                            <option value=""><spring:message code='poCenter'/></option>
                            <c:forEach items="${poCenters}" var="code">
                                <option value="${code}">${code}</option>
                            </c:forEach>
                        </select>
                    </li>
                    <li>
                        <span id="QpoCenterTip" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <ul style="float:left;margin-left:10px;">
                    <li>
                        <input type="text" id="commodity" style="width: 140px;"  data-toggle="modal" data-target="#myModal" placeholder="commodity">
                    </li>
                </ul>
                <ul style="float:left;margin-left:10px;">
                    <li>
                        <input type="text" style="width: 140px;" id="sbuVal" value="${sbuVal}"  placeholder="sbu">
                    </li>
                </ul>


                <button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;"
                        type="submit"><spring:message code='query'/></button>
                <c:if test="${hasKey eq '1'}">
                    <button id="deleteBtn" class="btn search-btn btn-warning m-l-md" style="float:left;"
                            type="submit"><spring:message code='delete'/></button>
                </c:if>
                <button id="Download" style="float:left;" class="btn search-btn" type="button">
                    <spring:message code='download'/></button>
            </div>
        </div>
        <div id="NTD" style="clear: both;margin-left: 20px;display: none;"><h5>單位：NTD</h5></div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>

<div class="modal fade" id="myModal" style="display: none" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    commodity
                </h4>
                <span>全選 <input id="allCheck" type="checkbox"></span>
            </div>
            <div class="modal-body">
                <table id="commdityTable" border="0" cellpadding="0" cellspacing="1">
                    <c:forEach items="${commodityList}" var="column" varStatus="status">
                    <c:if test="${status.index %4 eq 0}">
                    <tr>
                        </c:if>
                        <td  height="25px" width="140px">
                            <input type="checkbox" class="userGroupVal" value="${column}">${column}
                        </td>
                        </c:forEach>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" id="closeBut" class="btn btn-default" data-dismiss="modal"><spring:message code="close"/>
                </button>
                <button type="button" id="affirmBut" class="btn btn-primary" data-dismiss="modal"><spring:message code="submit"/></button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
