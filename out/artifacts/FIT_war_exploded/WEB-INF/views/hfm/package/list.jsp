<%@page import="foxconn.fit.entity.base.EnumDimensionName"%>
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
	var dataType=$("#DataType").val();
	var queryCondition=$("#QueryCondition").serialize();
	$("#Content").load("${ctx}/hfm/package/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
												  dataType:dataType,queryCondition:queryCondition},
														function(){$("#loading").fadeOut(1000);});
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
				<c:forEach items="${titleList }" var="title">
					<th style="border-right:1px solid #eee;text-align: center;">${title[1] }</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="row">
				<tr>
					<c:forEach items="${row }" var="data" varStatus="status">
						<td style="border-right:1px solid #eee;">${data }</td>
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