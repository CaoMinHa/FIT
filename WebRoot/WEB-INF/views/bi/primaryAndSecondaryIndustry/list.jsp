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
	var SBU=$("#QSBU").val();
	$("#Content").load("${ctx}/bi/primaryAndSecondaryIndustry/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														SBU:SBU});
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
				<th>BU</th>
				<th>SBU</th>
				<th>PM主管</th>
				<th>收集窗口</th>
				<th>產品品號</th>
				<th>客戶產品品號</th>
				<th>帳款客戶（代碼）</th>
				<th>帳款客戶（名稱）</th>
				<th>帳款客戶（集團）</th>
				<th>對照檔（產品料號+客戶料號+客戶代碼）</th>
				<th>主產業</th>
				<th>次產業</th>
				<th>品牌or直售</th>
				<th>品牌客戶名稱</th>
				<th>客戶分級分類</th>
				<th>更新匯總時間</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;">${mapping.BU}</td>
					<td style="border-right:1px solid #eee;">${mapping.SBU}</td>
					<td style="border-right:1px solid #eee;">${mapping.pmDirector}</td>
					<td style="border-right:1px solid #eee;">${mapping.collectionWindow}</td>
					<td style="border-right:1px solid #eee;">${mapping.product}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerProduct}</td>
					<td style="border-right:1px solid #eee;">${mapping.accountCustomerCode}</td>
					<td style="border-right:1px solid #eee;">${mapping.accountCustomerName}</td>
					<td style="border-right:1px solid #eee;">${mapping.accountCustomerGroup}</td>
					<td style="border-right:1px solid #eee;">${mapping.document}</td>
					<td style="border-right:1px solid #eee;">${mapping.majorIndustry}</td>
					<td style="border-right:1px solid #eee;">${mapping.secondaryIndustry}</td>
					<td style="border-right:1px solid #eee;">${mapping.brand}</td>
					<td style="border-right:1px solid #eee;">${mapping.brandCustomerName}</td>
					<td style="border-right:1px solid #eee;">${mapping.customerLevel}</td>
					<td style="border-right:1px solid #eee;">${mapping.aggregateTime}</td>
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