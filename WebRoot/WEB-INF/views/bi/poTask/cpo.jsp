<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
	/*.table thead th{vertical-align: middle;!important;}*/
	.table-condensed td{padding:5px 5px;!important;}

	.table th, .table td {
		border-top: 1px solid #c4c4c4;
		border-right: 1px solid #c4c4c4;
	}
	.table thead th{
		font-size: 11px;
	}
	.table tbody tr td{
		font-size: 10px;
	}
</style>
<head>
<script type="text/javascript">
$(function() {
	$("#roleCode").hide();
	$("#QDate").hide();
	$("#type").hide();
	$("#name").hide();
	$("#Query").hide();
	$("#taskDetails tr:gt(2)").each(function(){
		$(this).children('td').each(function(e){
			if(e>2&&e<11&&e!=6&&e!=10){
					$(this).css("text-align", "right");
			}
		});
	})
	<%--style="<c:if test="${statusType != '0' || user == 'Y'}">display: none;</c:if>">--%>
console.error("********************"+$("#taskDetails tr:eq(3) td span[style='width: 1px;'] a").length);
	if($("#taskDetails tr:eq(3) td span[style='width: 1px;'] a").length!=1){
		$("#taskDetails tr input").attr("disabled","true");
	}
});




$("a[name='update']").click(function(){
	var updateData="";
	$(this).parent().parent().parent().find("input").each(function(i){
		var columnName=$(this).attr("name");
		var value=$(this).val();
		updateData+=columnName+"="+value+"&";
	});
	updateData=updateData.substring(0,updateData.length-1);
	$("#loading").show();
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poFlow/update",
		async:true,
		dataType:"json",
		data:{tableName:"FIT_PO_Target_CPO_CD_DTL",updateData:updateData},
		success: function(data){
			layer.alert(data.msg);
			$("#loading").hide();
	    },
		error: function() {
			$("#loading").hide();
			window.location.href="${ctx}/logout";
		}
	});
	var tId=$("#tId").val();
	var statusType = $("#statusType").val();
	var role=$("#role").val();
	$("#Content").load("${ctx}/bi/poTask/audit",{id:tId,statusType:statusType,role:role});
});

$(function () {
	$("table tbody").find("tr").each(function(){
		var val=$(this).children('td:eq(2)').text();
		if (val==''){
			$(this).children('td:eq(2)').remove();
			$(this).children('td:eq(1)').attr("colspan","2");
			$(this).css("background-color", "#cfecff" );
		}
	});
})


function submitTaskXQYM(e) {
	event.preventDefault();
	$("#loading").show();
	var id = $("#tId").val();
	var taskType = $("#taskType").val();
	var obj={
		id:id,
		taskType:taskType,
		roleCode:$("#roleCode").val()
	}
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poTask/submitTask",
		async:false,
		dataType:"json",
		data:obj,
		success: function(data){
			layer.alert(data.msg);
			if(data.flag=="success"){
				$(e).hide();
				$(".file").hide();
				$(".table tr input").attr("disabled","true");
			}
			$("#loading").hide();
		},
		error: function() {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
}
function submitOneAuditXQ(e) {
	var name = $("#taskName").val();
	var id = $("#tId").val();
	var taskType = $("#taskType").val();
	$("#taskName2").val(name);
	$("#modal-audit").dialog({
		modal: true,
		title: "一级審核",
		height: 400,
		width: 350,
		position: "center",
		draggable: true,
		resizable: true,
		autoOpen: false,
		autofocus: false,
		closeText: "<spring:message code='close'/>",
		buttons: [
			{
				text: "<spring:message code='submit'/>",
				click: function () {
					var $dialog = $(this);
					var d = {};
					$("#loading").show();
					var t = $("#taskForm").serializeArray();
					$.each(t, function() {
						d[this.name] = this.value;
					});
					var flag=d.flag;
					var obj={
						id:id,
						status:flag,
						remark:d.remark,
						taskType:taskType,
						roleCode:$("#roleCode").val()
					}
					$.ajax({
						type:"POST",
						url:"${ctx}/bi/poTask/submitOneAudit",
						async:false,
						dataType:"json",
						data:obj,
						success: function(data){
							$dialog.dialog("destroy");
							layer.alert(data.msg);
							if(data.flag=="success"){
								$(".table tr input").attr("disabled","true");
								$(e).hide();
								$(".file").hide();
							}
							$("#loading").hide();
							// refresh();
						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							layer.alert("<spring:message code='connect_fail'/>");
						}
					});
				}
			},
			{
				text: "<spring:message code='close'/>",
				click: function () {
					$(this).dialog("destroy");
					$("#rolenameTip").hide();
				}
			}
		],
		close: function () {
			$(this).dialog("destroy");
			$("#rolenameTip").hide();
		}
	}).dialog("open");
}
function submitAuditXQ(e) {
	var name = $("#taskName").val();
	var id = $("#tId").val();
	var taskType = $("#taskType").val();
	$("#taskName2").val(name);
	$("#modal-audit").dialog({
		modal: true,
		title: "二级審核",
		height: 400,
		width: 350,
		position: "center",
		draggable: true,
		resizable: true,
		autoOpen: false,
		autofocus: false,
		closeText: "<spring:message code='close'/>",
		buttons: [
			{
				text: "<spring:message code='submit'/>",
				click: function () {
					var $dialog = $(this);
					$("#loading").show();
					var d = {};
					var t = $("#taskForm").serializeArray();
					$.each(t, function() {
						d[this.name] = this.value;
					});
					var flag=d.flag;
					var obj={
						id:id,
						status:flag,
						remark:d.remark,
						taskType:taskType
					}
					$.ajax({
						type:"POST",
						url:"${ctx}/bi/poTask/submitAudit",
						async:false,
						dataType:"json",
						data:obj,
						success: function(data){
							$dialog.dialog("destroy");
							layer.alert(data.msg);
							if(data.flag=="success"){
								$(".table tr input").attr("disabled","true");
								// refresh();
								$(e).hide();
								$(".file").hide();
							}
							$("#loading").hide();
						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							layer.alert("<spring:message code='connect_fail'/>");
						}
					});
				}
			},
			{
				text: "<spring:message code='close'/>",
				click: function () {
					$(this).dialog("destroy");
					$("#rolenameTip").hide();
				}
			}
		],
		close: function () {
			$(this).dialog("destroy");
			$("#rolenameTip").hide();
		}
	}).dialog("open");
}


function cancelAudit(e) {
	var taskType = $("#taskType").val();
	var id = $("#tId").val();
	var obj={
		id:id,
		taskType:taskType
	}
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poTask/cancelAudit",
		async:false,
		dataType:"json",
		data:obj,
		success: function(data){
			// refresh();
			$(e).hide();
			$("#loading").hide();
			layer.alert(data.msg);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
}

$(function () {
	$("#taskFileForm").fileupload({
		dataType: "json",
		url: "${ctx}/bi/poTask/upload",
		add: function (e, data) {
			$("#FileUpload").unbind();
			var filename = data.originalFiles[0]['name'];
			var acceptFileTypes = /(\.|\/)(xls|xlsx|XLS|XLSX)$/i;
			if (filename.length && !acceptFileTypes.test(filename)) {
				$(".tip").text("<spring:message code='click_select_excel'/>");
				layer.alert("<spring:message code='only_support_excel'/>");
				return;
			}
			if (data.originalFiles[0]['size'] > 1024 * 1024 * 30) {
				$(".tip").text("<spring:message code='click_select_excel'/>");
				layer.alert("<spring:message code='not_exceed_30M'/>");
				return;
			}
			$(".tip").text(filename);
			$(".upload-tip").attr("title", filename);
			$("#FileUpload").click(function () {
				$("#loading").show();
				data.submit();
			});
		},
		done: function (e, data) {
			$("#loading").delay(1000).hide();
			layer.alert(data.result.msg);
			$("#tableFile tr:eq(0)").append("<td>" +
					"<a  href='###' id='"+data.result.fileId+"' onclick=\"fileClick(this,'"+data.result.fileId+"/"+data.result.fileName+"')\">"+data.result.fileName+"&nbsp;&nbsp;&nbsp;</a>"+
					"</td>");
		},
		fail: function () {
			$("#loading").delay(1000).hide();
			layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
		},
		processfail: function (e, data) {
			$("#loading").delay(1000).hide();
			layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
		}
	});
	$(".upload-tip").click(function () {
		$(".input-file").trigger("click");
	});

	//最后加载判断是否显示上传文档
	if($("#tableButton tr:first td:eq(2) button[style='display: none;']").length<4){
		$(".file").show();
	}else{
		$(".file").hide();
	}
})
function fileClick(e,val) {
	//最后加载判断是否显示上传文档
	if($("#tableButton tr:first td:eq(2) button[style='display: none;']").length<4){
		var index = layer.confirm('刪除還是下載？', {
			btn: ['下載','刪除','关闭'], //按钮
			shade: false //不显示遮罩
		}, function(){
			//下載
			<%--window.location.href = "${ctx}/static/taskFile/"+val;--%>
			var tempwindow=window.open('_blank');
			tempwindow.location= "${ctx}/static/taskFile/"+val;
			layer.close(index);
		}, function(){
			layer.close(index);
			$("#loading").show();
			//刪除
			$.ajax({
				type:"POST",
				url: "${ctx}/bi/poTask/deleteUrl",
				async:true,
				dataType:"json",
				data:{fileId:$(e).attr("id")},
				success: function(data){
					$(e).parent('td').remove();
					$("#loading").hide();
					layer.alert(data.msg);
				},
				error: function(data) {
					$("#loading").hide();
					layer.alert(data.msg);
				}
			});
		}, function(){
			//关闭提示框
			layer.close(index);
		});
	}else{
		var index = layer.confirm('下載文件', {
			btn: ['下載','关闭'], //按钮
			shade: false //不显示遮罩
		}, function(){
			//下載
			<%--window.location.href = "${ctx}/static/taskFile/"+val;--%>
			var tempwindow=window.open('_blank');
			tempwindow.location= "${ctx}/static/taskFile/"+val;
			layer.close(index);
		}, function(){
			//关闭提示框
			layer.close(index);
		});
	}
}

</script>
</head>
<body>
<div style="width:99%;">
	<form id="taskFileForm" method="POST" enctype="multipart/form-data" class="form-horizontal">
		<input type="file" style="display:none;" class="input-file" multiple="false"/>
		<input style="display: none" id="taskId" name="taskId" value="${taskId}">
		<h3 style="margin-top: -35px;margin-bottom: 20px;">${taskName}</h3>
		<table id="tableButton" style="margin-top: -25px">
			<tr>
				<td class="file">
					<div title="<spring:message code='not_exceed_30M'/>，上傳文件格式（.xls/.xlsx/.pdf）">
						<div class="upload-tip" >
							<span class="tip">
								<c:if test="${languageS eq 'zh_CN'}">上傳附檔</c:if>
                                <c:if test="${languageS eq 'en_US'}">Upload the attached</c:if>
								</span>
						</div>
					</div>
				</td>
				<td class="file">
					<button id="FileUpload"  class="btn search-btn btn-warning" type="button">
						<spring:message code='upload'/></button>
					<input style="display: none" id="tId" value="${taskId}">
					<input style="display: none" id="taskType" value="${taskType}">
					<input style="display: none" id="taskName" value="${taskName}">
					<input style="display: none" id="role" value="${role}">
					<input style="display: none" id="statusType" value="${statusType}">
				</td>
				<td>
					<button class="btn search-btn btn-warning"
							<c:if test="${user != 'N' || statusType != '0'}">style="display: none;"</c:if>
							onclick="submitTaskXQYM(this)"><spring:message code="submit"/><spring:message code="submit"/></button>
					<button  class="btn search-btn btn-warning"
							 <c:if test="${user != 'C' || statusType != '1'}">style="display: none;"</c:if>
							 type="button"
							 onclick="submitOneAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">初審</c:if>
						<c:if test="${languageS eq 'en_US'}">Praeiudicium</c:if></button>
					<button  class="btn search-btn btn-warning"
							 <c:if test="${user != 'Z' || statusType != '2'}">style="display: none;"</c:if>
							 type="button"
							 onclick="submitAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">終審</c:if>
						<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></button>
					<button  class="btn search-btn btn-warning"
							 <c:if test="${user != 'TS' || statusType != '2'}">style="display: none;"</c:if>
							 type="button"
							 onclick="submitAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">終審</c:if>
						<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></button>
				</td>
				<td style="margin-top: 10px">
					<button  class="btn search-btn btn-warning" style="margin-left: -5px;
					<c:if test="${user != 'K' }">display: none;</c:if>"
							 type="button"
							 onclick="cancelAudit()">取消審批</button>
					<button  class="btn search-btn btn-warning" style="margin-left: -5px;
					<c:if test="${user != 'TS' }">display: none;</c:if>"
							 type="button"
							 onclick="cancelAudit()">取消審批</button>
				</td>
			</tr>
		</table>
		</p>
		<table id="tableFile">
			<tr>
				<c:forEach items="${fileList}" var="file">
					<td>
						<a href="###" id="${file.FILEID}" onclick="fileClick(this,'${file.FILEID}/${file.FILENAME}')">${file.FILENAME}&nbsp;&nbsp;&nbsp;</a>
					</td>
				</c:forEach>
			</tr>
		</table>
	</form>
	<table id="taskDetails" align="center" class="table table-condensed table-hover" >
		<thead style="background-color: #cfecff;">
			<tr>
				<th style="text-align:center" rowspan="3">採購機能</th>
				<th style="text-align:center" rowspan="3">Commodity</th>
				<th style="text-align:center" colspan="8" >Year Total</th>
				<th style="text-align:center" rowspan="3">處理人</th>
				<th style="text-align:center" rowspan="3">處理日期</th>
				<th style="text-align:center" rowspan="3">操作</th>
			</tr>
			<tr>
				<th  style="text-align:center"  style="text-align:center"  colspan="4">非客指</th>
				<th  style="text-align:center"  colspan="4">客指</th>
			</tr>
			<tr>
				<th style="text-align:center">採購金額(NTD)</th>
				<th style="text-align:center">CD金额(NTD)</th>
				<th style="text-align:center" >CD比率(%)</th>
				<th style="text-align:center" >CPO Approve(%)</th>
				<th style="text-align:center" >採購金額(NTD)</th>
				<th style="text-align:center">CD金额(NTD)</th>
				<th style="text-align:center" >CD比率(%)</th>
				<th style="text-align:center" >CPO Approve(%)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;display:none;"><input name="ID" type="text" style="display:none;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==6 && mapping[2] != null}">
								<td style="white-space: nowrap;"><input name="NO_CPO" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==10 && mapping[2] != null}">
								<td style="white-space: nowrap;"><input name="CPO" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>
							</c:when>
							<c:otherwise>
								<td>${mapping[i]}</td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<td style="white-space: nowrap; border-right:1px solid #eee;">
						<c:if test="${mapping[2] != null}">
							<span style="width: 1px;<c:if test="${user != 'N' || statusType != '0'}">display: none;</c:if>">
								<a href="javascript:void(0);" name="update"><spring:message code='update'/></a>
							</span>
							<span style="width: 1px;<c:if test="${user != 'C' || statusType != '1'}">display: none;</c:if>">
								<a href="javascript:void(0);" name="update"><spring:message code='update'/></a>
							</span>
							<span style="width: 1px;<c:if test="${user != 'Z' || statusType != '2'}">display: none;</c:if>">
								<a href="javascript:void(0);" name="update"><spring:message code='update'/></a>
							</span>
							<span style="width: 1px;<c:if test="${user != 'TS' || statusType != '2'}">display: none;</c:if>">
								<a href="javascript:void(0);" name="update"><spring:message code='update'/></a>
							</span>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<%--<div id="Fenye"></div>--%>
<%--<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />--%>
<%--<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />--%>
<%--<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />--%>
<%--<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />--%>

<c:if test="${fn:length(taskLogList) gt 0}">
	<h3>
		<c:if test="${languageS eq 'zh_CN'}">審批日志</c:if>
		<c:if test="${languageS eq 'en_US'}">Approval log</c:if>
	</h3><br>
	<table style="margin-top: -25px" class="table table-condensed table-hover">
		<thead>
		<tr>
			<th style="width: 10%">操作人</th>
			<th style="width: 15%">操作時間</th>
			<th style="width: 10%">狀態</th>
			<th style="width: 70%">審批意見</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${taskLogList}" var="taskLog">
			<tr>
				<td>${taskLog.CREATE_USER}</td>
				<td>${taskLog.CREATE_TIME}</td>
				<c:choose>
					<c:when test="${taskLog.FLAG eq '1'}">
						<td style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">提交</c:if>
							<c:if test="${languageS eq 'en_US'}">Submit</c:if></td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '2'}">
						<td  style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">初審</c:if>
							<c:if test="${languageS eq 'en_US'}">Praeiudicium</c:if>
						</td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '3'}">
						<td  style="border-right:1px solid #eee;"><c:if test="${languageS eq 'zh_CN'}">终審</c:if>
							<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '-1'}">
						<td  style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">駁回</c:if>
							<c:if test="${languageS eq 'en_US'}">Turn Down</c:if>
						</td>
					</c:when>
				</c:choose>
				<td>${taskLog.REMARK}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>
</body>
</html>