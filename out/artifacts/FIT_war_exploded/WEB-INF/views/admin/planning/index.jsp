<%@page import="foxconn.fit.entity.base.EnumBudgetVersion"%>
<%@page import="foxconn.fit.entity.base.EnumDimensionType"%>
<%@page import="foxconn.fit.entity.base.EnumScenarios"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
	String corporationCode=SecurityUtils.getCorporationCode();
	request.setAttribute("corporationCode", corporationCode);
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
	margin:0 20%;
	color:#ffffff;
	background-image: linear-gradient(to bottom, #fbb450, #f89406);
	background-color: #f89406 !important;
}

.ui-datepicker-calendar,.ui-datepicker-current{
	display:none;
}
.ui-datepicker-close{float:none !important;}
.ui-datepicker-buttonpane{text-align: center;}
.table thead th{vertical-align: middle;}
</style>
<script type="text/javascript">
$(function() {
	$(".AllCheck input").change(function(){
		var checked=$(this).is(":checked");
		$(this).parent().siblings().find("input").prop("checked",checked);
	});

	$(".Check input").change(function(){
		var length=$(this).parent().siblings(".Check").find("input:checked").length+$(this).is(":checked");
		var total=$(this).parent().siblings(".Check").length+1;
		$(this).parent().siblings(".AllCheck").find("input").prop("checked",length==total);
	});
    
	$("#PlanningScenarios").change(function(){
		if(($(this).val()=="<%=EnumScenarios.Forecast.getCode()%>") || ($(this).val()=="<%=EnumScenarios.Actual.getCode()%>")){
			$("#PlanningPeriodTip").show();
			$("#DIV_PlanningPeriod").show();
		}else{
			$("#DIV_PlanningPeriod").hide();
		}
	});
	
    $("#DownloadPlanning").click(function(){
    	$("#PlanningEntitysTip").hide();
    	$("#PlanningYearTip").hide();
    	$("#PlanningScenariosTip").hide();
    	$("#PlanningPeriodTip").hide();
    	$("#VersionTip").hide();
    	var flag=true;
    	if($("input[name=planningEntitys]:checked").length==0){
           	$("#PlanningEntitysTip").show();
           	flag=false;
        }
    	var year=$("#PlanningYear").val();
    	if(year==""){
    		$("#PlanningYearTip").show();
    		flag=false;
    	}
    	var scenarios=$("#PlanningScenarios").val();
    	var period="";
    	if(scenarios==""){
    		$("#PlanningScenariosTip").show();
    		flag=false;
    	}
    	if((scenarios=="<%=EnumScenarios.Forecast.getCode()%>") || (scenarios=="<%=EnumScenarios.Actual.getCode()%>")){
			$("#DIV_PlanningPeriod").show();
			$("#PlanningPeriodTip").hide();
			period=$("#PlanningPeriod").val();
			if(period==""){
	    		$("#PlanningPeriodTip").show();
	    		flag=false;
	    	}
		}
    	if(!flag){
    		return;
    	}
    	var sbu="";
		$("input[name=planningEntitys]:checked").each(function(i,dom){
			sbu+=$(dom).val()+",";
		});
    	
   		$("#loading").show();
   		$.ajax({
   			type:"POST",
   			url:"${ctx}/admin/planning/download",
   			async:true,
   			dataType:"json",
   			data:{sbu:sbu,scenarios:scenarios,year:year,period:period},
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
   		   		layer.alert("<spring:message code='connect_fail'/>");
   		   	}
   		});
    });
	
	$("#Copy").click(function(){
		$("#PlanningEntitysTip").hide();
    	$("#PlanningYearTip").hide();
    	$("#PlanningScenariosTip").hide();
    	$("#PlanningPeriodTip").hide();
    	$("#VersionTip").hide();
		var flag=true;
    	if($("input[name=planningEntitys]:checked").length==0){
           	$("#PlanningEntitysTip").show();
           	flag=false;
        }
    	var year=$("#PlanningYear").val();
    	if(year==""){
    		$("#PlanningYearTip").show();
    		flag=false;
    	}
    	var scenarios=$("#PlanningScenarios").val();
    	var period="";
    	if(scenarios==""){
    		$("#PlanningScenariosTip").show();
    		flag=false;
    	}
    	if((scenarios=="<%=EnumScenarios.Forecast.getCode()%>") || (scenarios=="<%=EnumScenarios.Actual.getCode()%>")){
			$("#DIV_PlanningPeriod").show();
			$("#PlanningPeriodTip").hide();
			period=$("#PlanningPeriod").val();
			if(period==""){
	    		$("#PlanningPeriodTip").show();
	    		flag=false;
	    	}
		}
    	var version=$("#Version").val();
    	if(version==""){
    		$("#VersionTip").show();
    		flag=false;
    	}
    	if(!flag){
    		return;
    	}
		
		var entitys="";
		$("input[name=planningEntitys]:checked").each(function(i,dom){
			entitys+=$(dom).val()+",";
		});
		$.ajax({
			type:"POST",
			url:"${ctx}/admin/planning/copy",
			async:false,
			dataType:"json",
			data:{year:year,scenarios:scenarios,entitys:entitys,version:version},
			success: function(data){
				layer.alert(data.msg);
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		layer.alert("<spring:message code='connect_fail'/>");
		   	}
		});
	});
});
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span>預算數據管理</span>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
               		<div class="pull-left">
						<div class="pull-left m-l-md" style="display:inline-block;">
							<ul class="nav dropdown">
								<li class="dropdown" style="width:60px;">
									<a data-toggle="dropdown" class="dropdown-toggle" href="#">SBU<strong class="caret"></strong></a>
									<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
										<li class="AllCheck" style="padding:0 10px;clear:both;">
											<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
											<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>
										</li>
										<c:forEach items="${sbuList}" var="sbu">
											<c:if test="${not empty sbu}">
												<li class="Check" style="padding:0 10px;clear:both;">
													<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${sbu}</span>
													<input type="checkbox" name="planningEntitys" style="font-size:15px;color:#7e8978;float:right;width:20px;" value="${sbu}"/>
												</li>
											</c:if>
										</c:forEach>
									</ul>
								</li>
								<li style="line-height:12px;">
									<span id="PlanningEntitysTip" style="display:none;line-height:16px;margin-left:0;" class="Validform_checktip Validform_wrong">请选择</span>
								</li>
							</ul>
						</div>
						<div class="pull-left" style="display:inline-block;">
							<ul>
								<li>
									<select id="PlanningScenarios" class="input-large" style="width:100px;height:38px;margin-left:20px;">
						           		<option value="">Scenarios</option>
										<c:forEach items="<%=EnumScenarios.values() %>" var="scenarios">
											<option value="${scenarios.code }" >${scenarios.name }</option>
										</c:forEach>
									</select>
								</li>
								<li style="line-height:26px;">
									<span id="PlanningScenariosTip" style="display:none;line-height:16px;margin-left:20px;" class="Validform_checktip Validform_wrong">请选择</span>
								</li>
							</ul>
						</div>
						<div class="pull-left" style="display:inline-block;">
							<ul>
								<li>
									<select id="PlanningYear" class="input-large" style="width:80px;height:38px;margin-left:20px">
							          	<option value=""><%=EnumDimensionType.Years.getCode() %></option>
										<c:forEach items="${yearsList }" var="years">
											<option value="${years }" >${years }</option>
										</c:forEach>
									</select>
								</li>
								<li style="line-height:26px;">
									<span id="PlanningYearTip" style="display:none;line-height:16px;margin-left:20px;" class="Validform_checktip Validform_wrong">请选择</span>
								</li>
							</ul>
						</div>
						<div class="pull-left" style="display:inline-block;">
							<ul id="DIV_PlanningPeriod" style="display:none;">
								<li>
									<select id="PlanningPeriod" class="input-large" style="width:100px;height:38px;margin-left:20px">
						           		<option value="">Period</option>
										<option value="1" >1</option>
										<option value="2" >2</option>
										<option value="3" >3</option>
										<option value="4" >4</option>
										<option value="5" >5</option>
										<option value="6" >6</option>
										<option value="7" >7</option>
										<option value="8" >8</option>
										<option value="9" >9</option>
										<option value="10" >10</option>
										<option value="11" >11</option>
									</select>
								</li>
								<li style="line-height:26px;">
									<span id="PlanningPeriodTip" style="display:none;line-height:16px;margin-left:20px;" class="Validform_checktip Validform_wrong">请选择</span>
								</li>
							</ul>
						</div>
						<button id="DownloadPlanning" style="margin:0 0 0 10px;" class="btn search-btn" type="button">Download Planning</button>
						<div style="display:inline-block;">
							<ul>
								<li>
									<select id="Version" name="version" class="input-large m-l-md" style="width:90px;">
										<option value="" >Version</option>
										<c:forEach items="<%=EnumBudgetVersion.values() %>" var="version">
											<c:if test="${version.code ne 'V00' }">
												<option value="${version.code }" >${version.code }</option>
											</c:if>
										</c:forEach>
									</select>
								</li>
								<li style="line-height:26px;">
									<span id="VersionTip" style="display:none;line-height:16px;margin-left:20px;" class="Validform_checktip Validform_wrong">请选择</span>
								</li>
							</ul>
						</div>
						<button id="Copy" style="margin:0 0 0 10px;" class="btn search-btn" type="button">Copy</button>
					</div>
				</div>
            </div>
        </div>
	</div>
</div>
</body>
</html>
