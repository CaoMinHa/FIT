<%@page import="foxconn.fit.entity.base.EnumScenarios"%>
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
	
	$(".table-condensed a.delete").click(function(){
		var $this=$(this);
		layer.confirm("<spring:message code='confirm'/>?",{btn: ['<spring:message code='confirm'/>', '<spring:message code='cancel'/>'], title: "<spring:message code='tip'/>"},function(index){
			layer.close(index);
			var id=$this.parent().attr("mappingId");
			$.ajax({
				type:"POST",
				url:"${ctx}/budget/forecastDetailRevenue/delete",
				async:true,
				dataType:"json",
				data:{id:id},
				success: function(data){
					layer.alert(data.msg);
					if(data.flag=="success"){
						refresh();
					}
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		});
	});
});

//用于触发当前点击事件
function clickPage(page){
	$("#PageNo").val(page);
	var entity=$("#QEntity").val();
	var year=$("#QYear").val();
	var scenarios=$("#QScenarios").val();
	var version=$("#QVersion").val();
	$("#Content").load("${ctx}/budget/forecastDetailRevenue/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														entity:entity,year:year,scenarios:scenarios,version:version});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:350%;">
	<table class="table table-condensed table-hover">
		<c:choose>
			<c:when test="${locale eq 'en_US'}">
				<thead>
					<tr>
						<th rowspan="2"><spring:message code='operation'/></th>
						<th rowspan="2" style="text-align: center;">Year</th>
						<th rowspan="2" style="text-align: center;">SBU_Legal Entity</th>
						<th rowspan="2" style="text-align: center;">Sub_Industry</th>
						<th rowspan="2" style="text-align: center;">PART NO</th>
						<th rowspan="2" style="text-align: center;">End To Cust.ID-Name</th>
						<th rowspan="2" style="text-align: center;">Bill To Cust.ID-Name</th>
						<th rowspan="2" style="text-align: center;">Trading Type</th>
						<th rowspan="2" style="text-align: center;">Trading Currency</th>
						<th rowspan="2" style="text-align: center;">Activity</th>
						<th rowspan="2" style="text-align: center;">Scenarios</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Industry Demand Trend</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Industry Demand Trend-Served</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Component Usage</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Average Sales Price</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Total Available Market</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Served Available Market</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Allocation</th>
						<th rowspan="2" style="text-align: center;">Year Total Sales Amount</th>
						<th rowspan="2" style="text-align: center;">Year Total Quantity</th>
						<th colspan="12" style="text-align: center;border:1px solid #989292;">Sales</th>
						<th colspan="12" style="text-align: center;border:1px solid #989292;">Average Sales Price</th>
						<th colspan="12" style="text-align: center;border:1px solid #989292;">Sales Amount</th>
					</tr>
					<tr>
						<th style="text-align: center;">Jan</th>
						<th style="text-align: center;">Feb</th>
						<th style="text-align: center;">Mar</th>
						<th style="text-align: center;">Apr</th>
						<th style="text-align: center;">May</th>
						<th style="text-align: center;">Jun</th>
						<th style="text-align: center;">Jul</th>
						<th style="text-align: center;">Aug</th>
						<th style="text-align: center;">Sep</th>
						<th style="text-align: center;">Oct</th>
						<th style="text-align: center;">Nov</th>
						<th style="text-align: center;">Dec</th>
						<th style="text-align: center;border-left:1px solid #989292;">Jan</th>
						<th style="text-align: center;">Feb</th>
						<th style="text-align: center;">Mar</th>
						<th style="text-align: center;">Apr</th>
						<th style="text-align: center;">May</th>
						<th style="text-align: center;">Jun</th>
						<th style="text-align: center;">Jul</th>
						<th style="text-align: center;">Aug</th>
						<th style="text-align: center;">Sep</th>
						<th style="text-align: center;">Oct</th>
						<th style="text-align: center;">Nov</th>
						<th style="text-align: center;">Dec</th>
						<th style="text-align: center;border-left:1px solid #989292;">Jan</th>
						<th style="text-align: center;">Feb</th>
						<th style="text-align: center;">Mar</th>
						<th style="text-align: center;">Apr</th>
						<th style="text-align: center;">May</th>
						<th style="text-align: center;">Jun</th>
						<th style="text-align: center;">Jul</th>
						<th style="text-align: center;">Aug</th>
						<th style="text-align: center;">Sep</th>
						<th style="text-align: center;">Oct</th>
						<th style="text-align: center;">Nov</th>
						<th style="text-align: center;border-right:1px solid #989292;">Dec</th>
					</tr>
				</thead>
			</c:when>
			<c:otherwise>
				<thead>
					<tr>
						<th rowspan="2"><spring:message code='operation'/></th>
						<th rowspan="2" style="text-align: center;">年</th>
						<th rowspan="2" style="text-align: center;">SBU_法人</th>
						<th rowspan="2" style="text-align: center;">次產業</th>
						<th rowspan="2" style="text-align: center;">產品料號</th>
						<th rowspan="2" style="text-align: center;">最終客戶</th>
						<th rowspan="2" style="text-align: center;">賬款客戶</th>
						<th rowspan="2" style="text-align: center;">交易類型</th>
						<th rowspan="2" style="text-align: center;">交易貨幣</th>
						<th rowspan="2" style="text-align: center;">戰術</th>
						<th rowspan="2" style="text-align: center;">場景</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Industry Demand Trend</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Industry Demand Trend-Served</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Component Usage</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Average Sales Price</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Total Available Market</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Served Available Market</th>
						<th rowspan="2" style="text-align: center;" class="small_th">Allocation</th>
						<th rowspan="2" style="text-align: center;">全年營收合計</th>
						<th rowspan="2" style="text-align: center;">全年銷量合計</th>
						<th colspan="12" style="text-align: center;border:1px solid #989292;">銷量</th>
						<th colspan="12" style="text-align: center;border:1px solid #989292;">单价</th>
						<th colspan="12" style="text-align: center;border:1px solid #989292;">營收</th>
					</tr>
					<tr>
						<th style="text-align: center;">1月</th>
						<th style="text-align: center;">2月</th>
						<th style="text-align: center;">3月</th>
						<th style="text-align: center;">4月</th>
						<th style="text-align: center;">5月</th>
						<th style="text-align: center;">6月</th>
						<th style="text-align: center;">7月</th>
						<th style="text-align: center;">8月</th>
						<th style="text-align: center;">9月</th>
						<th style="text-align: center;">10月</th>
						<th style="text-align: center;">11月</th>
						<th style="text-align: center;">12月</th>
						<th style="text-align: center;border-left:1px solid #989292;">1月</th>
						<th style="text-align: center;">2月</th>
						<th style="text-align: center;">3月</th>
						<th style="text-align: center;">4月</th>
						<th style="text-align: center;">5月</th>
						<th style="text-align: center;">6月</th>
						<th style="text-align: center;">7月</th>
						<th style="text-align: center;">8月</th>
						<th style="text-align: center;">9月</th>
						<th style="text-align: center;">10月</th>
						<th style="text-align: center;">11月</th>
						<th style="text-align: center;">12月</th>
						<th style="text-align: center;border-left:1px solid #989292;">1月</th>
						<th style="text-align: center;">2月</th>
						<th style="text-align: center;">3月</th>
						<th style="text-align: center;">4月</th>
						<th style="text-align: center;">5月</th>
						<th style="text-align: center;">6月</th>
						<th style="text-align: center;">7月</th>
						<th style="text-align: center;">8月</th>
						<th style="text-align: center;">9月</th>
						<th style="text-align: center;">10月</th>
						<th style="text-align: center;">11月</th>
						<th style="text-align: center;border-right:1px solid #989292;">12月</th>
					</tr>
				</thead>
			</c:otherwise>
		</c:choose>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;" mappingId="${mapping.id }">
						<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a>
					</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.year}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.entity}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.industry}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.product}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.combine}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.customer}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.type}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.currency}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.activity}</td>
					<td style="border-right:1px solid #eee;text-align: center;">
						<c:forEach items="<%=EnumScenarios.values() %>" var="scenarios">
							<c:if test="${scenarios.code eq mapping.scenarios}">
								<c:choose>
									<c:when test="${locale eq 'en_US'}">${scenarios.code }</c:when>
									<c:otherwise>${scenarios.name }</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.industryDemandTrend}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.industryDemandTrendServed}</td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.componentUsage}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.averageSalesPrice}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.totalAvailableMarket}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.servedAvailableMarket}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.allocation}</td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantity}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.quantityMonth12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.priceMonth12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;text-align: center;"><fmt:formatNumber value="${mapping.revenueMonth12}" pattern="#,##0.00"></fmt:formatNumber></td>
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