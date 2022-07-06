<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
		height:0px !important;
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
var Page;
$(function() {
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
});


//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var date=$("#QDate").val();
	var entity=$("#QEntity").val();
	var tableName=$("#QTableName").val();
	$("#Content").load("${ctx}/bi/poTaskList/audit",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		date:date,tableName:tableName,id:$("#tId").val()},function(){$("#loading").fadeOut(1000);});
}
function refresh(){
	clickPage("1");
}



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
function fileClick(e,val) {
	var index = layer.confirm('下載文件', {
		btn: ['下載','关闭'], //按钮
		shade: false //不显示遮罩
	}, function(){
		//下載
		var tempwindow=window.open('_blank');
		tempwindow.location= "${ctx}/static/download/"+val;
		<%--window.location.href = "${ctx}/static/download/"+val;--%>
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
		<input style="display: none" id="taskId" name="taskId" value="${taskId}">
		<h3 style="margin-top: -35px;margin-bottom: 20px;">${taskName}</h3>
		<table id="tableFile">
			<tr>
				<c:forEach items="${fileList}" var="file">
					<td>
						<input style="display: none" id="tId" value="${taskId}">
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