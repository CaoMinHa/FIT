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
            $("#Query").click(function(){
                var name=$("#name").val();
               // var username=$("#username").val();
                $("#loading").show();
                $("#Content").load("${ctx}/bi/plUser/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
                    orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),name:name},function(){$("#loading").fadeOut(1000);});
            }).click();
            
            $("#AddSBU").click(function(){
        		var sbu=$("#SBU").val();
        		if(sbu.length<1){
        			return;
        		}
        		$("#SBU").val("");
        		var existSBU=$("#SBUTargetList input[sbu='"+sbu+"']");
        		if(sbu.length>0 && existSBU.length<=0){
        			$("#SBUTargetList").append($("#SBUTemplate").children().clone().show());
        			$("#SBUTargetList").find("input[name=sbu]:last").val(sbu).attr("sbu",sbu);
        		}
        	});
        	
        	$("#SBUTargetList").on("click","button",function(){
        		$(this).parent().remove();
        	});
        });

        var periodId;
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span><spring:message code='PLUser'/></span>
            </h2>
        </div>

        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
            <div class="controls">
                <ul style="float:left;">
                    <li>
                        <input id="name" style="float:left;width:140px;text-align:center;margin-bottom:0;margin-right:15px;" placeholder="請輸入查詢賬號" type="text">
                    </li>
                    <li style="float:left; height:70px;">
                        <span id="QTableNameTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
                    </li>
                </ul>
                 <button id="Query" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
            </div>
        </div>
        <div class="p-l-md p-r-md p-b-md" id="Content"></div>

    </div>
</div>
<div id="modal-user-add" style="display:none;">
		<form id="userForm" class="form-horizontal">
			<div class="control-group" style="margin-bottom:0;">
				<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i><spring:message code='username'/></label>
				<div class="controls">
					<div class="pull-left"><input name="username" style="height: 30px !important;" type="text" datatype="s3-30" nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/></div>
					<div class="Validform_checktip"></div>
				</div>
			</div>
			<div id="SBU_DIV" class="control-group" style="display: none;">
				<label class="control-label"><i title="<spring:message code='required'/>" datatype="sbu" nullmsg="<spring:message code='please'/><spring:message code='add'/>SBU"></i>年度预算法人损益表權限</label>
				<div class="controls">
					<div id="SBUTip" style="width:150px;display:none;float:none;">
						<span ><spring:message code='please'/><spring:message code='add'/>年度预算法人损益表權限</span>
					</div>
					<div>
						<select id="SBU">
							<option value="">请选择LEGAL</option>
			           		<c:forEach items="${sbuList }" var="sbu">
			           			<option value="${sbu }">${sbu }</option>
			           		</c:forEach>
			           	</select>
			           	<button id="AddSBU" type="button" style="margin-left:30px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
					</div>
					<div id="SBUTargetList" class="m-t-md">
					</div>
				</div>
			</div>
		</form>
</div>
	
	<div id="SBUTemplate">
		<div style="display:none;" class="m-b-sm">
			<input type="text" name="sbu" class="sbu" value="" sbu="" readonly="readonly"/>
			<button style="margin-left:45px;" class="btn btn-small btn-danger" type="button"><spring:message code='delete'/></button>
		</div>
	</div>
</body>
</html>
