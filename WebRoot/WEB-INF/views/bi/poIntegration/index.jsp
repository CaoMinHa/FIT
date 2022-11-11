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
            padding: 7px 10px;
        }

        .modal-backdrop {
            position: initial !important;
        }
    </style>
    <script type="text/javascript">
        //FX获取文件路径方法
        function readFileFirefox(fileBrowser) {
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
            } catch (e) {
                alert('无法访问本地文件，由于浏览器安全设置。为了克服这一点，请按照下列步骤操作：(1)在地址栏输入"about:config";(2) 右键点击并选择 New->Boolean; (3) 输入"signed.applets.codebase_principal_support" （不含引号）作为一个新的首选项的名称;(4) 点击OK并试着重新加载文件');
                return;
            }
            var fileName = fileBrowser.value; //这一步就能得到客户端完整路径。下面的是否判断的太复杂，还有下面得到ie的也很复杂。
            var file = Components.classes["@mozilla.org/file/local;1"]
                .createInstance(Components.interfaces.nsILocalFile);
            try {
                // Back slashes for windows
                file.initWithPath(fileName.replace(/\//g, "\\\\"));
            } catch (e) {
                if (e.result != Components.results.NS_ERROR_FILE_UNRECOGNIZED_PATH) throw e;
                alert("File '" + fileName + "' cannot be loaded: relative paths are not allowed. Please provide an absolute path to this file.");
                return;
            }
            if (file.exists() == false) {
                alert("File '" + fileName + "' not found.");
                return;
            }
            return file.path;
        }

        //根据不同浏览器获取路径
        function getvl(obj) {
            //判断浏览器
            var Sys = {};
            var ua = navigator.userAgent.toLowerCase();
            var s;
            (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
                (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
                    (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                        (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                            (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
            var file_url = "";
            if (Sys.ie <= "6.0") {
                //ie5.5,ie6.0
                file_url = obj.value;
            } else if (Sys.ie >= "7.0") {
                //ie7,ie8
                obj.select();
                obj.blur();
                file_url = document.selection.createRange().text;
            } else if (Sys.firefox) {
                //fx
                //file_url = document.getElementById("file").files[0].getAsDataURL();//获取的路径为FF识别的加密字符串
                file_url = readFileFirefox(obj);
            } else if (Sys.chrome) {
                file_url = obj.value;
            } else {
                file_url = obj.value;
            }
            $("#addessExsel").val(file_url);
        }

        $(function () {
            $("#poForm").fileupload({
                dataType: "json",
                url: "${ctx}/bi/poIntegration/upload",
                add: function (e, data) {
                    $("#FileUpload").unbind();
                    var filename = data.originalFiles[0]['name'];
                    var acceptFileTypes = /(\.|\/)(xls|xlsx|XLS|XLSX)$/i;
                    if (filename.length && !acceptFileTypes.test(filename)) {
                        $(".tip").text("<spring:message code='click_select_excel'/>");
                        layer.alert("<spring:message code='only_support_excel'/>");
                        return;
                    }
                    if (data.originalFiles[0]['size'] > 1024 * 1024 * 30) {
                        $(".tip").text("<spring:message code='click_select_excel'/>");
                        layer.alert("<spring:message code='not_exceed_30M'/>");
                        return;
                    }
                    $(".tip").text(filename);
                    $(".upload-tip").attr("title", filename);
                    $("#UploadTip").hide();

                    $("#FileUpload").click(function () {
                        if (!$("#tableNamesOut1").val()) {
                            $("#TableNamesTipX1").show();
                            return;
                        }
                        $("#loading").show();
                        data.submit();
                    });
                },
                done: function (e, data) {
                    $("#loading").delay(1000).hide();
                    layer.alert(data.result.msg);
                },
                fail: function () {
                    $("#loading").delay(1000).hide();
                    layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
                },
                processfail: function (e, data) {
                    $("#loading").delay(1000).hide();
                    layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
                }
            });

            $("#FileUpload").click(function () {
                $("#UploadTip").show();
                if ($("#tableNamesOut1").val() == 'FIT_PO_SBU_YEAR_CD_SUM' || $("#tableNamesOut1").val() == 'FIT_PO_CD_MONTH_DTL') {
                    if (!$("#DateYearDownLoad").val()) {
                        $("#DateYearTipDownLoad").show();
                    } else {
                        $("#DateYearTipDownLoad").hide();
                        var r = /^\+?[1-9][0-9]*$/;
                        if ($("#DateYearDownLoad").val().length != 4 || !r.test($("#DateYearDownLoad").val())) {
                            $("#DateYearTipDownLoad").text("請填寫正確年份(Please fill in the correct year)");
                            $("#DateYearTipDownLoad").show();
                        }
                    }
                } else {
                    if (!$("#DateDownLoad").val()) {
                        $("#DateTipDownLoad").show();
                    } else {
                        $("#DateTipDownLoad").hide();
                    }
                }

                if (!$("#tableNamesOut1").val()) {
                    $("#TableNamesTipX1").show();
                } else {
                    $("#TableNamesTipX1").hide();
                }
            });

            $("#DateDownLoad").change(function () {
                if ($(this).val()) {
                    $("#DateTipDownLoad").hide();
                }
            })

            $("#DateYearDownLoad").change(function () {
                if ($(this).val()) {
                    $("#DateYearTipDownLoad").hide();
                }
            })

            $(".upload-tip").click(function () {
                $(".input-file").trigger("click");
            });

            $("#Download").click(function () {
                if ($("#QTableName").val().length == 0) {
                    $("#QTableNameTip").show();
                    return;
                }
                var tableName = $("#QTableName").val();
                var date = $("#QDate").val();
                var dateEnd = $("#QDateEnd").val();
                var DateYear = $("#DateYear").val();
                if(tableName=='FIT_PO_SBU_YEAR_CD_SUM'||tableName=='FIT_PO_CD_MONTH_DTL'||tableName=='FIT_PO_Target_CPO_CD_DTL'){
                    var r = /^\+?[1-9][0-9]*$/;
                    if (DateYear.length!=4 || !r.test(DateYear)) {
                        layer.alert("請填寫正確的年份(Please fill in the correct year)");
                        return;
                    }
                }else if(tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'||tableName=='FIT_PO_BUDGET_CD_DTL'){
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
                    data: {
                        date: date,
                        dateEnd: dateEnd,
                        DateYear: DateYear,
                        tableNames: tableName,
                        poCenter: entity,
                        sbuVal: sbuVal,
                        priceControl: $("#priceControl").val(),
                        commodity: $("#commodity").val(),
                        buVal: $("#buVal").val(),
                        founderVal: $("#founderVal").val()
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

            $("#DownloadTemplate").click(function () {
                $("#TableNameTip").hide();
                if (!$("#tableName").val()) {
                    $("#TableNameTip").show();
                    return;
                }
                if($("#tableName").val()=="FIT_PO_SBU_YEAR_CD_SUM"){
                    window.location.href="${ctx}/static/template/bi/SBU年度CD目標匯總表.xlsx";
                }else{
                    $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegration/template",
                    async: true,
                    dataType: "json",
                    data: {tableNames: $("#tableName").val()},
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
                }
            });

            $("#ui-datepicker-div").remove();
            $("#Date,#DateEnd,#DateDownLoad,#QDate,#QDateEnd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yy-MM',
                showButtonPanel: true,
                closeText: "<spring:message code='confirm'/>"
            });
            $("#Date,#DateEnd,#DateDownLoad,#QDate,#QDateEnd").click(function () {
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

            $("#QTableName").change(function () {
                $("#downCondition input").val("");
                $("#downCondition select").val("");
                $("#downCondition").hide();
                var tableName = $(this).val();
                if (tableName == 'FIT_PO_SBU_YEAR_CD_SUM' || tableName == 'FIT_PO_CD_MONTH_DOWN' ||
                    tableName == 'FIT_ACTUAL_PO_NPRICECD_DTL' || tableName == 'FIT_PO_BUDGET_CD_DTL'||tableName =='FIT_PO_Target_CPO_CD_DTL') {
                    $("#downCondition").show();
                    $("#QpoCenter").show();
                    $("#Scenario").text("");
                    $("#buVal").show();
                    $("#priceControl").show();
                    $("#founderVal").hide();
                    $("#sbuVal").show();
                    $("#commodity").show();
                    switch (tableName) {
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
                        case "FIT_PO_CD_MONTH_DOWN":
                            $("input[name='YYYY']").show();
                            $("input[name='YYYYMM']").hide();
                            $("#QpoCenter").hide();
                            $("#QpoCenter").change();
                            break;
                            //採購CD 目標核准表
                        case "FIT_PO_Target_CPO_CD_DTL":
                            $("input[name='YYYY']").show();
                            $("input[name='YYYYMM']").hide();
                            $("#QpoCenter").hide();
                            $("#buVal").hide();
                            $("#sbuVal").hide();
                            $("#priceControl").hide();
                            $("#founderVal").hide();
                            $("#commodity").hide();
                            break;
                    }
                }
            });
            $("#tableNamesOut1").change(function () {
                var tableName = $(this).val();
                $(".remark").hide();
                switch (tableName) {
                    case "FIT_ACTUAL_PO_NPRICECD_DTL":
                        $("#DateDownLoad").show();
                        $("#DateYearDownLoad").hide();
                        $("#DateYearDownLoad").val("");
                        $("#APND").show();
                        break;
                    case "FIT_PO_BUDGET_CD_DTL":
                        $("#DateDownLoad").show();
                        $("#DateYearDownLoad").hide();
                        $("#DateYearDownLoad").val("");
                        $("#PBCD").show();
                        break;
                    case "FIT_PO_SBU_YEAR_CD_SUM":
                        $("#DateYearDownLoad").show();
                        $("#DateDownLoad").hide();
                        $("#DateDownLoad").val("");
                        $("#PSYCS").show();
                        break;
                    case "FIT_PO_CD_MONTH_DTL":
                        $("#DateYearDownLoad").show();
                        $("#DateDownLoad").hide();
                        $("#DateDownLoad").val("");
                        $("#PCMD").show();
                        break;
                }
            });

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

        })
        var periodId;

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
                <span><spring:message code='poIntegration'/></span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md">
            <div class="controls">
                <div>
                    <form id="poForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data"
                          class="form-horizontal">
                        <div style="float:left;display:flex;">
                            <ul class="nav dropdown" style="margin-left:10px;">
                                <li class="dropdown">
                                    <select class="input-large" style="width:240px;" id="tableName" name="tableName">
                                        <option value=""><spring:message code='tableSelect'/></option>
                                        <c:forEach items="${tableListSelect }" var="poTable">
                                            <option value="${poTable.tableName }">${poTable.comments }</option>
                                        </c:forEach>
                                    </select>
                                </li>
                                <li>
                                <span id="TableNameTip" style="display:none;"
                                      class="Validform_checktip Validform_wrong"><spring:message
                                        code='please_select'/></span>
                                </li>
                            </ul>
                            <div>
                                <button id="DownloadTemplate" class="btn btn-link"
                                        style="vertical-align: top;height: 40px;font-size: 26px;text-decoration: underline;"
                                        type="button"><spring:message code='template'/></button>
                            </div>
                        </div>
                        <%--第二排--%>
                        <input type="file" style="display:none;" class="input-file" multiple="false" id="file"
                               onchange="getvl(this)"/>
                        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                            <ul class="nav dropdown" style="float:left;margin-left: -10px;">
                                <li class="dropdown" style="margin-top:0;">
                                    <select class="input-large" style="width:240px;" id="tableNamesOut1"
                                            name="tableNamesOut1">
                                        <option value=""><spring:message code='tableSelect'/></option>
                                        <c:forEach items="${tableListSelect }" var="poTable">
                                            <option value="${poTable.tableName }">${poTable.comments }</option>
                                        </c:forEach>
                                    </select>
                                </li>
                                <li>
                                    <span id="TableNamesTipX1" style="display:none;"
                                     class="Validform_checktip Validform_wrong"><spring:message
                                     code='please_select'/></span>
                                </li>
                            </ul>
                            <div style="float: left;text-align: right;margin-left: 10px;"
                                 title="<spring:message code='not_exceed_30M'/>">
                                <div class="upload-tip">
                                    <span class="tip"><spring:message code='click_select_excel'/></span>
                                </div>
                                <div id="UploadTip" style="display:none;float:left;">
                                    <span class="Validform_checktip Validform_wrong"><spring:message
                                            code='please_select'/></span>
                                </div>
                            </div>
                            <div style="float: left;text-align: right;margin-left: 1px;">
                                <div>
                                    <input id="addessExsel" name="date" style="width:400px;text-align:center;"
                                           type="text" value="" readonly>
                                </div>
                            </div>
                            <button id="FileUpload" style="float:left;" class="btn search-btn" type="button">
                                <spring:message code='upload'/></button>
                        </div>
                        <%--                        第三排--%>
                        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                            <div style="margin-top: 20px;">
                                <ul style="float:left;margin-right:10px;">
                                    <li>
                                        <select id="QTableName" class="input-large"
                                                style="width:240px;margin-bottom:0;margin-left:-10px;">
                                            <option value=""><spring:message code='tableSelect'/></option>
                                            <c:forEach items="${poTableOutList }" var="poTableOut">
                                                <option value="${poTableOut.tableName }">${poTableOut.comments}</option>
                                            </c:forEach>
                                        </select>
                                    </li>
                                    <li style="height:20px;">
                                        <span id="QTableNameTip" style="display:none;"
                                              class="Validform_checktip Validform_wrong"><spring:message
                                                code='please_select'/></span>
                                    </li>
                                </ul>
                                <span id="downCondition">
                                    <span id="Scenario"></span>
                                    <input id="DateYear" name="YYYY" type="text"
                                           style="width:80px;text-align:center;display: none;"
                                           placeholder="<spring:message code='year'/>">
                                    <span id="DateYearTip" style="display:none;"
                                          class="Validform_checktip Validform_wrong">請填寫正確的年份(Please fill in the correct year)</span>
                                    <input id="QDate" name="YYYYMM"
                                           style="width:80px;text-align:center;margin-bottom:0;"
                                           placeholder="<spring:message code='start_time'/>"
                                           type="text" value="" readonly>
                                    <input id="QDateEnd" name="YYYYMM"
                                           style="width:80px;text-align:center;margin-bottom:0;"
                                           type="text" value=""
                                           placeholder="<spring:message code='end_time'/>"
                                           readonly>
                                    <select id="QpoCenter" name="QpoCenter" class="input-large"
                                            style="width:120px;">
                                        <option value=""><spring:message code='poCenter'/></option>
                                        <c:forEach items="${commodityMap}" var="code">
                                            <option value="${code.key}">${code.key}</option>
                                        </c:forEach>
                                    </select>
                                    <input type="text" id="commodity" style="width: 120px;" data-toggle="modal"
                                           ondblclick="modelShow()" placeholder="commodity">
                                    <input type="text" style="width: 120px;" id="buVal" value="${buVal}"
                                           placeholder="BU">
                                    <input type="text" style="width: 120px;" id="sbuVal" value="${sbuVal}" data-toggle="modal"
                                           ondblclick="modelShowSbu()" placeholder="SBU">
                                    <input type="text" style="width: 120px;display: none;" id="founderVal" value="${founderVal}"
                                           placeholder="<spring:message code='founder'/>">
                                    <select id="priceControl" name="priceControl" class="input-large"
                                            style="width:100px;display: none;">
                                        <option value="">是否客指</option>
                                        <option value="客指">客指</option>
                                        <option value="非客指">非客指</option>
                                    </select>
                                </span>
                                <button id="Download" class="btn search-btn" type="button">
                                    <spring:message code='download'/></button>
                            </div>
                        </div>

                        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                            <div style="margin-top: 20px;">
                                <span id="APND" class="remark" style="color:#000;display: none">
                                    <span style="color:red;">
                                        上傳提醒：</br>
                                        上傳截至日期為當月10號，且歷史數據無法變更。</br>
                                        如遇特殊情況，請聯係管理員協助處理。</br>
                                    </span>
                                    </br>
                                    編制說明：</br>
                                    1.本表為非價格CD金額輸入表，由採購輸入，涉及的Commodity 有標準件，機加工件，...，汽車等等。</br>
                                    2.上傳報表管控邏輯：</br>
                                    &nbsp;&nbsp;1）Commodity /年/月份/SBU 為必填欄位，報表上傳時，系統需校驗4個欄位的內容是否與值集相同，不相同則報錯；</br>
                                    &nbsp;&nbsp;2）BI 系統顯示的BU 欄位內容，由SBU 與BU 綁定關係帶出；</br>
                                    3.上傳的報表需經對應的課/部級主管審核確認後，才能上傳到BI 系統；</br>
                                    &nbsp;&nbsp;1）審核流程： 發起人---課級主管---部級主管。提交及審核需觸發郵件通知。</br>
                                    &nbsp;&nbsp;2）上傳報表採用開關版管理，由Key User 設定指定時間。上傳報表的時間（年/月份）只能上傳指定月份的數據。</br>
                                    4.SBU 為必填欄位，BU 欄位可為空，BU 欄位信息可由SBU 的綁定關係帶入。SBU 內容要與值集信息做校驗；</br>
                                    5.其他Free Key in 欄位非必填，內容需為數字格式，不能為文字。若填報文字，系統會進行報錯提醒。</br>
                                </span>
                                <span id="PBCD" class="remark" style="color:#000;display: none">
                                    <span style="color:red;">
                                        上傳提醒：</br>
                                        上傳截至日期為當月10號，且歷史數據無法變更。</br>
                                        如遇特殊情況，請聯係管理員協助處理。</br>
                                    </span>
                                    </br>
                                    編制說明：</br>
                                    1.本表為手工上傳業務數據未對接BI系統的實際採購金額及價格CD金額；由採購輸入，涉及的Commodity 有標準件，機加工件，...，汽車等等。</br>
                                    2.上傳報表管控邏輯：</br>
                                    &nbsp;&nbsp;1）Commodity /年/月份/SBU 為必填欄位，報表上傳時，系統需校驗4個字段的內容是否與值集相同，不相同則報錯；客指/非客指也需要進行校驗；年月需要與所選擇年月進行匹配；</br>
                                    &nbsp;&nbsp;2）BI 系統顯示的BU 字段內容，由SBU 與BU 綁定關係帶出；</br>
                                    3.上傳的報表需經對應的課/部級主管審核確認後，才能上傳到BI 系統；</br>
                                    &nbsp;&nbsp;1）審核流程： 發起人---課級主管---部級主管。提交及審核需觸發郵件通知。接口平臺的四張表單在提交與審核時可以上傳附檔，提交時可以備註/說明。審核主管可根據實際情況增刪附件；</br>
                                    &nbsp;&nbsp;2）上傳報表採用開關版管理，開關版時間由Key user管理。上傳報表的時間（年/月份）只能上傳指定月份的數據。</br>
                                    4.CD比例=價格CD金額/（採購金額+價格CD金額）</br>
                                </span>
                                <span id="PSYCS" class="remark" style="color:#000;display: none">
                                    編制說明：</br>
                                    1.SBU by 年度/by（1~12月份）/by 年度CD 目標上傳數據；</br>
                                    2.上傳模板不包含年度採購金額合計，但在查詢報表中可查詢到年度採購金額合計；系統自動完成年度計算；</br>
                                    &nbsp;&nbsp;1）年度金額合計=1月+2月+...+12月採購金額</br>
                                    3.SBU 上傳成功後需觸發郵件通知到審核主管；</br>
                                    4.SBU主管審核通過後由系統自動發送郵件通知給到提交人及所有Commodity</br>
                                </span>
                                <span id="PCMD" class="remark" style="color:#000;display: none">
                                    編制說明：</br>
                                    1	此表單查詢結果中的“CD比率%”需要與《採購CD目標by月展開Form》中的“CD比率%”進行校驗，誤差範圍需控制在0.005%內，若誤差大於0.005%，則校驗無法上傳
                                </span>
                            </div>
                        </div>

                    </form>
                </div>
            </div>
        </div>
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
