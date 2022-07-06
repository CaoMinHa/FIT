<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	.modal-backdrop {
		position: initial!important;
	}
	.modal-header{
		background-color: #f5f5f5!important;
	}
	input {
		margin-bottom:0px !important;
	}
	.table th, .table td {
		border-top: 1px solid #c4c4c4;
		border-right: 1px solid #c4c4c4;
		border-left: 1px solid #c4c4c4;
	}
	.table{
		border: 1px solid white !important;
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

	$("#Fenye>input:first").bind("blur",function(){
		Page.jumpPage($(this).val());
		clickPage(Page.getPage());
	});
	$(".table tr:gt(2)").each(function(){
		$(this).children('td').each(function(e){
			if(e>2){
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
	$("#Content").load("${ctx}/bi/poFlow/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														date:date,tableName:tableName},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
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
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			$("#loading").hide();
			window.location.href="${ctx}/logout";
		}
	});
});
$(function () {
	$("#tableCpo tbody").find("tr").each(function () {
		var val = $(this).children('td:eq(2)').text();
		var vall = $(this).children('td:eq(1)').text();
		if (val == '') {
			$(this).children('td:eq(2)').remove();
			$(this).children('td:eq(1)').attr("colspan", "2");
			// $(this).children('td:eq(1)').text(vall+"合計：");
			$(this).css("background-color", "#cfecff");
		}
	});
})

</script>
</head>
<body>

<input style="display: none" id="total" value="${total}">
<input type="hidden" id="countNotUploadNumber" value="${countNotUploadNumber}" />
<button id="modalBtn"  data-toggle="modal" data-target="#myModal" class="btn search-btn btn-warning m-l-md"
		style="margin-left:20px;float:left;">SBU年度CD目標匯總 未上传明细</button>
<div style="width:95%;">
	<table align="center" class="table table-condensed table-hover" id="tableCpo">
		<thead style="background-color: #cfecff;">
		<tr style="background-color: white">
			<th style="border-right-color: white;border-left-color: white;" colspan="2">SBU總數量：${countSUM}</th>
			<th style="border-right-color: white;border-left-color: white;" colspan="12"><font color="red">未完成的SBU數量：${countNotUploadNumber }</font></th>
		</tr>
			<tr>
				<th style="text-align:center" rowspan="3">採購機能</th>
				<th style="text-align:center" rowspan="3">Commodity</th>
				<th style="text-align:center" colspan="8" >Year Total</th>
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
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index-1 }" varStatus="status">
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

<div class="modal fade" id="myModal" style="display: none" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					SBU年度CD目標匯總 未处理清单
				</h4>
			</div>
			<div class="modal-body">
				<table  border="0" cellpadding="0" cellspacing="1">
					<c:forEach items="${countNotUploadList }" var="column" varStatus="status">
					<c:if test="${status.index%5 eq 0}">
					    <tr>
					</c:if>
						<td height="25px" width="200px">${column}</td>
						</c:forEach>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button"  class="btn btn-default" data-dismiss="modal">关闭
				</button>
			</div>
		</div>
	</div>
</div>
</body>
</html>