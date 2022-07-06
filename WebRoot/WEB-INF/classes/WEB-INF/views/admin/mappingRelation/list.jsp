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
	
	$("a[sortBy]").click(function(){
		$(this).find("i").toggleClass("sort");
		clickPage(Page.getPage());
	});
	
	$.each("${page.orderDir}".split(","),function(i,n){
		$("a[sortBy]:eq("+i+")").find("i[sortDir="+n+"]").addClass("sort").siblings().removeClass("sort");
	});
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var entity=$("#QEntity").val();
	var dimensionName=$("#QDimensionName").val();
	var SRCKEY=$("#QSRCKEY").val();
	var TARGKEY=$("#QTARGKEY").val();
	var orderBy="",orderDir="";
	$("a[sortBy]").each(function(index,dom){
		orderBy=orderBy+$(dom).attr("sortBy")+",";
		orderDir=orderDir+$(dom).find("i.sort").attr("sortDir")+",";
	});
	if(orderBy.length>0 && orderDir.length>0){
		orderBy=orderBy.substr(0,orderBy.length-1);
		orderDir=orderDir.substr(0,orderDir.length-1);
	}
	$("#Content").load("${ctx}/admin/mappingRelation/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:orderBy,orderDir:orderDir,
														entity:entity,dimensionName:dimensionName,
														SRCKEY:SRCKEY,TARGKEY:TARGKEY},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div>
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th style="border-right:1px solid #eee;text-align: center;">公司编码<a sortBy="ENTITY" style="float:right;color:#a8a6a6;position: relative;"><i sortDir="asc" class="icon-sort-up" style="font-size:20px;position:absolute;"/><i sortDir="desc" class="icon-sort-down" style="font-size:20px;"/></a></th>
				<th style="border-right:1px solid #eee;text-align: center;">映射类别<a sortBy="DIMNAME" style="float:right;color:#a8a6a6;position: relative;"><i sortDir="asc" class="icon-sort-up" style="font-size:20px;position:absolute;"/><i sortDir="desc" class="icon-sort-down" style="font-size:20px;"/></a></th>
				<th style="border-right:1px solid #eee;text-align: center;">映射源值<a sortBy="SRCKEY" style="float:right;color:#a8a6a6;position: relative;"><i sortDir="asc" class="icon-sort-up" style="font-size:20px;position:absolute;"/><i sortDir="desc" class="icon-sort-down" style="font-size:20px;"/></a></th>
				<th style="border-right:1px solid #eee;text-align: center;">映射源值名称</th>
				<th style="border-right:1px solid #eee;text-align: center;">HFM映射目标值</th>
				<th style="border-right:1px solid #eee;text-align: center;">HFM映射目标名称</th>
				<th style="border-right:1px solid #eee;text-align: center;">CCT科目分類</th>
				<th style="border-right:1px solid #eee;text-align: center;">CCT科目屬性</th>
				<th style="border-right:1px solid #eee;text-align: center;">是否变号</th>
				<th style="border-right:1px solid #eee;text-align: center;">科目標識</th>
				<th style="border-right:1px solid #eee;text-align: center;">创建用户</th>
				<th style="border-right:1px solid #eee;text-align: center;">创建日期</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping">
				<tr>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.ENTITY}</td>
					<td style="border-right:1px solid #eee;text-align: center;">
						<c:forEach items="<%=EnumDimensionName.values() %>" var="dimension">
							<c:if test="${dimension.code eq mapping.DIMNAME}">${dimension.name }</c:if>
						</c:forEach>
					</td>
					<td style="border-right:1px solid #eee;">${mapping.SRCKEY}</td>
					<td style="border-right:1px solid #eee;">${mapping.SRCDESC}</td>
					<td style="border-right:1px solid #eee;">${mapping.TARGKEY}</td>
					<td style="border-right:1px solid #eee;">${mapping.TARGDESC}</td>
					<td style="border-right:1px solid #eee;">${mapping.CCT_ACCOUNT}</td>
					<td style="border-right:1px solid #eee;">${mapping.CCT_ACCOUNT_ATT}</td>
					<td style="border-right:1px solid #eee;text-align: center;">
						<c:choose>
							<c:when test="${mapping.CHANGESIGN eq true}">是</c:when>
							<c:otherwise>否</c:otherwise>
						</c:choose>
					</td>
					<td style="border-right:1px solid #eee;text-align: center;">${mapping.CATEGORY}</td>
					<td style="border-right:1px solid #eee;">${mapping.CREATED_BY}</td>
					<td style="border-right:1px solid #eee;">${mapping.CREATION_DATE}</td>
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