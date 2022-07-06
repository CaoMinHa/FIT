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
.ui-datepicker-calendar,.ui-datepicker-current{display:none;}
.ui-datepicker-close{float:none !important;}
.ui-datepicker-buttonpane{text-align: center;}
.table thead th{vertical-align: middle;}
.modal-backdrop {
	position: initial!important;
}
</style>
<script type="text/javascript">
$(function() {
	$("#masterDataForm").fileupload({
		dataType: "json",
	    url: "${ctx}/bi/rtMix/upload",
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
	            if($("#MasterData").val().length==0){
	    			$("#MasterDataTip").show();
	    			return
	    		}
	            
            	$("#loading").show();
            	data.submit();
    		});
        },
	    done:function(e,data){
	    	$("#loading").delay(1000).hide();
	    	layer.alert(data.result.msg);
			refresh();
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
	 	if($("#MasterData").val().length==0){
			$("#MasterDataTip").show();
		}
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#UploadTip").hide();
		$("#MasterDataTip").hide();
		var masterData=$("#MasterData").val();
		if(!masterData){
			$("#MasterDataTip").show();
			return;
		}
		$("#loading").show();
		var queryCondition=$("#QueryCondition").serialize();
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/rtMix/download",
			async:true,
			dataType:"json",
			data:{masterData:masterData,queryCondition:queryCondition},
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
	
	$("#Refresh").click(function(){
		$("#UploadTip").hide();
		$("#MasterDataTip").hide();
		var masterData=$("#MasterData").val();
		if(!masterData){
			$("#MasterDataTip").show();
			return;
		}
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/rtMix/refresh",
			async:true,
			dataType:"json",
			data:{masterData:masterData},
			success: function(data){
				$("#loading").hide();
				console.log(data)
				layer.alert(data.msg);

				if(data.flag=="success"){
					$("#Query").trigger("click");
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		$("#loading").hide();
		   		window.location.href="${ctx}/logout";
		   	}
		});
	});
	
	$("#MasterData").change(function(){
		$("#insertFrom input").val("");
		$("#insertFrom").empty();
		$("#QueryCondition").empty();
		$("#Content").empty();
		if($(this).val()!=""){
			$("#MasterDataTip").hide();
			$("#loading").show();
			var masterData=$(this).val();
			if(masterData.indexOf("SBU")>= 0){
               $("#FileUpload").hide()
			}else{
				$("#FileUpload").show();
			}
			$.ajax({
				type:"POST",
				url:"${ctx}/bi/rtMix/queryRtMapping",
				async:true,
				dataType:"json",
				data:{masterData:masterData},
				success: function(data){
					$("#loading").hide();
					if(data.flag=="success"){
						$.each(data.queryList, function(i, n){
							var COL_NAME=n[0];
							var COL_DESC=n[1];
							$("#QueryCondition").append("<input name='"+COL_NAME+"' class='m-l-md' style='width:150px;' placeholder='"+COL_DESC+"' type='text' value=''>");
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
		$("#UploadTip").hide();
		$("#MasterDataTip").hide();
		var masterData=$("#MasterData").val();
		if(!masterData){
			$("#MasterDataTip").show();
			return;
		}
		$("#loading").show();
		var queryCondition=$("#QueryCondition").serialize();
		$("#Content").load("${ctx}/bi/rtMix/list",{masterData:masterData,queryCondition:queryCondition},function(){$("#loading").fadeOut(1000);});
	});
	$("#insert").click(function () {
		if(!$("#MasterData").val()){
			layer.msg("請先選擇營收映射表！(Please select the revenue mapping table first)");
		}else{
			$("#myModal").modal('show');
		}
	})
});
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='rtMix'/></span>
			</h2>
		</div>
		<div class="m-t-md m-r-md">
			<div class="controls">
               	<div class="m-l-md">
					<form id="masterDataForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
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
							<div style="float: left;margin:0 10px 0 20px;">
							    <div>
							    	<select id="MasterData" name="masterData" class="input-xlarge" style="width:120px;color:#9f9a9a;font-weight:bold;">
										<option value="" style="color:#9f9a9a;font-weight:bold;"><spring:message code='rtMix'/></option>
										<c:forEach items="${supplierList }" var="supplier">
											<option value="${fn:split(supplier,'|')[0]}">${fn:split(supplier,'|')[1]}</option>
										</c:forEach>
									</select>
							    </div>
							    <div id="MasterDataTip" style="display:none;">
									<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</div>
					    	</div>
							<div style="float:left;text-align: center;margin-bottom:30px;">
								<button id="Refresh" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='refresh'/></button>
								<button id="FileUpload" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
								<button id="Download" style="margin:0 10px;" class="btn search-btn" type="button"><spring:message code='download'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-sm" style="clear:both;">
	        <div class="controls" style="display:inline-block;vertical-align:top;width:100%;">
	        	<form id="QueryCondition" style="float:left;margin:0;"></form>
	        	<button id="Query" class="btn search-btn btn-warning m-l-md" type="button"><spring:message code='query'/></button>
	        	<button id="insert" class="btn search-btn btn-warning m-l-md" type="button" >
					<c:if test="${languageS eq 'zh_CN'}">新增</c:if>
					<c:if test="${languageS eq 'en_US'}">New</c:if>
				</button>
	        </div>
		</div>
		<div class="p-l-md p-r-md p-b-md p-t-sm" id="Content" style="max-width:150%;"></div>
	</div>
</div>
</body>
</html>
