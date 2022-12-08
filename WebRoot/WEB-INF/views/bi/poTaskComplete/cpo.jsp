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
	$("#roleCode").hide();
	$("#QDate").hide();
	$("#type").hide();
	$("#name").hide();
	$("#Query").hide();
	$("#taskDetails tr:gt(2)").each(function(){
		$(this).children('td').each(function(e){
			if(e>2&&e<11){
					$(this).css("text-align", "right");
			}
		});
	})
	if($("#taskDetails tr:eq(3) td span[style='width: 1px;'] a").length!=1){
		$("#taskDetails tr input").attr("disabled","true");
	}
});

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

$(function () {
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

</script>
</head>
<body>
<div style="width:99%;">
	<form id="taskFileForm" method="POST" enctype="multipart/form-data" class="form-horizontal">
		<input type="file" style="display:none;" class="input-file" multiple="false"/>
		<h3 style="margin-top: -35px;">${taskName}</h3>
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
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;display:none;"><input name="ID" type="text" style="display:none;" value="${mapping[i]}"/></td>
							</c:when>
<%--							<c:when test="${status.index==6 && mapping[2] != null}">--%>
<%--								<td style="white-space: nowrap;"><input name="NO_CPO" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>--%>
<%--							</c:when>--%>
<%--							<c:when test="${status.index==10 && mapping[2] != null}">--%>
<%--								<td style="white-space: nowrap;"><input name="CPO" type="text" style="text-align: right;height:25px !important;width:100px;line-height: 15px !important;" value="${mapping[i]}"/></td>--%>
<%--							</c:when>--%>
							<c:otherwise>
								<td>${mapping[i]}</td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
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