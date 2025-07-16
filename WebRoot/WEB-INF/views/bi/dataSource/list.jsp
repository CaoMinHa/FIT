<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<head>
	<style type="text/css">
		.theadCss th{
			border-left: 1px solid #e3e0e0;
			text-align:center;
		}
	</style>
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
	$("#PageNo").val(page);
	if(!$("#QScenarios").val()){
		layer.alert("請選擇場景！(Please select a scene)");
		return;
	}
	$("#loading").show();
	/*if(!$("#QEntity").val()){
		layer.alert("請選擇法人！(Please select a legal)");
		return;
	}*/
	$("#Content").load("${ctx}/bi/dataSource/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		scenarios:$("#QScenarios").val(),
		entitys:$("#QEntity").val(),years:$("#QYear").val(),
		versions:$("#QVersion").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:230%;">
	<c:choose>
		<c:when test="${scenarios eq 'Budget'}">
			<table class="table table-condensed table-hover">
				<c:if test="${languageS eq 'en_US'}">
					<thead class="theadCss">
					   <tr>
							<th rowspan="1" colspan="8">Basic Info</th>
							<th colspan="12">FY${year}</th>
							<th colspan="1">FY${year+1}</th>
							<th colspan="1">FY${year+2}</th>
							<th colspan="1">FY${year+3}</th>
							<th colspan="1">FY${year+4}</th>
						</tr>
						<tr>
						    <th>Date</th>
							<th>Version</th>
							<th>SBU_entity</th>
							<th>SBU_entityName</th>
							<th>department</th>
							<th>departmentName</th>
							<th>Account</th>
							<th>AccountName</th>
							<th>Jan</th>
							<th>Feb</th>
							<th>Mar</th>
							<th>Apr</th>
							<th>May</th>
							<th>Jun</th>
							<th>Jul</th>
							<th>Aug</th>
							<th>Sep</th>
							<th>Oct</th>
							<th>Nov</th>
							<th>Dec</th>
							<th>Year Total</th>
							<th>Year Total</th>
							<th>Year Total</th>
							<th>Year Total</th>
						</tr>
					</thead>
				</c:if>
				<c:if test="${languageS eq 'zh_CN'}">
					<thead class="theadCss">
					   <tr>
							<th rowspan="1" colspan="8">主數據</th>
							<th colspan="12">FY${year}</th>
							<th colspan="1">FY${year+1}</th>
							<th colspan="1">FY${year+2}</th>
							<th colspan="1">FY${year+3}</th>
							<th colspan="1">FY${year+4}</th>
						</tr>
						<tr>
						    <th>日期</th>
							<th>版本</th>
							<th>SBU_法人</th>
							<th>SBU_法人名稱</th>
							<th>費用代碼</th>
							<th>費用代碼名稱</th>
							<th>科目</th>
							<th>科目名稱</th>
							<th>1月</th>
						    <th>2月</th>
						    <th>3月</th>
						    <th>4月</th>
						    <th>5月</th>
						    <th>6月</th>
						    <th>7月</th>
						    <th>8月</th>
						    <th>9月</th>
						    <th>10月</th>
						    <th>11月</th>
						    <th>12月</th>
							<th>全年合計</th>
							<th>全年合計</th>
							<th>全年合計</th>
							<th>全年合計</th>
						</tr>

					</thead>
				</c:if>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.date}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.version}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entityName}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.department}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.departmentName}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.account}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.accountName}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountJan}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountFeb}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountMar}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountApr}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountMay}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountJun}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountJul}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountAug}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountSep}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountOct}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountNov}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountDec}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountNext1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountNext2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountNext3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountNext4}" pattern="#,##0.##"></fmt:formatNumber></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
	</c:choose>

</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>