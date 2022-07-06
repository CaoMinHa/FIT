<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
</style>
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

	$("#Fenye input:first").bind("blur",function(){
		Page.jumpPage($(this).val());
		clickPage(Page.getPage());
	});
	$("#Fenye input:first").bind("keypress",function(){
		if(event.keyCode == "13"){
			Page.jumpPage($(this).val());
			clickPage(Page.getPage());
		}
	});
	$(".auditBtn3").hide();
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var date=$("#QDate").val();
	var type=$("#type").val();
	var tableName=$("#QTableName").val();
	var QDate=$("#QDate").val();
	var QDateEnd=$("#QDateEnd").val();
	$("#Content").load("${ctx}/bi/poTaskList/list",{
		pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		date:date,tableName:tableName,type:type,QDate:QDate,QDateEnd:QDateEnd
	},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}

//跳轉到審核頁面
function toUser(index){
	var id = $('input[type=checkbox]')[index].value;
	var statusType = document.getElementsByName("statusType")[index].value;
	$("#task").hide();
	$("#audit").show();
	$("#loading").show();
	$("#taskStatus").hide();
	$("#type").hide();
	$("#name").hide();
	$("#Content").load("${ctx}/bi/poTaskList/audit",{pageNo:"1",pageSize:"15",id:id,statusType:statusType},function(){$("#loading").fadeOut(1000);});
}

</script>
</head>
<body>
<div style="width:100%;">
	<div style="display: none">
	<input  id="sbu" type="text" value="${sbu}">
	<input  id="email" type="text" value="${email}">
	</div>
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center;width: 50px;display: none;">序号</th>
				<th style="text-align:center;display: none" >任務類型</th>
				<th style="text-align:center" >任務名稱</th>
				<th style="text-align:center" >狀態</th>
				<th style="text-align:center" >審批意見</th>
				<th style="text-align:center" >創建人</th>
				<th style="text-align:center" >創建時間</th>
				<th style="text-align:center" >更新人</th>
				<th style="text-align:center" >更新時間</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping" varStatus="sort">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;border-right:1px solid #eee;display: none;" >
									<input name="ID" type="checkbox"  value="${mapping[i]}"/>
								</td>
							</c:when>
							<c:when test="${status.index eq 1}">
								<input style="display: none" name="tasType" type="text"  value="${mapping[i]}"/>
								<td style="border-right:1px solid #eee;display: none;">${mapping[i]}</td>
							</c:when>
							<c:when test="${status.index eq 2}">
								<input style="display: none" name="tasName" type="text"  value="${mapping[i]}"/>
								<td style="border-right:1px solid #eee;">
									<a href="javascript:void(0);" class="update" onclick="toUser(${sort.index})">${mapping[i]}</a>
								</td>
							</c:when>
							<c:when test="${status.index eq 3}">
								<input style="display: none" name="statusType" type="hidden"  value="${mapping[i]}"/>
								<c:choose>
									<c:when test="${mapping[i] eq '0'}">
										<td  style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">未提交</c:if>
											<c:if test="${languageS eq 'en_US'}">Unsubmitted</c:if></td>
									</c:when>
									<c:when test="${mapping[i] eq '1'}">
										<td style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">初審</c:if>
											<c:if test="${languageS eq 'en_US'}">Praeiudicium</c:if></td>
										</td>
									</c:when>
									<c:when test="${mapping[i] eq '2'}">
										<td  style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">终審中</c:if>
											<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if>
										</td>
									</c:when>
									<c:when test="${mapping[i] eq '3'}">
										<td  style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">完成</c:if>
											<c:if test="${languageS eq 'en_US'}">Finish</c:if>
										</td>
									</c:when>
									<c:when test="${mapping[i] eq '-1'}">
										<td  style="border-right:1px solid #eee;">
											<c:if test="${languageS eq 'zh_CN'}">駁回</c:if>
											<c:if test="${languageS eq 'en_US'}">Turn Down</c:if>
										</td>
									</c:when>
								</c:choose>
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
	<input id="role" style="display: none" placeholder="<c:if test="${languageS eq 'zh_CN'}">請輸入查詢名稱</c:if><c:if test="${languageS eq 'en_US'}">Please enter a query name</c:if>" type="text" value="${role}">
</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>