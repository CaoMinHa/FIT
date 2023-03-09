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
		font-size: 14px;
	}
	.table tbody tr td{
		font-size: 12px;
	}
</style>
<head>
<script type="text/javascript">
	$(function () {
		$("#taskDetails tbody").find("tr").each(function(){
			var val=$(this).children('td:eq(1)').text();
			if (val==''){
				$(this).children('td:eq(1)').remove();
				$(this).children('td:eq(0)').attr("colspan","2");
				$(this).css("background-color", "#cfecff" );
			}
		});
		$("#taskDetails tbody tr:last").css("background-color", "#7fc4f1");
	})
</script>
</head>
<body>
<div style="width:99%;">
	<table id="taskDetails" align="center" class="table table-condensed table-hover" >
		<thead style="background-color: #cfecff;">
			<tr>
				<th style="text-align:center" rowspan="3">採購機能</th>
				<th style="text-align:center" rowspan="3">Commodity</th>
				<th style="text-align:center" colspan="6" >Year Total</th>
			</tr>
			<tr>
				<th  style="text-align:center"  style="text-align:center"  colspan="3">非客指</th>
				<th  style="text-align:center"  colspan="3">客指</th>
			</tr>
			<tr>
				<th style="text-align:center">採購金額(NTD)</th>
				<th style="text-align:center">CD金额(NTD)</th>
				<th style="text-align:center" >CPO Approve(%)</th>
				<th style="text-align:center" >採購金額(NTD)</th>
				<th style="text-align:center" >SBU CD比率(%)</th>
				<th style="text-align:center" >CPO Approve(%)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<td>${mapping[i]}</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</body>
</html>