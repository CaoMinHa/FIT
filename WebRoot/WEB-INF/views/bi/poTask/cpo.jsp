<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
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
	$("#taskDetails tr:gt(2)").each(function(){
		$(this).children('td').each(function(e){
			if(e>2&&e<11&&e!=6&&e!=10){
					$(this).css("text-align", "right");
			}
		});
	})
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
			toUser();
	    },
		error: function() {
			$("#loading").hide();
			window.location.href="${ctx}/logout";
		}
	});
});
function updateAll(){
	var updateData="";
	var tableObj = document.getElementById("dataUpdate");
	//从第二行开始获取数据
	for (var i = 0; i < tableObj.rows.length; i++) {  //遍历Table的所有Row
			var tableVal=tableObj.rows[i].getElementsByTagName("input");
			if(tableVal.length>1){
				updateData+=tableVal[0].name+"="+tableVal[0].value+"&"+tableVal[1].name+"="+tableVal[1].value+"&"+tableVal[2].name+"="+tableVal[2].value+";";
			}
	}

	$("#loading").show();
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poFlow/updateAll",
		async:true,
		dataType:"json",
		data:{tableName:"FIT_PO_Target_CPO_CD_DTL",updateData:updateData},
		success: function(data){
			layer.alert(data.msg);
			$("#loading").hide();
			toUser();
		},
		error: function() {
			$("#loading").hide();
			window.location.href="${ctx}/logout";
		}
	});
}
function toUser(){
	var id = $("#tId").val();
	var role = $("#roleCode").val();
	var statusType = $("#statusType").val();
	$("#Content").load("${ctx}/bi/poTask/audit",{pageNo:"1",pageSize:"15",id:id,statusType:statusType,role:role},function(){$("#loading").fadeOut(1000);});
}
$(function () {
	$("#taskDetails tbody").find("tr").each(function(){
		var val=$(this).children('td:eq(2)').text();
		if (val==''){
			$(this).children('td:eq(2)').remove();
			$(this).children('td:eq(1)').attr("colspan","2");
			$(this).css("background-color", "#cfecff" );
		}
	});
	$("#taskDetails tbody tr:last").css("background-color", "#7fc4f1");
})
function submitTaskXQYM(e) {
	event.preventDefault();
	var name = $("#taskName").val();
	var id = $("#tId").val();
	var taskType = $("#taskType").val();
	$("#taskName21").val(name);
	$("#modal-audit1").dialog({
		modal: true,
		title: "提交",
		height: 500,
		width: 400,
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
					var t = $("#taskForm1").serializeArray();
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
						url:"${ctx}/bi/poTask/submitTask",
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
function submitOneAuditXQ(e,type,url) {
	var name = $("#taskName").val();
	var id = $("#tId").val();
	var taskType = $("#taskType").val();
	$("#taskName2").val(name);
	$("#modal-audit").dialog({
		modal: true,
		title: type,
		height: 500,
		width: 400,
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
						url:"${ctx}/bi/poTask/"+url,
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
		title: "終審",
		height: 500,
		width: 400,
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
	var name = $("#taskName").val();
	$("#taskName21").val(name);
	$("#modal-audit1").dialog({
		modal: true,
		title: "取消審核",
		height: 500,
		width: 400,
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
					var t = $("#taskForm1").serializeArray();
					$.each(t, function() {
						d[this.name] = this.value;
					});
					var obj={
						id:id,
						remark:d.remark,
						taskType:taskType
					}
					$.ajax({
						type:"POST",
						url:"${ctx}/bi/poTask/cancelAudit",
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
								$("#btnKeyUser").hide();
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

function calculateCpo(m,e) {
	var cd=$(m).val().replaceAll(",","");
	if(Number(cd)==0){
		return;
	}
	var tr=$(m).parent("td").parent("tr");
	if(e=="Y"){
		var r=tr.children('td').eq(7).text().replaceAll(",","");
		r=Number(cd)/(Number(cd)+Number(r))*100;
		tr.children('td').eq(10).children('input').val(r);
	}else{
		var r=tr.children('td').eq(3).text().replaceAll(",","");
		r=Number(cd)/(Number(cd)+Number(r))*100;
		tr.children('td').eq(6).children('input').val(r);
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
					<c:if test="${user == 'N' && statusType == '0'}">
						<button class="btn search-btn btn-warning"
								onclick="submitTaskXQYM(this)"><spring:message code="submit"/></button>
					</c:if>

					<c:if test="${user == 'C' && statusType == '1'}">
						<button  class="btn search-btn btn-warning"
								 type="button"
								 onclick="submitOneAuditXQ(this,'初審','CPOAudit')">
							<spring:message code='praeiudicium'/></button>
					</c:if>

					<c:if test="${user == 'P' && statusType == '10'}">
						<button  class="btn search-btn btn-warning"
								 type="button"
								 onclick="submitOneAuditXQ(this,'審核','submitOneAudit')">審核</button>
					</c:if>
					<c:if test="${user == 'Z' && statusType == '2'}">
						<button  class="btn search-btn btn-warning"
								 type="button"
								 onclick="submitAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">終審</c:if>
							<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></button>
					</c:if>

					<c:if test="${keyUser == 'TS' && statusType == '2'}">
					<button  class="btn search-btn btn-warning"
							 type="button"
							 onclick="submitAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">終審</c:if>
						<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></button>
					</c:if>
					<c:if test="${(statusType == '0'&&user == 'N')||(statusType == '10' &&user == 'P')||(statusType == '2' &&user == 'Z')}">
						<button  class="btn search-btn btn-warning"
								 type="button"
								 onclick="updateAll()"><c:if test="${languageS eq 'zh_CN'}">一鍵更新</c:if>
							<c:if test="${languageS eq 'en_US'}">Updates</c:if></button>
					</c:if>
				</td>
				<td style="margin-top: 10px">
					<button name="btnKeyUser" class="btn search-btn btn-warning" style="
					<c:if test="${user != 'K' || statusType == '0' || statusType == '-1'}">display: none;</c:if>"
							type="button"
							onclick="cancelAudit(this)">取消審批</button>
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
				<th style="text-align:center" >SBU CD比率(%)</th>
				<th style="text-align:center" >CPO Approve(%)</th>
				<th style="text-align:center" >採購金額(NTD)</th>
				<th style="text-align:center">CD金额(NTD)</th>
				<th style="text-align:center" >SBU CD比率(%)</th>
				<th style="text-align:center" >CPO Approve(%)</th>
			</tr>
		</thead>
		<tbody id="dataUpdate">
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;display:none;"><input name="ID" type="text" style="display:none;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==4 && mapping[2] != null}">
								<td style="white-space: nowrap;"><input onBlur="calculateCpo(this,'N')" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==6 && mapping[2] != null}">
								<td style="white-space: nowrap;"><input name="NO_CPO" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==8 && mapping[2] != null}">
								<td style="white-space: nowrap;"><input onBlur="calculateCpo(this,'Y')" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>
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
							<c:if test="${(statusType == '0'&&user == 'N')||(statusType == '10' &&user == 'P')||(statusType == '2' &&user == 'Z')}">
							<span style="width: 1px;">
								<a href="javascript:void(0);" name="update"><spring:message code='update'/></a>
							</span>
							</c:if>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
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
							<spring:message code='submit'/></td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '2'}">
						<td  style="border-right:1px solid #eee;">
							<spring:message code='praeiudicium'/>
						</td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '10'}">
						<td  style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">審核</c:if>
							<c:if test="${languageS eq 'en_US'}">Audit</c:if>
						</td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '3'}">
						<td  style="border-right:1px solid #eee;"><spring:message code='finish'/></td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '-1'}">
						<td  style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">駁回</c:if>
							<c:if test="${languageS eq 'en_US'}">Turn Down</c:if>
						</td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '-2'}">
						<td  style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">用戶取消</c:if>
							<c:if test="${languageS eq 'en_US'}">User cancelled</c:if>
						</td>
					</c:when>
					<c:when test="${taskLog.FLAG eq '-3'}">
						<td  style="border-right:1px solid #eee;">
							<c:if test="${languageS eq 'zh_CN'}">管理員取消</c:if>
							<c:if test="${languageS eq 'en_US'}">Administrator cancel</c:if>
						</td>
					</c:when>
					<c:otherwise>
						<td  style="border-right:1px solid #eee;"></td>
					</c:otherwise>
				</c:choose>
				<td>${taskLog.REMARK}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>
</body>
</html>