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
	
	$(".table-condensed a.delete").click(function(){
		var $this=$(this);
		layer.confirm("<spring:message code='confirm'/>?",{btn: ['<spring:message code='confirm'/>', '<spring:message code='cancel'/>'], title: "<spring:message code='tip'/>"},function(index){
			layer.close(index);
			var id=$this.parent().attr("mappingId");
			if(!$("#QScenarios").val()){
				layer.alert("請選擇場景！(Please select a scene)");
				return;
			}
			$.ajax({
				type:"POST",
				url:"${ctx}/bi/budgetForecastDetailRevenue/delete",
				async:true,
				dataType:"json",
				data:{id:id,scenarios: $("#QScenarios").val()},
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
	if($("#onlyQuery").val()=="Y"){
		$(".onlyQuery").hide();
	}
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
	$("#Content").load("${ctx}/bi/budgetForecastDetailRevenue/list",{pageNo:$("#PageNo").val(),
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
<div style="width:450%;">
	<c:choose>
		<c:when test="${scenarios eq 'forecast'}">
			<table class="table table-condensed table-hover">
				<thead class="theadCss">
					<tr>
						<th rowspan="3"><spring:message code='operation'/></th>
						<th rowspan="2" colspan="15">基礎數據</th>
						<th colspan="39">FY${year}</th>
					</tr>
					<tr>
						<th>全年合計</th>
						<th>全年合計</th>
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
						<th>全年合計</th>
					</tr>
					<tr>
						<th>SBU_銷售法人</th>
						<th>SBU_製造法人</th>
						<th>Segment</th>
						<th>主產業</th>
						<th>次產業</th>
						<th>3+3</th>
						<th>三大技術</th>
						<th>產品系列</th>
						<th>產品料號</th>
						<th>賬款客戶</th>
						<th>最終客戶</th>
						<th>機種</th>
						<th>交易類型</th>
						<th>報告幣種</th>
						<th>PM</th>
						<th>銷貨收入</th>
						<th>銷售數量</th>
						<th colspan="12">銷售數量</th>
						<th colspan="12">平均單價</th>
						<th colspan="13">銷貨收入</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td class="onlyQuery"  style="border-right:1px solid #eee;" mappingId="${mapping.id }">
							<c:if test="${mapping.version eq 'V00'}">
								<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a>
							</c:if>
						</td>
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.makeEntity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.segment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.mainIndustry}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.industry}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.mainBusiness}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.three}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productSeries}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productNo}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.loanCustomer}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.endCustomer}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.typeOfAirplane}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.tradeType}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.currency}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.pm}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantity}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.##"></fmt:formatNumber></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:when test="${scenarios eq 'budget'}">
			<table class="table table-condensed table-hover">
					<thead class="theadCss">
						<tr>
							<th class="onlyQuery"  rowspan="3"><spring:message code='operation'/></th>
							<th rowspan="2" colspan="15">基礎數據</th>
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
							<th colspan="13">FY${year}</th>
						</tr>
						<tr>
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
							<th>全年合計</th>
						</tr>
						<tr>
							<th>SBU_銷售法人</th>
							<th>SBU_製造法人</th>
							<th>Segment</th>
							<th>主產業</th>
							<th>次產業</th>
							<th>3+3</th>
							<th>三大技術</th>
							<th>產品系列</th>
							<th>產品料號</th>
							<th>賬款客戶</th>
							<th>最終客戶</th>
							<th>機種</th>
							<th>交易類型</th>
							<th>報告幣種</th>
							<th>PM</th>
							<th colspan="5">銷貨收入</th>
							<th colspan="5">銷售數量</th>
							<th colspan="12">銷售數量</th>
							<th colspan="12">平均單價</th>
							<th colspan="13">銷貨收入</th>
						</tr>
					</thead>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td class="onlyQuery" style="border-right:1px solid #eee;" mappingId="${mapping.id }">
							<c:if test="${mapping.version eq 'V00'}">
								<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a>
							</c:if>
						</td>
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.makeEntity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.segment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.mainIndustry}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.industry}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.mainBusiness}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.three}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productSeries}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productNo}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.loanCustomer}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.endCustomer}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.typeOfAirplane}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.tradeType}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.currency}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.pm}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueNextyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueTwoyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueThreeyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueFouryear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantity}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityNextyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityTwoyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityThreeyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityFouryear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.quantityMonth12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.priceMonth12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenueMonth12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.##"></fmt:formatNumber></td>
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