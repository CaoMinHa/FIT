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
	$("#loading").show();
	$("#PageNo").val(page);
	var entity="";
	$("input[name=QEntity]:checked").each(function(i,dom){
		entity+=$(dom).val()+",";
	});
	var date=$("#QDate").val();
	var ACCOUNTCODE=$("#QACCOUNTCODE").val();
	var ACCOUNTDESC=$("#QACCOUNTDESC").val();
	var ISICP=$("#QISICP").val();
	var CUSTNAME=$("#QCUSTNAME").val();
	var TC=$("#QTC").val();
	$("#Content").load("${ctx}/hfm/reconciliation/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														entity:entity,date:date,
														ACCOUNTCODE:ACCOUNTCODE,ACCOUNTDESC:ACCOUNTDESC,
														ISICP:ISICP,CUSTNAME:CUSTNAME,TC:TC},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:200%;">
	<table class="table table-condensed table-hover">
		<c:choose>
			<c:when test="${locale eq 'en_US'}">
				<thead>
					<tr>
						<th style="border-right:1px solid #eee;text-align:center;">Year</th>
						<th style="border-right:1px solid #eee;text-align:center;">Month</th>
						<th style="border-right:1px solid #eee;text-align:center;">Company Code</th>
						<th style="border-right:1px solid #eee;text-align:center;">Company Name</th>
						<th style="border-right:1px solid #eee;text-align:center;">Account Code</th>
						<th style="border-right:1px solid #eee;text-align:center;">Account Name</th>
						<th style="border-right:1px solid #eee;text-align:center;">Merchant Code</th>
						<th style="border-right:1px solid #eee;text-align:center;">Merchant Name</th>
						<th style="border-right:1px solid #eee;text-align:center;">Related Entities</th>
						<th style="border-right:1px solid #eee;text-align:center;">Data Source</th>
						<th style="border-right:1px solid #eee;text-align:center;">Local Currency</th>
						<th style="border-right:1px solid #eee;text-align:center;">Opening Balance of Local Currency</th>
						<th style="border-right:1px solid #eee;text-align:center;">Local Currency Debit Accumulation</th>
						<th style="border-right:1px solid #eee;text-align:center;">Local Currency Credit Accumulation</th>
						<th style="border-right:1px solid #eee;text-align:center;">Ending Balance of Local Currency</th>
						<th style="border-right:1px solid #eee;text-align:center;">Transaction Currency</th>
						<th style="border-right:1px solid #eee;text-align:center;">Opening Balance of Transaction Currency</th>
						<th style="border-right:1px solid #eee;text-align:center;">Transaction Currency Debit Accumulation</th>
						<th style="border-right:1px solid #eee;text-align:center;">Transaction Currency Credit Accumulation</th>
						<th style="border-right:1px solid #eee;text-align:center;">Ending Balance of Transaction Currency</th>
						<th style="border-right:1px solid #eee;text-align:center;">Creation User</th>
						<th style="border-right:1px solid #eee;text-align:center;">Creation Date</th>
					</tr>
				</thead>
			</c:when>
			<c:otherwise>
				<thead>
					<tr>
						<th style="border-right:1px solid #eee;text-align:center;">年份</th>
						<th style="border-right:1px solid #eee;text-align:center;">月份</th>
						<th style="border-right:1px solid #eee;text-align:center;">公司編碼</th>
						<th style="border-right:1px solid #eee;text-align:center;">公司名稱</th>
						<th style="border-right:1px solid #eee;text-align:center;">科目編碼</th>
						<th style="border-right:1px solid #eee;text-align:center;">科目名稱</th>
						<th style="border-right:1px solid #eee;text-align:center;">客商編碼</th>
						<th style="border-right:1px solid #eee;text-align:center;">客商名稱</th>
						<th style="border-right:1px solid #eee;text-align:center;">是否關聯方</th>
						<th style="border-right:1px solid #eee;text-align:center;">數據來源</th>
						<th style="border-right:1px solid #eee;text-align:center;">本幣幣種</th>
						<th style="border-right:1px solid #eee;text-align:center;">本幣期初餘額</th>
						<th style="border-right:1px solid #eee;text-align:center;">本幣借方累計</th>
						<th style="border-right:1px solid #eee;text-align:center;">本幣貸方累計</th>
						<th style="border-right:1px solid #eee;text-align:center;">本幣期末餘額</th>
						<th style="border-right:1px solid #eee;text-align:center;">交易幣種</th>
						<th style="border-right:1px solid #eee;text-align:center;">交易幣期初餘額</th>
						<th style="border-right:1px solid #eee;text-align:center;">交易幣借方累計</th>
						<th style="border-right:1px solid #eee;text-align:center;">交易幣貸方累計</th>
						<th style="border-right:1px solid #eee;text-align:center;">交易幣期末餘額</th>
						<th style="border-right:1px solid #eee;text-align:center;">創建用戶</th>
						<th style="border-right:1px solid #eee;text-align:center;">創建日期</th>
					</tr>
				</thead>
			</c:otherwise>
		</c:choose>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.YEAR}</td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.PERIOD}</td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.ENTITY_CODE}</td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.ENTITY_NAME}</td>
					<td style="border-right:1px solid #eee;">${mapping.ACCOUNT_CODE}</td>
					<td style="border-right:1px solid #eee;">${mapping.ACCOUNT_NAME}</td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.CUST_CODE}</td>
					<td style="border-right:1px solid #eee;">${mapping.CUST_NAME}</td>
					<td style="border-right:1px solid #eee;text-align:center;"><c:if test="${mapping.ISICP eq 'Y' }"><spring:message code='yes'/></c:if><c:if test="${mapping.ISICP eq 'N' }"><spring:message code='no'/></c:if></td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.SOURSYS}</td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.LC}</td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.LC_BBAL}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.LC_DR}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.LC_CR}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.LC_EBAL}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:center;">${mapping.TC}</td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.TC_BBAL}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.TC_DR}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.TC_CR}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align:right;"><fmt:formatNumber value="${mapping.TC_EBAL}" pattern="#,##0.00#"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;">${mapping.CREATED_BY}</td>
					<td style="border-right:1px solid #eee;">${mapping.CREATION_DATE}</td>
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