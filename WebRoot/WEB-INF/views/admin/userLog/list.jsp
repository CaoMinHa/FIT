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
	
	$("a.more").click(function(){
		layer.alert($(this).attr("entire"),{title:"详情"});
	});
});

//用于触发当前点击事件
function clickPage(page){
	$("#PageNo").val(page);
	var method=$("#QMethod").val();
	var username=$("#QUsername").val();
	var status=$("#QStatus").val();
	var startTime=$("#QStartTime").val();
	var endTime=$("#QEndTime").val();
	$("#Content").load("${ctx}/admin/userLog/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														method : method, operator : username,
														status : status,startTime:startTime,endTime:endTime});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<table class="table table-condensed table-hover">
	<thead>
		<tr>
			<th>方法</th>
			<th>参数</th>
			<th>状态</th>
			<th>结果</th>
			<th>操作人</th>
			<th>操作时间</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.result}" var="mapping">
			<tr>
				<td style="border-right:1px solid #eee;min-width:160px;">${mapping.method}</td>
				<td style="border-right:1px solid #eee;">
					<c:choose>
						<c:when test="${fn:length(mapping.parameter) > 50}"><a class="more" entire="${mapping.parameter }">${fn:substring(mapping.parameter, 0, 50)}......</a></c:when>
						<c:otherwise>${mapping.parameter }</c:otherwise>
					</c:choose>
				</td>
				<td style="border-right:1px solid #eee;">${mapping.status}</td>
				<td style="border-right:1px solid #eee;">
					<c:choose>
						<c:when test="${fn:length(mapping.message) > 50}"><a class="more" entire="${mapping.message }">${fn:substring(mapping.message, 0, 50)}......</a></c:when>
						<c:otherwise>${mapping.message }</c:otherwise>
					</c:choose>
				</td>
				<td style="border-right:1px solid #eee;">${mapping.operator}</td>
				<td style="border-right:1px solid #eee;"><fmt:formatDate value="${mapping.operatorTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>