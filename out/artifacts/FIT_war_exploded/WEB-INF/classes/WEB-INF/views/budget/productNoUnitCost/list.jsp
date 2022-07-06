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
				url:"${ctx}/budget/productNoUnitCost/delete",
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
	var year=$("#QYear").val();
	var scenarios=$("#QScenarios").val();
	var sbu=$("#QSBU").val();
	var product=$("#QProduct").val();
	var version=$("#QVersion").val();
	$("#Content").load("${ctx}/budget/productNoUnitCost/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														year:year,scenarios:scenarios,product:product,version:version,sbu:sbu});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:500%;">
	<table class="table table-condensed table-hover">
		<c:choose>
			<c:when test="${locale eq 'en_US'}">
				<thead>
					<tr>
						<th rowspan="2" style="border:1px solid #989292;"><spring:message code='operation'/></th>
						<th rowspan="2" style="border:1px solid #989292;">Year</th>
						<th rowspan="2" style="border:1px solid #989292;">Scenarios</th>
						<th rowspan="2" style="border:1px solid #989292;">SBU</th>
						<th rowspan="2" style="border:1px solid #989292;">SBU_Legal Entity</th>
						<th rowspan="2" style="border:1px solid #989292;">PART NO</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Jan</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Feb</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Mar</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Apr</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">May</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Jun</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Jul</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Aug</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Sep</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Oct</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Nov</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">Dec</th>
					</tr>
					<tr>
						<th>Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
						
						<th style="border-left:1px solid #989292;">Unit Material Cost_Standard</th>
						<th>Unit Material Cost_Adjusted</th>
						<th>Unit Material Cost</th>
						<th>Unit Production Time_Standard</th>
						<th>Unit Production Time_Adjusted</th>
						<th>Unit Production Time</th>
						<th>Unit Labor Cost Rate_Standard</th>
						<th>Unit Labor Cost Rate_Adjusted</th>
						<th>Unit Labor Cost Rate</th>
						<th>Unit Labor Cost</th>
						<th>Unit Manufacturing Cost Rate_Standard</th>
						<th>Unit Manufacturing Cost Rate_Adjusted</th>
						<th>Unit Manufacturing Cost Rate</th>
						<th>Unit Manufacturing Cost</th>
						<th style="border-right:1px solid #989292;">Unit Cost</th>
					</tr>
				</thead>
			</c:when>
			<c:otherwise>
				<thead>
					<tr>
						<th rowspan="2" style="border:1px solid #989292;"><spring:message code='operation'/></th>
						<th rowspan="2" style="border:1px solid #989292;">年</th>
						<th rowspan="2" style="border:1px solid #989292;">场景</th>
						<th rowspan="2" style="border:1px solid #989292;">SBU</th>
						<th rowspan="2" style="border:1px solid #989292;">SBU_法人</th>
						<th rowspan="2" style="border:1px solid #989292;">產品料號</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">1月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">2月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">3月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">4月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">5月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">6月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">7月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">8月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">9月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">10月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">11月</th>
						<th colspan="15" style="text-align:center;border:1px solid #989292;">12月</th>
					</tr>
					<tr>
						<th>單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
						
						<th style="border-left:1px solid #989292;">單位材料標準成本</th>
						<th>單位材料調整成本</th>
						<th>單位材料成本</th>
						<th>單位標準工時</th>
						<th>單位調整工時</th>
						<th>單位工時</th>
						<th>單位人工標準費率</th>
						<th>單位人工調整費率</th>
						<th>單位人工費率</th>
						<th>單位人工成本</th>
						<th>單位製造標準費率</th>
						<th>單位製造調整費率</th>
						<th>單位製造費率</th>
						<th>單位製造成本</th>
						<th style="border-right:1px solid #989292;">單位成本</th>
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
					<td style="border-right:1px solid #eee;">${mapping.year}</td>
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
					<td style="border-right:1px solid #eee;">${mapping.sbu}</td>
					<td style="border-right:1px solid #eee;">${mapping.entity}</td>
					<td style="border-right:1px solid #eee;">${mapping.product}</td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost1}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost1}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost2}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost2}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost3}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost3}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost4}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost4}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost5}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost5}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost6}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost6}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost7}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost7}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost8}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost8}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost9}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost9}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost10}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost10}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost11}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost11}" pattern="#,##0.00"></fmt:formatNumber></td>
					
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialStandardCost12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialAdjustCost12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.materialCost12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.standardHours12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.adjustHours12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.hours12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualStandardRate12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualAdjustRate12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualRate12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manualCost12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureStandardRate12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureAdjustRate12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureRate12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.manufactureCost12}" pattern="#,##0.00"></fmt:formatNumber></td>
					<td style="border-right:1px solid #eee;"><fmt:formatNumber value="${mapping.unitCost12}" pattern="#,##0.00"></fmt:formatNumber></td>
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