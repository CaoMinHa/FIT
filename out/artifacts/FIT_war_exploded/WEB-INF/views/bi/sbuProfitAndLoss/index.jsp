<%@page import="foxconn.fit.util.SecurityUtils"%>
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
	var sbuProfitAndLossFormValid=$("#sbuProfitAndLossForm").Validform({
		tiptype:2
	});
	
	$("#Synchronize").click(function(){
		if(sbuProfitAndLossFormValid.check()){
			var month=$("#Month").val();
			var version=$("#Version").val();
			$("#loading").show(true);
			$.ajax({
				type:"POST",
				url:"${ctx}/bi/sbuProfitAndLoss/synchronize",
				async:true,
				dataType:"json",
				data:{period:month,version:version},
				success: function(data){
					$("#loading").hide();
					layer.alert(data.msg);
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		layer.alert("<spring:message code='connect_fail'/>");
				   	$("#loading").hide();
			   	}
			});
		}
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
		periodId=$(this).attr("id");
		$("#"+periodId+"Tip").find("span").addClass("Validform_wrong").removeClass("Validform_right").text("<spring:message code='please_select'/>");
	});
	$("#ui-datepicker-div").on("click", ".ui-datepicker-close", function() {
		var month = $("#ui-datepicker-div .ui-datepicker-month option:selected").val();//得到选中的月份值
        var year = $("#ui-datepicker-div .ui-datepicker-year option:selected").val();//得到选中的年份值
        $("#"+periodId).val(year+'-'+(parseInt(month)+1));//给input赋值，其中要对月值加1才是实际的月份
        if($("#"+periodId+"Tip").length>0){
        	$("#"+periodId+"Tip").find("span").addClass("Validform_right").removeClass("Validform_wrong").text("");
        }
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
				<span><spring:message code='sbuProfitAndLoss'/></span>
			</h2>
		</div>
		<div class="m-l-md m-t-xl m-r-md">
			<form id="sbuProfitAndLossForm" class="form-horizontal">
			    <div class="control-group">
			    	<label class="control-label" style="margin-left:30%;text-align:center;font-size:20px;width:20%;">實際損益數據</label>
					<div class="controls" style="margin-left:0;">
						<div>
							<input id="Month" style="width:150px;text-align:center;font-size:20px;" placeholder="<spring:message code='please_select'/><spring:message code='month'/>" type="text" datatype="*" nullmsg="<spring:message code='please_select'/>" errormsg="<spring:message code='please_select'/>" value="" readonly>
						</div>
						<div id="MonthTip" style="margin-left:50%;width:45%;" class="Validform_checktip"></div>
					</div>
				</div>
			    <div class="control-group">
			    	<label class="control-label" style="margin-left:30%;text-align:center;font-size:20px;width:20%;">對應周預估版本</label>
					<div class="controls" style="margin-left:0;">
						<div>
							<select id="Version" style="width:150px;height:38px;font-size:20px;" datatype="*" nullmsg="<spring:message code='please_select'/>" errormsg="<spring:message code='please_select'/>">
								<option value=""><spring:message code='version'/></option>
								<c:forEach var="i" begin="1" end="54" step="1">
									<c:choose>
										<c:when test="${i lt 10 }">
											<option value"W0${i }>W0${i }</option>
										</c:when>
										<c:otherwise>
											<option value"W${i }>W${i }</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</div>
						<div style="margin-left:50%;width:45%;" class="Validform_checktip"></div>
					</div>
				</div>
			    <div class="control-group">
					<div class="text-center">
						<button id="Synchronize" style="width:150px;" class="btn search-btn" type="button"><spring:message code='synchronize_to_bi'/></button>
					</div>
				</div>
			</form>
        </div>
	</div>
</div>
</body>
</html>
