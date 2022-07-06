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

        .table-condensed td{
            padding: 7px 10px;
        }
        .modal-backdrop {
            position: initial!important;
        }
    </style>
    <script type="text/javascript">
        //FX获取文件路径方法
        function readFileFirefox(fileBrowser) {
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
            }
            catch (e) {
                alert('无法访问本地文件，由于浏览器安全设置。为了克服这一点，请按照下列步骤操作：(1)在地址栏输入"about:config";(2) 右键点击并选择 New->Boolean; (3) 输入"signed.applets.codebase_principal_support" （不含引号）作为一个新的首选项的名称;(4) 点击OK并试着重新加载文件');
                return;
            }
            var fileName=fileBrowser.value; //这一步就能得到客户端完整路径。下面的是否判断的太复杂，还有下面得到ie的也很复杂。
            var file = Components.classes["@mozilla.org/file/local;1"]
                .createInstance(Components.interfaces.nsILocalFile);
            try {
                // Back slashes for windows
                file.initWithPath( fileName.replace(/\//g, "\\\\") );
            }
            catch(e) {
                if (e.result!=Components.results.NS_ERROR_FILE_UNRECOGNIZED_PATH) throw e;
                alert("File '" + fileName + "' cannot be loaded: relative paths are not allowed. Please provide an absolute path to this file.");
                return;
            }
            if ( file.exists() == false ) {
                alert("File '" + fileName + "' not found.");
                return;
            }


            return file.path;
        }
        //根据不同浏览器获取路径
        function getvl(obj){
//判断浏览器
            var Sys = {};
            var ua = navigator.userAgent.toLowerCase();
            var s;
            (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
                (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
                    (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                        (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                            (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
            var file_url="";
            if(Sys.ie<="6.0"){
                //ie5.5,ie6.0
                file_url = obj.value;
            }else if(Sys.ie>="7.0"){
                //ie7,ie8
                obj.select();
                obj.blur();
                file_url = document.selection.createRange().text;
            }else if(Sys.firefox){
                //fx
                //file_url = document.getElementById("file").files[0].getAsDataURL();//获取的路径为FF识别的加密字符串
                file_url = readFileFirefox(obj);
            }else if(Sys.chrome){
                file_url = obj.value;
            }else {
                file_url =  obj.value;
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
                        if($("#tableNamesOut1").val()=='FIT_PO_SBU_YEAR_CD_SUM'||$("#tableNamesOut1").val()=='FIT_PO_CD_MONTH_DTL'){
                            if (!$("#DateYearDownLoad").val()) {
                                $("#DateYearTipDownLoad").show();
                                return;
                            }else{
                                var r = /^\+?[1-9][0-9]*$/;
                                if ($("#DateYearDownLoad").val().length!=4 || !r.test($("#DateYearDownLoad").val())) {
                                    $("#DateYearTipDownLoad").text("請填寫正確年份");
                                    $("#DateYearTipDownLoad").show();
                                }
                            }
                        }else{
                            if (!$("#DateDownLoad").val()) {
                                $("#DateTipDownLoad").show();
                                return;
                            }
                        }

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
                if($("#tableNamesOut1").val()=='FIT_PO_SBU_YEAR_CD_SUM'||$("#tableNamesOut1").val()=='FIT_PO_CD_MONTH_DTL'){
                    if (!$("#DateYearDownLoad").val()) {
                        $("#DateYearTipDownLoad").show();
                    }else{
                        $("#DateYearTipDownLoad").hide();
                        var r = /^\+?[1-9][0-9]*$/;
                        if ($("#DateYearDownLoad").val().length!=4 || !r.test($("#DateYearDownLoad").val())) {
                            $("#DateYearTipDownLoad").text("請填寫正確年份(Please fill in the correct year)");
                            $("#DateYearTipDownLoad").show();
                        }
                    }
                }else{
                    if (!$("#DateDownLoad").val()) {
                        $("#DateTipDownLoad").show();
                    }else {
                        $("#DateTipDownLoad").hide();
                    }
                }

                if (!$("#tableNamesOut1").val()) {
                    $("#TableNamesTipX1").show();
                }else{
                    $("#TableNamesTipX1").hide();
                }
            });

            $("#DateDownLoad").change(function () {
                if($(this).val()){
                    $("#DateTipDownLoad").hide();
                }
            })
            $("#DateYearDownLoad").change(function () {
                if($(this).val()){
                    $("#DateYearTipDownLoad").hide();
                }
            })
            $("#tableNamesOut1").change(function () {
                if($(this).val()){
                    $("#TableNamesTipX1").hide();
                }
            })
            $(".upload-tip").click(function () {
                $(".input-file").trigger("click");
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

            $("#DownloadTemplate").click(function () {
                $("#TableNameTip").hide();
                if (!$("#tableName").val()) {
                    $("#TableNameTip").show();
                    return;
                }
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

            $("input[type='radio']").change(function () {
                if($(this).val()=='FIT_PO_CD_MONTH_DTL'){
                    $("#cdFormula").show();
                }else{
                    $("#cdFormula").hide();
                }
            });


            $("#QTableName").change(function () {
                if($(this).val()!='FIT_PO_SBU_YEAR_CD_SUM'&&$(this).val()!='FIT_PO_CD_MONTH_DOWN'
                    &&$(this).val()!='FIT_ACTUAL_PO_NPRICECD_DTL'&&$(this).val()!='FIT_PO_BUDGET_CD_DTL'){
                    $("#downCondition").hide();
                }else{
                    $("#downCondition").show();
                    if($(this).val()=='FIT_PO_CD_MONTH_DTL'){
                        $("#QpoCenter").hide();
                    }else{
                        $("#QpoCenter").show();
                    }
                    if($(this).val()=='FIT_PO_SBU_YEAR_CD_SUM'||$(this).val()=='FIT_PO_CD_MONTH_DOWN'){
                        $("ul[name='YYYY']").show();
                        $("#QDateEnd").val("");
                        $("#QDate").val("");
                        $("ul[name='YYYYMM']").hide();
                    }else{
                        $("ul[name='YYYY']").hide();
                        $("ul[name='YYYYMM']").show();
                        $("#DateYear").val("");
                    }
                }
            });
            $("#tableNamesOut1").change(function () {
                if($(this).val()=='FIT_PO_SBU_YEAR_CD_SUM'||$(this).val()=='FIT_PO_CD_MONTH_DTL'){
                    $("#DateYearDownLoad").show();
                    $("#DateDownLoad").hide();
                    $("#DateDownLoad").val("");
                }else{
                    $("#DateDownLoad").show();
                    $("#DateYearDownLoad").hide();
                    $("#DateYearDownLoad").val("");
                }
            });


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
                        jQuery.each(data, function(i,item){
                            console.log("hahha"+item+"eee"+i);
                            if(i%4==0){
                                $("#commdityTable").append("<tr>");
                            }
                            $("#commdityTable").append("<td height='25px' width='140px'> <input type='checkbox' class='userGroupVal' value='"+item+"'>"+item+"</td>");
                        })
                    },
                    error: function() {
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            })

        })

        // $("input[name=tableNamesOut]").change(function(){
        //     if($("input[name=tableNamesOut]:checked").val()=="FIT_ACTUAL_PO_NPRICECD_DTL"||
        //         $("input[name=tableNamesOut]:checked").val()=="FIT_PO_Target_CPO_CD_DTL"||
        //         $("input[name=tableNamesOut]:checked").val()=="FIT_PO_BUDGET_CD_DTL"||
        //         $("input[name=tableNamesOut]:checked").val()=="BIDEV.V_PO_PRICE_DIFF_SUM"||
        //         $("input[name=tableNamesOut]:checked").val()=="BIDEV.V_PO_PRICE_DIFF"||
        //         $("input[name=tableNamesOut]:checked").val()=="FIT_PO_SBU_YEAR_CD_SUM"||
        //         $("input[name=tableNamesOut]:checked").val()=="FIT_PO_CD_MONTH_DOWN"
        //     ){
        //         $("#poCenter").show();
        //     }else{
        //         $("#poCenter").hide();
        //         $("#poCenter").val("");
        //     }
        // })
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
                                <li class="dropdown" >
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

<%--                        第二排--%>
                        <input type="file" style="display:none;" class="input-file" multiple="false" id="file"  onchange="getvl(this)"/>
                        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                            <ul class="nav dropdown" style="float:left;margin-left: -10px;">
                                <li class="dropdown" style="margin-top:0;">
                                    <select class="input-large" style="width:240px;" id="tableNamesOut1" name="tableNamesOut1">
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

                            <div style="float: left;text-align: right;margin-left: 10px;" id="divMonthDownLoad">
                                <div>
                                    <input id="DateDownLoad" name="dateDownLoad" style="width:100px;text-align:center;"
                                           placeholder="<spring:message code="date"/>"
                                           type="text" value="" readonly>
                                </div>
                                <div style="float:left;">
                                    <span id="DateTipDownLoad" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message
                                            code='please_select'/></span>
                                </div>
                            </div>
                            <div style="float: left;text-align: right;margin-left: 10px;" id="divYearDownLoad">
                                <div>
                                    <input id="DateYearDownLoad" name="dateDownLoad" style="display:none;float:left;width:100px;text-align:center;margin-bottom:0;"
                                           placeholder="<spring:message code="please_select"/><spring:message code="year"/>"
                                           type="text" value="${DateYear}">
                                </div>
                                <div style="float:left;">
                                        <span id="DateYearTipDownLoad" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message
                                                code='please_select'/></span>
                                </div>
                            </div>

                            <div style="float: left;text-align: right;margin-left: 10px;" title="<spring:message code='not_exceed_30M'/>">
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
                                <ul style="float:left;margin-right:20px;">
                                    <li>
                                        <select id="QTableName" class="input-large" style="width:240px;margin-bottom:0;margin-left:-10px;">
                                            <option value=""><spring:message code='tableSelect'/></option>
                                            <c:forEach items="${poTableOutList }" var="poTableOut">
                                                <option value="${poTableOut.tableName }">${poTableOut.comments}</option>
                                            </c:forEach>
                                        </select>
                                    </li>
                                    <li style="height:20px;">
                                        <span id="QTableNameTip" style="display:none;"
                                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                                    </li>
                                </ul>
                                <div id="downCondition">
                                <ul style="float:left;display: none;margin-left:-10px;" name="YYYY">
                                    <li>
                                        <input id="DateYear" style="float:left;width:140px;text-align:center;margin-bottom:0;"
                                               placeholder="<spring:message code='please_select'/><spring:message code='year'/>">
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
                                </ul>
                                <ul style="float:left;" name="YYYYMM">
                                    <li>
                                        <input id="QDateEnd" style="float:left;width:140px;text-align:center;margin-bottom:0;"
                                               type="text" value=""
                                               placeholder="<spring:message code='end_time'/>"
                                                readonly>
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
                               </div>

                                <button id="Download" style="float:left;" class="btn search-btn" type="button">
                                    <spring:message code='download'/></button>
                            </div>
                        </div>

<%--                        <div style="float:left;margin-left:15px;display:inline-block;">--%>
<%--                            <div>--%>
<%--                                <input id="Date" name="date" style="width:70px;text-align:center;"--%>
<%--                                       placeholder="<spring:message code='please_select'/><spring:message code='month'/>"--%>
<%--                                       type="text" value="" readonly>--%>
<%--                                <input id="DateEnd" name="dateEnd" style="width:70px;text-align:center;"--%>
<%--                                       placeholder="<spring:message code='please_select'/><spring:message code='month'/>"--%>
<%--                                       type="text" value="" readonly>--%>
<%--                            </div>--%>
<%--                            <div style="float:left;">--%>
<%--                                    <span id="DateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message--%>
<%--                                            code='please_select'/></span>--%>
<%--                            </div>--%>
<%--                        </div>--%>


<%--                        <div style="float:left;padding-right:10px;">--%>
<%--                            <ul style="float:left;margin-left:10px;">--%>
<%--                                <li>--%>
<%--                                    <select id="dataRange" name="dataRange" class="input-large"--%>
<%--                                            style="width:140px;">--%>
<%--                                        <option value="">職能數據範圍</option>--%>
<%--                                        <c:forEach items="${dataRange}" var="code">--%>
<%--                                            <option value="<spring:message code='${code}'/>">${code}</option>--%>
<%--                                        </c:forEach>--%>
<%--                                    </select>--%>
<%--                                    <input id="dataRangeStr" style="display:none" value="${dataRange}">--%>
<%--                                </li>--%>
<%--                            </ul>--%>
<%--                            <ul style="float:left;margin-left:10px;">--%>
<%--                                <li>--%>
<%--                                    <select id="poCenter" name="poCenter" class="input-large" style="width:140px;">--%>
<%--                                        <option value=""><spring:message code='poCenter'/></option>--%>
<%--                                        <c:forEach items="${poCenters}" var="code">--%>
<%--                                            &lt;%&ndash; <option value="${code}">${code}-<spring:message code="${code}"/></option>&ndash;%&gt;--%>
<%--                                            &lt;%&ndash;												 <option value="<spring:message code='${code}'/>">${code}-<spring:message code="${code}"/></option>&ndash;%&gt;--%>
<%--                                            <option value="${code}">${code}</option>--%>
<%--                                        </c:forEach>--%>
<%--                                    </select>--%>
<%--                                </li>--%>
<%--                                <li>--%>
<%--                                        <span id="poCenterTip" style="display:none;"--%>
<%--                                              class="Validform_checktip Validform_wrong"><spring:message--%>
<%--                                                code='please_select'/></span>--%>
<%--                                </li>--%>
<%--                            </ul>--%>
<%--                            <ul class="nav dropdown" style="float:left;margin-left:10px;">--%>
<%--                                <li class="dropdown" style="margin-top:0;">--%>
<%--                                    <a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message--%>
<%--                                            code='tableSelect'/><strong class="caret"></strong></a>--%>
<%--                                    <ul class="dropdown-menu"--%>
<%--                                        style="left:-20%;max-height:350px;overflow-y:scroll;min-width:330px;">--%>
<%--                                        <c:forEach items="${poTableList }" var="poTable">--%>
<%--                                            <li class="Check" style="padding:0 10px;clear:both;">--%>
<%--                                                <span style="font-size:15px;color:#7e8978;float:left;line-height:38px;display:contents;">${poTable.comments }</span>--%>
<%--                                                <input type="radio" name="tableNames"--%>
<%--                                                       style="font-size:15px;color:#7e8978;float:right;width:20px;"--%>
<%--                                                       value="${poTable.tableName}"/>--%>
<%--                                            </li>--%>
<%--                                        </c:forEach>--%>
<%--                                    </ul>--%>
<%--                                </li>--%>
<%--                                <li>--%>
<%--                                        <span id="TableNamesTip" style="display:none;"--%>
<%--                                              class="Validform_checktip Validform_wrong"><spring:message--%>
<%--                                                code='please_select'/></span>--%>
<%--                                </li>--%>
<%--                            </ul>--%>

<%--                        </div>--%>
<%--                        <div style="clear: both;display:flex;margin-top: 10px;">--%>
<%--                            <ul class="nav dropdown" style="float:left;margin-left:10px;">--%>
<%--                                <li class="dropdown" style="margin-top:0;">--%>
<%--                                    <select class="input-large" style="width:240px;" id="tableNamesOut">--%>
<%--                                        <option value=""><spring:message code='tableSelect'/></option>--%>
<%--                                        <c:forEach items="${poTableOutList }" var="poTableOut">--%>
<%--                                            <option value="${poTableOut.tableName }">${poTableOut.comments}</option>--%>
<%--                                        </c:forEach>--%>
<%--                                    </select>--%>
<%--                                </li>--%>
<%--                                <li>--%>
<%--                                    <span id="TableNamesTipX" style="display:none;"--%>
<%--                                          class="Validform_checktip Validform_wrong"><spring:message--%>
<%--                                            code='please_select'/></span>--%>
<%--                                </li>--%>
<%--                            </ul>--%>



<%--                        </div>--%>

                        <div id="cdFormula" style="display: none">
                            <span class="Validform_checktip" style="color: red;">
                                <c:if test="${languageS eq 'zh_CN'}">預估年度CD% - CPO核準的年度CD% > -0.005%</c:if>
                                <c:if test="${languageS eq 'en_US'}">Estimated annual CD% - CPO approved annual CD% > -0.005%</c:if>
                                </span>
                        </div>

                    </form>
                </div>
            </div>
        </div>
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
