<%@page import="foxconn.fit.entity.base.EnumScenarios"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<head>
	<style type="text/css">
		.tableCss th{
			border-left: 1px solid #eee;
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
				url:"${ctx}/bi/budgetProductNoUnitCost/delete",
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
});

//用于触发当前点击事件
function clickPage(page){
	if(!$("#QScenarios").val()){
		layer.alert("請選擇場景！(Please select a scene)");
		return;
	}
	$("#loading").show();
	$("#PageNo").val(page);
	var entity="";
	$("input[name=entitys]:checked").each(function(i,dom){
		entity+=$(dom).val()+",";
	});

	$("#Content").load("${ctx}/bi/budgetProductNoUnitCost/list",{pageNo:$("#PageNo").val(),
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
<div style="width:500%;">
	<c:choose>
		<c:when test="${scenarios eq 'forecast'}">
			<table class="table table-condensed table-hover">
				<thead class="tableCss">
				<tr>
					<th rowspan="3"><spring:message code='operation'/></th>
					<th rowspan="3">SBU_法人</th>
					<th rowspan="3">產品系列</th>
					<th rowspan="3">交易類型</th>
					<th colspan="60">FY${year}</th>
					<th rowspan="2" colspan="4">FY${year}</th>
				</tr>
				<tr>
					<th colspan="5">1月</th>
					<th colspan="5">2月</th>
					<th colspan="5">3月</th>
					<th colspan="5">4月</th>
					<th colspan="5">5月</th>
					<th colspan="5">6月</th>
					<th colspan="5">7月</th>
					<th colspan="5">8月</th>
					<th colspan="5">9月</th>
					<th colspan="5">10月</th>
					<th colspan="5">11月</th>
					<th colspan="5">12月</th>
				</tr>
				<tr>
					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td style="border-right:1px solid #eee;" mappingId="${mapping.id }">
							<c:if test="${mapping.version eq 'V00'}">
								<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a>
							</c:if>
						</td>
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;">${mapping.product}</td>
						<td style="border-right:1px solid #eee;">${mapping.tradeType}</td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost1}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost2}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost3}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost4}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost5}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost6}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost7}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost8}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost9}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost10}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost11}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost12}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost}" pattern="#,##0.##"></fmt:formatNumber></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:when test="${scenarios eq 'budget'}">
			<table class="table table-condensed table-hover">
				<thead class="tableCss">
				<tr>
					<th rowspan="3"><spring:message code='operation'/></th>
					<th rowspan="3">SBU_法人</th>
					<th rowspan="3">產品系列</th>
					<th rowspan="3">交易類型</th>
					<th colspan="60">FY${year}</th>
					<th rowspan="2" colspan="4">FY${year}</th>
					<th rowspan="2" colspan="4">FY${year+1}</th>
					<th rowspan="2" colspan="4">FY${year+2}</th>
					<th rowspan="2" colspan="4">FY${year+3}</th>
					<th rowspan="2" colspan="4">FY${year+4}</th>
				</tr>
				<tr>
					<th colspan="5">1月</th>
					<th colspan="5">2月</th>
					<th colspan="5">3月</th>
					<th colspan="5">4月</th>
					<th colspan="5">5月</th>
					<th colspan="5">6月</th>
					<th colspan="5">7月</th>
					<th colspan="5">8月</th>
					<th colspan="5">9月</th>
					<th colspan="5">10月</th>
					<th colspan="5">11月</th>
					<th colspan="5">12月</th>
				</tr>
				<tr>
					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>銷售數量</th>
					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>

					<th>材料成本</th>
					<th>人工成本</th>
					<th>製造費用</th>
					<th>銷貨成本</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td style="border-right:1px solid #eee;" mappingId="${mapping.id }">
							<c:if test="${mapping.version eq 'V00'}">
								<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a>
							</c:if>
						</td>
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;">${mapping.product}</td>
						<td style="border-right:1px solid #eee;">${mapping.tradeType}</td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost1}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost1}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost2}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost2}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost3}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost3}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost4}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost4}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost5}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost5}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost6}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost6}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost7}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost7}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost8}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost8}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost9}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost9}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost10}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost10}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost11}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost11}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.salesQuantity12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost12}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost12}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCost}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCost}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCostNextyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCostNextyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCostNextyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCostNextyear}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCostTwoyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCostTwoyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCostTwoyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCostTwoyear}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCostThreeyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCostThreeyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCostThreeyear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCostThreeyear}" pattern="#,##0.##"></fmt:formatNumber></td>

						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCostFouryear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.laborCostFouryear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCostFouryear}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.sellingCostFouryear}" pattern="#,##0.##"></fmt:formatNumber></td>
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