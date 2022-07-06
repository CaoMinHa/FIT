<%@page import="foxconn.fit.entity.base.EnumBudgetMenu"%>
<%@page import="foxconn.fit.entity.base.EnumMenu"%>
<%@page import="foxconn.fit.entity.base.EnumUserType"%>
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
<style type="text/css">
.corporation{width:200px;height:30px !important;}
.MLabel{display: inline-block;font-size: 18px;font-family: serif;}
.sbu{width: 200px;height: 30px !important;}
</style>
<script type="text/javascript">
$(function() {
	var userFormValid;
	
	$("a.btnAdd").click(function(){
		$("input[type=checkbox][data-toggle=toggle]").bootstrapToggle("destroy");
		
		$("#userForm")[0].reset();
		$("#CorporationList").children(":gt(0)").remove();
		$("#EBSList").children(":gt(0)").remove();
		$("#EntityList").children(":gt(0)").remove();
		$("#Menu_DIV").hide();
		$("#userForm select[name=type]").trigger("change");
		
		userFormValid=$("#userForm").Validform({
			tiptype:2,
			ignoreHidden:true,
			datatype:{
				"menu":function(gets,obj,curform,datatype){
					if($("#userForm input[name=menus]:checked").length>0){
						$("#MenuTip").hide();
						return true;
					}
					$("#MenuTip").show();
					
					return false;
				},
				"sbu":function(gets,obj,curform,datatype){
					if($("#userForm input[name=sbu]").length>0){
						$("#SBUTip").hide();
						return true;
					}
					$("#SBUTip").show();
					
					return false;
				}
			}
		});
		
		$("#modal-user-add").dialog({
			modal:true,
			title: "<spring:message code='add_user'/>",
			height:650,
			width:700,
			position:"top",
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
		            	if(userFormValid.check()){
		            		var data=$("#userForm").serialize();
		            		$("#userForm input[name=updatable]").each(function(i){
		            			data+="&readonly="+$(this).prop('checked');
		            		});
		            		$("#userForm input[name=ebsUpdatable]").each(function(i){
		            			data+="&ebsReadonly="+$(this).prop('checked');
		            		});
		            		$("#userForm input[name=entityUpdatable]").each(function(i){
		            			data+="&entityReadonly="+$(this).prop('checked');
		            		});
		            		
		            		$.ajax({
		        				type:"POST",
		        				url:"${ctx}/admin/user/add",
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
	});
	
	$("#Content").load("${ctx}/admin/user/list");
	
	$("button.search-btn").click(function(){
		clickPage(1);
	});
	
	$("#CorporationList #AddCorporation").click(function(){
		$("#CorporationList").append($("#CorporationTemplate").children().clone().show());
		$("#CorporationList").find("input[type=checkbox][data-toggle=toggle]:last").bootstrapToggle();
		userFormValid.config();
	});
	
	$("#EBSList #AddEBS").click(function(){
		$("#EBSList").append($("#EBSTemplate").children().clone().show());
		$("#EBSList").find("input[type=checkbox][data-toggle=toggle]:last").bootstrapToggle();
		userFormValid.config();
	});
	
	$("#CorporationList").on("click",".js_delete",function(){
		$(this).parent().parent().remove();
	});
	$("#EBSList").on("click",".js_delete",function(){
		$(this).parent().parent().remove();
	});
	
	$("#EntityList #AddEntity").click(function(){
		$("#EntityList").append($("#EntityTemplate").children().clone().show());
		$("#EntityList").find("input[type=checkbox][data-toggle=toggle]:last").bootstrapToggle();
		userFormValid.config();
	});
	
	$("#EntityList").on("click",".js_delete",function(){
		$(this).parent().parent().remove();
	});
	
	$("#userForm select[name=type]").change(function(){
		var type=$(this).val();
		if("<%=EnumUserType.HFM.getCode()%>"==type){
			$("#Menu_DIV").hide();
			$("#SBU_DIV").hide();
			$("#Attribute_Div").show();
			$("#CORP_CODE").show();
			$("#EBS_CODE").show();
			$("#Entity_CODE").show();
			$("#ReferenceUser").show();
			$("#CopyUser").show();
			$("#ReferenceUser>option[userType="+type+"]").show();
			$("#ReferenceUser>option[userType!="+type+"]").hide();
			$("#ReferenceUser>option:eq(0)").show();
		}else if("<%=EnumUserType.BI.getCode()%>"==type){
			$("#Attribute_Div").hide();
			$("#SBU_DIV").hide();
			$("#EBS_CODE").hide();
			$("#Entity_CODE").hide();
			$("#CORP_CODE").show();
			$("#ReferenceUser").show();
			$("#CopyUser").show();
			$("#BiMenu").show();
			$("#BudgetMenu").hide();
			$("#Menu_DIV").show();
			$("#ReferenceUser>option[userType="+type+"]").show();
			$("#ReferenceUser>option[userType!="+type+"]").hide();
			$("#ReferenceUser>option:eq(0)").show();
		}else{
			$("#Attribute_Div").hide();
			$("#CORP_CODE").hide();
			$("#EBS_CODE").hide();
			$("#Entity_CODE").hide();
			$("#ReferenceUser").hide();
			$("#CopyUser").hide();
			$("#BiMenu").hide();
			$("#BudgetMenu").show();
			$("#Menu_DIV").show();
			$("#SBU_DIV").show();
		}
	});
	
	$("#userForm input[name=menus]").change(function(){
		if($("#userForm input[name=menus]:checked").length>0){
			$("#MenuTip").hide();
		}else{
			$("#MenuTip").show();
		}
	});
	
	$("#userForm input[name=sbu]").change(function(){
		if($("#userForm input[name=sbu]:checked").length>0){
			$("#SBUTip").hide();
		}else{
			$("#SBUTip").show();
		}
	});
	
	$("#CopyUser").click(function(){
		var userId=$("#ReferenceUser").val();;
		if(userId==""){
			layer.alert("请先选择参考用户");
		}else{
			$.ajax({
				type:"POST",
				url:"${ctx}/admin/user/getUserInfo",
				async:true,
				dataType:"json",
				data:{userId:userId},
				success: function(data){
					if(data.flag=="success"){
						$("#CorporationList").children(":gt(0)").remove();
						$.each(data.corpCodes, function(i, n){
							$("#CorporationList").append($("#CorporationTemplate").children().clone().show());
							$("#CorporationList").find("input[name=corporationCode]:last").val(n.substring(2));
							var readonly=n.substring(0,1);
							if(readonly=="F"){
								$("#CorporationList").find("input[type=checkbox][data-toggle=toggle]:last").bootstrapToggle();
								$("#CorporationList").find("div.toggle-group:last").trigger("click");
							}else{
								$("#CorporationList").find("input[type=checkbox][data-toggle=toggle]:last").bootstrapToggle();
							}
						});
						userFormValid.config();
					}else{
						layer.alert(data.msg);
					}
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		}
	});
	
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
</script>
</head>
<body>
	<div class="content">
		<div class="container-fluid">
			<div class="row-fluid bg-white">
				<div class="span12">
					<div class="page-header bg-white">
						<h2><span><spring:message code='user_list'/></span></h2>
					</div>
					<div class="m-l-md m-t-md m-r-md">
                    <div class="controls">
                       <input id="QUsername" type="text" placeholder="<spring:message code='username'/>" class="input-xlarge" style="width: 240px;">
                       <input id="QCorporationCode" type="text" placeholder="<spring:message code='corp_code'/>" class="input-xlarge" style="width: 240px;">
                       <select id="QType" style="width: 240px;height:38px;">
                       		<option value=""><spring:message code='user_type'/></option>
                       		<c:forEach items="<%=EnumUserType.values() %>" var="type">
								<c:if test="${type.code ne 'Admin'}">
									<option value="${type.code }">${type.name }</option>
								</c:if>
							</c:forEach>
                       </select>
                       <button class="btn search-btn btn-warning m-l-md" type="submit"><spring:message code='query'/></button>
                    </div>
					<a class="btn btn-primary btn-large btnAdd pull-right" role="button"><div class="divAdd"><spring:message code='add_user'/></div></a></div>		
					<div class="p-l-md p-t-md p-r-md p-b-md" id="Content"></div>
				</div>
			</div>
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
			<div class="control-group" style="margin-bottom:0;">
				<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i><spring:message code='user_type'/></label>
				<div class="controls">
					<div class="pull-left">
						<select name="type" style="width:100px" datatype="*" nullmsg="<spring:message code='please_select'/>">
							<c:forEach items="<%=EnumUserType.values() %>" var="type">
								<c:if test="${type.code ne 'Admin'}">
									<option value="${type.code }">${type.name }</option>
								</c:if>
							</c:forEach>
						</select>
						<select id="ReferenceUser" style="width:120px;background:#faebd7;">
							<option value="">参考用户</option>
							<c:forEach items="${userList }" var="user">
								<option value="${user[0] }" userType="${user[1] }">${user[2] }</option>
							</c:forEach>
						</select>
						<button id="CopyUser" style="background:#faebd7;" class="btn" type="button">复制</button>
					</div>
					<div class="Validform_checktip"></div>
				</div>
			</div>
			<div id="CORP_CODE" class="control-group">
				<label class="control-label"><span>Tiptop<spring:message code='corp_code'/></span></label>
				<div class="controls">
					<div id="CorporationList" style="margin-left:0;" class="controls">
						<div>
							<button id="AddCorporation" type="button" style="margin-left:50px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
						</div>
					</div>
				</div>
			</div>
			<div id="EBS_CODE" class="control-group" style="margin-bottom:0;">
				<label class="control-label"><span>EBS公司编码</span></label>
				<div class="controls">
					<div id="EBSList" style="margin-left:0;" class="controls">
						<div>
							<button id="AddEBS" type="button" style="margin-left:50px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
						</div>
					</div>
				</div>
			</div>
			<div id="Attribute_Div" class="control-group" style="margin-bottom:0;">
				<label class="control-label">HFM用户属性</label>
				<div class="controls">
					<div class="pull-left">
						<input id="single" name="attribute" type="radio" style="width:20px;" type="text" value="single"/>
						<label for="single" style="display:inline-block;">单体用户</label>
						<input id="group" name="attribute" type="radio" style="width:20px;margin-left:30px;" type="text" value="group"/>
						<label for="group" style="display:inline-block;">集团用户</label>
					</div>
				</div>
			</div>
			<div id="Entity_CODE" class="control-group" style="margin-bottom:0;">
				<label class="control-label"><span>HFM公司编码</span></label>
				<div class="controls">
					<div id="EntityList" style="margin-left:0;" class="controls">
						<div>
							<button id="AddEntity" type="button" style="margin-left:50px;" class="btn btn-inverse btn-small" type="button"><spring:message code='add'/></button>
						</div>
					</div>
				</div>
			</div>
			<div id="Menu_DIV" class="control-group" style="display: none;">
				<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>" datatype="menu" nullmsg="<spring:message code='please_select'/>"></i><spring:message code='menu'/></label>
				<div class="controls">
					<div id="MenuTip" style="width:150px;display:none;float:none;" class="Validform_checktip">
						<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
					</div>
					<div id="BiMenu" class="pull-left" style="display: none;">
<%--						<c:forEach items="<%=EnumMenu.values() %>" var="menu">--%>
<%--							<input id="M_${menu.code }" type="checkbox" name="menus" style="width:20px;" value="${menu.code }"/>--%>
<%--							<label for="M_${menu.code }" class="MLabel"><spring:message code='${menu.code }'/></label>--%>
<%--							<br>--%>
<%--						</c:forEach>--%>

<c:forEach items="${menuList}" var="menu">
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
					<div id="BudgetMenu" class="pull-left" style="display: none;">
						<c:forEach items="<%=EnumBudgetMenu.values() %>" var="menu">
							<input id="M_${menu.code }" type="checkbox" name="menus" style="width:20px;" value="${menu.code }"/>
							<label for="M_${menu.code }" class="MLabel"><spring:message code='${menu.code }'/></label>
							<br>
						</c:forEach>
					</div>
				</div>
			</div>
			<div id="SBU_DIV" class="control-group" style="display: none;">
				<label class="control-label"><i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>" datatype="sbu" nullmsg="<spring:message code='please'/><spring:message code='add'/>SBU"></i>SBU</label>
				<div class="controls">
					<div id="SBUTip" style="width:150px;display:none;float:none;" class="Validform_checktip">
						<span class="Validform_checktip Validform_wrong"><spring:message code='please'/><spring:message code='add'/>SBU</span>
					</div>
					<div>
						<select id="SBU">
							<option value="">请选择SBU</option>
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
	<div id="CorporationTemplate" >
		<div style="display:none;margin-top:20px;">
			<div>
				<input name="corporationCode" type="text" class="corporation" datatype="*" placeHolder="<spring:message code='please_input'/>" errormsg="<spring:message code='not_null'/>" nullmsg="<spring:message code='please_input'/>"/>
		 		<input name="updatable" type="checkbox" checked="true" data-toggle="toggle" data-on="<spring:message code='readonly'/>" data-off="<spring:message code='updatable'/>" data-onstyle="success" data-offstyle="danger">
		 		<button style="margin-right:50px;float:right;" class="btn btn-small btn-danger js_delete" type="button"><spring:message code='delete'/></button>
			</div>
			<div class="Validform_checktip"></div>
		</div>
	</div>
	<div id="EBSTemplate" >
		<div style="display:none;margin-top:20px;">
			<div>
				<input name="ebs" type="text" class="corporation" datatype="*" placeHolder="<spring:message code='please_input'/>" errormsg="<spring:message code='not_null'/>" nullmsg="<spring:message code='please_input'/>"/>
		 		<input name="ebsUpdatable" type="checkbox" checked="true" data-toggle="toggle" data-on="<spring:message code='readonly'/>" data-off="<spring:message code='updatable'/>" data-onstyle="success" data-offstyle="danger">
		 		<button style="margin-right:50px;float:right;" class="btn btn-small btn-danger js_delete" type="button"><spring:message code='delete'/></button>
			</div>
			<div class="Validform_checktip"></div>
		</div>
	</div>
	<div id="EntityTemplate" >
		<div style="display:none;margin-top:20px;">
			<div>
				<input name="entity" type="text" class="corporation" datatype="*" placeHolder="<spring:message code='please_input'/>" errormsg="<spring:message code='not_null'/>" nullmsg="<spring:message code='please_input'/>"/>
		 		<input name="entityUpdatable" type="checkbox" checked="true" data-toggle="toggle" data-on="<spring:message code='readonly'/>" data-off="<spring:message code='updatable'/>" data-onstyle="success" data-offstyle="danger">
		 		<button style="margin-right:50px;float:right;" class="btn btn-small btn-danger js_delete" type="button"><spring:message code='delete'/></button>
			</div>
			<div class="Validform_checktip"></div>
		</div>
	</div>
	<div id="SBUTemplate">
		<div style="display:none;" class="m-b-sm">
			<input type="text" name="sbu" class="sbu" value="" sbu="" readonly="readonly"/>
			<button style="margin-left:45px;" class="btn btn-small btn-danger" type="button"><spring:message code='delete'/></button>
		</div>
	</div>
</body>
</html>
