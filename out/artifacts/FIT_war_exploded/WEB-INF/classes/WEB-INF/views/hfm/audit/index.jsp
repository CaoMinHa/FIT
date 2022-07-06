<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
	String entity=SecurityUtils.getEntity();
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
	$("#auditForm").fileupload({
		dataType: "json",
	    url: "${ctx}/hfm/audit/upload",
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
	            if($("#Date").val().length==0){
	            	$("#DateTip").show();
	            	return;
	            }
	            if($("#UEntity").val().length==0){
					$("#UEntityTip").show();
					return;
				}
	            if($("input[name=tableNames]:checked").length==0){
					$("#TableNamesTip").show();
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
		$("#DEntityTip").hide();
		$("#UploadTip").show();
		if($("#Date").val().length==0){
           	$("#DateTip").show();
        }
		if($("input[name=tableNames]:checked").length==0){
			$("#TableNamesTip").show();
		}
		if($("#UEntity").val().length==0){
			$("#UEntityTip").show();
		}
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#UploadTip").hide();
		$("#UEntityTip").hide();
		var flag=true;
		var date=$("#Date").val();
		if(date.length==0){
           	$("#DateTip").show();
           	flag=false;
        }
		var DEntity=$("#DEntity").val();
		if(DEntity.length==0){
           	$("#DEntityTip").show();
           	flag=false;
        }
		if($("input[name=tableNames]:checked").length==0){
           	$("#TableNamesTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		
		var tableNames="";
		$("input[name=tableNames]:checked").each(function(i,dom){
			tableNames+=$(dom).val()+",";
		});
		tableNames=tableNames.substring(0,tableNames.length-1);
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/audit/download",
			async:true,
			dataType:"json",
			data:{date:date,entity:DEntity,tableNames:tableNames},
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
	
	$("#CheckValidation").click(function(){
		$("#UploadTip").hide();
		$("#UEntityTip").hide();
		var flag=true;
		var date=$("#Date").val();
		if(date.length==0){
           	$("#DateTip").show();
           	flag=false;
        }
		var DEntity=$("#DEntity").val();
		if(DEntity.length==0){
           	$("#DEntityTip").show();
           	flag=false;
        }
		if($("input[name=tableNames]:checked").length==0){
           	$("#TableNamesTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		
		var tableNames="";
		$("input[name=tableNames]:checked").each(function(i,dom){
			tableNames+=$(dom).val()+",";
		});
		tableNames=tableNames.substring(0,tableNames.length-1);
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/audit/check",
			async:true,
			dataType:"json",
			data:{date:date,entity:DEntity,tableNames:tableNames},
			success: function(data){
				$("#loading").hide();
				if(data.msg.length<=100){
					layer.alert(data.msg);
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
		   		layer.alert("<spring:message code='connect_fail'/>");
		   	}
		});
	});
	
	$("#CheckAllValidation").click(function(){
		$("#QTableNameTip").hide();
		$("#QEntityTip").hide();
		var date=$("#QDate").val();
		if(date.length==0){
           	$("#QDateTip").show();
        }else{
        	$("#QDateTip").hide();
        	
        	$("#loading").show();
    		$.ajax({
    			type:"POST",
    			url:"${ctx}/hfm/audit/checkAll",
    			async:true,
    			dataType:"json",
    			data:{date:date},
    			success: function(data){
    				$("#loading").hide();
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
    		   	},
    		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
    		   		$("#loading").hide();
    		   		layer.alert("<spring:message code='connect_fail'/>");
    		   	}
    		});
        }
	});
	
	$("#DownloadTemplate").click(function(){
		if($("input[name=tableNames]:checked").length==0){
           	$("#TableNamesTip").show();
           	return;
        }
		var tableNames="";
		$("input[name=tableNames]:checked").each(function(i,dom){
			tableNames+=$(dom).val()+",";
		});
		tableNames=tableNames.substring(0,tableNames.length-1);
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/audit/template",
			async:true,
			dataType:"json",
			data:{tableNames:tableNames},
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
	
	$("#UEntity,#DEntity,#QTableName,#QEntity").change(function(){
		if($(this).val().length>0){
			$("#"+$(this).attr("id")+"Tip").hide();
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
	
	$("#QueryBtn").click(function(){
		$("#QDateTip").hide();
		var flag=true;
		var tableName=$("#QTableName").val();
		if($("#QTableName").val().length==0){
			$("#QTableNameTip").show();
           	flag=false;
        }
		var entity=$("#QEntity").val();
		if(entity.length==0){
           	$("#QEntityTip").show();
           	flag=false;
        }
		if(!flag){
			return;
		}
		$("#QTableNameTip").hide();
		$("#QEntityTip").hide();
		$("#PageNo").val(1);
		var date=$("#QDate").val();
		$("#loading").show();
		$("#Content").load("${ctx}/hfm/audit/list",{date:date,entity:entity,tableName:tableName},function(){$("#loading").fadeOut(1000);});
	});
	
	$("#ConsolidationDataBtn").click(function(){
		$("#QTableNameTip").hide();
		$("#QEntityTip").hide();
		$("#QTypeTip").hide();
		var date=$("#QDate").val();
		if(date.length==0){
           	$("#QDateTip").show();
           	return;
        }
       	$("#QDateTip").hide();
		var type=$("#QType").val();
		if(type.length==0){
           	$("#QTypeTip").show();
           	return;
        }
       	$("#QTypeTip").hide();
       	
       	$("#loading").show();
   		$.ajax({
   			type:"POST",
   			url:"${ctx}/hfm/audit/consolidationDataDownload",
   			async:true,
   			dataType:"json",
   			data:{date:date,type:type},
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
	
	$("#DataImport").click(function(){
		$("#UploadTip").hide();
		$("#DateTip").hide();
		$("#UEntityTip").hide();
		$("#TableNamesTip").hide();
		$("#DEntityTip").hide();
		var flag=true;
		var date=$("#Date").val();
		if(date.length==0){
           	$("#DateTip").show();
           	flag=false;
        }
		var entity=$("#UEntity").val();
		if(entity.length==0){
			$("#UEntityTip").show();
			flag=false;
		}
		if(!flag){
			return;
		}
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/audit/dataImport",
			async:true,
			dataType:"json",
			data:{date:date,entity:entity},
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
	
});
var periodId;
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='auditPackage'/></span>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
					<form id="auditForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
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
							<div style="float:left;margin-left:15px;display:inline-block;">
								<div>
						    		<input id="Date" name="date" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" value="" readonly>
								</div>
								<div style="float:left;">
									<span id="DateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
						    <div style="float:left;padding-right:10px;">
								<ul style="float:left;margin-left:10px;">
									<li>
										<select id="UEntity" name="entity" class="input-large" style="width:100px;">
							           		<option value=""><spring:message code='entity'/></option>
											<c:forEach items="${fn:split(entity,',') }" var="code">
												<c:if test="${fn:startsWith(code,'F_') }">
													<option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
												</c:if>
											</c:forEach>
										</select>
									</li>
									<li>
										<span id="UEntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="DataImport" style="float:left;" class="btn search-btn" type="button"><spring:message code='dataImport'/></button>
								<ul class="nav dropdown" style="float:left;margin-left:10px;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='tableSelect'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:350px;overflow-y:scroll;min-width:330px;">
											<li class="AllCheck" style="padding:0 10px;clear:both;">
												<span style="font-size: 20px;color: #938a8a;float: left;line-height: 38px;font-weight: bold;"><spring:message code='all_check'/></span>
												<input type="checkbox" style="font-size:15px;color:#7e8978;float:right;width:20px;" value=""/>
											</li>
											<c:forEach items="${auditTableList }" var="auditTable">
												<li class="Check" style="padding:0 10px;clear:both;">
													<span style="font-size:15px;color:#7e8978;float:left;line-height:38px;display:contents;">${auditTable.comments }</span>
													<input type="checkbox" name="tableNames" style="font-size:15px;color:#7e8978;float:right;width:20px;" value="${auditTable.tableName}"/>
												</li>
											</c:forEach>
										</ul>
									</li>
									<li>
										<span id="TableNamesTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="FileUpload" style="float:left;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
							<div style="float:left;padding-right:10px;">
								<ul style="float:left;">
									<li>
										<select id="DEntity" class="input-large" style="width:100px;">
							           		<option value=""><spring:message code='entity'/></option>
											<c:forEach items="${fn:split(entity,',') }" var="code">
												<c:if test="${not empty code }">
													<option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
												</c:if>
											</c:forEach>
										</select>
									</li>
									<li>
										<span id="DEntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
									</li>
								</ul>
								<button id="Download" style="float:left;" class="btn search-btn" type="button"><spring:message code='download'/></button>
								<button id="CheckValidation" style="float:left;" class="btn search-btn" type="button"><spring:message code='checkValidation'/></button>
							</div>
					    	<div style="margin-bottom:30px;">
							    <button id="DownloadTemplate" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 26px;text-decoration: underline;" type="button"><spring:message code='template'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
	        <div class="controls">
	        	<ul style="float:left;">
					<li>
			        	<input id="QDate" style="float:left;width:140px;text-align:center;margin-bottom:0;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" value="" readonly>
					</li>
					<li style="height:30px;">
						<span id="QDateTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
					</li>
				</ul>
				<ul style="float:left;margin-left:20px;">
					<li>
			        	<select id="QEntity" class="input-large" style="width:100px;margin-bottom:0;">
			           		<option value=""><spring:message code='entity'/></option>
							<c:forEach items="${fn:split(entity,',') }" var="code">
								<c:if test="${not empty code }">
									<option value="${fn:substring(code,2,-1) }">${fn:substring(code,2,-1) }</option>
								</c:if>
							</c:forEach>
						</select>
					</li>
					<li style="height:30px;">
						<span id="QEntityTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
					</li>
				</ul>
				<ul style="float:left;margin-left:20px;">
					<li>
			        	<select id="QTableName" class="input-large" style="width:200px;margin-bottom:0;">
			           		<option value=""><spring:message code='tableSelect'/></option>
							<c:forEach items="${auditTableList }" var="auditTable">
								<option value="${auditTable.tableName }">${auditTable.comments }</option>
							</c:forEach>
						</select>
					</li>
					<li style="height:30px;">
						<span id="QTableNameTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
					</li>
				</ul>
	        	<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="submit"><spring:message code='query'/></button>
	        	<c:if test="${attribute eq 'group'}">
		        	<button id="CheckAllValidation" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;float:left;" type="button"><spring:message code='checkAllValidation'/></button>
		        	<ul style="float:left;margin-left:20px;">
						<li>
				        	<select id="QType" class="input-large" style="width:160px;margin-bottom:0;">
				           		<option value=""><spring:message code='please_select'/><spring:message code='type'/></option>
				           		<c:forEach items="${typeList }" var="type">
									<option value="${type }">${type }</option>
				           		</c:forEach>
							</select>
						</li>
						<li style="height:30px;">
							<span id="QTypeTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						</li>
					</ul>
		        	<button id="ConsolidationDataBtn" class="btn search-btn btn-warning m-l-md" style="margin-left:20px;" type="button"><spring:message code='consolidationData'/></button>
	        	</c:if>
	        </div>
		</div>		
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
