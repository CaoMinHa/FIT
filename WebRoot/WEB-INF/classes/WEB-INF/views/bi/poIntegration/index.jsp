<%@page import="foxconn.fit.entity.base.EnumGenerateType" %>
<%@page import="foxconn.fit.util.SecurityUtils" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglibs.jsp" %>
<%
    String entity = SecurityUtils.getEntity();
    request.setAttribute("entity", entity);

//	ArrayList<String> poCenters = new ArrayList<String>(Arrays.asList(SecurityUtils.getPoCenter()));
//	request.setAttribute("poCenters",poCenters);

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
    </style>
    <script type="text/javascript">
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
                        if ($("#Date").val().length == 0) {
                            $("#DateTip").show();
                            return;
                        }
                        // if($("#poCenter").val().length==0){
                        // 	$("#poCenterTip").show();
                        // 	return;
                        // }
                        if ($("input[name=tableNames]:checked").length == 0) {
                            $("#TableNamesTip").show();
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
                //$("#DEntityTip").hide();
                $("#UploadTip").show();
                if ($("#Date").val().length == 0) {
                    $("#DateTip").show();
                }
                if ($("input[name=tableNames]:checked").length == 0) {
                    $("#TableNamesTip").show();
                }
                if ($("#poCenter").val().length == 0) {
                    $("#poCenterTip").show();
                }
            });

            $(".upload-tip").click(function () {
                $(".input-file").trigger("click");
            });

            $("#Download").click(function () {
                $("#UploadTip").hide();
                $("#UEntityTip").hide();
                $("#poCenterTip").hide();
                $("#TableNamesTip").hide();
                var flag = true;
                var date = $("#Date").val();
                var dateEnd = $("#DateEnd").val();
                if (date.length == 0) {
                    $("#DateTip").show();
                    flag = false;
                }
                /*var DEntity=$("#DEntity").val();
                if(DEntity.length==0){
                       $("#DEntityTip").show();
                       flag=false;
                }*/
                var entity=$("#poCenter").val();
                if ($("input[name=tableNamesOut]:checked").length == 0) {
                    $("#TableNamesTipX").show();
                    flag = false;
                }
                if (!flag) {
                    return;
                }

                var tableNames = "";
                $("input[name=tableNamesOut]:checked").each(function (i, dom) {
                    tableNames += $(dom).val() + ",";
                });
                tableNames = tableNames.substring(0, tableNames.length - 1);
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegration/download",
                    async: true,
                    dataType: "json",
                    data: {date: date,
                           dateEnd: dateEnd,
                           tableNames: tableNames,
                           entity:entity},
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

            $("#CheckValidation").click(function () {
                $("#UploadTip").hide();
                $("#poCenterTip").hide();
                var flag = true;
                var date = $("#Date").val();
                if (date.length == 0) {
                    $("#DateTip").show();
                    flag = false;
                }
                /*var DEntity=$("#DEntity").val();
                if(DEntity.length==0){
                       $("#DEntityTip").show();
                       flag=false;
                }*/
                if ($("input[name=tableNames]:checked").length == 0) {
                    $("#TableNamesTip").show();
                    flag = false;
                }
                if (!flag) {
                    return;
                }

                var tableNames = "";
                $("input[name=tableNames]:checked").each(function (i, dom) {
                    tableNames += $(dom).val() + ",";
                });
                tableNames = tableNames.substring(0, tableNames.length - 1);
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegration/check",
                    async: true,
                    dataType: "json",
                    data: {date: date, tableNames: tableNames},
                    success: function (data) {
                        $("#loading").hide();
                        if (data.msg.length <= 100) {
                            layer.alert(data.msg);
                        } else {
                            $("#MessageDialog").html(data.msg);
                            $("#MessageDialog").dialog({
                                modal: true,
                                title: "信息",
                                height: 500,
                                width: "auto",
                                draggable: true,
                                resizable: true,
                                autoOpen: true,
                                autofocus: true,
                                closeText: "<spring:message code='close'/>",
                                buttons: [
                                    {
                                        text: "<spring:message code='close'/>",
                                        click: function () {
                                            $(this).dialog("destroy");
                                        }
                                    }
                                ],
                                close: function () {
                                    $(this).dialog("destroy");
                                }
                            });
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        $("#loading").hide();
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            });

            $("#CheckAllValidation").click(function () {
                $("#QTableNameTip").hide();
                $("#QpoCenterTip").hide();
                var date = $("#QDate").val();
                if (date.length == 0) {
                    $("#QDateTip").show();
                } else {
                    $("#QDateTip").hide();

                    $("#loading").show();
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/bi/poIntegration/checkAll",
                        async: true,
                        dataType: "json",
                        data: {date: date},
                        success: function (data) {
                            $("#loading").hide();
                            $("#MessageDialog").html(data.msg);
                            $("#MessageDialog").dialog({
                                modal: true,
                                title: "信息",
                                height: 500,
                                width: "auto",
                                draggable: true,
                                resizable: true,
                                autoOpen: true,
                                autofocus: true,
                                closeText: "<spring:message code='close'/>",
                                buttons: [
                                    {
                                        text: "<spring:message code='close'/>",
                                        click: function () {
                                            $(this).dialog("destroy");
                                        }
                                    }
                                ],
                                close: function () {
                                    $(this).dialog("destroy");
                                }
                            });
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            $("#loading").hide();
                            layer.alert("<spring:message code='connect_fail'/>");
                        }
                    });
                }
            });

            $("#DownloadTemplate").click(function () {
                $("#UploadTip").hide();
                $("#DateTip").hide();
                $("#poCenterTip").hide();
                $("#TableNamesTipX").hide();
                if ($("input[name=tableNames]:checked").length == 0) {
                    $("#TableNamesTip").show();
                    return;
                }
                var tableNames = "";
                $("input[name=tableNames]:checked").each(function (i, dom) {
                    tableNames += $(dom).val() + ",";
                });
                tableNames = tableNames.substring(0, tableNames.length - 1);
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegration/template",
                    async: true,
                    dataType: "json",
                    data: {tableNames: tableNames},
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
                dateFormat: 'yy-MM',
                showButtonPanel: true,
                closeText: "<spring:message code='confirm'/>"
            });

            $("#Date,#QDate,#DateEnd").click(function () {
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
                $("#QDateTip").hide();
                var flag = true;
                var tableName = $("#QTableName").val();
                var date = $("#QDate").val();
                if (date.length == 0) {
                    $("#QDateTip").show();
                    flag = false;
                }
                if ($("#QTableName").val().length == 0) {
                    $("#QTableNameTip").show();
                    flag = false;
                }
                var entity = $("#QpoCenter").val();
                var fuzzy = $("#fuzzy").val();
                if (!flag) {
                    return;
                }
                $("#QTableNameTip").hide();
                $("#QpoCenterTip").hide();
                $("#PageNo").val(1);
                var date = $("#QDate").val();
                $("#loading").show();
                $("#Content").load("${ctx}/bi/poIntegration/list", {
                    date: date,
                    tableName: tableName,
                    poCenter: entity,
                    fuzzy: fuzzy
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
                    url: "${ctx}/bi/poIntegration/consolidationDataDownload",
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

            $("#DataImport").click(function () {
                $("#UploadTip").hide();
                /*	$("#DateTip").hide();
                    $("#UEntityTip").hide();*/
                $("#TableNamesTip").hide();
                //	$("#DEntityTip").hide();
                var flag = true;
                var date = $("#Date").val();
                if (date.length == 0) {
                    $("#DateTip").show();
                    flag = false;
                }
                /*var entity=$("#UEntity").val();
                if(entity.length==0){
                    $("#UEntityTip").show();
                    flag=false;
                }*/
                if (!flag) {
                    return;
                }
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/poIntegration/dataImport",
                    async: true,
                    dataType: "json",
                    data: {date: date},
                    success: function (data) {
                        $("#loading").hide();
                        layer.alert(data.msg);
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        $("#loading").hide();
                        window.location.href = "${ctx}/logout";
                    }
                });
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
                alert("請勾選要刪除的數據！");
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
                    url:"${ctx}/bi/poIntegration/delete",
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
        var periodId;
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
                            <div style="float:left;margin-left:15px;display:inline-block;">
                                <div>
                                    <input id="Date" name="date" style="width:70px;text-align:center;"
                                           placeholder="<spring:message code='please_select'/><spring:message code='month'/>"
                                           type="text" value="" readonly>
                                    <input id="DateEnd" name="dateEnd" style="width:70px;text-align:center;"
                                           placeholder="<spring:message code='please_select'/><spring:message code='month'/>"
                                           type="text" value="" readonly>
                                </div>
                                <div style="float:left;">
                                    <span id="DateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message
                                            code='please_select'/></span>
                                </div>
                            </div>
                            <div style="float:left;padding-right:10px;">
                                        <ul style="float:left;margin-left:10px;">
                                            <li>
                                                <select id="dataRange" name="dataRange" class="input-large"
                                                        style="width:140px;">
                                                    <option value="">職能數據範圍</option>
                                                    <c:forEach items="${dataRange}" var="code">
                                                        <option value="<spring:message code='${code}'/>">${code}</option>
                                                    </c:forEach>
                                                </select>
                                                <input id="dataRangeStr" style="display:none" value="${dataRange}">
                                            </li>
                                        </ul>
                                <ul style="float:left;margin-left:10px;">
                                    <li>
                                        <select id="poCenter" name="poCenter" class="input-large" style="width:140px;">
                                            <option value=""><spring:message code='poCenter'/></option>
                                            <c:forEach items="${poCenters}" var="code">
                                                <%-- <option value="${code}">${code}-<spring:message code="${code}"/></option>--%>
                                                <%--												 <option value="<spring:message code='${code}'/>">${code}-<spring:message code="${code}"/></option>--%>
                                                <option value="${code}">${code}</option>
                                            </c:forEach>
                                        </select>
                                    </li>
                                    <li>
                                        <span id="poCenterTip" style="display:none;"
                                              class="Validform_checktip Validform_wrong"><spring:message
                                                code='please_select'/></span>
                                    </li>
                                </ul>
                                <%--<ul style="float:left;margin-left:10px;">
                                    <li>
                                        <select id="UEntity" name="entity" class="input-large" style="width:100px;">
                                               <option value=""><spring:message code='entity'/></option>
                                            <c:forEach items="${fn:split(entity,',') }" var="code">
                                                <c:if test="${fn:startsWith(code,'F_') }">
                                                    <option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
                                                </c:if>
                                            </c:forEach>
                                        </select>
                                    </li>
                                    <li>
                                        <span id="UEntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                                    </li>
                                </ul>--%>
                                <%--
                                                                <button id="DataImport" style="float:left;" class="btn search-btn" type="button"><spring:message code='dataImport'/></button>
                                --%>
                                <ul class="nav dropdown" style="float:left;margin-left:10px;">
                                    <li class="dropdown" style="margin-top:0;">
                                        <a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message
                                                code='tableSelect'/><strong class="caret"></strong></a>
                                        <ul class="dropdown-menu"
                                            style="left:-20%;max-height:350px;overflow-y:scroll;min-width:330px;">
                                            <%--											<li class="AllCheck" style="padding:0 10px;clear:both;">--%>
                                            <%--												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>--%>
                                            <%--												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>--%>
                                            <%--											</li>--%>
                                            <c:forEach items="${poTableList }" var="poTable">
                                                <li class="Check" style="padding:0 10px;clear:both;">
                                                    <span style="font-size:15px;color:#7e8978;float:left;line-height:38px;display:contents;">${poTable.comments }</span>
                                                    <input type="radio" name="tableNames"
                                                           style="font-size:15px;color:#7e8978;float:right;width:20px;"
                                                           value="${poTable.tableName}"/>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                    <li>
                                        <span id="TableNamesTip" style="display:none;"
                                              class="Validform_checktip Validform_wrong"><spring:message
                                                code='please_select'/></span>
                                    </li>
                                </ul>
                                <button id="FileUpload" style="float:left;" class="btn search-btn" type="button">
                                    <spring:message code='upload'/></button>

                                <button id="DownloadTemplate" class="btn btn-link"
                                        style="vertical-align: top;height: 40px;font-size: 26px;text-decoration: underline;"
                                        type="button"><spring:message code='template'/></button>

                            </div>


                        </div>
                        <div style="clear: both;margin-left: 608px;padding-right:10px;">
                            <%--<ul style="float:left;">
                                <li>
                                    <select id="DEntity" class="input-large" style="width:100px;">
                                           <option value=""><spring:message code='entity'/></option>
                                        <c:forEach items="${fn:split(entity,',') }" var="code">
                                            <c:if test="${not empty code }">
                                                <option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                                </li>
                                <li>
                                    <span id="DEntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                                </li>
                            </ul>--%>
                            <ul class="nav dropdown" style="float:left;margin-left:10px;">
                                <li class="dropdown" style="margin-top:0;">
                                    <a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message
                                            code='tableSelect'/><strong class="caret"></strong></a>
                                    <ul class="dropdown-menu"
                                        style="left:-20%;max-height:350px;overflow-y:scroll;min-width:330px;">
                                        <%--										<li class="AllCheck" style="padding:0 10px;clear:both;">--%>
                                        <%--											<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>--%>
                                        <%--											<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>--%>
                                        <%--										</li>--%>
                                        <c:forEach items="${poTableOutList }" var="poTableOut">
                                            <li class="Check" style="padding:0 10px;clear:both;">
                                                <span style="font-size:15px;color:#7e8978;float:left;line-height:38px;display:contents;">${poTableOut.comments }</span>
                                                <input type="radio" name="tableNamesOut"
                                                       style="font-size:15px;color:#7e8978;float:right;width:20px;"
                                                       value="${poTableOut.tableName}"/>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </li>
                                <li>
                                    <span id="TableNamesTipX" style="display:none;"
                                          class="Validform_checktip Validform_wrong"><spring:message
                                            code='please_select'/></span>
                                </li>
                            </ul>
                            <button id="Download" style="float:left;" class="btn search-btn" type="button">
                                <spring:message code='download'/></button>
                            <%--<button id="CheckValidation" style="float:left;" class="btn search-btn" type="button"><spring:message code='checkValidation'/></button>--%>
                        </div>
                    </form>
                </div>
            </div>
        </div>


        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls">
                <ul style="float:left;">
                    <li>
                        <input id="QDate" style="float:left;width:140px;text-align:center;margin-bottom:0;"
                               placeholder="<spring:message code='please_select'/><spring:message code='month'/>"
                               type="text" value="" readonly>
                    </li>
                    <li style="height:30px;">
                        <span id="QDateTip" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <ul style="float:left;margin-left:10px;">
                    <li>
                        <select id="QpoCenter" name="QpoCenter" class="input-large" style="width:140px;">
                            <option value=""><spring:message code='poCenter'/></option>
                            <c:forEach items="${poCenters}" var="code">
                                <%-- <option value="${code}">${code}-<spring:message code="${code}"/></option>--%>
                                <option value="${code}">${code}</option>
                            </c:forEach>
                        </select>
                    </li>
                    <li>
                        <span id="QpoCenterTip" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <%--<ul style="float:left;margin-left:20px;">
                    <li>
                        <select id="QEntity" class="input-large" style="width:100px;margin-bottom:0;">
                               <option value=""><spring:message code='entity'/></option>
                            <c:forEach items="${fn:split(entity,',') }" var="code">
                                <c:if test="${not empty code }">
                                    <option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </li>
                    <li style="height:30px;">
                        <span id="QEntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>--%>
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <input type="text" id="fuzzy" placeholder="请输入sbu">
                    </li>
                </ul>
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <select id="QTableName" class="input-large" style="width:200px;margin-bottom:0;">
                            <option value=""><spring:message code='tableSelect'/></option>
                            <c:forEach items="${poTableList }" var="poTable">
                                <option value="${poTable.tableName }">${poTable.comments }</option>
                            </c:forEach>
                        </select>
                    </li>
                    <li style="height:30px;">
                        <span id="QTableNameTip" style="display:none;"
                              class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>

                <button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;"
                        type="submit"><spring:message code='query'/></button>
                <c:if test="${hasKey eq '1'}">
                <button id="deleteBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;"
                        type="submit"><spring:message code='delete'/></button>
                </c:if>
                <c:if test="${attribute eq 'group'}">
                    <button id="CheckAllValidation" class="btn search-btn btn-warning m-l-md"
                            style="margin-left:20px;float:left;" type="button"><spring:message
                            code='checkAllValidation'/></button>
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
