<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
    String entity=SecurityUtils.getEntity();
    request.setAttribute("entity", entity);

//    ArrayList<String> poCenters = new ArrayList<String>(Arrays.asList(SecurityUtils.getPoCenter()));
//    request.setAttribute("poCenters",poCenters);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <style type="text/css">
        .search-btn{
            height:40px;
            margin-left:10px;
            color:#ffffff;
            background-image: linear-gradient(to bottom, #fbb450, #f89406);
            background-color: #f89406 !important;
        }
        .ui-datepicker select.ui-datepicker-month{
            display: none;
        }
        .ui-datepicker-calendar,.ui-datepicker-current{
            display:none;
        }
        .ui-datepicker-close{float:none !important;}
        .ui-datepicker-buttonpane{text-align: center;}
        .table thead th{vertical-align: middle;}
        .table-condensed td{padding:7px 10px;}
    </style>
    <script type="text/javascript">
        $(function() {


            $("#ui-datepicker-div").remove();
            $("#Date,#QDate,#DateEnd").datepicker({
                changeMonth: true,
                changeYear: true,
                dateFormat: 'yy-MM',
                showButtonPanel:true,
                closeText:"<spring:message code='confirm'/>"
            });

            $("#Date,#QDate,#DateEnd").click(function(){
                periodId=$(this).attr("id");
                $(this).val("");
            });

            $("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
                var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
                $("#"+periodId).val(year);//给input赋值，其中要对月值加1才是实际的月份
                if($("#"+periodId+"Tip").length>0){
                    $("#"+periodId+"Tip").hide();
                }
            });







            $("#QueryBtn").click(function(){
                $("#QDateTip").hide();
                var flag=true;
                var tableName=$("#QTableName").val();
                var date=$("#QDate").val();
                if(date.length==0){
                    $("#QDateTip").show();
                    flag=false;
                }
                console.log(tableName)
                if($("#QTableName").val().length==0){
                    $("#QTableNameTip").show();
                    flag=false;
                }
                if(!flag){
                    return;
                }
                $("#QTableNameTip").hide();
                $("#PageNo").val(1);
                var date=$("#QDate").val();

                $("#loading").show();
                $("#Content").load("${ctx}/bi/poFlow/list",{date:date,tableName:tableName},function(){$("#loading").fadeOut(1000);});
            });
            $("#toTaskBtn").click(function () {

                var year=$("#QDate").val();
                var total=$("#total").val();
                var obj={
                    year:year
                }
                console.log(total);
                if(total==undefined||total=="0"){
                    alert("请先查询cpo表格数据")
                }else{
                    $.ajax({
                        type:"POST",
                        url:"${ctx}/bi/poTask/addCpo",
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
            })




        });
        var periodId;
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><spring:message code='poFlow'/></span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls">
                <ul style="float:left;">
                    <li>
                        <input id="QDate" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="<spring:message code='please_select'/><spring:message code='year'/>" type="text" value="" readonly>
                    </li>
                    <li style="height:30px;">
                        <span id="QDateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <select id="QTableName" class="input-large" style="width:200px;margin-bottom:0;">
                            <option  value=""><spring:message code='tableSelect'/></option>
                                <option value="FIT_PO_Target_CPO_CD_DTL">採購CD 目標核准表</option>
                        </select>
                    </li>
                    <li style="height:30px;">
                        <span id="QTableNameTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
                <button id="toTaskBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit">新建任务</button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>
