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
                url: "${ctx}/bi/plMapping/upload",
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
                        if(!$("#tableType").val()){
                            layer.msg("請選擇數據類型！(Please select the data type.)")
                            return;
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
                }
            });

            $(".upload-tip").click(function () {
                $(".input-file").trigger("click");
            });

            $("#Download").click(function () {
                if(!$("#tableType").val()){
                    layer.msg("請選擇數據類型！(Please select the data type.)")
                    return;
                }
                $("#UploadTip").hide();
                var queryCondition=$("#QueryCondition").serialize();
                queryCondition = decodeURIComponent(queryCondition,true);
                console.log(queryCondition+"查询条件");
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/plMapping/download",
                    async: true,
                    dataType: "json",
                    data: {queryCondition:queryCondition,tableName:$("#tableType").val()},
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
                if(!$("#tableType").val()){
                    layer.msg("請選擇數據類型！(Please select the data type.)")
                    return;
                }
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/plMapping/template",
                    async: true,
                    dataType: "json",
                    data: {tableName:$("#tableType").val()},
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
                if(!$("#tableType").val()){
                    layer.msg("請選擇數據類型！(Please select the data type.)")
                    return;
                }
                var queryCondition=$("#QueryCondition").serialize();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/plMapping/list", {
                    queryCondition:decodeURIComponent(queryCondition),
                    type:$("#tableType").val()
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
                    var obj = {no: data,tableName:$("#tableType").val()}
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/bi/plMapping/delete",
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
                if(!$("#tableType").val()){
                    layer.msg("請選擇數據類型！(Please select the data type.)")
                    return;
                }
                $("#UploadTip").show();
            });

            $("#tableType").change(function () {
                $("#Date").hide();
                $("#loading").show();
                $("#queryInput").empty();
                $.ajax({
                    type:"POST",
                    url:"${ctx}/bi/plMapping/query",
                    async:true,
                    dataType:"json",
                    data: {type: $("#tableType :checked").val()},
                    success: function(data){
                        if(data.flag=="success"){
                            $.each(data.queryList, function(i, n){
                                $("#queryInput").append("<input name='"+n["COLUMN_NAME"]+"' class='m-l-md' style='width:150px;' placeholder='"+n["COMMENTS"]+"' type='text' value=''>");
                            });
                            $("#QueryBtn").click();
                        }else{
                            layer.alert(data.msg);
                            $("#loading").hide();
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $("#loading").hide();
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            })
        })
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><c:if test="${languageS eq 'zh_CN'}">三表映射表</c:if><c:if test="${languageS eq 'en_US'}">Mapping table</c:if></span>
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
                                <select id="tableType" name="tableName" class="input-large" style="width:200px;float:left;margin-left:23px;" >
                                    <option value=""><c:if test="${languageS eq 'zh_CN'}">請選擇數據類型</c:if><c:if test="${languageS eq 'en_US'}">Please select the data type</c:if></option>
                                    <option value="epmebs.CUX_BI_COMPANY_CODE"><c:if test="${languageS eq 'zh_CN'}">Entity編碼（FIT體系）</c:if><c:if test="${languageS eq 'en_US'}">Entity Code (FIT system)</c:if></option>
                                    <option value="epmebs.CUX_FIT_SBU_CODE"><c:if test="${languageS eq 'zh_CN'}">SBU代碼</c:if><c:if test="${languageS eq 'en_US'}">SBU Code</c:if></option>
                                </select>
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
                    <span id="queryInput"></span>
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
