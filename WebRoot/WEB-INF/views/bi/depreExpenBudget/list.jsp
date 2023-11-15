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
				url:"${ctx}/bi/depreExpenBudget/delete",
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
	$("#Content").load("${ctx}/bi/depreExpenBudget/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
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
<div style="width:180%;">
	<c:choose>
		<c:when test="${scenarios eq 'forecast'}">
			<table class="table table-condensed table-hover">
				<thead class="theadCss">
				<tr>
					<th class="onlyQuery" rowspan="3"><spring:message code='operation'/></th>
					<th rowspan="2" colspan="3">基礎數據</th>
					<th colspan="12">FY${year}</th>
				</tr>
				<tr>
					<th colspan="12">折舊費用（在製）(本位幣)</th>
				</tr>
				<tr>
					<th>SBU_法人</th>
					<th>提出部門</th>
					<th>設備類別</th>
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
				</thead>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td class="onlyQuery" style="border-right:1px solid #eee;" mappingId="${mapping.id }">
							<c:if test="${mapping.version eq 'V00'}">
								<a href="javascript:void(0);"  class="m-r-md delete"><spring:message code='delete'/></a>
							</c:if>
						</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.department}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.categoryEquipment}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.jan}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.feb}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.mar}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.apr}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.may}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.jun}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.jul}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.aug}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.sep}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.oct}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.nov}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dec}" pattern="#,##0.##"></fmt:formatNumber></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:when test="${scenarios eq 'budget'}">
			<table class="table table-condensed table-hover">
				<c:if test="${languageS eq 'en_US'}">
					<thead class="theadCss">
					<tr>
						<th class="onlyQuery" rowspan="3"><spring:message code='operation'/></th>
						<th rowspan="2" colspan="3">Basic Info</th>
						<th colspan="12">FY${year}</th>
					</tr>
					<tr>
						<th colspan="12">Depreciation expense (in process) (standard currency)</th>
					</tr>
					<tr>
						<th>SBU_Legal Entity</th>
						<th>Proposing department</th>
						<th>Equipment category</th>
						<th>Jan</th>
						<th>Feb</th>
						<th>Mar</th>
						<th>Apr</th>
						<th>May</th>
						<th>Jun</th>
						<th>Jul</th>
						<th>Aug</th>
						<th>Sep</th>
						<th>Oct</th>
						<th>Nov</th>
						<th>Dec</th>
					</tr>
					</thead>
				</c:if>
				<c:if test="${languageS eq 'zh_CN'}">
					<thead class="theadCss">
					<tr>
						<th class="onlyQuery" rowspan="3"><spring:message code='operation'/></th>
						<th rowspan="2" colspan="3">基礎數據</th>
						<th colspan="12">FY${year}</th>
					</tr>
					<tr>
						<th colspan="12">折舊費用（在製）(本位幣)</th>
					</tr>
					<tr>
						<th>SBU_法人</th>
						<th>提出部門</th>
						<th>設備類別</th>
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
					</thead>
				</c:if>
				<tbody>
				<c:forEach items="${page.result}" var="mapping">
					<tr>
						<td class="onlyQuery" style="border-right:1px solid #eee;" mappingId="${mapping.id }">
							<c:if test="${mapping.version eq 'V00'}">
								<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a>
							</c:if>
						</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.entity}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.department}</td>
						<td style="border-right:1px solid #eee;text-align: left;">${mapping.categoryEquipment}</td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.jan}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.feb}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.mar}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.apr}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.may}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.jun}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.jul}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.aug}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.sep}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.oct}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.nov}" pattern="#,##0.##"></fmt:formatNumber></td>
						<td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping.dec}" pattern="#,##0.##"></fmt:formatNumber></td>
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