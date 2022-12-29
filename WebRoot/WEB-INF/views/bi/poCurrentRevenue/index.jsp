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
                url: "${ctx}/bi/poCurrentRevenue/upload",
                add: function (e, data) {
                    $("#FileUpload").unbind();
                    var filename = data.originalFiles[0]['name'];
                    var acceptFileTypes = /(\.|\/)(xlsx|XLSX)$/i;
                    if (filename.length && !acceptFileTypes.test(filename)) {
                        $(".tip").text("<spring:message code='click_select_excel'/>");
                        layer.alert("請上傳後綴為.xlsx的文檔(Please upload a file with the suffix.xlsx)");
                        return;
                    }
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
                    url: "${ctx}/bi/poCurrentRevenue/download",
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
                    url: "${ctx}/bi/poCurrentRevenue/template",
                    async: true,
                    dataType: "json",
                    success: function (data) {
                        $("#loading").hide();
                        if (data.flag == "success") {
                            window.location.href = "${ctx}/static/download/"+data.fileName;
                        } else {
                            layer.alert(data.msg);
                        }
                        $("#loading").hide();
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
                $("#Content").load("${ctx}/bi/poCurrentRevenue/list", {
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
                        url: "${ctx}/bi/poCurrentRevenue/delete",
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
            var month;
            var year;
            var periodId;
            $("#Date").click(function () {
                debugger;
                periodId = $(this).attr("id");
                $("#ui-datepicker-div .ui-datepicker-month option[value='"+month+"']").attr("selected",true);
                $("#ui-datepicker-div .ui-datepicker-year option[value='"+year+"']").attr("selected",true);
            });

            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
                month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
                year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
                $("#"+periodId).val(year+'-'+(parseInt(month)+1));//给input赋值，其中要对月值加1才是实际的月份
            });
            $("#Content").load("${ctx}/bi/poCurrentRevenue/list",{queryCondition:decodeURIComponent($("#QueryCondition").serialize())},function(){$("#loading").fadeOut(1000);});

        })
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><c:if test="${languageS eq 'zh_CN'}">非FIT體系當期收入表</c:if>
                    <c:if test="${languageS eq 'en_US'}">Non-fit income statement for the period</c:if></span>
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
                                <button id="FileUpload" style="float: left;" class="btn search-btn" type="button">
                                    <spring:message code='upload'/></button>
                                <button id="DownloadTemplate" class="btn btn-link"
                                        style="vertical-align: top;height: 40px;font-size: 26px;text-decoration: underline;"
                                        type="button"><spring:message code='template'/></button>
                                <button id="Download" style="float:left;margin-left: 10px;" class="btn search-btn" type="button">
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
                    <input id="Date" name='PERIOD' style="float:left;width:150px;text-align:center;" class='m-l-md' placeholder="<spring:message code='please_select'/>"
                           type="text" value="${PERIOD}" readonly>
                    <input name="BU" style="float:left;width:150px;text-align:center;margin-left:10px;" placeholder="BU" type="text">
                    <input name="SBU" style="float:left;width:150px;text-align:center;margin-left:10px;" placeholder="SBU" type="text">
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
