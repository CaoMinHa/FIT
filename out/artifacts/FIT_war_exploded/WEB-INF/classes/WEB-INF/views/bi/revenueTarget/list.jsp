<%@page import="foxconn.fit.entity.base.EnumVersion"%>
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
	var period=$("#QPeriod").val();
	var version=$("#QVersion").val();
	$("#Content").load("${ctx}/bi/revenueTarget/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														year:period,version:version});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:200%;">
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th style="text-align: center;">年</th>
				<th style="text-align: center;">版本</th>
				<th style="text-align: center;">組織</th>
				<th style="text-align: center;">體系</th>
				<th style="text-align: center;">情景</th>
				<th style="text-align: center;">主產業</th>
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
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.year}</td>
					<td style="border-right:1px solid #eee;text-align: center;">
						<c:forEach items="<%=EnumVersion.values() %>" var="version">
							<c:if test="${version.value eq mapping.version}">${version.name }</c:if>
						</c:forEach>
					</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.organization}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.system}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.scene}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.industry}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month1}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month2}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month3}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month4}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month5}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month6}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month7}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month8}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month9}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month10}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month11}</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.month12}</td>
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