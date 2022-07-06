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
	$("#PageNo").val(page);
	var year=$("#QYear").val();
	$("#Content").load("${ctx}/bi/cct/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														year:year});
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
			<th style="text-align: center;">年</th>
			<th style="text-align: center;">組織</th>
			<th style="text-align: center;">項目</th>
			<th style="text-align: center;">CAP</th>
			<th style="text-align: center;">滾動預測</th>
			<th style="text-align: center;">vs CAP</th>
			<th style="text-align: center;">%</th>
			<th style="text-align: center;">年度CAP佔比</th>
			<th style="text-align: center;">燈號</th>
			<th style="text-align: center;">預警說明</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.result}" var="mapping">
			<tr>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.year}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.organization}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.item}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.cap}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.rollingForecast}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.vsCAP}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.percent}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.yearRate}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.light}</td>
				<td style="border-right:1px solid #eee;text-align: center;">${mapping.warningInstructions}</td>
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