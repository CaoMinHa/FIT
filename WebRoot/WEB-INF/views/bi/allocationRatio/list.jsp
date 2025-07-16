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
	if(!$("#QYear").val()){
		layer.alert("请选择年份(Please select year)");
		return;
	}
	if(!$("#QType").val()){
		layer.alert("请选择數據類型(Please select DataType)");
		return;
	}
	$("#loading").show();
	$("#Content").load("${ctx}/bi/allocationRatio/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		scenarios:$("#QScenarios").val(),
		years:$("#QYear").val(),
		types:$("#QType").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:110%;">
	<c:choose>
		<c:when test="${scenarios eq 'Budget'}">
			<table class="table table-condensed table-hover">
				<c:if test="${languageS eq 'en_US'}">
					<thead class="theadCss">
					   <tr>
							<th rowspan="1" colspan="5">Basic Info</th>
							<th colspan="1">FY${year}</th>
							<th colspan="1">FY${year+1}</th>
							<th colspan="1">FY${year+2}</th>
							<th colspan="1">FY${year+3}</th>
							<th colspan="1">FY${year+4}</th>
						</tr>
						<tr>
						    <th>Date</th>
							<th>department</th>
							<th>departmentName</th>
							<th>SBU</th>
							<th>dataType</th>
							<th>allocationRatio</th>
							<th>allocationRatio</th>
							<th>allocationRatio</th>
							<th>allocationRatio</th>
							<th>allocationRatio</th>
						</tr>
					</thead>
				</c:if>
				<c:if test="${languageS eq 'zh_CN'}">
					<thead class="theadCss">
					   <tr>
							<th rowspan="1" colspan="5">主數據</th>
							<th colspan="1">FY${year}</th>
							<th colspan="1">FY${year+1}</th>
							<th colspan="1">FY${year+2}</th>
							<th colspan="1">FY${year+3}</th>
							<th colspan="1">FY${year+4}</th>
						</tr>
						<tr>
						    <th>日期</th>
							<th>費用代碼</th>
							<th>費用代碼名稱</th>
							<th>SBU</th>
							<th>數據類型</th>
						    <th>分攤比例</th>
							<th>分攤比例</th>
							<th>分攤比例</th>
							<th>分攤比例</th>
							<th>分攤比例</th>
						</tr>

					</thead>
				</c:if>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.date}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.department}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.departmentName}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.dataType}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dataBudyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dataNext1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dataNext2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dataNext3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dataNext4}" pattern="#,##0.##"></fmt:formatNumber></td>
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