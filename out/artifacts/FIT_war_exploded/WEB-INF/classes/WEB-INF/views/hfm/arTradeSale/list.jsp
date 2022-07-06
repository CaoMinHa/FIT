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
	var period=$("#QPeriod").val();
	$("#Content").load("${ctx}/hfm/arTradeSale/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														corporationCode:corporationCode,period:period});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:200%;">
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th>法人编码</th>
				<th>年</th>
				<th>期間</th>
				<th>帳款編號</th>
				<th>傳票編號</th>
				<th>銷單/出貨單</th>
				<th>項次</th>
				<th>客商代码</th>
				<th>客商名稱</th>
				<th>客戶類型</th>
				<th>借方科目代碼</th>
				<th>借方科目描述</th>
				<th>貸方科目代碼</th>
				<th>貸方科目描述</th>
				<th>交易币别</th>
				<th>未稅額（原幣）</th>
				<th>未稅額（本幣）</th>
				<th>部門代碼</th>
				<th>摘要</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;">${mapping.corporationCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.year}</td>
					<td style="border-right:1px solid #eee;">${mapping.period}</td>
					<td style="border-right:1px solid #eee;">${mapping.document}</td>
					<td style="border-right:1px solid #eee;">${mapping.summons}</td>
					<td style="border-right:1px solid #eee;">${mapping.sale}</td>
					<td style="border-right:1px solid #eee;">${mapping.category}</td>
					<td style="border-right:1px solid #eee;">${mapping.customer}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerName}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerType}</td>
					<td style="border-right:1px solid #eee;">${mapping.borrowItemCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.borrowItemDesc}</td>
					<td style="border-right:1px solid #eee;">${mapping.itemCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.itemDesc}</td>
					<td style="border-right:1px solid #eee;">${mapping.currency}</td>
					<td style="border-right:1px solid #eee;">${mapping.untaxSrcAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.untaxCurrencyAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.department}</td>
					<td style="border-right:1px solid #eee;">${mapping.summary}</td>
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