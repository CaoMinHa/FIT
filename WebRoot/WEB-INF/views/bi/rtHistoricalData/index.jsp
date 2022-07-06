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
            padding: 1px 1px;
            border-right:1px solid #eee;
        }
        .ui-datepicker-calendar, .ui-datepicker-current {
            display: none;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $("#poForm").fileupload({
                dataType: "json",
                url: "${ctx}/bi/rtHistoricalData/upload",
                add: function (e, data) {
                    $("#FileUpload").unbind();
                    var filename = data.originalFiles[0]['name'];
                    var acceptFileTypes = /(\.|\/)(xlsx|XLSX)$/i;
                    if (filename.length && !acceptFileTypes.test(filename)) {
                        $(".tip").text("<spring:message code='click_select_excel'/>");
                        layer.alert("請上傳後綴為.xlsx的文檔(Please upload a file with the suffix.xlsx)");
                        return;
                    }
                    <%--if (data.originalFiles[0]['size'] > 1024 * 1024 * 50) {--%>
                    <%--    $(".tip").text("<spring:message code='click_select_excel'/>");--%>
                    <%--    layer.alert("<spring:message code='not_exceed_50M'/>");--%>
                    <%--    return;--%>
                    <%--}--%>

                    $(".tip").text(filename);
                    $(".upload-tip").attr("title", filename);
                    $("#UploadTip").hide();
                    $("#FileUpload").click(function () {
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
                var queryCondition=$("#QueryCondition").serialize();
                queryCondition = decodeURIComponent(queryCondition,true);
                console.log(queryCondition+"查询条件");
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtHistoricalData/download",
                    async: true,
                    dataType: "json",
                    data: {queryCondition:queryCondition},
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
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/rtHistoricalData/template",
                    async: true,
                    dataType: "json",
                    data: {},
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
                var queryCondition=$("#QueryCondition").serialize();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/rtHistoricalData/list", {
                    queryCondition:decodeURIComponent(queryCondition)
                }, function () {
                    $("#loading").fadeOut(1000);
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
                    var obj = {no: data}
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/bi/rtHistoricalData/delete",
                        async: false,
                        dataType: "json",
                        data: obj,
                        success: function (data) {
                            if (data.flag == "success") {
                                $("#QueryBtn").click();
                            } else {
                                layer.alert(data.msg);
                            }
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            layer.alert("<spring:message code='connect_fail'/>");
                        }
                    });
                }
            });

            $("#FileUpload").click(function(){
                $("#UploadTip").show();
            });

            $("#ui-datepicker-div").remove();
            $("#Date").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yy-MM',
                showButtonPanel:true,
                closeText:"<spring:message code='confirm'/>"
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
        })
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><c:if test="${languageS eq 'zh_CN'}">手工版營收歷史數據表</c:if><c:if test="${languageS eq 'en_US'}">Handmade Historical Data</c:if></span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md">
            <div class="controls">
                <div>
                    <form id="poForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data"
                          class="form-horizontal">
                        <input type="file" style="display:none;" class="input-file" multiple="false"/>
                        <div>
                            <div style="float: left;text-align: right;">
                                <div class="upload-tip">
                                    <span class="tip"><spring:message code='click_select_excel'/></span>
                                </div>
                                <div id="UploadTip" style="display:none;float:left;">
                                    <span class="Validform_checktip Validform_wrong"><spring:message
                                            code='please_select'/></span>
                                </div>
                            </div>
                            <div style="padding-right:10px;">
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
            <div class="controls" style="display:inline-block;vertical-align:top;width:100%;margin-left:-20px;">
                <form id="QueryCondition" style="float:left;margin:0;">
                    <c:forEach items="${queryList}" var="query">
                        <c:if test="${query.key=='YEAR_MONTH'}">
                            <input id="Date" name='${query.key}'
                                   class='m-l-md' style='width:150px;'
                                   placeholder="<spring:message code='please_select'/>"
                                   type="text" value="" readonly>
                        </c:if>
                        <c:if test="${query.key!='YEAR_MONTH'}">
                            <input name='${query.key}' class='m-l-md' style='width:150px;'
                                   placeholder='${query.val}' type='text' value=''>
                        </c:if>
                    </c:forEach>
                    <div style="margin-left:20px;float:right;">
                        <button id="QueryBtn" class="btn search-btn btn-warning m-l-md"
                                type="button"><spring:message code='query'/></button>
                        <button id="deleteBtn" class="btn search-btn btn-warning m-l-md"
                                type="button"><spring:message code='delete'/></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>
