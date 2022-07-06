<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
	.table thead th{vertical-align: middle;!important;}
	.table-condensed td{padding:7px 10px;!important;}
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

	$("#Fenye>input:first").bind("blur",function(){
		Page.jumpPage($(this).val());
		clickPage(Page.getPage());
	});
	
});


//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var date=$("#QDate").val();
	var entity=$("#QEntity").val();
	var tableName=$("#QTableName").val();
	$("#Content").load("${ctx}/bi/poTask/audit",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		date:date,tableName:tableName,id:$("#tId").val()},function(){$("#loading").fadeOut(1000);});

function refresh(){
	clickPage("1");
}
}
$("a.update").click(function(){
	var updateData="";
	$(this).parent().parent().find("input").each(function(i){
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
			refresh();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			$("#loading").hide();
			window.location.href="${ctx}/logout";
		}
	});
});

</script>
</head>
<body>
<input style="display: none" id="tId" value="${taskId}">
<div style="width:95%;">
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center" rowspan="3">採購機能</th>
				<th style="text-align:center" rowspan="3">Commodity</th>
				<th style="text-align:center" colspan="6" >Year Total</th>
				<th style="text-align:center" rowspan="3">核准人</th>
				<th style="text-align:center" rowspan="3">核准日期</th>
				<th style="text-align:center" rowspan="3">操作</th>
			</tr>
			<tr>
				<th  style="text-align:center"  style="text-align:center"  colspan="3">非客指</th>
				<th  style="text-align:center"  colspan="3">客指</th>
			</tr>
			<tr>
				<th style="text-align:center">採購金額(NTD)</th>
				<th style="text-align:center" >CD比率</th>
				<th style="text-align:center" >CPO Approve</th>
				<th style="text-align:center" >採購金額(NTD)</th>
				<th style="text-align:center" >CD比率</th>
				<th style="text-align:center" >CPO Approve</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;border-right:1px solid #eee;display:none;"><input name="ID" type="text" style="display:none;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==5}">
								<td style="white-space: nowrap; border-right:1px solid #eee;"><input name="NO_CPO" type="text" style="height:25px !important;width:150px;" value="${mapping[i]}"/></td>
							</c:when>
							<c:when test="${status.index==8}">
								<td style="white-space: nowrap; border-right:1px solid #eee;"><input name="CPO" type="text" style="height:25px !important;width:150px;" value="${mapping[i]}"/></td>
							</c:when>
							<c:otherwise>
								<td style="border-right:1px solid #eee;">${mapping[i]}</td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<td style="white-space: nowrap; border-right:1px solid #eee;"><a href="javascript:void(0);" class="update"><spring:message code='update'/></a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>