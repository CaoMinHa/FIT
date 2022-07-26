<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
	.table th, .table td{
		padding:0px !important;
		vertical-align: initial !important;
	}
</style>
<head>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-select.css">
	<script src="${ctx}/static/js/bootstrap-select.js"></script>
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
	$("#Fenye input:first").bind("keypress",function(){
		if(event.keyCode == "13"){
			Page.jumpPage($(this).val());
			clickPage(Page.getPage());
		}
	});
	$('.selectpicker').selectpicker('render');

	$("#allCheck").click(function(){
		if ($("#allCheck").prop("checked") == true) {
			$(".userID").prop("checked", true);
		} else {
			$(".userID").prop("checked", false);
		}
	});
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var queryCondition=$("#QueryCondition").serialize();
	$("#Content").load("${ctx}/bi/poUploadOverdue/list",{
		pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),query:decodeURIComponent(queryCondition)
	},function(){$("#loading").fadeOut(1000);});
}

function refresh() {
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:95%;">
	<table align="center" class="table table-hover" style="margin-left: 30px;">
		<thead>
			<tr>
				<th style="text-align:center;width: 5%;"><input id="allCheck" type="checkbox"/></th>
				<th style="text-align:center;width: 10%;">用戶賬號</th>
				<th style="text-align:center;width: 10%;" >用戶姓名</th>
                <th style="text-align:center;width: 35%;" >SBU</th>
				<th style="text-align:center;width: 35%;" >物料大类</th>
				<th style="text-align:center;width: 5%;"  >狀態</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.result}" var="mapping" varStatus="sort">
			<tr>
				<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq 0}">
							<td style="border-right:1px solid #eee;">
								<input  class="userID" type="checkbox" style="margin-left: 40%;"  value="${mapping[i]}"/>
							</td>
						</c:when>
						<c:when test="${status.index eq 3 || status.index eq 4}">
							<td style="border-right:1px solid #eee;">
								<textarea rows="1" style="width:95%;" readonly>${mapping[i]}</textarea>
							</td>
						</c:when>
						<c:otherwise>
							<td style="border-right:1px solid #eee;">${mapping[i]}</td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>

<div id="Fenye" style="height: 50px"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>