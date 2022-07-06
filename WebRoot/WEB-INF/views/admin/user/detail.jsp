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
			"menu":function(gets,obj,curform,datatype){
				if($("#userEditForm input[name=menus]:checked").length>0){
					$("#MenuEditTip").hide();
					return true;
				}
				$("#MenuEditTip").show();

				return false;
			},
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
	
	$("#CorporationEditList input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
	$("#CorporationEditList #EditCorporation").click(function(){
		$("#CorporationEditList input[type=checkbox][data-toggle=toggle]").bootstrapToggle("destroy");
		$("#CorporationEditList").append($("#CorporationTemplate").children().clone().show());
		$("#CorporationEditList").find("input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
		userEditFormValid.config();
	});
	$("#CorporationEditList").on("click",".js_delete",function(){
		$(this).parent().parent().remove();
	});
	
	$("#EBSEditList input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
	$("#EBSEditList #EditEBS").click(function(){
		$("#EBSEditList input[type=checkbox][data-toggle=toggle]").bootstrapToggle("destroy");
		$("#EBSEditList").append($("#EBSTemplate").children().clone().show());
		$("#EBSEditList").find("input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
		userEditFormValid.config();
	});
	$("#EBSEditList").on("click",".js_delete",function(){
		$(this).parent().parent().remove();
	});
	
	$("#EntityEditList input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
	$("#EntityEditList #EditEntity").click(function(){
		$("#EntityEditList input[type=checkbox][data-toggle=toggle]").bootstrapToggle("destroy");
		$("#EntityEditList").append($("#EntityTemplate").children().clone().show());
		$("#EntityEditList").find("input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
		userEditFormValid.config();
	});
	$("#EntityEditList").on("click",".js_delete",function(){
		$(this).parent().parent().remove();
	});
	
	if($("#userEditForm input[name=menus]:checked").length>0){
		$("#MenuEditTip").hide();
	}
	
	$("#userEditForm input[name=menus]").change(function(){
		if($("#userEditForm input[name=menus]:checked").length>0){
			$("#MenuEditTip").hide();
		}else{
			$("#MenuEditTip").show();
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
	// if(userEditFormValid.check()){
		var data=$("#userEditForm").serialize();
		$("#userEditForm input[name=updatable]").each(function(i){
			data+="&readonly="+$(this).prop('checked');
		});
		$("#userEditForm input[name=ebsUpdatable]").each(function(i){
			data+="&ebsReadonly="+$(this).prop('checked');
		});
		$("#userEditForm input[name=entityUpdatable]").each(function(i){
			data+="&entityReadonly="+$(this).prop('checked');
		});
		
		$.ajax({
			type:"POST",
			url:"${ctx}/admin/user/update",
			async:true,
			dataType:"json",
			data:data,
			success: function(data){
				if(data.flag=="success"){
					$("#modal-user-edit").dialog("destroy");
					refresh();
				}else{
					layer.alert(data.msg);
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		layer.alert("<spring:message code='connect_fail'/>");
		   	}
		});
	// }
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
				<input name="username" type="text" datatype="s3-30" value="${user.username }" nullmsg="<spring:message code='please_input'/>" errormsg="<spring:message code='s3_30'/>"/>
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
	<div class="control-group">
		<label class="control-label">
			Tiptop法人編碼
		</label>
		<div class="controls">
			<div id="CorporationEditList" style="margin-left:0;" class="controls">
				<div>
					<button id="EditCorporation" style="margin-left:50px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
				</div>
				<c:forEach items="${codeList }" var="code">
					<div style="margin-top:20px;">
						<div>
							<input name="corporationCode" type="text" value="${fn:substring(code, 2, -1)}" class="corporation" datatype="*" placeHolder="<spring:message code='please_input'/>" errormsg="<spring:message code='not_null'/>" nullmsg="<spring:message code='please_input'/>"/>
					 		<input name="updatable" type="checkbox" <c:if test="${fn:substring(code, 0, 1) eq 'T'}">checked="true"</c:if> data-toggle="toggle" data-on="<spring:message code='readonly'/>" data-off="<spring:message code='updatable'/>" data-onstyle="success" data-offstyle="danger">
					 		<button style="margin-right:50px;float:right;" class="btn btn-small btn-danger js_delete" type="button"><spring:message code='delete'/></button>
						</div>
						<div class="Validform_checktip"></div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">
			EBS法人編碼
		</label>
		<div class="controls">
			<div id="EBSEditList" style="margin-left:0;" class="controls">
				<div>
					<button id="EditEBS" style="margin-left:50px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
				</div>
				<c:forEach items="${ebsList }" var="code">
					<div style="margin-top:20px;">
						<div>
							<input name="ebs" type="text" value="${fn:substring(code, 2, -1)}" class="corporation" datatype="*" placeHolder="<spring:message code='please_input'/>" errormsg="<spring:message code='not_null'/>" nullmsg="<spring:message code='please_input'/>"/>
					 		<input name="ebsUpdatable" type="checkbox" <c:if test="${fn:substring(code, 0, 1) eq 'T'}">checked="true"</c:if> data-toggle="toggle" data-on="<spring:message code='readonly'/>" data-off="<spring:message code='updatable'/>" data-onstyle="success" data-offstyle="danger">
					 		<button style="margin-right:50px;float:right;" class="btn btn-small btn-danger js_delete" type="button"><spring:message code='delete'/></button>
						</div>
						<div class="Validform_checktip"></div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<div class="control-group" style="margin-bottom:0;">
		<label class="control-label">HFM用户属性</label>
		<div class="controls">
			<div class="pull-left">
				<input id="single" name="attribute" type="radio" style="width:20px;" type="text" value="single" <c:if test="${user.attribute eq 'single'}">checked="checked"</c:if>/>
				<label for="single" style="display:inline-block;">单体用户</label>
				<input id="group" name="attribute" type="radio" style="width:20px;margin-left:30px;" type="text" value="group" <c:if test="${user.attribute eq 'group'}">checked="checked"</c:if>/>
				<label for="group" style="display:inline-block;">集团用户</label>
			</div>
		</div>
	</div>
	<div class="control-group" style="margin-bottom:0;">
		<label class="control-label">
			HFM公司编码
		</label>
		<div class="controls">
			<div id="EntityEditList" style="margin-left:0;" class="controls">
				<div>
					<button id="EditEntity" style="margin-left:50px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
				</div>
				<c:forEach items="${entityList }" var="entity">
					<div style="margin-top:20px;">
						<div>
							<input name="entity" type="text" value="${fn:substring(entity, 2, -1)}" class="corporation" datatype="*" placeHolder="<spring:message code='please_input'/>" errormsg="<spring:message code='not_null'/>" nullmsg="<spring:message code='please_input'/>"/>
					 		<input name="entityUpdatable" type="checkbox" <c:if test="${fn:substring(entity, 0, 1) eq 'T'}">checked="true"</c:if> data-toggle="toggle" data-on="<spring:message code='readonly'/>" data-off="<spring:message code='updatable'/>" data-onstyle="success" data-offstyle="danger">
					 		<button style="margin-right:50px;float:right;" class="btn btn-small btn-danger js_delete" type="button"><spring:message code='delete'/></button>
						</div>
						<div class="Validform_checktip"></div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<c:if test="${user.type.code eq 'BI'}">
		<div class="control-group">
			<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>" datatype="menu" nullmsg="<spring:message code='please_select'/>"></i><spring:message code='menu'/></label>
			<div class="controls">
				<div id="MenuEditTip" style="width:150px;float:none;" class="Validform_checktip">
					<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
				</div>
				<div class="pull-left">
<%--					<c:forEach items="<%=EnumMenu.values() %>" var="menu">--%>
<%--						<c:set var="checked" value=""/>--%>
<%--						<c:forEach items="${menus }" var="umenu">--%>
<%--							<c:if test="${menu.code eq umenu }"><c:set var="checked" value="checked"/></c:if>--%>
<%--						</c:forEach>--%>
<%--						<input id="M_${menu.code }" type="checkbox" name="menus" ${checked } style="width:20px;" value="${menu.code }"/>--%>
<%--						<label for="M_${menu.code }" class="MLabel"><spring:message code='${menu.code }'/></label>--%>
<%--						<br>--%>
<%--					</c:forEach>--%>
<%--					--%>
					<c:forEach items="${menuList}" var="menu">
						<c:set var="checked" value=""/>
						<c:forEach items="${menus }" var="umenu">
							<c:if test="${menu.menuCode eq umenu }"><c:set var="checked" value="checked"/></c:if>
						</c:forEach>
						<input id="M_${menu.menuCode }" type="checkbox" name="menus" ${checked } style="width:20px;" value="${menu.menuCode }"/>
						<label for="M_${menu.menuCode }" class="MLabel">
							<c:choose>
								<c:when test="${languageS == 'en_US'}">
									${menu.menuNameE }
								</c:when>
								<c:when test="${languageS == 'zh_CN'}">
									${menu.menuName }
								</c:when>
							</c:choose>
						</label>
						<br>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:if>
	<c:if test="${user.type.code eq 'Budget'}">
		<div class="control-group">
			<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>" datatype="menu" nullmsg="<spring:message code='please_select'/>"></i><spring:message code='menu'/></label>
			<div class="controls">
				<div id="MenuEditTip" style="width:150px;float:none;" class="Validform_checktip">
					<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
				</div>
				<div class="pull-left">
					<c:forEach items="<%=EnumBudgetMenu.values() %>" var="menu">
						<c:set var="checked" value=""/>
						<c:forEach items="${menus }" var="umenu">
							<c:if test="${menu.code eq umenu }"><c:set var="checked" value="checked"/></c:if>
						</c:forEach>
						<input id="M_${menu.code }" type="checkbox" name="menus" ${checked } style="width:20px;" value="${menu.code }"/>
						<label for="M_${menu.code }" class="MLabel"><spring:message code='${menu.code }'/></label>
						<br>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>" datatype="sbu" nullmsg="<spring:message code='please'/><spring:message code='add'/>SBU"></i>SBU</label>
			<div class="controls">
				<div id="SBUEditTip" style="width:150px;display:none;float:none;" class="Validform_checktip">
					<span class="Validform_checktip Validform_wrong"><spring:message code='please'/><spring:message code='add'/>SBU</span>
				</div>
				<div>
		           	<select id="SBU_Input">
							<option value="">请选择SBU</option>
			           		<c:forEach items="${sbuList }" var="sbu">
			           			<option value="${sbu }">${sbu }</option>
			           		</c:forEach>
			           	</select>
		           	<button id="EditSBU" type="button" style="margin-left:30px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
				</div>
				<div id="SBUEditTargetList" class="m-t-md">
					<c:forEach items="${fn:split(user.corporationCode,',') }" var="u_sbu">
						<div class="m-b-sm">
							<input type="text" name="sbu" class="sbu" value="${u_sbu }" sbu="${u_sbu }" readonly="readonly"/>
							<button style="margin-left:45px;" class="btn btn-small btn-danger" type="button"><spring:message code='delete'/></button>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:if>
</form>
</body>
</html>