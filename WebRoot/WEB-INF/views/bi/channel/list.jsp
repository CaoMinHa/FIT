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
	var corporationCode=$("#QCorporationCode").val();
	$("#Content").load("${ctx}/bi/channel/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														corporationCode:corporationCode});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:150%;">
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th>法人</th>
				<th>客戶代碼</th>
				<th>客戶簡稱</th>
				<th>客戶全名</th>
				<th>地區別</th>
				<th>國別</th>
				<th>所屬集團</th>
				<th>客戶分類-1</th>
				<th>客戶分類-2</th>
				<th>收款條件</th>
				<th>價格條件</th>
				<th>交易客戶歸類</th>
				<th>對照檔（法人+客戶代碼）</th>
				<th>營收渠道</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;">${mapping.corporationCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerShortName}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerName}</td>
					<td style="border-right:1px solid #eee;">${mapping.area}</td>
					<td style="border-right:1px solid #eee;">${mapping.country}</td>
					<td style="border-right:1px solid #eee;">${mapping.groups}</td>
					<td style="border-right:1px solid #eee;">${mapping.classification1}</td>
					<td style="border-right:1px solid #eee;">${mapping.classification2}</td>
					<td style="border-right:1px solid #eee;">${mapping.receiptCondition}</td>
					<td style="border-right:1px solid #eee;">${mapping.priceCondition}</td>
					<td style="border-right:1px solid #eee;">${mapping.tradeCustomer}</td>
					<td style="border-right:1px solid #eee;">${mapping.document}</td>
					<td style="border-right:1px solid #eee;">${mapping.channel}</td>
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