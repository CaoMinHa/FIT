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
        .ui-datepicker-calendar,.ui-datepicker-current{
            display:none;
        }
        .ui-datepicker-close{float:none !important;}
        .ui-datepicker-buttonpane{text-align: center;}
    </style>
    <script type="text/javascript">
        $(function () {
            $("#fileForm").fileupload({
                dataType: "json",
                url: "${ctx}/bi/rtRevenueGrossProfit/upload",
                add: function (e, data) {
                    $("#FileUpload").unbind();
                    debugger;
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
                    url: "${ctx}/bi/rtRevenueGrossProfit/download",
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
                    url: "${ctx}/bi/rtRevenueGrossProfit/template",
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
                $("#Content").load("${ctx}/bi/rtRevenueGrossProfit/list", {
                    queryCondition:decodeURIComponent(queryCondition)
                }, function () {
                    $("#loading").fadeOut(1000);
                });
            });

            $("#FileUpload").click(function(){
                $("#UploadTip").show();
            });

            $("#Content").load("${ctx}/bi/rtRevenueGrossProfit/list",{queryCondition:decodeURIComponent($("#QueryCondition").serialize())},function(){$("#loading").fadeOut(1000);});


            $("#ui-datepicker-div").remove();
            $("#Date").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yy-MM',
                showButtonPanel:true,
                closeText:"<spring:message code='confirm'/>"
            });
            $("#Date").click(function(){
                periodId=$(this).attr("id");
                $(this).val("");
            });

            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
                var month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
                var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
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
                <span><c:if test="${languageS eq 'zh_CN'}">手工版營收毛利</c:if>
                    <c:if test="${languageS eq 'en_US'}">Manual revenue gross profit</c:if></span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md">
            <div class="controls">
                <div>
                    <form id="fileForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data"
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
                    <input id="Date" name='YEAR_MONTH'
                           class='m-l-md' style='width:100px;float:left;'
                           placeholder="<spring:message code='please_select'/>"
                           type="text" value="" readonly>
                    <input name="SBU" style="float:left;width:100px;text-align:center;margin-left:10px;"  placeholder="SBU" type="text">
                    <input name="SEGMENT" style="float:left;width:100px;text-align:center;margin-left:10px;"  placeholder="Segment" type="text">
                    <div style="margin-left:20px;float:right;">
                        <button id="QueryBtn" class="btn search-btn btn-warning m-l-md"
                                type="button"><spring:message code='query'/></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>