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
						url:"${ctx}/bi/investmentBudget/delete",
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
			$("#Content").load("${ctx}/bi/investmentBudget/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
				orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
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
<div style="width:260%;">
	<c:choose>
		<c:when test="${scenarios eq 'forecast'}">
			<table class="table table-condensed table-hover">
				<thead class="theadCss">
				<tr>
					<th class="onlyQuery" rowspan="2"><spring:message code='operation'/></th>
					<th colspan="9">基礎數據</th>
					<th colspan="12">FY${year}</th>
					<th colspan="2">FY${year+1}</th>
					<th colspan="2">FY${year+2}</th>
				</tr>
				<tr>
					<th>投資編號</th>
					<th>設備類別</th>
					<th>SBU_法人</th>
					<th>提出部門</th>
					<th>Segment</th>
					<th>Main business</th>
					<th>產業</th>
					<th>使用部門</th>
					<th>投資類型</th>
					<th>專案名稱.</th>
					<th>投資對象(設備)名稱</th>
					<th>產品系列</th>
					<th>需求數量(或場地面積)</th>
					<th>產品生命周期-年(用於購置設備)</th>
					<th>請購單或模治具執行單年月</th>
					<th>驗收單年月(轉固定資產月份)</th>
					<th>結報年月或模治具執行單結案年月</th>
					<th>投資金額(本位幣)</th>
					<th>投資説明</th>
					<th>預估收益-營收(本位幣)</th>
					<th>預估收益-淨利(本位幣)</th>
					<th>預估收益-營收(本位幣)</th>
					<th>預估收益-淨利(本位幣)</th>
					<th>預估收益-營收(本位幣)</th>
					<th>預估收益-淨利(本位幣)</th>
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
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.investmentNo}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.categoryEquipment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.department}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.segment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.mainBusiness}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.industry}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.useDepartment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.investmentType}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.projectName}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.objectInvestment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productSeries}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.quantityRequired}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productLifeCycle}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.poPeriod}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.receiptDate}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.duePeriod}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountInvestment}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.descriptionInvestment}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.profit}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.nextRevenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.nextProfit}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.afterRevenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.afterProfit}" pattern="#,##0.##"></fmt:formatNumber></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:when test="${scenarios eq 'budget'}">
			<table class="table table-condensed table-hover">
				<thead class="theadCss">
				<tr>
					<th class="onlyQuery" rowspan="2"><spring:message code='operation'/></th>

					<th colspan="9">基礎數據</th>
					<th colspan="12">FY${year}</th>
					<th colspan="2">FY${year+1}</th>
					<th colspan="2">FY${year+2}</th>
				</tr>
				<tr>
					<th>投資編號</th>
					<th>設備類別</th>
					<th>SBU_法人</th>
					<th>提出部門</th>
					<th>Segment</th>
					<th>Main business</th>
					<th>產業</th>
					<th>使用部門</th>
					<th>投資類型</th>
					<th>專案名稱.</th>
					<th>投資對象(設備)名稱</th>
					<th>產品系列</th>
					<th>需求數量(或場地面積)</th>
					<th>產品生命周期-年(用於購置設備)</th>
					<th>請購單或模治具執行單年月</th>
					<th>驗收單年月(轉固定資產月份)</th>
					<th>結報年月或模治具執行單結案年月</th>
					<th>投資金額(本位幣)</th>
					<th>投資説明</th>
					<th>預估收益-營收(本位幣)</th>
					<th>預估收益-淨利(本位幣)</th>
					<th>預估收益-營收(本位幣)</th>
					<th>預估收益-淨利(本位幣)</th>
					<th>預估收益-營收(本位幣)</th>
					<th>預估收益-淨利(本位幣)</th>
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
						<td style="border-right:1px solid #eee;text-align:left;">${mapping.investmentNo}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.categoryEquipment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.department}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.segment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.mainBusiness}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.industry}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.useDepartment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.investmentType}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.projectName}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.objectInvestment}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productSeries}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.quantityRequired}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.productLifeCycle}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.poPeriod}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.receiptDate}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.duePeriod}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.amountInvestment}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.descriptionInvestment}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.revenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.profit}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.nextRevenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.nextProfit}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.afterRevenue}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.afterProfit}" pattern="#,##0.##"></fmt:formatNumber></td>
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