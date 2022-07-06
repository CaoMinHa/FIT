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
	$("#forecastDetailRevenueForm").fileupload({
		dataType: "json",
	    url: "${ctx}/budget/forecastDetailRevenue/upload",
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
            $("#YearTip").hide();
            $("#EntitysTip").hide();
            $("#ScenariosTip").hide();
            
            $("#FileUpload").click(function(){
	            if($("#Year").val().length==0){
	            	$("#YearTip").show();
	            	return;
	            }
	            if($("input[name=entitys]:checked").length==0){
					$("#EntitysTip").show();
					return;
				}
	            if($("#Scenarios").val().length==0){
	            	$("#ScenariosTip").show();
	            	return;
	            }
	            
            	$("#loading").show();
            	data.submit();
    		});
        },
	    done:function(e,data){
	    	$("#loading").delay(1000).hide();
	    	layer.alert(data.result.msg);
  		},
  		fail:function(){
  			$("#loading").delay(1000).hide();
  			layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
  	  	},
  		processfail:function(e,data){
  			$("#loading").delay(1000).hide();
  			layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
  	  	}
	});
	
	$("#FileUpload").click(function(){
		$("#UploadTip").show();
		if($("#Year").val().length==0){
           	$("#YearTip").show();
        }
		if($("#Scenarios").val().length==0){
           	$("#ScenariosTip").show();
        }
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#UploadTip").hide();
		$("#YearTip").hide();
		$("#PeriodTip").hide();
		$("#ScenariosTip").hide();
		var flag=true;
		var year=$("#Year").val();
		if(year.length==0){
           	$("#YearTip").show();
           	flag=false;
        }
		if($("input[name=entitys]:checked").length==0){
           	$("#EntitysTip").show();
           	flag=false;
        }
		var scenarios=$("#Scenarios").val();
		if(scenarios.length==0){
           	$("#ScenariosTip").show();
           	flag=false;
        }
		var period=$("#Period").val();
		if(scenarios=="<%=EnumScenarios.Actual.getCode()%>"){
			if(period.length==0){
	           	$("#PeriodTip").show();
	           	flag=false;
	        }
		}
		if(!flag){
			return;
		}
		
		$("#loading").show();
		
		var entitys="";
		$("input[name=entitys]:checked").each(function(i,dom){
			entitys+=$(dom).val()+",";
		});
		var version=$("#Version").val();
		$.ajax({
			type:"POST",
			url:"${ctx}/budget/forecastDetailRevenue/download",
			async:true,
			dataType:"json",
			data:{year:year,period:period,entitys:entitys,scenarios:scenarios,version:version},
			success: function(data){
				$("#loading").hide();
				if(data.flag=="success"){
					download("${ctx}/unauth/file/download?fileName="+data.fileName,data.templateName);
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
	
	$("#Delete").click(function(){
		$("#UploadTip").hide();
		$("#YearTip").hide();
		$("#PeriodTip").hide();
		$("#ScenariosTip").hide();
		$("#SBUTip").hide();
		var flag=true;
		var year=$("#Year").val();
		if(year.length==0){
           	$("#YearTip").show();
           	flag=false;
        }
		if($("input[name=entitys]:checked").length==0){
           	$("#EntitysTip").show();
           	flag=false;
        }
		var scenarios=$("#Scenarios").val();
		if(scenarios.length==0){
           	$("#ScenariosTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		
		var entitys="";
		$("input[name=entitys]:checked").each(function(i,dom){
			entitys+=$(dom).val()+",";
		});
		layer.confirm("<spring:message code='confirm'/>?",{btn: ['<spring:message code='confirm'/>', '<spring:message code='cancel'/>'], title: "<spring:message code='tip'/>"},function(index){
			layer.close(index);
			$.ajax({
				type:"POST",
				url:"${ctx}/budget/forecastDetailRevenue/deleteVersion",
				async:false,
				dataType:"json",
				data:{year:year,scenarios:scenarios,entitys:entitys},
				success: function(data){
					layer.alert(data.msg);
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		});
	});
	
	$("#DownloadTemplate").click(function(){
		if("${locale}"=="en_US"){
			window.location.href="${ctx}/static/template/budget/Sales Detail.xlsx";
		}else{
			window.location.href="${ctx}/static/template/budget/預算(預測)營收明細.xlsx";
		}
	});
	$("#ActualTemplate").click(function(){
		if("${locale}"=="en_US"){
			window.location.href="${ctx}/static/template/budget/Sales Detail Actual.xlsx";
		}else{
			window.location.href="${ctx}/static/template/budget/營收明細實際數.xlsx";
		}
	});
	<%--$("#DimensionTable").click(function(){--%>
	<%--	window.location.href="${ctx}/static/template/budget/FIT_PBCS Dimension table 0917.xlsx";--%>
	<%--});--%>
	$("#DimensionTable").click(function(){
	$("#loading").show();
		$.ajax({
			type: "POST",
			url: "${ctx}/budget/forecastDetailRevenue/dimension",
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
	
	$("#Year").change(function(){
		if($(this).val().length>0){
			$("#YearTip").hide();
		}
	});
	
	$("#Content").load("${ctx}/budget/forecastDetailRevenue/list");
	$("#QueryBtn").click(function(){
		clickPage(1);
	});
	
	$("#Scenarios").change(function(){
		if($(this).val()=="<%=EnumScenarios.Actual.getCode()%>"){
			$("#PeriodTip").show();
			$("#DIV_Period").show();
		}else{
			$("#DIV_Period").hide();
		}
	});
});

var periodId;

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
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='forecastDetailRevenue'/></span>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
					<form id="forecastDetailRevenueForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
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
							    	<select id="Scenarios" name="scenarios" class="input-large" style="width:100px;">
						           		<option value=""><spring:message code='scenarios'/></option>
										<c:forEach items="<%=EnumScenarios.values() %>" var="scenarios">
											<option value="${scenarios.code }" >
												<c:choose>
													<c:when test="${locale eq 'en_US'}">${scenarios.code }</c:when>
													<c:otherwise>${scenarios.name }</c:otherwise>
												</c:choose>
											</option>
										</c:forEach>
									</select>
								</div>
								<div style="float:left;">
									<span id="ScenariosTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
							<div style="float:left;margin-left:10px;display:inline-block;">
								<div>
									<select id="Year" name="year" class="input-large" style="width:80px;">
						           		<option value=""><%=EnumDimensionType.Years.getCode() %></option>
										<c:forEach items="${yearsList }" var="years">
											<option value="${years }" >${years }</option>
										</c:forEach>
									</select>
								</div>
								<div style="float:left;">
									<span id="YearTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
						    <div id="DIV_Period" style="float:left;margin-left:10px;display:none;">
								<div>
									<select id="Period" name="period" class="input-large" style="width:80px;">
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
								</div>
								<div style="float:left;">
									<span id="PeriodTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
						    <div style="float:left;margin-left:10px;">
								<ul class="nav dropdown" style="float:left;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#">SBU<strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
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
									<li>
										<span id="EntitysTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="FileUpload" style="float:left;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
								<button id="Delete" style="margin:0 0 0 10px;display:none;" class="btn search-btn" type="button"><spring:message code='delete'/></button>
								<select id="Version" name="version" class="input-large m-l-md" style="width:60px;">
									<c:forEach items="<%=EnumBudgetVersion.values() %>" var="version">
										<option value="${version.code }" >${version.code }</option>
									</c:forEach>
								</select>
								<button id="Download" class="btn search-btn" type="button"><spring:message code='download'/></button>
							</div>
					    	<div style="text-align: center;margin-bottom:30px;">
							    <button id="DownloadTemplate" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='budgetForecast'/></button>
								<button id="ActualTemplate" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='actual'/></button>
								<button id="DimensionTable" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='dimension'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-md m-r-md">
	        <div class="controls text-center">
	           	<select id="QYear" class="input-large" style="width:80px;">
	           		<option value=""><%=EnumDimensionType.Years.getCode() %></option>
					<c:forEach items="${yearsList }" var="years">
						<option value="${years }" >${years }</option>
					</c:forEach>
				</select>
	           	<select id="QEntity" class="input-large">
	           		<option value="">SBU</option>
					<c:forEach items="${fn:split(corporationCode,',') }" var="sbu">
						<c:if test="${not empty sbu}">
							<option value="${sbu }" >${sbu }</option>
						</c:if>
					</c:forEach>
				</select>
	           	<select id="QScenarios" class="input-large">
	           		<option value=""><spring:message code='scenarios'/></option>
					<c:forEach items="<%=EnumScenarios.values() %>" var="scenarios">
						<option value="${scenarios.code }" >
							<c:choose>
								<c:when test="${locale eq 'en_US'}">${scenarios.code }</c:when>
								<c:otherwise>${scenarios.name }</c:otherwise>
							</c:choose>
						</option>
					</c:forEach>
				</select>
				<select id="QVersion" class="input-large" style="width:60px;">
					<c:forEach items="<%=EnumBudgetVersion.values() %>" var="version">
						<option value="${version.code }" >${version.code }</option>
					</c:forEach>
				</select>
	        	<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" type="submit"><spring:message code='query'/></button>
	        </div>
		</div>		
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
