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
			$("#depreExpenBudgetForm").fileupload({
				dataType: "json",
				url: "${ctx}/bi/depreExpenBudget/upload",
				add: function (e, data) {
					$("#FileUpload").unbind();
					var filename=data.originalFiles[0]['name'];
					var acceptFileTypes = /(\.|\/)(xls|xlsx|XLS|XLSX)$/i;
					if(filename.length && !acceptFileTypes.test(filename)) {
						$(".tip").text("<spring:message code='click_select_excel'/>");
						layer.alert("<spring:message code='only_support_excel'/>");
						return;
					}
					if (data.originalFiles[0]['size'] > 1024*1024*30) {
						$(".tip").text("<spring:message code='click_select_excel'/>");
						layer.alert("<spring:message code='not_exceed_30M'/>");
						return;
					}
					$(".tip").text(filename);
					$(".upload-tip").attr("title",filename);
					$("#UploadTip").hide();
					$("#FileUpload").click(function(e){
						if(!$("#scenarios").val()){
							$("#scenariosTip").show();
							return;
						}
						$("#loading").show();
						data.submit();
					});
				},
				done:function(e,data){
					$("#loading").delay(1000).hide();
					layer.alert(data.result.msg);
					clickPage(1);
				},
				fail:function(){
					$("#loading").delay(1000).hide();
					layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
				},
				processfail:function(e,data){
					$("#loading").delay(1000).hide();
					layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
					clickPage(1);
				}
			});

			$("#FileUpload").click(function(){
				$("#UploadTip").show();
			});

			$(".upload-tip").click(function(){
				$(".input-file").trigger("click");
			});

			$("#Download").click(function(){
				$("#UploadTip").hide();
				if(!$("#QScenarios").val()){
					layer.alert("請選擇場景！(Please select a scene)");
					return;
				}
				if(!$("#QYear").val()){
					layer.alert("请选择年份(Please select year)");
					return;
				}
				var entitys="";
				if(!$("input[name=entitys]:checked")){
					layer.alert("请选择SBU(Please select SBU)");
					return;
				}else{
					$("input[name=entitys]:checked").each(function(i,dom){
						entitys+=$(dom).val()+",";
					});
				}
				$("#loading").show();
				$.ajax({
					type:"POST",
					url:"${ctx}/bi/depreExpenBudget/download",
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

			$("#DimensionTable").click(function(){
				$("#loading").show();
				$.ajax({
					type: "POST",
					url: "${ctx}/bi/depreExpenBudget/dimension",
					async: true,
					dataType: "json",
					success: function (data) {
						$("#loading").hide();
						if (data.flag == "success") {
							window.location.href = "${ctx}/static/download/" + data.fileName;
						} else {
							layer.alert(data.msg);
						}
					},
					error: function () {
						$("#loading").hide();
						layer.alert("下載失敗！(Download Failed)");
					}
				});
			});

			$("#Version").click(function(){
                if(!$("#QScenarios").val()){
                    layer.alert("請選擇場景！(Please select a scene)");
                    return;
                }
				$("#loading").show();
				$.ajax({
					type: "POST",
					url: "${ctx}/bi/depreExpenBudget/version",
					async: true,
					dataType: "json",
                    data:{scenarios:$("#QScenarios").val()},
					success: function (data) {
						$("#loading").hide();
						if (data.flag == "success") {
							$("#QVersion").append("<option value='"+data.version+"' selected>"+data.version+"</option>");
							clickPage(1);
						} else {
							layer.alert(data.msg);
						}
					},
					error: function () {
						$("#loading").hide();
						layer.alert("版本更新失敗！(Version update failure)");
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
			$("#scenarios").change(function () {
				if($(this).val()){
					$("#QScenarios").val($(this).val());
					var date=new Date;
					var year=date.getFullYear();
					if($(this).val()=="forecast"){
						$("#QYear").val("FY"+year.toString().substring(2));
					}else{
						year=year+1;
						$("#QYear").val("FY"+year.toString().substring(2));
					}
				}
			})
			$("#QScenarios").change(function () {
				if($(this).val()){
					var date=new Date;
					var year=date.getFullYear();
					if($(this).val()=="forecast"){
						$("#QYear").val("FY"+year.toString().substring(2));
					}else{
						year=year+1;
						$("#QYear").val("FY"+year.toString().substring(2));
					}
					clickPage(1);
				}
			})

			$("#DownloadPlanning").click(function(){
				if(!$("#QScenarios").val()){
					layer.alert("請選擇場景！(Please select a scene)");
					return;
				}
				if($("input[name=entitys]:checked").length==0){
					layer.alert("請選擇SBU(Please select SBU)");
					return;
				}
				var sbu="";
				$("input[name=entitys]:checked").each(function(i,dom){
					sbu+=$(dom).val()+",";
				});
				$("#loading").show();
				$.ajax({
					type:"POST",
					url:"${ctx}/bi/budget/create",
					async:true,
					dataType:"json",
					data:{sbu:sbu,year:$("#QYear").val(),scenarios:$("#QScenarios").val(),type:"depreExpen"},
					success: function(data){
						$("#loading").hide();
						if(data.flag=="success"){
							if(data.role=="YES"){
								window.location.href="${ctx}/static/download/"+data.fileName;
							}else{
								layer.alert(data.msg);
							}
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
		});

		function DownloadTemplate(type){
			$("#loading").show();
			if(!$("#QYear").val()){
				layer.alert("请选择年份(Please select year)");
				return;
			}
			$.ajax({
				type: "POST",
				url: "${ctx}/bi/depreExpenBudget/template",
				async: true,
				dataType: "json",
				data: {type:type,year:$("#QYear").val()},
				success: function (data) {
					$("#loading").hide();
					if (data.flag == "success") {
						window.location.href = "${ctx}/static/download/" + data.fileName;
					} else {
						layer.alert(data.msg);
					}
				},
				error: function () {
					$("#loading").hide();
					layer.alert("下載失敗！(Download Failed)");
				}
			});
		};

		$("#Content").load("${ctx}/bi/depreExpenBudget/list",{scenarios:"",entity:"",year:$("#QYear").val(),version:$("#QVersion").val()},function(){$("#QueryBtn").click();});

	</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='depreExpenBudget'/></span>
				<input id="onlyQuery" style="display: none" value="${onlyQuery}"/>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md" <c:if test="${onlyQuery  eq 'Y'}"> style="display: none"</c:if>>
			<div class="controls">
				<div>
					<form id="depreExpenBudgetForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
						<input type="file" style="display:none;" class="input-file" multiple="false"/>
						<div>
							<div style="float: left;text-align: right;" title="<spring:message code='not_exceed_30M'/>">
								<div class="upload-tip">
									<span class="tip"><spring:message code='click_select_excel'/></span>
								</div>
								<div id="UploadTip" style="display:none;float:left;">
									<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</div>
							</div>
							<div style="float:left;margin-left:10px;display:inline-block;">
								<div>
									<select id="scenarios" name="scenarios" class="input-large" style="width:100px;">
										<option value=""><spring:message code='scenarios'/></option>
										<option value="budget"><%=EnumScenarios.Budget %></option>
										<option value="forecast"><%=EnumScenarios.Forecast%></option>
									</select>
								</div>
								<div id="scenariosTip" style="display:none;float:left;">
									<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</div>
							</div>
							<div style="float:left;margin-left:10px;display:inline-block;">
								<button id="FileUpload" style="float:left;width: 100px;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
							<div style="text-align: right">
								<button onclick="DownloadTemplate('budget')" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='depreExpenBudget'/><spring:message code="template"/></button>
								<button onclick="DownloadTemplate('forecast')" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='depreExpenForecast'/><spring:message code="template"/></button>
								<button id="DimensionTable" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='dimension'/></button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="m-l-md m-t-md m-r-md" style="clear:both;">
			<div class="controls">
				<select id="QScenarios" name="scenarios" class="input-large" style="width:100px;">
					<option value="budget"><%=EnumScenarios.Budget %></option>
					<option value="forecast"><%=EnumScenarios.Forecast%></option>
				</select>
				<select id="QYear" class="input-large" style="width:100px;">
					<c:forEach items="${yearsList }" var="years">
						<option value="${years }" <c:if test="${years eq yearVal}">selected</c:if>>${years}</option>
					</c:forEach>
				</select>
				<select id="QVersion" class="input-large" style="width:100px;">
					<option value="V00" selected>V00</option>
					<c:forEach items="${versionList}" var="version">
						<option value="${version}">${version }</option>
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
				<button id="Download" class="btn search-btn" style="width: 100px;" type="button"><spring:message code='download'/></button>
				<button id="Version" class="btn search-btn" style="width: 100px;<c:if test="${onlyQuery  eq 'Y'}">display: none;</c:if>" type="button"><spring:message code='version'/></button>
				<button id="DownloadPlanning" style="margin:0 0 0 10px;<c:if test="${onlyQuery  eq 'Y'}">display: none;</c:if>" class="btn search-btn" type="button"><spring:message code='submit'/></button>
                <c:if test="${onlyQuery  eq 'Y'}"><button id="DimensionTable" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='dimension'/></button></c:if>
			</div>
		</div>
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
