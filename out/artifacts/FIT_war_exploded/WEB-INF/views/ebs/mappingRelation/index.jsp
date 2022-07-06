<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@page import="foxconn.fit.entity.base.EnumDimensionName"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
	String entity=SecurityUtils.getEBS();
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
.ui-datepicker-calendar,.ui-datepicker-current{display:none;}
.ui-datepicker-close{float:none !important;}
.ui-datepicker-buttonpane{text-align: center;}
.table thead th{vertical-align: middle;}
</style>
<script type="text/javascript">
$(function() {
	$("#Download").click(function(){
		$("#UploadTip").hide();
		var flag=true;
		if($("input[name=entitys]:checked").length==0){
           	$("#EntitysTip").show();
           	flag=false;
        }
		var dimensionName=$("#dimensionName").val();
		if(dimensionName==""){
           	$("#DimensionTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		
		$("#loading").show();
		
		var entity="";
		$("input[name=entitys]:checked").each(function(i,dom){
			entity+=$(dom).val()+",";
		});
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/mappingRelation/download",
			async:true,
			dataType:"json",
			data:{entity:entity,dimensionName:dimensionName},
			success: function(data){
				$("#loading").hide();
				if(data.flag=="success"){
					window.location.href="${ctx}/static/download/"+data.fileName;
				}else{
					layer.alert(data.msg);
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		$("#loading").hide();
		   		window.location.href="${ctx}/logout";
		   	}
		});
	});
	
	$("#DownloadTemplate").click(function(){
		window.location.href="${ctx}/static/template/ebs/映射關係表.xlsx";
	});
	
	$("#dimensionName").change(function(){
		if($(this).val()!=""){
			$("#DimensionTip").hide();
		}
	});
	
	$(".AllCheck input").change(function(){
		var checked=$(this).is(":checked");
		$(this).parent().siblings().find("input").prop("checked",checked);
		if(!checked){
			$(this).parent().parent().parent().siblings().find("span").show();
		}else{
			$(this).parent().parent().parent().siblings().find("span").hide();
		}
	});

	$(".Check input").change(function(){
		var length=$(this).parent().siblings(".Check").find("input:checked").length+$(this).is(":checked");
		var total=$(this).parent().siblings(".Check").length+1;
		$(this).parent().siblings(".AllCheck").find("input").prop("checked",length==total);
		if(length>0){
			$(this).parent().parent().parent().siblings().find("span").hide();
		}else{
			$(this).parent().parent().parent().siblings().find("span").show();
		}
	});
	
	$("#Content").load("${ctx}/hfm/mappingRelation/list",{orderBy:"ENTITY,DIMNAME,SRCKEY",orderDir:"asc,asc,asc"});
	$("#QueryBtn").click(function(){
		clickPage(1);
	});
});
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="m-t-md m-r-md">
			<div class="controls">
               	<div class="m-l-md">
					<form id="mappingRelationForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
					    <input type="file" style="display:none;" class="input-file" multiple="false"/>
					    <div>
							<div style="float:left;padding-left:20px;">
								<ul class="nav dropdown">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='entity'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
											<li class="AllCheck" style="padding:0 10px;clear:both;">
												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>
											</li>
											<c:forEach items="${fn:split(entity,',') }" var="code">
												<c:if test="${not empty code and fn:substring(code,2,-1) eq 'EBS'}">
													<li class="Check" style="padding:0 10px;clear:both;">
														<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${fn:substring(code,2,-1)}</span>
														<input type="checkbox" name="entitys" style="font-size:15px;color:#7e8978;float:right;width:20px;" value="${fn:substring(code,2,-1)}"/>
													</li>
												</c:if>
											</c:forEach>
										</ul>
									</li>
									<li>
										<span id="EntitysTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
							</div>
							<div style="float: left;margin:0 10px 0 20px;">
							    <div>
							    	<select id="dimensionName" name="dimensionName" class="input-xlarge" style="width:160px;color:#9f9a9a;font-weight:bold;">
										<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='mappingCategory'/></option>
										<c:forEach items="<%=EnumDimensionName.values() %>" var="dimension">
											<option value="${dimension.code }">
												<c:choose>
													<c:when test="${locale eq 'en_US'}">${dimension.code }</c:when>
													<c:otherwise>${dimension.name }</c:otherwise>
												</c:choose>
											</option>
										</c:forEach>
									</select>
							    </div>
							    <div id="DimensionTip" style="display:none;">
									<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</div>
					    	</div>
							<div style="float:left;">
								<button id="Download" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='download'/></button>
							</div>
					    	<div style="float:left;text-align: center;display:none;margin-bottom:30px;">
							    <button id="DownloadTemplate" class="btn btn-link template" type="button"><spring:message code='template'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-sm" style="clear:both;">
	        <div class="controls" style="display:inline-block;vertical-align:top;width:100%;">
	        	<select id="QEntity" class="input-xlarge" style="width:160px;color:#9f9a9a;font-weight:bold;">
	           		<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='entity'/></option>
					<c:forEach items="${entityList }" var="entity">
						<option value="${entity }" >${entity }</option>
					</c:forEach>
				</select>
				<select id="QDimensionName" class="input-xlarge m-l-md" style="width:160px;color:#9f9a9a;font-weight:bold;">
					<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='mappingCategory'/></option>
					<c:forEach items="<%=EnumDimensionName.values() %>" var="dimension">
						<option value="${dimension.code }">
							<c:choose>
								<c:when test="${locale eq 'en_US'}">${dimension.code }</c:when>
								<c:otherwise>${dimension.name }</c:otherwise>
							</c:choose>
						</option>
					</c:forEach>
				</select>
				<input id="QSRCKEY" list="SRCKEYList" class="m-l-md" style="width:175px;" placeholder="<spring:message code='mappingSourceValue'/>" type="text" value="">
				<datalist id="SRCKEYList">
	           		<c:forEach items="${SRCKEYList }" var="SRCKEY">
	           			<option value="${SRCKEY }"/>
	           		</c:forEach>
	           	</datalist>
				<input id="QTARGKEY" list="TARGKEYList" class="m-l-md" style="width:170px;" placeholder="<spring:message code='mappingTargetValue'/>" type="text" value="">
				<datalist id="TARGKEYList">
	           		<c:forEach items="${TARGKEYList }" var="TARGKEY">
	           			<option value="${TARGKEY }"/>
	           		</c:forEach>
	           	</datalist>
	        	<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" type="button"><spring:message code='query'/></button>
	        </div>
		</div>
		<div class="p-l-md p-r-md p-b-md p-t-sm" id="Content"></div>
	</div>
</div>
</body>
</html>
