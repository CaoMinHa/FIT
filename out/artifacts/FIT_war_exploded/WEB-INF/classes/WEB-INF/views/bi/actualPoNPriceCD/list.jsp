<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
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
    var scenario=$("#scenario").val();
    var period=$("#QPeriod").val();
    var vView =$("#vView").val();
    var commodity =$("#commodity").val();
	$("#Content").load("${ctx}/bi/actualPoNPriceCD/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
        scenario:scenario,vView:vView,period:period,commodity:commodity});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<table class="table table-condensed table-hover">
	<thead>
		<tr>
			<th style="text-align: center;">Scenario</th>
			<th style="text-align: center;">Year</th>
			<th style="text-align: center;">Preiod</th>
			<th style="text-align: center;">View</th>
			<th style="text-align: center;">Commodity</th>
			<th style="text-align: center;">BU</th>
			<th style="text-align: center;">SBU</th>
			<th style="text-align: center;">公差下限</th>
			<th style="text-align: center;">材料替代 (料號有變更)th>
			<th style="text-align: center;">second source 導入(料號有變更)</th>
			<th style="text-align: center;">買贏競爭對手</th>
			<th style="text-align: center;">價格延漲和限漲</th>
			<th style="text-align: center;">低於客戶合約價</th>
			<th style="text-align: center;">Rebate</th>
			<th style="text-align: center;">懲罰性扣款</th>
			<th style="text-align: center;">免費送樣</th>
			<th style="text-align: center;">協助BU呆滯處理</th>
			<th style="text-align: center;">協助BU呆滯處理</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.result}" var="mapping">
			<tr>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.scenario}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.year}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.period}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.commodity}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.vView}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.bu}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.sbu}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.lowerToleranceLimit}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.changeItem}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.secondSource}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.competor}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.priceContinueLimit}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.lowerContact}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.rebate}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.punitiveDeduction}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.freeSample}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.sluggishTreatment}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.tradeTerm}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>