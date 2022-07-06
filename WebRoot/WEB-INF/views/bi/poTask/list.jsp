<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
</style>
<head>
<script type="text/javascript">
var Page;
$(function() {
	if("${detailsTsak}" == "Y"){
		var res = $("#roleCode").find("option[value='${roleCode}']");
		if (res.length > 0) {
			$("#roleCode").val("${roleCode}");
			$("#task").hide();
			$("#audit").show();
			$("#loading").show();
			$("#Content").load("${ctx}/bi/poTask/audit",{pageNo:"1",pageSize:"15",id:"${taskId}",
				statusType:"${statusType}",role:"${roleCode}"},function(){$("#loading").fadeOut(1000);});
		}
	}
	Page=$("#Fenye").myPagination({
		currPage : eval('${fn:escapeXml(page.pageNo)}'),
		pageCount: eval('${fn:escapeXml(page.totalPages)}'),
		pageNumber : 5,
		panel : {
			tipInfo_on : true,
			tipInfo : '跳{input}/{sumPage}页',
			tipInfo_css : {
			width : "20px",
			height : "20px",
			border : "2px solid #f0f0f0",
			padding : "0 0 0 5px",
			margin : "0 5px 20px 5px",
			color : "red"
			}
		},
		ajax: {
            on: false,
            url:"",
            pageCountId : 'pageCount',
			param:{on:true,page:1},
            dataType: 'json',
            onClick:clickPage,
            callback:null
	   }
	});

	$("#Fenye input:first").bind("blur",function(){
		Page.jumpPage($(this).val());
		clickPage(Page.getPage());
	});
	$("#Fenye input:first").bind("keypress",function(){
		if(event.keyCode == "13"){
			Page.jumpPage($(this).val());
			clickPage(Page.getPage());
		}
	});
	$(".auditBtn3").hide();
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var roleCode=$("#roleCode").val();
	var date=$("#QDate").val();
	var type=$("#type").val();
	var tableName=$("#QTableName").val();
	$("#Content").load("${ctx}/bi/poTask/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														date:date,tableName:tableName,roleCode:roleCode,type:type},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}

//跳轉到審核頁面
function toUser(index){
	var id = $('input[type=checkbox]')[index].value;
	var statusType = document.getElementsByName("statusType")[index].value;
	var role = document.getElementsByName("role")[index].value;
	$("#task").hide();
	$("#audit").show();
	$("#loading").show();
	$("#Content").load("${ctx}/bi/poTask/audit",{pageNo:"1",pageSize:"15",id:id,statusType:statusType,role:role},function(){$("#loading").fadeOut(1000);});
}

function submitTask (index) {
	var id = $('input[type=checkbox]')[index].value;
	var taskType = document.getElementsByName("tasType")[index].value;
	var obj={
		id:id,
		taskType:taskType,
		roleCode:$("#roleCode").val()
	}
	$("#loading").show();
	if(taskType=="FIT_PO_SBU_YEAR_CD_SUM"){
		layer.confirm("同個SBU下的commodity請同時間上傳，且一旦審核通過，數據不予更改！",{btn: ['确定', '取消'], title: "提示"},function(index){
			$.ajax({
				type:"POST",
				url:"${ctx}/bi/poTask/submitTask",
				async:false,
				dataType:"json",
				data:obj,
				success: function(data){
					$("#loading").hide();
					layer.alert(data.msg);
					// refresh();
					$("#Query").click();
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					layer.alert("<spring:message code='connect_fail'/>");
				}
			});
		});
	}else{
	$.ajax({
			type:"POST",
			url:"${ctx}/bi/poTask/submitTask",
			async:false,
			dataType:"json",
			data:obj,
			success: function(data){
				    $("#loading").hide();
					layer.alert(data.msg);
                    // refresh();
				$("#Query").click();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				layer.alert("<spring:message code='connect_fail'/>");
			}
		});
	}
}
function submitOneAudit(index) {
	var name = document.getElementsByName("tasName")[index].value;
	var id = $('input[type=checkbox]')[index].value;
    var taskType = document.getElementsByName("tasType")[index].value;
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
								// refresh();
							$("#Query").click();
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
function submitAudit(index) {
	var name = document.getElementsByName("tasName")[index].value;
	var id = $('input[type=checkbox]')[index].value;
	var taskType = document.getElementsByName("tasType")[index].value;
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
					if(taskType=="FIT_PO_SBU_YEAR_CD_SUM"){
						layer.confirm("一旦審核通過，數據不予更改！",{btn: ['确定', '取消'], title: "提示"},function(index){
							$.ajax({
								type:"POST",
								url:"${ctx}/bi/poTask/submitAudit",
								async:false,
								dataType:"json",
								data:obj,
								success: function(data){
									$dialog.dialog("destroy");
									layer.alert(data.msg);
									$("#Query").click();
								},
								error: function(XMLHttpRequest, textStatus, errorThrown) {
									layer.alert("<spring:message code='connect_fail'/>");
								}
							});
						});
					}else{
					$.ajax({
						type:"POST",
						url:"${ctx}/bi/poTask/submitAudit",
						async:false,
						dataType:"json",
						data:obj,
						success: function(data){
								$dialog.dialog("destroy");
							    layer.alert(data.msg);
								// refresh();
							$("#Query").click();
						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							layer.alert("<spring:message code='connect_fail'/>");
						}
					});
					}
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
function cancelAudit(index) {
	var taskType = document.getElementsByName("tasType")[index].value;
	var id = $('input[type=checkbox]')[index].value;
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
			debugger;
			layer.alert(data.msg);
			$("#Query").click();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});



}
function cancelTask(index) {

	var id = $('input[type=checkbox]')[index].value;
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poTask/cancelTask",
		async:false,
		dataType:"json",
		data:{id:id},
		success: function(data){
			// refresh();
			$("#Query").click();
			layer.alert(data.msg);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
}
</script>
</head>
<body>
<div style="width:100%;">
	<div style="display: none">
	<input  id="sbu" type="text" value="${sbu}">
	<input  id="email" type="text" value="${email}">
	</div>
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center;width: 50px;display: none;">序号</th>
				<th style="text-align:center;display: none" >任務類型</th>
				<th style="text-align:center" >任務名稱</th>
				<th style="text-align:center" >狀態</th>
				<th style="text-align:center" >審批意見</th>
				<th style="text-align:center" >創建人</th>
				<th style="text-align:center" >創建時間</th>
				<th style="text-align:center" >更新人</th>
				<th style="text-align:center" >更新時間</th>
				<th style="text-align:center" style="white-space: nowrap; border-right:1px solid #eee;text-align: center;"><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping" varStatus="sort">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;border-right:1px solid #eee;display: none;" >
									<input name="ID" type="checkbox"  value="${mapping[i]}"/>
								</td>
							</c:when>
							<c:when test="${status.index eq 1}">
								<input style="display: none" name="tasType" type="text"  value="${mapping[i]}"/>
								<td style="border-right:1px solid #eee;display: none;">${mapping[i]}</td>
							</c:when>
							<c:when test="${status.index eq 2}">
								<input style="display: none" name="tasName" type="text"  value="${mapping[i]}"/>
								<td style="border-right:1px solid #eee;">
									<a href="javascript:void(0);" class="update" onclick="toUser(${sort.index})">${mapping[i]}</a>
								</td>
							</c:when>
							<c:when test="${status.index eq 3}">
								<input style="display: none" name="statusType" type="hidden"  value="${mapping[i]}"/>
								<c:choose>
									<c:when test="${mapping[i] eq '0'}">
										<td  style="border-right:1px solid #eee;"><c:if test="${languageS eq 'zh_CN'}">未提交</c:if>
											<c:if test="${languageS eq 'en_US'}">Unsubmitted</c:if></td></td>
									</c:when>
									<c:when test="${mapping[i] eq '1'}">
										<td style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">初審中</c:if>
											<c:if test="${languageS eq 'en_US'}">praeiudicium</c:if></td>
									</c:when>
									<c:when test="${mapping[i] eq '2'}">
										<td  style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">终審中</c:if>
											<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if>
										</td>
									</c:when>
									<c:when test="${mapping[i] eq '3'}">
										<td  style="border-right:1px solid #eee;"><c:if test="${languageS eq 'zh_CN'}">完成</c:if>
											<c:if test="${languageS eq 'en_US'}">Finish</c:if></td>
									</c:when>
									<c:when test="${mapping[i] eq '-1'}">
										<td  style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">駁回</c:if>
											<c:if test="${languageS eq 'en_US'}">Turn Down</c:if>
										</td>
									</c:when>
								</c:choose>
							</c:when>
							<c:otherwise>
								<td style="border-right:1px solid #eee;">${mapping[i]}</td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<td style="white-space: nowrap; border-right:1px solid #eee;">
						<input type="hidden" name="role" value="${role}">
							<c:choose>
							<c:when test="${role eq 'TDC' || role eq 'BASE'}">
								<c:if test="${mapping[3] eq '0'}">
									<a href="javascript:void(0);" class="auditBtn" onclick="submitTask(${sort.index})"><spring:message code="submit"/></a>
								</c:if>
								<c:if test="${mapping[3] != '3' && mapping[3] != '-1'}">
								<a href="javascript:void(0);" class="auditBtn auditBtn${mapping[3]}" onclick="cancelTask(${sort.index})">取消任務</a>
								</c:if>
							</c:when>
						    <c:when test="${mapping[3] eq '1' && role eq 'CLASS' || mapping[3] eq '1' && role eq 'T_MANAGER' || mapping[3] eq '1' && role eq 'PD'}">
						    <a href="javascript:void(0);" class="auditBtn" onclick="submitOneAudit(${sort.index})">
								<c:if test="${languageS eq 'zh_CN'}">初審</c:if>
								<c:if test="${languageS eq 'en_US'}">Praeiudicium</c:if>
							</a>
						    </c:when>
						    <c:when test="${mapping[3] eq '2' && role eq 'MANAGER' || mapping[3] eq '2' && role eq 'CPO'}">
						    <a href="javascript:void(0);" class="auditBtn" onclick="submitAudit(${sort.index})">终審</a>
						    </c:when>
						    <c:when test="${role eq 'KEYUSER'}">
							<c:choose>
						          <c:when test="${mapping[3] eq '2' && mapping[1] eq 'FIT_PO_SBU_YEAR_CD_SUM'}">
						          	<a href="javascript:void(0);" class="auditBtn" onclick="submitAudit(${sort.index})">终審</a>
									<a href="javascript:void(0);" class="auditBtn" onclick="cancelAudit(${sort.index})">取消審批</a>
						          </c:when>
								  	<c:when test="${mapping[3] eq '3' && mapping[1] eq 'FIT_PO_SBU_YEAR_CD_SUM'}">
								  </c:when>
						<c:when test="${mapping[3] != '0' && mapping[3] != '-1'}">
						<a href="javascript:void(0);" class="auditBtn" onclick="cancelAudit(${sort.index})">取消審批</a>
						</c:when>
						    </c:choose>
						    </c:when>
							</c:choose>
				     </tr>
			</c:forEach>

		</tbody>
	</table>
	<input id="role" style="display: none" placeholder="請輸入查詢名稱" type="text" value="${role}">
</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>