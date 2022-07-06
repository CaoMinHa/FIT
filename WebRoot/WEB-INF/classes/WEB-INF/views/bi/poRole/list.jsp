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
$(function(){
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

})
// (function() {
//
// });

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var date=$("#QDate").val();
	var entity=$("#QEntity").val();
	var tableName=$("#QTableName").val();
	$("#Content").load("${ctx}/bi/poRole/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
														date:date,tableName:tableName},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}

//更新數據
$("a.update").click(function(){
	var updateData="";
	$(this).parent().parent().find("input,select").each(function(i){
		var columnName=$(this).attr("name");
		var value=$(this).val();
		updateData+=columnName+"="+value+"&";
	});
	updateData=updateData.substring(0,updateData.length-1);
	console.log(updateData);
	$("#loading").show();
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poRole/update",
		async:true,
		dataType:"json",
		data:{updateData:updateData},
		success: function(data){
			layer.alert(data.msg);
			$("#loading").hide();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
});
//跳轉到用戶列表
function toUser(index){

	var id = $('input[type=checkbox]')[index].value;
	var roleName = document.getElementsByName("name")[index].value;
	$("#role").hide();
	$("#user").show();
	$("#Content").load("${ctx}/bi/poRole/userList",{pageNo:"1",pageSize:"10",id:id,name:"",roleName:roleName},function(){$("#loading").fadeOut(1000);});


}




</script>
</head>
<body>
<div style="width:95%;">
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center;width: 50px" >序号</th>
				<th style="text-align:center" >角色名称</th>
				<th style="text-align:center" >角色代码</th>
				<th style="text-align:center" >角色等级</th>
				<th style="text-align:center" >状态</th>
				<th style="text-align:center" >备注</th>
				<th style="text-align:center" >创建人</th>
				<th style="text-align:center" >创建时间</th>
				<th style="text-align:center" >更新人</th>
				<th style="text-align:center" >更新时间</th>
				<th style="text-align:center" style="white-space: nowrap; border-right:1px solid #eee;text-align: center;"><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="mapping" varStatus="sort">
				<tr>
					<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;border-right:1px solid #eee;">
									<input name="ID" type="checkbox"  value="${mapping[i]}"/>
								</td>
							</c:when>
							<c:when test="${status.index eq 1}">
								<td style="white-space: nowrap;border-right:1px solid #eee;">
									<input style="width: 100px" name="name" type="text"  value="${mapping[i]}"/>
								</td>
							</c:when>
<%--							<c:when test="${status.index eq 2}">--%>
<%--								<td style="white-space: nowrap;border-right:1px solid #eee;">--%>
<%--									<input style="width: 100px" name="code" type="text"  value="${mapping[i]}"/>--%>
<%--								</td>--%>
<%--							</c:when>--%>
<%--							<c:when test="${status.index eq 3}">--%>
<%--								<td style="white-space: nowrap;border-right:1px solid #eee;">--%>
<%--									<input style="width: 100px" name="grade" type="text"  value="${mapping[i]}"/>--%>
<%--								</td>--%>
<%--							</c:when>--%>
							<c:when test="${status.index eq 4}">
								<td style="white-space: nowrap;border-right:1px solid #eee;">
									<select id="flag"  name="flag" style="width:100px" datatype="*" nullmsg="<spring:message code='please_select'/>">
										<option value="1" <c:if test="${mapping[i] eq '1'}">selected="selected"</c:if>>正常</option>
										<option value="0" <c:if test="${mapping[i] eq '0'}">selected="selected"</c:if>>禁用</option>
									</select>
								</td>
							</c:when>
							<c:when test="${status.index eq 5}">
								<td style="white-space: nowrap;border-right:1px solid #eee;">
									<input style="width: 100px" name="remark" type="text"  value="${mapping[i] eq 'null'?'':mapping[i]}"/>
								</td>
							</c:when>
							<c:otherwise>
								<td style="border-right:1px solid #eee;">${mapping[i]}</td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<td style="white-space: nowrap; border-right:1px solid #eee;">
						<a href="javascript:void(0);" class="update"><spring:message code='update'/></a>
						<a href="javascript:void(0);" class="role2User" onclick="addPerm(${sort.index})">数据操作权限</a>
						<a href="javascript:void(0);" class="role2User" onclick="toUser(${sort.index})">分配用户</a>
					</td>
				</tr>
			</c:forEach>

		</tbody>

	</table>
</div>
<div>

</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>

</html>