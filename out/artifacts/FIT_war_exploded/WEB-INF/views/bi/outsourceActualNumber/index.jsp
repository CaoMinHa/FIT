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
	$("#outsourceActualNumberForm").fileupload({
		dataType: "json",
	    url: "${ctx}/bi/outsourceActualNumber/upload",
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
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/outsourceActualNumber/download",
			async:true,
			dataType:"json",
			data:{month:month},
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
	
	$("#Synchronize").click(function(){
		$("#UploadTip").hide();
		var month=$("#Month").val();
		if(month.length==0){
           	$("#MonthTip").show();
           	return;
        }
		$("#loading").show();
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/outsourceActualNumber/synchronize",
			async:true,
			dataType:"json",
			data:{period:month},
			success: function(data){
				$("#loading").hide();
				layer.alert(data.msg);
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		layer.alert("<spring:message code='connect_fail'/>");
			   	$("#loading").hide();
		   	}
		});
	});
	
	$("#DownloadTemplate").click(function(){
		window.location.href="${ctx}/static/template/bi/外包costdown收集_模板.xlsx";
	});
	
	$("#ui-datepicker-div").remove();
	$("#Month,#QPeriod").datepicker({
		changeMonth: true,
        changeYear: true,
        dateFormat: 'yy-MM',
        showButtonPanel:true,
        closeText:"<spring:message code='confirm'/>"
	});
	
	$("#Month,#QPeriod").click(function(){
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
	
	$("#Content").load("${ctx}/bi/outsourceActualNumber/list",{orderBy:"year,period",orderDir:"asc,asc"});
	$("#QueryBtn").click(function(){
		clickPage(1);
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
				<span><spring:message code='outsourceActualNumber'/></span>
			</h2>
		</div>
		<div class="m-l-md m-t-md m-r-md">
			<div class="controls">
               	<div>
					<form id="outsourceActualNumberForm" style="margin-bottom: 0;margin-top:0;" method="POST" enctype="multipart/form-data" class="form-horizontal">
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
						    		<input id="Month" name="month" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" value="" readonly>
								</div>
								<div style="float:left;">
									<span id="MonthTip" style="display:none;" class="Validform_checktip Validform_wrong"><spring:message code='please_select'/></span>
						    	</div>
						    </div>
							<div style="float:left;margin-left:20px;">
								<button id="FileUpload" style="width:100px;" class="btn search-btn" type="button"><spring:message code='upload'/></button>
							</div>
							<div style="float:left;margin-left:10px;">
								<button id="Download" style="width:100px;margin-left:10px;" class="btn search-btn" type="button"><spring:message code='download'/></button>
							</div>
							<div style="float:left;margin-left:20px;">
								<button id="Synchronize" style="width:140px;" class="btn search-btn" type="button"><spring:message code='synchronize_to_bi'/></button>
							</div>
					    	<div style="text-align: center;margin-bottom:30px;">
							    <button id="DownloadTemplate" class="btn btn-link" style="vertical-align: top;height: 40px;font-size: 20px;text-decoration: underline;" type="button"><spring:message code='template'/></button>
							</div>
					    </div>
					</form>
				</div>
            </div>
        </div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
	        <div class="controls text-center">
	           	<select id="QSBU" class="input-large">
	           		<option value="">- - SBU - -</option>
					<c:forEach items="${codelist }" var="code">
						<option value="${code }" >${code }</option>
					</c:forEach>
				</select>
				<input id="QPeriod" style="width:140px;text-align:center;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" value="" readonly>
	        	<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin:0 0 0 5%" type="submit"><spring:message code='query'/></button>
	        </div>
		</div>		
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
