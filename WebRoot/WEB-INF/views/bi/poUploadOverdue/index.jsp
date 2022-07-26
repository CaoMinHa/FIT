<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <script type="text/javascript">
        $(function() {
            $("#Query").click(function(){
                var queryCondition=$("#QueryCondition").serialize();
                $("#loading").show();
                $("#Content").load("${ctx}/bi/poUploadOverdue/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),query:decodeURIComponent(queryCondition)},function(){$("#loading").fadeOut(1000);});
            }).click();
        });
    function stateBut(type) {
        if($(".userID:checked").length==0){
            layer.msg("請勾選需要分配的用戶！")
            return;
        }
        var userId="";
        $(".userID:checked").each(function(i,dom){
            userId+="'"+$(dom).val()+"',";
        });
        $.ajax({
            type:"POST",
            url:"${ctx}/bi/poUploadOverdue/allocate",
            async:false,
            dataType:"json",
            data:{userId:userId.substring(0,userId.length-1),type:type},
            success: function(data){
                if(data.flag=="success"){
                    $("#Query").trigger("click");
                }else{
                    layer.alert(data.msg);
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                layer.alert("<spring:message code='connect_fail'/>");
            }
        });
    }
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span>用戶模塊</span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls">
                <form id="QueryCondition" style="float:left;margin:0;">
                    <input name="username" style="float:left;width:140px;text-align:center;margin-left:28px;" placeholder="賬號/姓名查詢" type="text">
                    <input name="SBU" style="float:left;width:140px;text-align:center;margin-left:20px;" placeholder="SBU查詢" type="text">
                    <input name="COMMODITY_MAJOR" style="float:left;width:140px;text-align:center;margin-left:20px;" placeholder="Commodity查詢" type="text">
                    <select name="state" class="input-large" style="width:140px;margin-left:20px;">
                        <option value="">請選擇狀態</option>
                        <option value="Y">Y</option>
                        <option value="N">N</option>
                    </select>
                </form>
                <button id="Query" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;"><spring:message code='query'/></button>
                <button id="stateY" onclick="stateBut('Y')" class="btn search-btn btn-warning m-l-md" type="button" >分配</button>
                <button id="stateN" onclick="stateBut('N')" class="btn search-btn btn-warning m-l-md" type="button" >取消</button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
    </div>
</div>
</body>
</html>
