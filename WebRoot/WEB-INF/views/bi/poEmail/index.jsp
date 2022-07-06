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
	.modal-backdrop {
		position: initial!important;
	}
	.modal-header{
		background-color: #f5f5f5!important;
	}
	.ui-datepicker-buttonpane{display:none;}
</style>
<script type="text/javascript">
$(document).ready(function(){
	$("#ui-datepicker-div").remove();
	$("#endDate").datepicker({
		dateFormat: 'yy-m-dd',
		showButtonPanel: true,
		changeYear:true,
		changeMonth:true
	});

	var array=new Array();


	$("#FileUpload").click(function(){
		$("#loading").show();
		if($("input[type='checkbox']:checked").length===0){
			layer.msg("請選擇用戶分組！");
		}else if(!$("#emailTitle").val()){
			layer.msg("請填寫郵件主題！");
		}else if(!$("#emailContent").val()){
			layer.msg("郵件内容不能爲空！");
		}else{
			$.ajax({
				type:"POST",
				url:"${ctx}/bi/poEmail/sendEmail",
				async:true,
				dataType:"json",
				data:{
					emailGroup:$("#groupUserText").val(),
					title:$("#emailTitle").val(),
					content:$("#emailContent").val(),
					endDate:$("#endDate").val(),
					type:"1"
				},
				success: function(data){
					if(data.flag=="success"){
						layer.alert("郵件發送成功！");
						$("#loading").hide();
					}else{
						layer.alert(data.msg);
						$("#loading").hide();
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					layer.alert("發送失敗！");
					$("#loading").hide();
				}
			});
		}
	});

	$(".groupUser").change(function(e) {
		var name=$(this).attr("name");
		if(this.checked){
			$("input[name='"+name+"']").prop("checked",this.checked);
		}else{
			$("input[name='"+name+"']").prop("checked",false);
		}
	});
	$("#affirmBut").click(function () {
		var valueUser='';
		$(".userGroupVal:checked").each(function () {
			valueUser+=$(this).val()+",";
		})
		$("#groupUserText").val(valueUser.substring(0,valueUser.length-1));
	})
	$("#closeBut").click(function () {
			$("input[type='checkbox']").prop("checked",false);
	})
	$("#resetBtn").click(function () {
		$("input[type='checkbox']").prop("checked",false);
		$("#groupUserText").val("");
		$("#emailTitle").val("");
		$("#emailContent").val("");
		$("#endDate").val("");
		$(".tip").text("請選擇文件");
		$(".upload-tip").attr("title", "請選擇文件");
		$("#excelVal span").remove();
		array=new Array();
	});

	$("#poEmailA").fileupload({
		dataType: "json",
		url: "${ctx}/bi/poEmail/sendEmail",
		singleFileUploads: false,
		add: function (e, data) {
			debugger;
			array[array.length]=data.files[0];
			$("#FileUpload").unbind();
			var filename = data.originalFiles[0]['name'];
			if (data.originalFiles[0]['size'] > 1024 * 1024 * 30) {
				layer.alert("<spring:message code='not_exceed_30M'/>");
				return;
			}
			$("#excelVal").append("<span name='"+data.originalFiles[0].lastModified+"'>"+filename+"<button id='"+data.originalFiles[0].lastModified+"' name='deleteBtn' type='button' style='color: red;background-color: white;border: #f3f3f3;'>&times;</button></br></span>");
			data.originalFiles=array;
			data.files=array;
			$("#FileUpload").click(function () {
				data.originalFiles=array;
				data.files=array;
				if($("input[type='checkbox']:checked").length===0){
					layer.msg("請選擇用戶分組！");
				}else if(!$("#emailTitle").val()){
					layer.msg("請填寫郵件主題！");
				}else if(!$("#emailContent").val()){
					layer.msg("郵件内容不能爲空！");
				}else{
					$("#loading").show();
					debugger;
					if(array.length==0){
						$.ajax({
							type:"POST",
							url:"${ctx}/bi/poEmail/sendEmail",
							async:true,
							dataType:"json",
							data:{
								emailGroup:$("#groupUserText").val(),
								title:$("#emailTitle").val(),
								content:$("#emailContent").val(),
								endDate:$("#endDate").val(),
								type:"1"
							},
							success: function(data){
								if(data.flag=="success"){
									layer.alert("郵件發送成功！");
									$("#loading").hide();
								}else{
									layer.alert(data.msg);
									$("#loading").hide();
								}
							},
							error: function(XMLHttpRequest, textStatus, errorThrown) {
								layer.alert("發送失敗！");
								$("#loading").hide();
							}
						});
					}else{
						data.submit();
					}
				}
			});
			$("button[name='deleteBtn']").click(function () {
				debugger;
				var a=$(this).attr("id");
				for(var i=0;i<array.length;i++){
					if(array[i].lastModified==a){
						array.splice(i,1);
						$("span[name='"+a+"']").remove();
						break;
					}
				}
			})
		},
		done: function (e, data) {
			$("#loading").delay(1000).hide();
			layer.alert(data.result.msg);
			console.log("qqq")

			$.each(data.result.files, function (index, file) {
				$('<p/>').text(file.name).appendTo(document.body);
			});
		},
		fail: function (e, data) {
			$("#loading").delay(1000).hide();
			layer.alert("郵件發送成功！");
			console.log("222")
		},
		processfail: function (e, data) {
			$("#loading").delay(1000).hide();
			layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
			console.log("333")
		}
	});
	$(".upload-tip").click(function () {
		$(".input-file").trigger("click");
	});
	$('#poEmailA').bind('fileuploadsubmit', function (e, data) {
		data.formData={
			emailGroup:$("#groupUserText").val(),
					title:$("#emailTitle").val(),
					content:$("#emailContent").val(),
					endDate:$("#endDate").val(),
			type:"2"
		};
	});
})
</script>

</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><c:if test="${languageS eq 'zh_CN'}">SBU VOC 收集</c:if><c:if test="${languageS eq 'en_US'}">SBU VOC Collection</c:if></span>
			</h2>
		</div>
	</div>
	<div class="p-l-md p-r-md p-b-md">
		<div style="width:95%;">
			<form style="margin-top: 90px;text-align: center;">
				<div>
					<label style="display: inline!important;">用戶分組：</label>
					<input id="groupUserText" style="width: 615px;" readonly type="text" placeholder="請點擊按鈕選擇用戶分組">
					<a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#myModal">
						選擇用戶
					</a>
				</div>
				<div>
					<label style="display: inline!important;" >郵件主題：</label>
					<input id="emailTitle" style="width: 700px;" type="text" placeholder="请输入邮箱主题">
				</div>
				<div>
					<label style="display: inline!important;">郵件内容：</label>
					<textarea style="width: 700px" id="emailContent" rows="10"></textarea>
				</div>
				<div style="margin-right: 210px;">
					<label style="display: inline!important;">截止时间：</label>
					<input id="endDate" style="z-index: 9999;background-color: #ffffff;
                               width: 200px;text-align:center;"
						   placeholder="截止时间"
						   type="text" value="" readonly>
					<font style="font-size: smaller" color="red">截止日期為系統自動通知功能所用，如不需要請勿設置</font>
				</div>
			</form>
			<form id="poEmailA" style="margin-bottom: 0;margin-top:0;text-align: center;" method="POST" enctype="multipart/form-data"
				  class="form-horizontal" >
				<input type="file"  class="input-file" multiple="false" style="display:none;" />
				<div style="text-align: center;" title="<spring:message code='not_exceed_30M'/>">
					<div class="upload-tip" style="margin-left: 522px">
						<span class="tip">請選擇文件</span>
					</div>
					<div id="excelVal">
					</div>
				</div>
				<br>
				<div>
					<button  id="FileUpload"  type="button" style="width: 100px;margin-right: 10px;" class="btn btn-danger"><spring:message code="submit"/></button>
					<button type="button" id="resetBtn" style="width: 100px;margin-left: 10px;"  class="btn">重置</button>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade" id="myModal" style="display: none" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					用戶分組
				</h4>
			</div>
			<div class="modal-body">
				<table  border="0" cellpadding="0" cellspacing="1">
						<c:forEach items="${listGroup}" var="columnV" varStatus="statusV">
							<c:forEach items="${columnV}" var="columnVV" varStatus="statusVV">
								<c:if test="${statusVV.index %4 eq 0}">
									<tr>
								</c:if>
								<c:choose>
									<c:when test="${statusVV.index eq 0}">
											<td>
												<input type="checkbox" class="groupUser" name="${statusV.count}" value="${columnVV}">
												<font color="blue" style="font-weight: bold">${columnVV}:</font>
											</td>
									</c:when>
									<c:otherwise>
											<td  height="25px" width="140px">
												<input type="checkbox" class="userGroupVal" name="${statusV.count}" value="${columnVV}">${columnVV}
											</td>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<tr><td colspan="4">&nbsp;</td></tr>
						</c:forEach>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" id="closeBut" class="btn btn-default" data-dismiss="modal"><spring:message code="close"/>
				</button>
				<button type="button" id="affirmBut" class="btn btn-primary" data-dismiss="modal"><spring:message code="submit"/></button>
			</div>
		</div>
	</div>
</div>
</body>
</html>
