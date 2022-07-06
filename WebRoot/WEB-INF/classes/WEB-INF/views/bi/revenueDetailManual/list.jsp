<%@page import="foxconn.fit.entity.base.EnumRevenveDetailType"%>
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
	var version=$("#QVersion").val();
	$("#Content").load("${ctx}/bi/revenueDetailManual/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														corporationCode:corporationCode,period:period,version:version});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:500%;">
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th>版本</th>
				<th>序號</th>
				<th>立賬年度</th>
				<th>立賬季度</th>
				<th>立賬月份</th>
				<th>管報法人編碼</th>
				<th>法人簡稱</th>
				<th>客戶代碼</th>
				<th>客戶全稱</th>
				<th>業務部門</th>
				<th>類別</th>
				<th>發票項次</th>
				<th>發票號碼</th>
				<th>發票日期</th>
				<th>發票立賬日期</th>
				<th>銷單項次</th>
				<th>銷單號碼</th>
				<th>銷貨日期</th>
				<th>倉碼</th>
				<th>SBU</th>
				<th>產品品號</th>
				<th>客戶產品品號</th>
				<th>數量</th>
				<th>單價</th>
				<th>幣別</th>
				<th>匯率</th>
				<th>未稅金額(原幣)</th>
				<th>未稅金額(本幣)</th>
				<th>財報匯率(USD)</th>
				<th>營收未稅金額(USD)</th>
				<th>財報匯率(NTD)</th>
				<th>營收未稅金額(NTD)</th>
				<th>送貨客戶代碼</th>
				<th>送貨客戶名稱</th>
				<th>生產單位</th>
				<th>CD</th>
				<th>銷售大類</th>
				<th>對照檔</th>
				<th>部門別</th>
				<th>主產業1</th>
				<th>主產業2</th>
				<th>主產業3</th>
				<th>主產業4</th>
				<th>主產業5</th>
				<th>次產業</th>
				<th>主產業是否唯一</th>
				<th>客戶簡稱規範</th>
				<th>客戶全稱規範</th>
				<th>客戶集團規範</th>
				<th>客戶分級分類</th>
				<th>區域</th>
				<th>渠道</th>
				<th>產品BCG</th>
				<th>策略</th>
				<th>發票NO.</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;">
						<c:forEach items="<%=EnumRevenveDetailType.values() %>" var="type">
							<c:if test="${type.code eq mapping.version}"><spring:message code='${type.name }'/></c:if>
						</c:forEach>
					</td>
					<td style="border-right:1px solid #eee;">${mapping.serialNumber}</td>
					<td style="border-right:1px solid #eee;">${mapping.year}</td>
					<td style="border-right:1px solid #eee;">${mapping.quarter}</td>
					<td style="border-right:1px solid #eee;">${mapping.period}</td>
					<td style="border-right:1px solid #eee;">${mapping.legalCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.corporationCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerName}</td>
					<td style="border-right:1px solid #eee;">${mapping.department}</td>
					<td style="border-right:1px solid #eee;">${mapping.category}</td>
					<td style="border-right:1px solid #eee;">${mapping.invoiceItem}</td>
					<td style="border-right:1px solid #eee;">${mapping.invoiceNo}</td>
					<td style="border-right:1px solid #eee;">${mapping.invoiceDate}</td>
					<td style="border-right:1px solid #eee;">${mapping.invoiceSignDate}</td>
					<td style="border-right:1px solid #eee;">${mapping.saleItem}</td>
					<td style="border-right:1px solid #eee;">${mapping.saleNo}</td>
					<td style="border-right:1px solid #eee;">${mapping.saleDate}</td>
					<td style="border-right:1px solid #eee;">${mapping.storeNo}</td>
					<td style="border-right:1px solid #eee;">${mapping.sbu}</td>
					<td style="border-right:1px solid #eee;">${mapping.productNo}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerProductNo}</td>
					<td style="border-right:1px solid #eee;">${mapping.quantity}</td>
					<td style="border-right:1px solid #eee;">${mapping.price}</td>
					<td style="border-right:1px solid #eee;">${mapping.currency}</td>
					<td style="border-right:1px solid #eee;">${mapping.rate}</td>
					<td style="border-right:1px solid #eee;">${mapping.sourceUntaxAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.currencyUntaxAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.currentyRate}</td>
					<td style="border-right:1px solid #eee;">${mapping.monthRevenueAmount}</td>
					<td style="border-right:1px solid #eee;">${mapping.monthRate}</td>
					<td style="border-right:1px solid #eee;">${mapping.monthRevenueRate}</td>
					<td style="border-right:1px solid #eee;">${mapping.supplierCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.supplierName}</td>
					<td style="border-right:1px solid #eee;">${mapping.productionUnit}</td>
					<td style="border-right:1px solid #eee;">${mapping.cd}</td>
					<td style="border-right:1px solid #eee;">${mapping.saleCategory}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerInfo}</td>
					<td style="border-right:1px solid #eee;">${mapping.segment}</td>
					<td style="border-right:1px solid #eee;">${mapping.leadingIndustry1}</td>
					<td style="border-right:1px solid #eee;">${mapping.leadingIndustry2}</td>
					<td style="border-right:1px solid #eee;">${mapping.leadingIndustry3}</td>
					<td style="border-right:1px solid #eee;">${mapping.leadingIndustry4}</td>
					<td style="border-right:1px solid #eee;">${mapping.leadingIndustry5}</td>
					<td style="border-right:1px solid #eee;">${mapping.secondaryIndustry}</td>
					<td style="border-right:1px solid #eee;">${mapping.isUnique}</td>
					<td style="border-right:1px solid #eee;">${mapping.simpleSpecification}</td>
					<td style="border-right:1px solid #eee;">${mapping.fullSpecification}</td>
					<td style="border-right:1px solid #eee;">${mapping.groupSpecification}</td>
					<td style="border-right:1px solid #eee;">${mapping.grade}</td>
					<td style="border-right:1px solid #eee;">${mapping.area}</td>
					<td style="border-right:1px solid #eee;">${mapping.channel}</td>
					<td style="border-right:1px solid #eee;">${mapping.BCG}</td>
					<td style="border-right:1px solid #eee;">${mapping.strategy}</td>
					<td style="border-right:1px solid #eee;">${mapping.invoiceNumber}</td>
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