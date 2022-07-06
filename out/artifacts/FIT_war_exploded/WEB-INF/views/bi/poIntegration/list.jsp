<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
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
	var date=$("#QDate").val();
	var entity=$("#QEntity").val();
	var tableName=$("#QTableName").val();
	$("#Content").load("${ctx}/bi/poIntegration/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														date:date,tableName:tableName,poCenter:$("#QpoCenter").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div <c:choose><c:when test="${fn:length(columns) gt 30}">style="width:500%;"</c:when><c:when test="${fn:length(columns) gt 25}">style="width:400%;"</c:when><c:when test="${fn:length(columns) gt 20}">style="width:300%;"</c:when><c:otherwise>style="width:200%;"</c:otherwise></c:choose>>
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th>序号</th>
				<c:forEach items="${columns }" var="column" varStatus="status">
					<c:choose>
						<c:when test="${column.comments=='CD2 Total'||column.comments=='CD3 Total'}">
							<th style="background-color: beige">${column.comments }</th>
						</c:when>
						<c:otherwise>
							<th >${column.comments }</th>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index}" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;border-right:1px solid #eee;">
									<input name="ID" type="checkbox" value="${mapping[i]}"/>
								</td>
							</c:when>
							<c:when test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==21|| tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==27}">
								<td style="border-right:1px solid #eee;background-color: beige">${mapping[i]}</td>
							</c:when>
							<c:otherwise>
								<td style="border-right:1px solid #eee;">${mapping[i]}</td>
							</c:otherwise>
						</c:choose>
<%--						<td style="border-right:1px solid #eee;">${mapping[i]}</td>--%>
					</c:forEach>
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