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

        .ui-datepicker select.ui-datepicker-month {
            display: none;
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

        .ui-datepicker-year {
            width: 190px !important;
        }

        .ui-datepicker-next {
            display: none
        }

        .ui-datepicker-prev {
            display: none
        }

        .table thead th {
            vertical-align: middle;
        }

        .table-condensed td {
            padding: 7px 10px;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $("#poForm").fileupload({
                dataType: "json",
                url: "${ctx}/bi/rtIntegration/upload",
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
                        console.log(1111)
                        var tableName = $("#QTableName").val();
                        if (tableName.length =="") {
                            $("#QTableNameTip").show();
                            return;
                        } else {
                            if ($("input[id='CUX_RT_SALES_TARGET']:checked").val() == "CUX_RT_SALES_TARGET") {
                                if (!$("#Date").val()) {
                                    $("#QDateTip").show();
                                    return;
                                }
                            }
                        }

                        $("#loading").show();
                        data.submit();
                    });
                },
                done: function (e, data) {
                    $("#loading").delay(1000).hide();
                    layer.alert(data.result.msg);
                    $("#QueryBtn").click();
                },
                fail: function () {
                    $("#loading").delay(1000).hide();
                    layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
                },
                processfail: function (e, data) {
                    $("#loading").delay(1000).hide();
                    layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
                    $("#QueryBtn").click();
                }
            });

            $(".upload-tip").click(function () {
                $(".input-file").trigger("click");
            });

            $("#Download").click(function () {
                $("#UploadTip").hide();
                $("#UEntityTip").hide();
                $("#poCenterTip").hide();
                $("#QTableNameTip").hide();
                var flag = true;
                var queryCondition=$("#QueryCondition").serialize();
                var tableName = $("#QTableName").val();
                console.log(tableName)
                if (tableName === "") {
                    $("#QTableNameTip").show();
                    flag = false;
                }
                if (!flag) {
                    return;
                }
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtIntegration/download",
                    async: true,
                    dataType: "json",
                    data: {queryCondition:queryCondition, tableNames: tableName},
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
                console.log(222)
                $("#QTableNameTip").hide();
                $("#UploadTip").hide();
                $("#DateTip").hide();

                var tableName = $("#QTableName").val();
                console.log(tableName)
                if (tableName.length == 0) {
                    $("#QTableNameTip").show();
                    return;
                }
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtIntegration/template",
                    async: true,
                    dataType: "json",
                    data: {tableNames: tableName},
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
            $("#Date,#QDate,#DateEnd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yyyy',
                showButtonPanel: true,
                closeText: "<spring:message code='confirm'/>"
            });

            $("#Date,#QDate,#DateEnd").click(function () {
                periodId = $(this).attr("id");
                $(this).val("");
            });

            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function () {
                var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
                $("#" + periodId).val(year);//给input赋值，其中要对月值加1才是实际的月份
                if ($("#" + periodId + "Tip").length > 0) {
                    $("#" + periodId + "Tip").hide();
                }
            });

            $("#QTableName").change(function () {
                if ($(this).val().length > 0) {
                    $("#" + $(this).attr("id") + "Tip").hide();
                }
            });

            $(".AllCheck input").change(function () {
                var checked = $(this).is(":checked");
                $(this).parent().siblings().find("input").prop("checked", checked);
                if (!checked) {
                    $(this).parent().parent().parent().siblings().find("span").show();
                } else {
                    $(this).parent().parent().parent().siblings().find("span").hide();
                }
            });

            $(".Check input").change(function () {
                var length = $(this).parent().siblings(".Check").find("input:checked").length + $(this).is(":checked");
                var total = $(this).parent().siblings(".Check").length + 1;
                $(this).parent().siblings(".AllCheck").find("input").prop("checked", length == total);
                if (length > 0) {
                    $(this).parent().parent().parent().siblings().find("span").hide();
                } else {
                    $(this).parent().parent().parent().siblings().find("span").show();
                }
            });

            $("#QueryBtn").click(function () {
                var queryCondition=$("#QueryCondition").serialize();
                var tableName = $("#QTableName").val();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/rtIntegration/list", {
                    tableName: tableName,
                    queryCondition:decodeURIComponent(queryCondition)
                }, function () {
                    $("#loading").fadeOut(1000);
                });
            });

            $("#ConsolidationDataBtn").click(function () {
                $("#QTableNameTip").hide();
                $("#QQpoCenterTip").hide();
                $("#QTypeTip").hide();
                var date = $("#QDate").val();
                if (date.length == 0) {
                    $("#QDateTip").show();
                    return;
                }
                $("#QDateTip").hide();
                var type = $("#QType").val();
                if (type.length == 0) {
                    $("#QTypeTip").show();
                    return;
                }
                $("#QTypeTip").hide();

                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtIntegration/consolidationDataDownload",
                    async: true,
                    dataType: "json",
                    data: {date: date, type: type},
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


        });
        $('#deleteBtn').click(function () {
            var ids = $('input[name=checkboxID]');
            var data = '';
            ids.each(function () {
                //获取当前元素的勾选状态
                if ($(this).prop("checked")) {
                    data = data + $(this).val() + ",";
                }
            });
            if (data === "") {
                alert("請勾選要刪除的數據！");
            } else {
                data = data.substring(0, data.length - 1);
                console.log(data)
                var tableName = $("#QTableName").val();
                var obj = {
                    id: data,
                    tableName: tableName
                }
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtIntegration/delete",
                    async: false,
                    dataType: "json",
                    data: obj,
                    success: function (data) {
                        if (data.flag == "success") {
                            refresh();
                        } else {
                            layer.alert(data.msg);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            }
            //去最后的点

        })
        var periodId;
        $("#QTableName").change(function(){
            $("#QTableNameTip").hide();
            $("#QueryCondition").empty();
            $("#Content").empty();
            var tableName = $("#QTableName").val();
            if (tableName.length == 0) {
                $("#QTableNameTip").show();
                return;
            }
            console.log(1111)
                $.ajax({
                    type:"POST",
                    url:"${ctx}/bi/rtIntegration/queryCondition",
                    async:true,
                    dataType:"json",
                    data:{tableName:tableName},
                    success: function(data){
                        $("#loading").hide();
                        if(data.flag=="success"){
                            $.each(data.queryList, function(i, n){
                                var COL_NAME=n[0];
                                var COL_DESC=n[1];
                                $("#QueryCondition").append("<input name='"+COL_NAME+"' class='m-l-md' style='width:150px;' placeholder='"+COL_DESC+"' type='text' value=''>");
                            });
                            $("#QueryBtn").trigger("click");
                        }else{
                            layer.alert(data.msg);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $("#loading").hide();
                        console.log(errorThrown)
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
        });
        $("#FileUpload").click(function(){
            $("#UploadTip").show();
            if($("#QTableName").val()==""){
                $("#QTableNameTip").show();
            }
        });

    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><spring:message code='rtIntegration'/></span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md">
            <div class="controls">
                <div>
                    <form id="poForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data"
                          class="form-horizontal">
                        <input type="file" style="display:none;" class="input-file" multiple="false"/>
                        <div>
                            <div style="float: left;text-align: right;" title="<spring:message code='not_exceed_30M'/>">
                                <div class="upload-tip">
                                    <span class="tip"><spring:message code='click_select_excel'/></span>
                                </div>
                                <div id="UploadTip" style="display:none;float:left;">
                                    <span class="Validform_checktip Validform_wrong"><spring:message
                                            code='please_select'/></span>
                                </div>
                            </div>
                            <div style="float:left;margin-left:15px;display:inline-block;display: none" id="dateShow">
                                <ul style="float:left;">
                                    <li>
                                        <input id="Date" name="date"
                                               style="float:left;width:140px;text-align:center;margin-bottom:0;"
                                               placeholder="<spring:message code='please_select'/><spring:message code='year'/>"
                                               type="text" value="" readonly>
                                    </li>
                                    <li style="height:30px;">
                                        <span id="QDateTip" style="display:none;"
                                              class="Validform_checktip Validform_wrong"><spring:message
                                                code='please_select'/></span>
                                    </li>
                                </ul>
                            </div>
                            <div style="padding-right:10px;">
                                <ul style="float:left;margin-left:20px;">
                                    <li>
                                        <select id="QTableName" name="tableNames" class="input-large"
                                                style="width:200px;margin-bottom:0;">
                                            <option value=""><spring:message code='tableSelect'/></option>
                                            <c:forEach items="${poTableList }" var="poTable">
                                                <option value="${poTable.tableName}">${poTable.comments }</option>
                                            </c:forEach>
                                        </select>
                                    </li>
                                    <li>
                                        <span id="QTableNameTip" style="display:none;" class="Validform_checktip Validform_wrong">
                                            <spring:message code='please_select'/>
                                        </span>
                                    </li>
                                </ul>
                                <button id="FileUpload" style="float:left;" class="btn search-btn" type="button">
                                    <spring:message code='upload'/></button>

                                <button id="DownloadTemplate" class="btn btn-link"
                                        style="vertical-align: top;height: 40px;font-size: 26px;text-decoration: underline;"
                                        type="button"><spring:message code='template'/></button>
                                <button id="Download" style="float:left;" class="btn search-btn" type="button">
                                    <spring:message code='download'/></button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls" style="display:inline-block;vertical-align:top;width:100%;">
                <form id="QueryCondition" style="float:left;margin:0;"></form>
                <button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;"
                        type="submit"><spring:message code='query'/></button>
                <button id="deleteBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;"
                        type="submit"><spring:message code='delete'/></button>
                <c:if test="${attribute eq 'group'}">
                    <ul style="float:left;margin-left:20px;">
                        <li>
                            <select id="QType" class="input-large" style="width:160px;margin-bottom:0;">
                                <option value=""><spring:message code='please_select'/><spring:message
                                        code='type'/></option>
                                <c:forEach items="${typeList}" var="type">
                                    <option value="${type }">${type }</option>
                                </c:forEach>
                            </select>
                        </li>
                        <li style="height:30px;">
                            <span id="QTypeTip" style="display:none;"
                                  class="Validform_checktip Validform_wrong"><spring:message
                                    code='please_select'/></span>
                        </li>
                    </ul>
                    <button id="ConsolidationDataBtn" class="btn search-btn btn-warning m-l-md"
                            style="margin-left:20px;" type="button"><spring:message code='consolidationData'/></button>
                </c:if>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>
