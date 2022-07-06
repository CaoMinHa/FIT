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
	$("#Content").load("${ctx}/hfm/apBalanceInvoice/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
															orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
															corporationCode:corporationCode,period:period});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:250%;">
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th>法人编码</th>
				<th>年</th>
				<th>期間</th>
				<th>帳款編號</th>
				<th>傳票編號</th>
				<th>發票號碼</th>
				<th>供應商代码</th>
				<th>供應商名稱</th>
 				<th>供應商類型</th>
				<th>借方科目代碼</th>
				<th>借方科目描述</th>
				<th>貸方科目代碼</th>
				<th>貸方科目描述</th>
				<th>交易币别</th>
				<th>原幣含稅應付金額</th>
				<th>原幣稅額</th>
				<th>原幣未稅額</th>
				<th>本幣含稅應付金額</th>
				<th>本幣稅額</th>
				<th>本幣未稅額</th>
				<th>重評匯率</th>
				<th>本幣重評含稅應付金額</th>
				<th>重評稅額</th>
				<th>重評未稅額</th>
				<th>入帳日期</th>
				<th>到期日</th>
				<th>帳齡</th>
				<th>部門代碼</th>
				<th>付款條件</th>
				<th>摘要</th>
				<th>备注</th>
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
					<td style="border-right:1px solid #eee;">${mapping.invoice}</td>
					<td style="border-right:1px solid #eee;">${mapping.supplier}</td>
					<td style="border-right:1px solid #eee;">${mapping.supplierName}</td>
 					<td style="border-right:1px solid #eee;">${mapping.supplierType}</td>
					<td style="border-right:1px solid #eee;">${mapping.borrowItemCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.borrowItemDesc}</td>
					<td style="border-right:1px solid #eee;">${mapping.itemCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.itemDesc}</td>
					<td style="border-right:1px solid #eee;">${mapping.currency}</td>
					<td style="border-right:1px solid #eee;">${mapping.srcAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.srcTax}</td>
					<td style="border-right:1px solid #eee;">${mapping.srcUntax}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyTax}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyUntax}</td>
					<td style="border-right:1px solid #eee;">${mapping.exchangeRate}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyExAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyExTax}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyExUntax}</td>
					<td style="border-right:1px solid #eee;">${mapping.docDate}</td>
					<td style="border-right:1px solid #eee;">${mapping.dueDate}</td>
					<td style="border-right:1px solid #eee;">${mapping.aging}</td>
					<td style="border-right:1px solid #eee;">${mapping.department}</td>
					<td style="border-right:1px solid #eee;">${mapping.condition}</td>
					<td style="border-right:1px solid #eee;">${mapping.summary}</td>
					<td style="border-right:1px solid #eee;">${mapping.note}</td>
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