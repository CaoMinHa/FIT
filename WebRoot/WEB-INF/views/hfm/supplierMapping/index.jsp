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
	margin:0 20px;
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
	$("#supplierMappingForm").fileupload({
		dataType: "json",
	    url: "${ctx}/hfm/supplierMapping/upload",
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
	
	$("#FileUpload").click(function(){
		$("#UploadTip").show();
	});
	
	$(".upload-tip").click(function(){
		$(".input-file").trigger("click");
	});
	
	$("#Download").click(function(){
		$("#UploadTip").hide();
		if($("input[name=Downcode]:checked").length==0){
           	$("#DowncodeTip").show();
           	return;
        }
		
		$("#loading").show();
		
		var corporCode="";
		$("input[name=Downcode]:checked").each(function(i,dom){
			corporCode+=$(dom).val()+",";
		});
		$.ajax({
			type:"POST",
			url:"${ctx}/hfm/supplierMapping/download",
			async:true,
			dataType:"json",
			data:{corporCode:corporCode},
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
		window.location.href="${ctx}/static/template/hfm/供應商映射維護.xlsx";
	});
	
	$("#Content").load("${ctx}/hfm/supplierMapping/list",{orderBy:"corporationCode",orderDir:"asc"});
	$("#QueryBtn").click(function(){
		clickPage(1);
	});
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
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='supplier_mapping'/></span>
			</h2>
		</div>
		<div class="m-t-lg m-r-md">
			<div class="controls">
               	<div class="m-l-lg">
					<form id="supplierMappingForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
					    <input type="file" style="display:none;" class="input-file" multiple="false"/>
					    <div>
					    	<div style="width: 200px;display:inline-block;float:left;" title="<spring:message code='not_exceed_30M'/>">
							    <div class="upload-tip">
							    	<span class="tip"><spring:message code='click_select_excel'/></span>
							    </div>
							    <div id="UploadTip" style="margin-right:90px;display:none;float:right;">
									<span class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
								</div>
					    	</div>
							<div style="float:left;">
								<ul class="nav dropdown" style="float:left;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='corp_code'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:400px;overflow-y:scroll;">
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
								<button id="FileUpload" style="float:left;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
							<div style="float:left;">
								<ul class="nav dropdown" style="float:left;">
									<li class="dropdown" style="margin-top:0;">
										<a data-toggle="dropdown" class="dropdown-toggle" href="#"><spring:message code='corp_code'/><strong class="caret"></strong></a>
										<ul class="dropdown-menu" style="left:-20%;max-height:400px;overflow-y:scroll;">
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
							<div style="text-align: center;margin-bottom:30px;">
							    <button id="DownloadTemplate" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='template'/></button>
							</div>
					    </div>
					</form>
				</div>
    		</div>
    	</div>
		<div class="m-l-md m-t-md m-r-md">
	        <div class="controls text-center">
	           	<select id="QCorporationCode" class="input-large">
	           		<option value=""><spring:message code='corp_code'/></option>
					<c:forEach items="${codelist }" var="code">
						<option value="${code }" >${code }</option>
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