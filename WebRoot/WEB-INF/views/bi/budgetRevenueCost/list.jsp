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
	var entity="";
	$("input[name=entitys]:checked").each(function(i,dom){
		entity+=$(dom).val()+",";
	});
	$("#Content").load("${ctx}/bi/budgetRevenueCost/list",{pageNo:$("#PageNo").val(),
		scenarios:$("#QScenarios").val(),
		entitys:entity.substring(0,entity.length-1),year:$("#QYear").val(),
		version:$("#QVersion").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
	<c:choose>
		<c:when test="${scenarios eq 'forecast'}">
	<div style="width:400%;">
			<table class="table table-condensed table-hover" >
				<thead class="theadCss">
				<tr>
					<th rowspan="2" colspan="13">基礎數據</th>
					<th colspan="52">FY${year}</th>
				</tr>
				<tr>
					<th colspan="4">全年合計</th>
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
				</tr>
				<tr>
					<th>SBU_銷售法人</th>
					<th>SBU_製造法人</th>
					<th>Segment</th>
					<th>主產業</th>
					<th>次產業</th>
					<th>Main Business</th>
					<th>3+3</th>
					<th>產品系列</th>
					<th>產品料號</th>
					<th>賬款客戶</th>
					<th>最終客戶</th>
					<th>交易類型</th>
					<th>交易貨幣</th>
					<th>銷貨收入</th>
					<th>銷貨成本</th>
					<th>銷貨毛利</th>
					<th>銷貨數量</th>
					<th colspan="12">銷貨收入</th>
					<th colspan="12">銷貨成本</th>
					<th colspan="12">銷貨毛利</th>
					<th colspan="12">銷貨數量</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${page.result}" var="row">
					<tr>
						<c:forEach items="${row}" var="data" begin="2" varStatus="status">
							<td style="border-right:1px solid #eee;text-align: left;">${data}</td>
						</c:forEach>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:when test="${scenarios eq 'budget'}">
	<div style="width:650%;">
			<table class="table table-condensed table-hover">
					<thead class="theadCss">
						<tr>
							<th rowspan="2" colspan="13">基礎數據</th>
							<th>FY${year}</th>
							<th>FY${year+1}</th>
							<th>FY${year+2}</th>
							<th>FY${year+3}</th>
							<th>FY${year+4}</th>
							<th>FY${year}</th>
							<th>FY${year+1}</th>
							<th>FY${year+2}</th>
							<th>FY${year+3}</th>
							<th>FY${year+4}</th>
							<th>FY${year}</th>
							<th>FY${year+1}</th>
							<th>FY${year+2}</th>
							<th>FY${year+3}</th>
							<th>FY${year+4}</th>
							<th>FY${year}</th>
							<th>FY${year+1}</th>
							<th>FY${year+2}</th>
							<th>FY${year+3}</th>
							<th>FY${year+4}</th>
							<th colspan="12">FY${year}</th>
							<th colspan="12">FY${year}</th>
							<th colspan="12">FY${year}</th>
							<th colspan="12">FY${year}</th>
						</tr>
						<tr>
							<th colspan="5">全年合計</th>
							<th colspan="5">全年合計</th>
							<th colspan="5">全年合計</th>
							<th colspan="5">全年合計</th>
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
						</tr>
						<tr>
							<th>SBU_銷售法人</th>
							<th>SBU_製造法人</th>
							<th>Segment</th>
							<th>主產業</th>
							<th>次產業</th>
							<th>Main Business</th>
							<th>3+3</th>
							<th>產品系列</th>
							<th>產品料號</th>
							<th>賬款客戶</th>
							<th>最終客戶</th>
							<th>交易類型</th>
							<th>交易貨幣</th>
							<th colspan="5">銷貨收入</th>
							<th colspan="5">銷貨成本</th>
							<th colspan="5">銷貨毛利</th>
							<th colspan="5">銷貨數量</th>
							<th colspan="12">銷貨收入</th>
							<th colspan="12">銷貨成本</th>
							<th colspan="12">銷貨毛利</th>
							<th colspan="12">銷貨數量</th>
						</tr>
					</thead>
				<tbody>
				<c:forEach items="${page.result}" var="row">
						<tr>
							<c:forEach items="${row}" var="data" begin="2" varStatus="status">
								<td style="border-right:1px solid #eee;text-align: left;">${data}</td>
						    </c:forEach>
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