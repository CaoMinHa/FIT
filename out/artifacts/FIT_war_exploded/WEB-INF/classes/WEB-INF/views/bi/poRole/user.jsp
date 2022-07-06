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
	$("#Content").load("${ctx}/bi/poRole/userList",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		name:$("#name2").val(),hasRole:$("#hasRole").val(),id:$("#roleId2").val(),
	    roleName:$("#roleName2").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}

function addUserRole(index) {
	var userId = $('input[type=checkbox]')[index].value;
	var roleId=$('#roleId2').val();
	var obj={
		userId:userId,
		roleId:roleId
	}
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poRole/addUserRole",
		async:false,
		dataType:"json",
		data:obj,
		success: function(data){
			if(data.flag=="success"){
				$("#Content").load("${ctx}/bi/poRole/userList",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
					orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
					name:$("#name2").val(),hasRole:$("#hasRole").val(),id:roleId,
					roleName:$("#roleName2").val()},function(){$("#loading").fadeOut(1000);});
			}else{
				layer.alert(data.msg);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
}
function removeUserRole(index) {
	var userId = $('input[type=checkbox]')[index].value;
	var roleId=$('#roleId2').val();
	var obj={
		userId:userId,
		roleId:roleId
	}
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poRole/removeUserRole",
		async:false,
		dataType:"json",
		data:obj,
		success: function(data){
			if(data.flag=="success"){
				$("#Content").load("${ctx}/bi/poRole/userList",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
					orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
					name:$("#name2").val(),hasRole:$("#hasRole").val(),id:roleId,
					roleName:$("#roleName2").val()},function(){$("#loading").fadeOut(1000);});
			}else{
				layer.alert(data.msg);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
}





</script>
</head>
<body>
<h4 style="float:right;">為${roleName}分配角色</h4>
<div style="width:95%;">
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center;width: 50px" >序号</th>
				<th style="text-align:center" >用戶賬號</th>
				<th style="text-align:center" >創建人</th>
				<th style="text-align:center" >創建時間</th>
				<th style="text-align:center" style="white-space: nowrap; border-right:1px solid #eee;text-align: center;"><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
		<input id="roleId2" style="display: none" value="${roleId}" type="text">
		<input id="roleName2" style="display: none" value="${roleName}" type="text">
		<c:forEach items="${page.result}" var="mapping" varStatus="sort">
			<tr>
				<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq 0}">
							<td style="white-space: nowrap;border-right:1px solid #eee;">
								<input name="ID" type="checkbox"  value="${mapping[i]}"/>
							</td>
						</c:when>
						<c:otherwise>
							<td style="border-right:1px solid #eee;">${mapping[i]}</td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<td style="white-space: nowrap; border-right:1px solid #eee;">
					<c:choose>
				       <c:when test="${hasRole eq '1'}">
					    <a href="javascript:void(0);" class="role2User" onclick="removeUserRole(${sort.index})">取消分配</a>
				       </c:when>
				       <c:otherwise>
					   <a href="javascript:void(0);" class="role2User" onclick="addUserRole(${sort.index})">分配</a>
				       </c:otherwise>
					</c:choose>
				</td>
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