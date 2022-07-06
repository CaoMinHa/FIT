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
        .modal-backdrop {
            position: initial!important;
        }
    </style>

    <script type="text/javascript">
        $(function () {
            $("#Download").click(function () {
                $("#UploadTip").hide();
                var queryCondition=$("#QueryCondition").serialize();
                queryCondition = decodeURIComponent(queryCondition,true);
                console.log(queryCondition+"查询条件");
                $("#loading").show();
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/pmrPeerUpdates/download",
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
            $("#closeBut").click(function () {
                $("#DateAdd").val("");
                $("#peerUpdates").val("");
            })
            $("#affirmBut").click(function () {
                if(!$("#DateAdd").val()){
                    layer.alert("請選擇日期！(Please select a date)");
                }
                if(!$("#peerUpdates").val()){
                    layer.alert("Peer Updates不能爲空！(Peer's update cannot be null)");
                }
                $.ajax({
                    type: "POST",
                    url: "${ctx}/bi/pmrPeerUpdates/add",
                    async: true,
                    dataType: "json",
                    data: {dateAdd:$("#DateAdd").val(),peerUpdates:$("#peerUpdates").val()},
                    success: function (data) {
                        $("#loading").hide();
                        if (data.flag == "success") {
                            $("#DateAdd").val("");
                            $("#peerUpdates").val("");
                            $("#QueryBtn").click();
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

            $("#DeleteBtn").click(function () {
                var ids = $('input[name=checkboxID]');
                var data = '';
                ids.each(function () {
                    //获取当前元素的勾选状态
                    if ($(this).prop("checked")) {
                        data = data + $(this).val() + ",";
                    }
                });
                if (data === "") {
                    layer.msg("請勾選要刪除的數據！");
                } else {
                    data = data.substring(0, data.length - 1);
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/bi/pmrPeerUpdates/delete",
                        async: false,
                        dataType: "json",
                        data: {no: data},
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

            $("#QueryBtn").click(function () {
                var queryCondition=$("#QueryCondition").serialize();
                $("#PageNo").val(1);
                $("#loading").show();
                $("#Content").load("${ctx}/bi/pmrPeerUpdates/list", {
                    queryCondition:decodeURIComponent(queryCondition)
                }, function () {
                    $("#loading").fadeOut(1000);
                });
            });

            $("#ui-datepicker-div").remove();
            $("#Date,#DateAdd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yy-MM',
                showButtonPanel:true,
                closeText:"<spring:message code='confirm'/>"
            });

            $("#Date,#DateAdd").click(function () {
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
                <span>Peer Updates</span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls" style="display:inline-block;vertical-align:top;width:100%;margin-left:-20px;">
                <form id="QueryCondition" style="float:left;margin:0;">
                    <c:forEach items="${queryList}" var="query">
                        <c:if test="${query.key=='YEARS'}">
                            <input id="Date" name='${query.key}'
                                   class='m-l-md' style='width:150px;'
                                   placeholder="<spring:message code='please_select'/>"
                                   type="text" value="" readonly>
                        </c:if>
                        <c:if test="${query.key!='YEARS'}">
                            <input name='${query.key}' class='m-l-md' style='width:150px;'
                                   placeholder='${query.val}' type='text' value=''>
                        </c:if>
                    </c:forEach>
                    <div style="margin-left:20px;float:right;">
                        <button id="QueryBtn" class="btn search-btn btn-warning m-l-md"
                                type="button"><spring:message code='query'/></button>
                        <button  class="btn search-btn btn-warning m-l-md"  data-toggle="modal" data-target="#myModal" type="button">
                            <spring:message code='add'/></button>
                        <button id="DeleteBtn"  class="btn search-btn btn-warning m-l-md" type="button">
                            <spring:message code='delete'/></button>
                        <button id="Download"  class="btn search-btn btn-warning m-l-md" type="button">
                            <spring:message code='download'/></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md"  style="margin-top: 15px;" id="Content"></div>
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
                    Peer updates
                </h4>
            </div>
            <div class="modal-body">
                    <div>
                        <input id="DateAdd" name="addDate"
                               class='m-l-md' style='width:150px;margin: auto;'
                               placeholder="<spring:message code='please_select'/><spring:message code='month'/>"
                               type="text" value="" readonly>
                    </div>
                    <div style="margin-top: 20px;">
                        <label style="display: inline!important;">Peer Updates：</label>
                        <textarea style="width: 530px" id="peerUpdates" rows="10"></textarea>
                    </div>
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
