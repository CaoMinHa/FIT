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
			margin-left:10px;
			color:#ffffff;
			background-image: linear-gradient(to bottom, #fbb450, #f89406);
			background-color: #f89406 !important;
		}
		.table thead th{vertical-align: middle;}
		.small_th{padding:0;font-size:10px;}
	</style>
	<script type="text/javascript">
		$(function() {
			$("#Download").click(function(){
				if(!$("#QScenarios").val()){
					layer.alert("請選擇場景！(Please select a scene)");
					return;
				}
				if(!$("#QYear").val()){
					layer.alert("请选择年份(Please select year)");
					return;
				}
				if(!$("#QEntity").val()){
					layer.alert("请选择法人(Please select Legal)");
					return;
				}
				$("#loading").show();
				$.ajax({
					type:"POST",
					url:"${ctx}/bi/signedINVBudget/download",
					async:true,
					dataType:"json",
					data:{year:$("#QYear").val(),entitys:entitys,version:$("#QVersion").val(),scenarios:$("#QScenarios").val()},
					success: function(data){
						if (data.flag == "success") {
							window.location.href = "${ctx}/static/download/" + data.fileName;
						} else {
							layer.alert(data.msg);
						}
						$("#loading").hide();
					},
					error: function(XMLHttpRequest, textStatus, errorThrown) {
						$("#loading").hide();
						layer.alert("<spring:message code='connect_fail'/>");
					}
				});
			});
						
			$("#QueryBtn").click(function(){
				clickPage(1);
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
		});
		$("#Content").load("${ctx}/bi/signedINVBudget/list",{scenarios:$("#QScenarios").val(),entitys:"",year:$("#QYear").val()},function(){$("#QueryBtn").click();});


	</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='investmentBudget'/></span>
			</h2>
		</div>
		
		<div class="m-l-md m-t-md m-r-md" style="clear:both;">
			<div class="controls">				 
				<select id="QScenarios" name="scenarios" class="input-large" style="width:100px;">
					<option value="Budget"><%=EnumScenarios.Budget %></option>
					<option value="Forecast"><%=EnumScenarios.Forecast%></option>
				</select>
				<select id="QYear" class="input-large" style="width:100px;">
					<c:forEach items="${yearsList }" var="years">
						<option value="${years }"<c:if test="${years eq yearVal}">selected</c:if>>${years}</option>
					</c:forEach>
				</select> 
				<ul class="nav dropdown" style="float:left;margin-right:10px;">
					<li class="dropdown" style="margin-top:0;">
						<a data-toggle="dropdown" class="dropdown-toggle" href="#">SBU<strong class="caret"></strong></a>
						<ul class="dropdown-menu" style="left:-20%;max-height:350px;min-width:150px;overflow-y:scroll;">
							<li class="AllCheck" style="padding:0 10px;clear:both;">
								<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
								<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value=""/>
							</li>
							<c:forEach items="${fn:split(corporationCode,',') }" var="sbu">
								<c:if test="${not empty sbu}">
									<li class="Check" style="padding:0 10px;clear:both;">
										<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${sbu}</span>
										<input type="checkbox" name="entitys" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value="${sbu}"/>
									</li>
								</c:if>
							</c:forEach>
						</ul>
					</li>
				</ul>
				<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="width: 100px;" type="submit"><spring:message code='query'/></button>
			</div>
		</div>
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
