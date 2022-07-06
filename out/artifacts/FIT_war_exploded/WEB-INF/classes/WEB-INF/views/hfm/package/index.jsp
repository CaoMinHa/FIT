<%@page import="foxconn.fit.util.SecurityUtils"%>
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
var periodId;
$(function() {
	$("#ui-datepicker-div").remove();
	$("#Date,#QDate").datepicker({
		changeMonth: true,
        changeYear: true,
        dateFormat: 'yy-MM',
        showButtonPanel:true,
        closeText:"<spring:message code='confirm'/>"
	});
	
	$("#Date,#QDate").click(function(){
		periodId=$(this).attr("id");
		$(this).val("");
	});
	
	$("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
		var month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
        var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
        $("#"+periodId).val(year+'-'+(parseInt(month)+1));//给input赋值，其中要对月值加1才是实际的月份
        if($("#"+periodId+"Tip").length>0){
        	$("#"+periodId+"Tip").hide();
        }
	});
	
	$("#Download").click(function(){
		$("#DataTypeTip").hide();
		var entity=$("#Entity").val();
		var dataType=$("#DataType").val();
		if(!dataType){
			$("#DataTypeTip").show();
			return;
		}
		$("#loading").show();
		var queryCondition=$("#QueryCondition").serialize();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/package/download",
			async:true,
			dataType:"json",
			data:{dataType:dataType,queryCondition:queryCondition},
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
	
	$("#DataImport").click(function(){
		$("#EntityTip").hide();
		$("#DateTip").hide();
		$("#DataTypeTip").hide();
		var flag=true;
		var entity=$("#Entity").val();
		var date=$("#Date").val();
		if(entity.length==0){
           	$("#EntityTip").show();
           	flag=false;
        }
		if(date.length==0){
           	$("#DateTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/package/dataImport",
			async:true,
			dataType:"json",
			data:{entity:entity,date:date},
			success: function(data){
				$("#loading").hide();
				layer.alert(data.msg);
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		$("#loading").hide();
		   		window.location.href="${ctx}/logout";
		   	}
		});
	});
	
	$("#DataSync").click(function(){
		$("#EntityTip").hide();
		$("#DateTip").hide();
		$("#DataTypeTip").hide();
		var flag=true;
		var entity=$("#Entity").val();
		var date=$("#Date").val();
		if(entity.length==0){
           	$("#EntityTip").show();
           	flag=false;
        }
		if(date.length==0){
           	$("#DateTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/package/dataSync",
			async:true,
			dataType:"json",
			data:{entity:entity,date:date},
			success: function(data){
				if(data.flag=="success"){
					$("#DestinationLog").hide();
					$("#DataSynchronizeLog").hide();
					
					$.ajax({
						type:"POST",
						url:"${ctx}/hfm/schedule/esbDataSynchronize",
						async:true,
						dataType:"json",
						data:{date:date,entitys:entity,type:'ALL'},
						success: function(data){
							$("#loading").hide();
							if(data.flag=="success"){
								jobId=data.jobId;
								
								refreshJob();
								interval=setInterval("refreshJob()",5000);
							}else{
								$("#MessageDialog").html(data.msg);
								$("#MessageDialog").dialog({
									modal:true,
									title: "信息",
									height:500,
									width:"auto",
									draggable: true,
									resizable: true,
									autoOpen:true,
									autofocus:true,
									closeText:"<spring:message code='close'/>",
									buttons: [
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
								});
							}
					   	},
					   	error: function(XMLHttpRequest, textStatus, errorThrown) {
						   	$("#loading").hide();
					   		window.location.href="${ctx}/logout";
					   	}
					});
				}else{
					$("#loading").hide();
					layer.alert(data.msg);
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		$("#loading").hide();
		   		window.location.href="${ctx}/logout";
		   	}
		});
	});
	
	$("#DataType").change(function(){
		$("#QueryCondition").empty();
		$("#Content").empty();
		if($(this).val()!=""){
			$("#DataTypeTip").hide();
			$("#loading").show();
			var dataType=$(this).val();
			$.ajax({
				type:"POST",
				url:"${ctx}/hfm/package/queryData",
				async:true,
				dataType:"json",
				data:{dataType:dataType},
				success: function(data){
					$("#loading").hide();
					if(data.flag=="success"){
						$.each(data.queryList, function(i, n){
							var COL_NAME=n[0];
							var COL_DESC=n[1];
							$("#QueryCondition").append("<input name='"+COL_NAME+"' class='m-l-md' style='width:100px;' placeholder='"+COL_DESC+"' type='text' value=''>");
						});
						$("#Query").trigger("click");
					}else{
						layer.alert(data.msg);
					}
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		$("#loading").hide();
			   		window.location.href="${ctx}/logout";
			   	}
			});
		}
	});
	
	$("#Query").click(function(){
		$("#DataTypeTip").hide();
		var dataType=$("#DataType").val();
		if(!dataType){
			$("#DataTypeTip").show();
			return;
		}
		$("#loading").show();
		var queryCondition=$("#QueryCondition").serialize();
		$("#Content").load("${ctx}/hfm/package/list",{dataType:dataType,queryCondition:queryCondition},function(){$("#loading").fadeOut(1000);});
	});
	
	$("#DataSynchronizeLog").click(function(){
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/schedule/dataSynchronizeLog",
			async:true,
			dataType:"json",
			data:{jobId:jobId,type:"Synchronization"},
			success: function(data){
				$("#loading").hide();
				if(data.flag=="success"){
					layer.open({
						  type: 1,
						  skin: 'layui-layer-rim', //加上边框
						  area: ['800px', '400px'], //宽高
						  content: data.msg
						});
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
	
	$("#DestinationLog").click(function(){
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/schedule/dataSynchronizeLog",
			async:true,
			dataType:"json",
			data:{jobId:jobId,type:"Destination"},
			success: function(data){
				$("#loading").hide();
				if(data.flag=="success"){
					layer.open({
						  type: 1,
						  skin: 'layui-layer-rim', //加上边框
						  area: ['800px', '400px'], //宽高
						  content: data.msg
						});
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
	
});
var jobId="";
var interval=null;

function refreshJob(){
	if(jobId.length==0 || $("#jobDetail").length==0){
		clearInterval(interval);
		return;
	}
	$("#jobDetail td").text("");
	
	$.ajax({
		type:"POST",
		url:"${ctx}/hfm/schedule/getJobsElement",
		async:true,
		dataType:"json",
		data:{jobId:jobId},
		success: function(data){
			if(data.flag=="success"){
				if(data.status=="Completed" && interval){
					clearInterval(interval);
					$("#DestinationLog").show();
					$("#DataSynchronizeLog").show();
				}
				if($("#jobDetail").length>0){
					$("#jobDetail td:eq(0)").text(data.id);
					$("#jobDetail td:eq(1)").text(data.description);
					$("#jobDetail td:eq(2)").text(data.lastUpdatedTime);
					$("#jobDetail td:eq(3)").text(data.status);
				}
			}else{
				layer.alert(data.msg);
			}
	   	},
	   	error: function(XMLHttpRequest, textStatus, errorThrown) {
	   		window.location.href="${ctx}/logout";
	   	}
	});
}
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="m-t-md m-r-md">
			<div class="controls">
               	<div class="m-l-md">
				    <div>
				    	<div style="float:left;padding-right:10px;">
				    		<ul style="float:left;margin-left:10px;">
								<li>
									<select id="Entity" class="input-large" style="width:100px;">
						           		<option value=""><spring:message code='entity'/></option>
										<c:forEach items="${fn:split(entity,',') }" var="code">
											<c:if test="${fn:startsWith(code,'F_') }">
												<option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
											</c:if>
										</c:forEach>
									</select>
								</li>
								<li>
									<span id="EntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</li>
							</ul>
						</div>
						<div style="float:left;padding-right:10px;">
							<div>
					    		<input id="Date" name="date" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" value="" readonly>
							</div>
							<div style="float:left;">
								<span id="DateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
					    	</div>
					    </div>
					    <div style="float:left;padding-right:10px;">
					    	<button id="DataImport" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='dataImport'/></button>
					    	<button id="DataSync" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='data_synchronize'/></button>
					    	<a id="DestinationLog" style="display:none;line-height:38px;font-size:20px;text-align:center;text-decoration: underline;"><spring:message code='destinationLog'/></a>
							<a id="DataSynchronizeLog" style="display:none;margin-right:10px;line-height:38px;font-size:20px;text-align:center;text-decoration: underline;"><spring:message code='dataSynchronizeLog'/></a>
					    </div>
					    <div style="float:left;padding-right:10px;">
						    <div>
						    	<select id="DataType" name="dataType" class="input-xlarge" style="width:120px;color:#9f9a9a;font-weight:bold;">
									<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='dataType'/></option>
									<c:forEach items="${optionList }" var="option">
										<option value="${fn:split(option,'|')[0]}">${fn:split(option,'|')[1]}</option>
									</c:forEach>
								</select>
						    </div>
						    <div id="DataTypeTip" style="display:none;">
								<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
							</div>
				    	</div>
				    </div>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-sm" style="clear:both;">
			<div style="display:inline-block;width:700px;">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th width="15%" style="padding:5px 0 5px 14px;">ID</th>
							<th width="35%" style="padding:5px 0;"><spring:message code='desc'/></th>
							<th width="30%" style="padding:5px 0;"><spring:message code='last_update_time'/></th>
							<th width="20%" style="padding:5px 0;"><spring:message code='status'/></th>
						</tr>
					</thead>
					<tbody>
						<tr id="jobDetail" style="height:30px;">
							<td style="padding:5px 0 5px 14px;"></td>
							<td style="padding:5px 0;"></td>
							<td style="padding:5px 0;"></td>
							<td style="padding:5px 0;"></td>
						</tr>
					</tbody>
				</table>
			</div>
        </div>
        <div class="m-l-md m-t-sm" style="clear:both;">
	        <div class="controls" style="display:inline-block;vertical-align:top;width:100%;">
	        	<form id="QueryCondition" style="float:left;margin:0;"></form>
	        	<button id="Query" class="btn search-btn btn-warning m-l-md" type="button"><spring:message code='query'/></button>
	        	<button id="Download" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='download'/></button>
	        </div>
		</div>
		<div class="p-l-md p-r-md p-b-md p-t-sm" id="Content" style="max-width:150%;"></div>
	</div>
</div>
</body>
</html>
