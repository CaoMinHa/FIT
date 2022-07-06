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
$(function() {
	if(interval){
		clearInterval(interval);
	}
	
	$("#reconciliationForm").fileupload({
		dataType: "json",
	    url: "${ctx}/hfm/reconciliation/upload",
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
	            if($("input[name=entitys]:checked").length==0){
					$("#EntitysTip").show();
					return;
				}
	            if($("#Date").val().length==0){
	            	$("#DateTip").show();
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
		$("#selfdefine-datepicker").hide();
		$("#UploadTip").show();
		if($("input[name=entitys]:checked").length==0){
			$("#EntitysTip").show();
		}
		if($("#Date").val().length==0){
           	$("#DateTip").show();
        }
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#selfdefine-datepicker").hide();
		var date=$("#QDate").val();
		if(date==""){
			$("#QDateTip").css("visibility","visible");
			return;
		}
		var entity="";
		$("input[name=QEntity]:checked").each(function(i,dom){
			entity+=$(dom).val()+",";
		});
		var ACCOUNTCODE=$("#QACCOUNTCODE").val();
		var ACCOUNTDESC=$("#QACCOUNTDESC").val();
		var ISICP=$("#QISICP").val();
		var CUSTNAME=$("#QCUSTNAME").val();
		var TC=$("#QTC").val();
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/reconciliation/download",
			async:true,
			dataType:"json",
			data:{entity:entity,date:date,ACCOUNTCODE:ACCOUNTCODE,ACCOUNTDESC:ACCOUNTDESC,ISICP:ISICP,CUSTNAME:CUSTNAME,TC:TC},
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
		window.location.href="${ctx}/static/template/ebs/科目餘額表.xlsx";
	});
	
	$("#DataImport").click(function(){
		$("#selfdefine-datepicker").hide();
		if($("input[name=entitys]:checked").length==0){
           	$("#EntitysTip").show();
           	return;
        }
		var date=$("#Date").val();
		if(date.length==0){
        	$("#DateTip").show();
        	return;
        }
		var entitys="";
		$("input[name=entitys]:checked").each(function(i,dom){
			entitys+=$(dom).val()+",";
		});
		entitys=entitys.substring(0,entitys.length-1);
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/reconciliation/dataImport",
			async:true,
			dataType:"json",
			data:{entitys:entitys,date:date},
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
	
	$("#Synchronize").click(function(){
		$("#selfdefine-datepicker").hide();
		$("#UploadTip").hide();
		var flag=true;
		if($("input[name=entitys]:checked").length==0){
           	$("#EntitysTip").show();
           	flag=false;
        }
		var date=$("#Date").val();
		if(date.length==0){
           	$("#DateTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		
		layer.confirm("<spring:message code='synchronizeConfirm1'/>",{btn: ["<spring:message code='confirm'/>", "<spring:message code='cancel'/>"], title: "<spring:message code='tip'/>"},function(index){
			layer.close(index);
			
			layer.confirm("<spring:message code='synchronizeConfirm2'/>",{btn: ["<spring:message code='confirm'/>", "<spring:message code='cancel'/>"], title: "<spring:message code='tip'/>"},function(index){
				layer.close(index);
				
				$("#DestinationLog").hide();
				$("#DataSynchronizeLog").hide();
				$("#loading").show();
				
				var entitys="";
				$("input[name=entitys]:checked").each(function(i,dom){
					entitys+=$(dom).val()+",";
				});
				
				$.ajax({
					type:"POST",
					url:"${ctx}/hfm/schedule/esbDataSynchronize",
					async:true,
					dataType:"json",
					data:{date:date,entitys:entitys,type:'MR'},
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
			});
		});
		
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
	
	$("#Date,#QDate").click(function(){
		var date=new Date();
		var curYear=date.getFullYear();
		var curMonth=date.getMonth();
		curMonth=curMonth+1;
		if(curMonth.length=1){
			curMonth="0"+curMonth;
		}
		try{
			$("#selfdefine-datepicker .ui-datepicker-year>option[value="+curYear+"]").get(0).selected="selected";
			$("#selfdefine-datepicker .ui-datepicker-month>option[value="+curMonth+"]").get(0).selected="selected";
		}catch(err){
		}
		var offset=$(this).offset();
		$("#selfdefine-datepicker").css({left:offset.left,top:(offset.top+$(this).outerHeight())}).show();
		periodId=$(this).attr("id");
		$(this).val("");
	});
	
	$("#selfdefine-datepicker .js_confirm").click(function() {
		var month = $("#selfdefine-datepicker .ui-datepicker-month>option:selected").val();//得到选中的月份值
        var year = $("#selfdefine-datepicker .ui-datepicker-year>option:selected").val();//得到选中的年份值
        $("#"+periodId).val(year+'-'+parseInt(month));//给input赋值，其中要对月值加1才是实际的月份
        $("#selfdefine-datepicker").hide();
        if($("#"+periodId+"Tip").length>0){
        	if(periodId=="QDate"){
        		$("#"+periodId+"Tip").css("visibility","hidden");
        	}else{
	        	$("#"+periodId+"Tip").hide();
        	}
        }
	});
	
	$("#selfdefine-datepicker .js_close").click(function() {
		$("#selfdefine-datepicker").hide();
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
	
	$("#Content").load("${ctx}/hfm/reconciliation/list",{orderBy:"id",orderDir:"asc"});
	$("#QueryBtn").click(function(){
		$("#selfdefine-datepicker").hide();
		$("#QDateTip").css("visibility","hidden");
		clickPage(1);
	});
});

var periodId;
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
<div id="selfdefine-datepicker" tabindex="0" class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"
	style="position: fixed; top: 129px; left: 240px; z-index: 1; display: none;width:9em;">
	<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix ui-corner-all">
		<div class="ui-datepicker-title" style="margin:0;">
			<select class="ui-datepicker-year" style="width:50%;">
				<c:forEach items="${yearList }" var="year">
					<option value="${year }">${year }</option>
				</c:forEach>
			</select>
			<select class="ui-datepicker-month" style="width:40%;">
				<c:forEach items="${monthList }" var="month">
					<option value="${month }">${month }</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="ui-datepicker-buttonpane ui-widget-content">
		<button type="button" class="js_close ui-state-default ui-priority-primary ui-corner-all">关闭</button>
		<button type="button" class="js_confirm ui-state-default ui-priority-primary ui-corner-all">确定</button>
	</div>
</div>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
					<form id="reconciliationForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
					    <input type="file" style="display:none;" class="input-file" multiple="false"/>
					    <div>
					    	<div style="float: left;text-align: right;display:none;" title="<spring:message code='not_exceed_30M'/>">
							    <div class="upload-tip">
							    	<span class="tip"><spring:message code='click_select_excel'/></span>
							    </div>
							    <div id="UploadTip" style="display:none;float:left;">
									<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</div>
					    	</div>
							<div style="float:left;padding-left:10px;">
								<ul class="nav dropdown">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='entity'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
											<li class="AllCheck" style="padding:0 10px;clear:both;">
												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>
											</li>
											<c:forEach items="${fn:split(entity,',') }" var="code">
												<c:if test="${not empty code }">
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
							<div style="float:left;margin:0 10px 0 10px;display:inline-block;">
								<div>
						    		<input id="Date" name="date" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='date'/>" type="text" value="" readonly>
								</div>
								<div style="float:left;">
									<span id="DateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
							<div style="float:left;display:none;">
								<button id="FileUpload" style="margin-right:5px;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
					    	<div style="float:left;text-align: center;display:none;">
							    <button id="DownloadTemplate" class="btn btn-link template" type="button"><spring:message code='template'/></button>
							</div>
							<div style="float:left;">
								<button id="DataImport" style="margin-right:5px;" class="btn search-btn" type="button"><spring:message code='dataImport'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-sm" style="clear:both;">
        	<div style="float:left;">
				<button id="Synchronize" style="margin-right:10px;" class="btn search-btn" type="button"><spring:message code='dataSynchronize'/></button>
				<a id="DestinationLog" style="display:none;line-height:38px;font-size:20px;text-align:center;text-decoration: underline;"><spring:message code='destinationLog'/></a>
				<a id="DataSynchronizeLog" style="display:none;margin-right:10px;line-height:38px;font-size:20px;text-align:center;text-decoration: underline;"><spring:message code='dataSynchronizeLog'/></a>
			</div>
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
				<ul class="nav dropdown m-r-sm" style="float:left;">
					<li class="dropdown" style="margin-top:0;">
						<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='entity'/><strong class="caret"></strong></a>
						<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;">
							<li class="AllCheck" style="padding:0 10px;clear:both;">
								<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
								<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>
							</li>
							<c:forEach items="${entityList}" var="code">
								<li class="Check" style="padding:0 10px;clear:both;">
									<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;">${code}</span>
									<input type="checkbox" name="QEntity" style="font-size:15px;color:#7e8978;float:right;width:20px;" value="${code}"/>
								</li>
							</c:forEach>
						</ul>
					</li>
				</ul>
				<div style="display:inline-block;">
					<input id="QDate" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='date'/>" type="text" value="" readonly>
					<span id="QDateTip" style="display:table-cell;visibility: hidden;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/><spring:message code='month'/></span>
				</div>
				<input id="QACCOUNTCODE" list="ACCOUNTCODEList" style="width:125px;" placeholder="<spring:message code='accountCode'/>" type="text" value="">
				<datalist id="ACCOUNTCODEList">
	           		<c:forEach items="${ACCOUNTCODEList }" var="ACCOUNTCODE">
	           			<option value="${ACCOUNTCODE }"/>
	           		</c:forEach>
	           	</datalist>
				<input id="QACCOUNTDESC" list="ACCOUNTDESCList" style="width:125px;" placeholder="<spring:message code='accountName'/>" type="text" value="">
				<datalist id="ACCOUNTDESCList">
	           		<c:forEach items="${ACCOUNTDESCList }" var="ACCOUNTDESC">
	           			<option value="${ACCOUNTDESC }"/>
	           		</c:forEach>
	           	</datalist>
	           	<select id="QISICP" class="input-xlarge" style="width:140px;color:#9f9a9a;font-weight:bold;">
	           		<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='relatedEntity'/></option>
					<option value="Y" ><spring:message code='yes'/></option>
					<option value="N" ><spring:message code='no'/></option>
				</select>
				<input id="QCUSTNAME" list="CUSTNAMEList" style="width:125px;" placeholder="<spring:message code='merchantName'/>" type="text" value="">
				<datalist id="CUSTNAMEList">
	           		<c:forEach items="${CUSTNAMEList }" var="CUSTNAME">
	           			<option value="${CUSTNAME }"/>
	           		</c:forEach>
	           	</datalist>
				<select id="QTC" class="input-xlarge" style="width:180px;color:#9f9a9a;font-weight:bold;">
	           		<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='transactionCurrency'/></option>
					<c:forEach items="${TCList }" var="TC">
						<option value="${TC }" >${TC }</option>
					</c:forEach>
				</select>
	        	<button id="QueryBtn" class="btn search-btn" type="button"><spring:message code='query'/></button>
	        	<button id="Download" class="btn search-btn" type="button"><spring:message code='download'/></button>
	        </div>
		</div>		
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
