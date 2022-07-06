<%@page import="foxconn.fit.entity.base.EnumDimensionType"%>
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
	$("#dimensionForm").fileupload({
		dataType: "json",
	    url: "${ctx}/admin/dimension/upload",
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
            $("#TypeTip").hide();
            
            $("#FileUpload").click(function(){
            	if($("#Type").val().length==0){
                   	$("#TypeTip").show();
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
		if($("#Type").val().length==0){
           	$("#TypeTip").show();
        }
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#UploadTip").hide();
		var type=$("#Type").val();
		if(type.length==0){
           	$("#TypeTip").show();
           	return false;
        }
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/admin/dimension/download",
			async:true,
			dataType:"json",
			data:{type:type},
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
	
	$("#Type").change(function(){
		if($(this).val().length>0){
			$("#TypeTip").hide();
		}
	});
	
	$("#Content").load("${ctx}/admin/dimension/list",{orderBy:"type,dimension",orderDir:"asc,asc"},function () {
		if($("#QType").val()=="Entity"){
			$(".ouName").show();
		}else{
			$(".ouName").hide();
		}
	});

	$("#QueryBtn").click(function(){
		clickPage(1);
	});

	$("#QType").change(function () {
		if($(this).val()=="Entity"){
           $(".ouName").show();
		}else{
			$(".ouName").hide();
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
				<span><spring:message code='dimension'/></span>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
					<form id="dimensionForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
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
					    	<div style="float:left;margin-left:20px;display:inline-block;">
						    	<div>
							    	<select id="Type" name="type" class="input-large">
						           		<option value=""><spring:message code='dimensionType'/></option>
										<c:forEach items="<%=EnumDimensionType.values() %>" var="dimension">
											<option value="${dimension.code }" >${dimension.code }</option>
										</c:forEach>
									</select>
								</div>
								<div style="float:left;">
									<span id="TypeTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
							<div style="float:left;margin-right:20px;">
								<button id="FileUpload" style="width:100px;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
							<div style="float:left;margin:0 20px 30px 0;">
								<button id="Download" style="width:100px;" class="btn search-btn" type="button"><spring:message code='download'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
	        <div class="controls text-center">
	           	<select id="QType" class="input-large">
	           		<option value=""><spring:message code='dimensionType'/></option>
					<c:forEach items="<%=EnumDimensionType.values() %>" var="dimension">
						<option value="${dimension.code }" >${dimension.code }</option>
					</c:forEach>
				</select>
	        	<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin:0 0 0 5%" type="submit"><spring:message code='query'/></button>
	        </div>
		</div>		
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
