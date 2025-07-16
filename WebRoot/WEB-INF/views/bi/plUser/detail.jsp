<%@page import="foxconn.fit.entity.base.EnumBudgetMenu"%>
<%@page import="foxconn.fit.entity.base.EnumMenu"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<head>
<style type="text/css">
.corporation{
	width:200px;height:30px !important;
}
</style>
<script type="text/javascript">
$(function() {
	userEditFormValid=$("#userEditForm").Validform({
		tiptype:2,
		datatype:{
			"sbu":function(gets,obj,curform,datatype){
				if($("#userEditForm input[name=sbu]").length>0){
					$("#SBUEditTip").hide();
					return true;
				}
				$("#SBUEditTip").show();

				return false;
			}
		}
	});

	
	$("#userEditForm input[name=sbu]").change(function(){
		if($("#userEditForm input[name=sbu]:checked").length>0){
			$("#SBUEditTip").hide();
		}else{
			$("#SBUEditTip").show();
		}
	});
	
	$("#EditSBU").click(function(){
		var sbu=$("#SBU_Input").val();
		if(sbu.length<1){
			return;
		}
		$("#SBU_Input").val("");
		var existSBU=$("#SBUEditTargetList input[sbu='"+sbu+"']");
		if(sbu.length>0 && existSBU.length<=0){
			$("#SBUEditTargetList").append($("#SBUTemplate").children().clone().show());
			$("#SBUEditTargetList").find("input[name=sbu]:last").val(sbu).attr("sbu",sbu);
		}
	});
	
	$("#SBUEditTargetList").on("click","button",function(){
		$(this).parent().remove();
	});
});
var userEditFormValid;

function userEditFormSubmit(){
	
		var data=$("#userEditForm").serialize();
/*
		$("#userEditForm input[name=sbu]").each(function(i){
			data+="&sbu="+$(this).prop('checked');
			
		});*/
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/plUser/update2",
			async:true,
			dataType:"json",
			data:data,
			success: function(data){
				if(data.flag=="success"){
					$("#modal-user-edit").dialog("destroy");
					$("button[title='關閉']").click();
					layer.alert(data.msg);
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
</script>
</head>
<body>
<form id="userEditForm" class="form-horizontal">
	<div class="control-group">
		<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i><spring:message code='username'/></label>
		<div class="controls">
			<div class="pull-left">
				<input type="hidden" name="id" value="${user.id }"/>
				<input name="username" type="text" datatype="s3-30" value="${user.username }" readonly="readonly" nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/>
			</div>
			
		</div>
	</div>
	<div class="control-group">
		<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>用戶姓名</label>
		<div class="controls">
			<div class="pull-left">
				<input name="realname" type="text" datatype="s3-30" value="${user.realname }"  nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/>
			</div>
			<div class="Validform_checktip"></div>	
		</div>
	</div>
	
	<div class="control-group">
		<label class="control-label"><spring:message code='user_type'/></label>
		<div class="controls">
			<div class="pull-left">
				<input type="text" value="${user.type.name }" readonly="readonly"/>
			</div>
		</div>
	</div>
	<c:if test="${user.type.code eq 'BI'}">
	   <div class="control-group">
			<label class="control-label"><i title="<spring:message code='required'/>" datatype="sbu" nullmsg="<spring:message code='please'/><spring:message code='add'/>SBU"></i>年度预算法人损益表權限</label>
			<div class="controls">
				<div id="SBUEditTip" style="width:150px;display:none;float:none;" class="Validform_checktip">
					<span class="Validform_checktip Validform_wrong"><spring:message code='please'/><spring:message code='add'/>年度预算法人损益表權限</span>
				</div>
				<div>
		           	<select id="SBU_Input">
							<option value="">请选择LEGAL</option>
			           		<c:forEach items="${entityList }" var="entity">
			           			<option value="${entity }">${entity }</option>
			           		</c:forEach>
			           	</select>
		           	<button id="EditSBU" type="button" style="margin-left:30px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
				</div>
				<div id="SBUEditTargetList" class="m-t-md">
				
					<c:forEach items="${fn:split(legalList,',') }" var="u_sbu">
						<div class="m-b-sm">
							<c:if test="${u_sbu !=''}">
							<input type="text" name="sbu" class="sbu" value="${u_sbu }" sbu="${u_sbu }" readonly="readonly"/>
							
							<button style="margin-left:45px;" class="btn btn-small btn-danger" type="button"><spring:message code='delete'/></button>
							</c:if>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:if>
</form>
</body>
</html>