<%@page import="foxconn.fit.entity.base.EnumScheduleType"%>
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
	if(interval){
		clearInterval(interval);
	}
	
	$("#generalLedgerForm").fileupload({
		dataType: "json",
	    url: "${ctx}/hfm/generalLedger/upload",
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
            
            $("#FileUpload").click(function(){
	            if($("#Month").val().length==0){
	            	$("#MonthTip").show();
	            	return;
	            }
	            if($("input[name=Upcode]:checked").length==0){
					$("#UpcodeTip").show();
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
	
	$("#Extract").click(function(){
		var month=$("#Month").val();
		if(month.length==0){
           	$("#MonthTip").show();
           	return;
           }
		if($("input[name=Upcode]:checked").length==0){
			$("#UpcodeTip").show();
			return;
		}
		if($("input[name=Upcode]:checked").length>5){
			layer.alert("<spring:message code='extract_num'/>");
			return;
		}
		
		var code="";
		$("input[name=Upcode]:checked").each(function(i,dom){
			code+=$(dom).val()+",";
		});
		
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/generalLedger/dataExtract",
			async:true,
			dataType:"json",
			data:{month:month,corporCode:code},
			success: function(data){
				$("#loading").hide();
				if(data.flag=="success"){
					var extractList=data.extractList;
					$.each(extractList, function(i, n){
						var id=n[0];
						var code=n[1];
						var period=n[2];
						var beginTime=n[3];
						$("#ExtractBody").append("<tr class='info'><td id='"+id+"' style='display:none;'></td><td width='20%'>"+code+"</td><td width='20%'>"+period+"</td><td width='20%' class='status'><spring:message code='start'/></td><td width='20%'>"+beginTime+"</td><td width='20%' class='endTime'></td></tr>");
					});
					$("#ExtractBody").append("<tr class='warning'><td></td><td></td><td></td><td></td><td></td></tr>");
				}else{
					layer.alert(data.msg);
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		layer.alert("<spring:message code='connect_fail'/>");
			   	$("#loading").hide();
		   	}
		});
	});
	
	$("#FileUpload").click(function(){
		$("#UploadTip").show();
		if($("#Month").val().length==0){
           	$("#MonthTip").show();
           }
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#UploadTip").hide();
		var month=$("#Month").val();
		if(month.length==0){
           	$("#MonthTip").show();
           	return;
        }
		if($("input[name=Downcode]:checked").length==0){
           	$("#DowncodeTip").show();
           	return;
        }
		
		$("#loading").show();
		
		var corporCode="";
		$("input[name=Downcode]:checked").each(function(i,dom){
			corporCode+=$(dom).val()+",";
		});
		var excelName="<spring:message code='general_ledger'/>";
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/generalLedger/download",
			async:true,
			dataType:"json",
			data:{month:month,corporCode:corporCode,excelName:excelName},
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
	
	$("#DownloadTemplate").click(function(){
		window.location.href="${ctx}/static/template/hfm/<spring:message code='general_ledger'/>.xlsx";
	});
	
	$("#ui-datepicker-div").remove();
	$("#Month").datepicker({
		changeMonth: true,
           changeYear: true,
           dateFormat: 'yy-MM',
           showButtonPanel:true,
           closeText:"<spring:message code='confirm'/>"
	});
	
	$("#Month").click(function(){
		$(this).val("");
	});
	
	$("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
		var month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
           var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
           $("#Month").val(year+'-'+(parseInt(month)+1));//给input赋值，其中要对月值加1才是实际的月份
           $("#MonthTip").hide();
	});
	
	$("#Synchronize").click(function(){
		var month=$("#Month").val();
		if(month.length==0){
           	$("#MonthTip").show();
           	return;
           }
		if($("input[name=Datacode]:checked").length==0){
           	$("#DataTip").show();
           	return;
           }
		$("#loading").show();
		
		var code="";
		$("input[name=Datacode]:checked").each(function(i,dom){
			code+=$(dom).val()+",";
		});
		
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/schedule/dataSynchronize",
			async:true,
			dataType:"json",
			data:{month:month,code:code,type:'<%=EnumScheduleType.GeneralLedger.getCode()%>'},
			success: function(data){
				$("#loading").hide();
				if(data.flag=="success"){
					jobId=data.jobId;
					
					refresh();
					interval=setInterval("refresh()",5000);
				}else{
					layer.alert(data.msg);
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		layer.alert("<spring:message code='connect_fail'/>");
			   	$("#loading").hide();
		   	}
		});
	});
	
	$("#refresh").click(function(){
		refresh();
	});
	
});

var jobId="";
var interval=null;

function refresh(){
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
				}
				if($("#jobDetail").length>0){
					$("#jobDetail td:eq(0)").text(data.id);
					$("#jobDetail td:eq(1)").text(data.description);
					$("#jobDetail td:eq(2)").text(data.lastUpdatedTime);
					$("#jobDetail td:eq(3)").text(data.status);
					$("#jobDetail td:eq(4)").text("<spring:message code='data_synchronize'/>");
					$("#jobDetail td:eq(5)").text(data.userIdentification);
				}
			}
	   	},
	   	error: function(XMLHttpRequest, textStatus, errorThrown) {
	   		layer.alert("<spring:message code='connect_fail'/>");
	   	}
	});
}

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
				<span><spring:message code='general_ledger'/></span>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
					<form id="generalLedgerForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
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
							<div style="float:left;margin:0 30px 0 30px;display:inline-block;">
								<div>
						    		<input id="Month" name="month" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" value="" readonly>
								</div>
								<div style="float:left;">
									<span id="MonthTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
					    	<div style="text-align: center;margin-bottom:30px;">
							    <button id="DownloadTemplate" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='template'/></button>
							</div>
					    </div>
						<div style="clear:both;text-align:center;">
							<div style="float:left;padding-right:20px;">
								<ul class="nav dropdown" style="float:left;margin-left:10px;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='corp_code'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
											<li class="AllCheck" style="padding:0 10px;clear:both;">
												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value=""/>
											</li>
											<c:forEach items="${fn:split(corporationCode,',') }" var="code">
												<c:if test="${fn:startsWith(code,'F_') }">
													<li class="Check" style="padding:0 10px;clear:both;">
														<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${fn:substring(code,2,-1) }</span>
														<input type="checkbox" name="Upcode" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value="${fn:substring(code,2,-1) }"/>
													</li>
												</c:if>
											</c:forEach>
										</ul>
									</li>
									<li>
										<span id="UpcodeTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="Extract" style="float:left;margin-right:0;" class="btn search-btn" type="button"><spring:message code='data_extract'/></button>
								<button id="FileUpload" style="float:left;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
							<div style="float:left;padding-right:20px;">
								<ul class="nav dropdown" style="float:left;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='corp_code'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
											<li class="AllCheck" style="padding:0 10px;clear:both;">
												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value=""/>
											</li>
											<c:forEach items="${fn:split(corporationCode,',') }" var="code">
												<c:if test="${not empty code }">
													<li class="Check" style="padding:0 10px;clear:both;">
														<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${fn:substring(code,2,-1) }</span>
														<input type="checkbox" name="Downcode" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value="${fn:substring(code,2,-1) }"/>
													</li>
												</c:if>
											</c:forEach>
										</ul>
									</li>
									<li>
										<span id="DowncodeTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="Download" style="float:left;" class="btn search-btn" type="button"><spring:message code='download'/></button>
							</div>
							<div style="float:left;">
								<ul class="nav dropdown" style="float:left;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='corp_code'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
											<li class="AllCheck" style="padding:0 10px;clear:both;">
												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value=""/>
											</li>
											<c:forEach items="${fn:split(corporationCode,',') }" var="code">
												<c:if test="${not empty code }">
													<li class="Check" style="padding:0 10px;clear:both;">
														<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${fn:substring(code,2,-1) }</span>
														<input type="checkbox" name="Datacode" style="font-size:15px;color:#7e8978;float:right;width:20px;" checked="checked" value="${fn:substring(code,2,-1) }"/>
													</li>
												</c:if>
											</c:forEach>
										</ul>
									</li>
									<li>
										<span id="DataTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="Synchronize" style="float:left;" class="btn search-btn" type="button"><spring:message code='synchronize_to_hfm'/></button>
							</div>
						</div>
					</form>
				</div>
            </div>
        </div>
		<div class="p-l-md p-t-md p-r-md p-b-md" style="padding-top:60px;">
			<table style="width:100%;">
				<tr>
					<td style="vertical-align: top;display:table-cell;width:510px;">
						<table class="table table-condensed" style="width:510px;">
							<thead>
								<tr>
									<th width="20%"><spring:message code='corp_code'/></th>
									<th width="17%"><spring:message code='month'/></th>
									<th width="19%"><spring:message code='status'/></th>
									<th width="20%"><spring:message code='start_time'/></th>
									<th width="21%"><spring:message code='end_time'/></th>
								</tr>
							</thead>
						</table>
						<div style="max-height:300px;overflow-y:scroll;width:510px;">
							<table class="table table-condensed">
								<tbody id="ExtractBody">
								</tbody>
							</table>
						</div>
					</td>
					<td style="vertical-align: top;padding-left:10px;">
						<table class="table table-striped table-hover">
							<thead>
								<tr>
									<th>ID</th>
									<th><spring:message code='desc'/></th>
									<th><spring:message code='last_update_time'/></th>
									<th><spring:message code='status'/></th>
									<th><spring:message code='type'/></th>
									<th><spring:message code='creator'/></th>
								</tr>
							</thead>
							<tbody>
								<tr id="jobDetail">
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>
</body>
</html>
