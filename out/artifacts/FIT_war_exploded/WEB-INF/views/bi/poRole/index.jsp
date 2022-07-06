<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
    String entity=SecurityUtils.getEntity();
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
            $(".AllCheck input").change(function(){
                var checked=$(this).is(":checked");
                $(this).parent().siblings().find("input").prop("checked",checked);
                if(!checked){
                    $(this).parent().parent().parent().siblings().find("span").show();
                }else{
                    $(this).parent().parent().parent().siblings().find("span").hide();
                }
            });

            $("#user").hide();
            $("#QueryBtn").click(function(){

                var name=$("#name").val();
                $("#loading").show();
                $("#Content").load("${ctx}/bi/poRole/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
                    orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),name:name},function(){$("#loading").fadeOut(1000);});
            }).click();
        });
        $("#addBtn").click(function(){
            $("#modal-user-add").dialog({
                modal:true,
                title: "添加角色",
                height:400,
                width:350,
                position:"center",
                draggable: true,
                resizable: true,
                autoOpen:false,
                autofocus:false,
                closeText:"<spring:message code='close'/>",
                buttons: [
                    {
                        text: "<spring:message code='submit'/>",
                        click: function() {
                            var $dialog=$(this);
                            var flag=true;
                            var roleName=$("#roleName").val();
                            if(roleName.length==0){
                                $("#rolenameTip").show();
                                flag=false;
                            }
                            if(!flag){
                                return;
                            }
                                var data=$("#userForm").serialize();
                                console.log(data)

                                $.ajax({
                                    type:"POST",
                                    url:"${ctx}/bi/poRole/add",
                                    async:false,
                                    dataType:"json",
                                    data:data,
                                    success: function(data){
                                        if(data.flag=="success"){
                                            $dialog.dialog("destroy");
                                            refresh();
                                        }else{
                                            layer.alert(data.msg);
                                        }
                                    },
                                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                                        layer.alert("<spring:message code='connect_fail'/>");
                                    }
                                });
                            }
                    },
                    {
                        text: "<spring:message code='close'/>",
                        click: function() {
                            $(this).dialog("destroy");
                            $("#rolenameTip").hide();
                        }
                    }
                ],
                close:function(){
                    $(this).dialog("destroy");
                    $("#rolenameTip").hide();
                }
            }).dialog("open");
        });

        $('#deleteAllBtn').click(function () {
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
                var obj={
                    id:data
                }
                $.ajax({
                    type:"POST",
                    url:"${ctx}/bi/poRole/delete",
                    async:false,
                    dataType:"json",
                    data:obj,
                    success: function(data){
                        if(data.flag=="success"){
                            refresh();
                        }else{
                            layer.alert(data.msg);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            }
            //去最后的点

        })

        <%--//到用戶界面去--%>
        <%--$("a.userList").click(function(){--%>
        <%--    $("#role").hide();--%>
        <%--    $("#user").show();--%>
        <%--    $("#Content").load("${ctx}/bi/poAudit/userList",{pageNo:"1",pageSize:"5"},function(){$("#loading").fadeOut(1000);});--%>
        <%--});--%>

        //返回
        $('#backBtn').click(function () {
            $("#user").hide();
            $("#role").show();
            $("#Content").load("${ctx}/bi/poRole/list",{pageNo:"1",pageSize:"10"},function(){$("#loading").fadeOut(1000);});
        })

        $("#Query").click(function(){
            $("#loading").show();
            $("#Content").load("${ctx}/bi/poRole/userList",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
                orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),name:$("#name2").val(),hasRole:$("#hasRole").val(),
                id:$("#roleId2").val(),
                roleName:$("#roleName2").val()},function(){$("#loading").fadeOut(1000);});
        });
        $('#addRoleBtn').click(function () {
            var ids = $('input[type=checkbox]');
            var data = '';
            ids.each(function () {
                //获取当前元素的勾选状态
                if ($(this).prop("checked")) {
                    data = data + $(this).val() + ",";
                }
            });
            if(data===""){
                alert("請勾選要要分配角色的數據！");
            }else{
                data = data.substring(0, data.length - 1);
                console.log(data)
                var obj={
                    userId:data,
                    roleId:$("#roleId2").val()
                }
                $.ajax({
                    type:"POST",
                    url:"${ctx}/bi/poRole/addUserRole",
                    async:false,
                    dataType:"json",
                    data:obj,
                    success: function(data){
                        if(data.flag=="success"){
                            refresh();
                        }else{
                            layer.alert(data.msg);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        layer.alert("<spring:message code='connect_fail'/>");
                    }
                });
            }
            //去最后的点

        })

        function addPerm(index){
            var id = $('input[type=checkbox]')[index].value;
            var obj={id:id};
            $.ajax({
                type:"POST",
                url:"${ctx}/bi/poRole/findPerms",
                async:false,
                dataType:"json",
                data:obj,
                success: function(data){
                    if(data.flag=="fail"){
                        alert(data.msg)
                    }else{
                        $("#allCheck").html("")
                        var list=data.poTableList;
                        var list1=data.poTableList1;
                        console.error(list);
                        console.error(list1);
                        var html='';
                        for(var j=0;j<list1.length;j++){
                            html+='<li class="Check" style="padding:0 10px;clear:both;">'
                            html+='<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;width:100%">'
                            for (var i=0;i<list.length;i++){
                                if(list1[j]==list[i].name){
                                    switch (list[i].type) {
                                        case "1":
                                            html+=list[i].comment;
                                            html+='<dev style="float:right;">';
                                            if(list[i].flag=="1"){
                                                html+='<font style="color: #7e8978;font-size:14px;">下載：</font><input type="checkbox" name="tableName" checked value="'+list[i].roleId+'">&nbsp;'
                                            }else{
                                                html+='<font style="color: #7e8978;font-size:14px;">下載：</font><input type="checkbox" name="tableName"  value="'+list[i].roleId+'">&nbsp;'
                                            }
                                            break;
                                        case "2":
                                            if(list[i].flag=="1"){
                                                html+='<font style="color: #7e8978;font-size:14px;">上傳：</font><input type="checkbox" name="tableName" checked value="'+list[i].roleId+'">&nbsp;'
                                            }else{
                                                html+='<font style="color: #7e8978;font-size:14px;">上傳：</font><input type="checkbox" name="tableName"  value="'+list[i].roleId+'">&nbsp;'
                                            }
                                            break;
                                        case "3":
                                            if(list[i].flag=="1"){
                                                html+='<font style="color: #7e8978;font-size:14px;">查詢：</font><input type="checkbox" name="tableName" checked value="'+list[i].roleId+'">&nbsp;'
                                            }else{
                                                html+='<font style="color: #7e8978;font-size:14px;">查詢：</font><input type="checkbox" name="tableName"  value="'+list[i].roleId+'">&nbsp;'
                                            }
                                            break;
                                        case "4":
                                            if(list[i].flag=="1"){
                                                html+='<font style="color: #7e8978;font-size:14px;">創建任務：</font><input type="checkbox" name="tableName" checked value="'+list[i].roleId+'">&nbsp;'
                                            }else{
                                                html+='<font style="color: #7e8978;font-size:14px;">創建任務：</font><input type="checkbox" name="tableName"  value="'+list[i].roleId+'">&nbsp;'
                                            }
                                            break;
                                    }
                                }
                            }
                            html+='</dev>';
                            html+='</span>';
                            html+='</li>'
                        }
                        $("#allCheck").append(html);
                    $("#modal-perm-add").dialog({
                            modal:true,
                            title: "添加角色数据采集权限",
                            height:350,
                            width:600,
                            position:"center",
                            draggable: true,
                            resizable: true,
                            autoOpen:false,
                            autofocus:false,
                            closeText:"<spring:message code='close'/>",
                            buttons: [
                                {
                                    text: "<spring:message code='submit'/>",
                                    click: function() {
                                        var that=$(this);
                                        var tableNames="";
                                        $("input[name=tableName]:checked").each(function(i,dom){
                                            tableNames+=$(dom).val()+",";
                                        });
                                        tableNames=tableNames.substring(0,tableNames.length-1);
                                        var data={
                                            id:id,
                                            perms:tableNames
                                        }
                                        $.ajax({
                                            type:"POST",
                                            url:"${ctx}/bi/poRole/updatePerms",
                                            async:false,
                                            dataType:"json",
                                            data:data,
                                            success: function(data){
                                                that.dialog("destroy");
                                                layer.alert(data.msg);
                                            },
                                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                                layer.alert("<spring:message code='connect_fail'/>");
                                            }
                                        });
                                    }
                                },
                                {
                                    text: "<spring:message code='close'/>",
                                    click: function() {
                                        $(this).dialog("destroy");

                                    }
                                }
                            ],
                            close:function(){
                                $(this).dialog("destroy");

                            }
                        }).dialog("open");
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    layer.alert("<spring:message code='connect_fail'/>");
                }
            });
        }
        var periodId;
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span>角色模塊</span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls" id="role">

                <ul style="float:left;">
                    <li>
                        <input id="name" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="請輸入查詢名稱" type="text">
                    </li>
                    <li style="float:left; height:70px;">
                        <span id="QTableNameTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
                <button id="addBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit">新增</button>
<%--                <button id="deleteAllBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit">批量刪除</button>--%>
            </div>
            <div class="controls" id="user">
                <ul style="float:left;">
                    <li>
                        <select id="hasRole" class="input-large"  style="width:200px;margin-bottom:0;">
                            <option value="0" selected="selected">未分配</option>
                            <option value="1">已分配</option>
                        </select>
                    </li>
                    <li style="height:30px;">
                        <span id="QTableNameTip2" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                <ul style="float:left;margin-left:20px;">
                    <li>
                        <input id="name2" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="請輸入查詢名稱" type="text">
                    </li>
                </ul>
                <button id="Query" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
                <button id="addRoleBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit">批量分配</button>
                <button id="backBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit">返回</button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>
        <div id="modal-user-add" style="display:none;">
            <form id="userForm" class="form-horizontal">
                <input id="id" name="id" style="display: none;"/>
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <div class="pull-left">
                        <i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
                        名称：<input id="roleName" name="rolename" style="height: 30px !important;" type="text" datatype="s3-30" nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/>
                        <span id="rolenameTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </div>
                </div>
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <div class="pull-left">
                        <i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
                        状态：<select id="flag" name="flag" style="width:100px" datatype="*" nullmsg="<spring:message code='please_select'/>">
                                <option value="1">正常</option>
                                <option value="0">禁用</option>
                              </select>
                    </div>
                </div>
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <div class="pull-left" style="margin-left: 40px">
                        备注：<input name="remark" style="height: 30px !important;" type="text" datatype="s3-30" nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/>
                    </div>
                </div>
            </form>
        </div>

        <div id="modal-perm-add" style="display:none;">
            <form id="permForm" class="form-horizontal">
                <div class="control-group" style="margin:0px 0px 20px 10px;">
                    <ul  style="left:-20%;">
                        <li class="AllCheck" style="padding:0 10px;clear:both;" id="allCheck">
                            <span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
                            <input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>
                        </li>
<%--                        <c:forEach items="${poTableList }" var="poTable">--%>
<%--                            <li class="Check" style="padding:0 10px;clear:both;">--%>
<%--                                <span style="font-size:15px;color:#7e8978;float:left;line-height:38px;display:contents;">${poTable.comments }</span>--%>
<%--                                <input type="checkbox" name="tableName" style="font-size:15px;color:#7e8978;float:right;width:20px;" value="${poTable.tableName}"/>--%>
<%--                            </li>--%>
<%--                        </c:forEach>--%>
                    </ul>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
