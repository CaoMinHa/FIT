<%@page import="foxconn.fit.entity.base.EnumDimensionName"%>
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
	
	$("a.update").click(function(){
		var masterData=$("#MasterData").val();
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
			url:"${ctx}/bi/poMix/update",
			async:true,
			dataType:"json",
			data:{masterData:masterData,updateData:updateData},
			success: function(data){
				layer.alert(data.msg);
				$("#loading").hide();
				$("#Query").click();
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		$("#loading").hide();
		   		window.location.href="${ctx}/logout";
		   	}
		});
	});
	$("a.delete").click(function(){
		var masterData=$("#MasterData").val();
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
			url:"${ctx}/bi/poMix/delete",
			async:true,
			dataType:"json",
			data:{masterData:masterData,updateData:updateData},
			success: function(data){
				layer.alert(data.msg);
				$("#loading").hide();
				refresh();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				$("#loading").hide();
				window.location.href="${ctx}/logout";
			}
		});
	});
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var masterData=$("#MasterData").val();
	var queryCondition=$("#QueryCondition").serialize();
	$("#Content").load("${ctx}/bi/poMix/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														masterData:masterData,queryCondition:queryCondition},
														function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
<div style="width:60%;">
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<c:forEach items="${titleList }" var="title">
					<th style="white-space: nowrap; border-right:1px solid #eee;text-align: center;">${title[1] }</th>
				</c:forEach>
				<c:if test="${!fn:contains(masterType,'SBU')}">
				<th style="white-space: nowrap; border-right:1px solid #eee;text-align: center;"><spring:message code='operation'/></th>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}"  var="row">
				<tr>
					<c:forEach items="${row }" var="data" begin="0" end="${fn:length(row)-index}" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq 0}">
								<td style="white-space: nowrap;border-right:1px solid #eee;display:none;"><input name="ID" type="text" style="display:none;" value="${data}"/></td>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${fn:split(data,'|')[1] eq 'W'}">
										<c:choose>
											<c:when test="${fn:split(data,'|')[0] eq 'vendor_name'}">
												<td style="white-space: nowrap; border-right:1px solid #eee;">
													<input name="${fn:split(data,'|')[0]}" type="text"
<%--														   style="height:25px !important;width:350px;"--%>
														   style="height:25px !important;width:350px;line-height: 15px !important;"
														   value="${fn:split(data,'|')[2] }"/>
												</td>
											</c:when>
											<c:otherwise>
												<td style="white-space: nowrap; border-right:1px solid #eee;">
													<input name="${fn:split(data,'|')[0]}" type="text"
<%--														   style="height:25px !important;width:150px;" --%>
														   style="height:25px !important;width:150px;line-height: 15px !important;"
														   value="${fn:split(data,'|')[2] }"/>
												</td>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:when test="${fn:split(data,'|')[1] eq 'W'&& !fn:split(data,'|')[0] eq 'vendor_name'}">
										<td style="white-space: nowrap; border-right:1px solid #eee;">
											<input name="${fn:split(data,'|')[0]}" type="text"
<%--												   style="height:25px !important;width:350px;" --%>
												   style="height:25px !important;width:350px;line-height: 15px !important;"
												   value="${fn:split(data,'|')[2] }"/>
										</td>
									</c:when>

									<c:when test="${fn:split(data,'|')[1] eq 'WS'}">
										<td style="white-space: nowrap;border-right:1px solid #eee;">
											<select name="${fn:split(data,'|')[0]}" style="width:auto;">
												<option value=""></option>
												<c:forEach items="${fn:split(fn:split(data,'|')[3],',') }" var="opt">
													<option value="${fn:split(opt,'-')[0] }" <c:if test="${fn:split(opt,'-')[0] eq fn:split(data,'|')[2]}">selected="selected"</c:if>>${fn:split(opt,'-')[1] }</option>
												</c:forEach>
											</select>
										</td>
									</c:when>
									<c:otherwise>
										<td style="white-space: nowrap; border-right:1px solid #eee;  text-align: center">${fn:split(data,'|')[2] }</td>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${!fn:contains(masterType,'SBU')}">
						<td style="white-space: nowrap; border-right:1px solid #eee;">
							<a href="javascript:void(0);" class="update"><spring:message code='update'/></a>
							<a href="javascript:void(0);" class="delete">刪除</a>
						</td>
					</c:if>

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