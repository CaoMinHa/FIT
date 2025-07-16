<%@page import="foxconn.fit.entity.base.EnumScenarios"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%
	String legalCode=SecurityUtils.getLegalCode();
	request.setAttribute("legalCode", legalCode);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<style type="text/css">
		.search-btn{
			height:40px;
			margin-left:10px;
			color:#ffffff;
			background-image: linear-gradient(to bottom, #fbb450, #f89406);
			background-color: #f89406 !important;
		}
		.table thead th{vertical-align: middle;}
		.small_th{padding:0;font-size:10px;}
	</style>
	<script type="text/javascript">
		$(function() {
		
						
			$("#QueryBtn").click(function(){
				clickPage(1);
			});

			$(".AllCheck input").change(function(){
				var checked=$(this).is(":checked");
				$(this).parent().siblings().find("input").prop("checked",checked);
				if(!checked){
					$(this).parent().parent().parent().siblings().find("span").show();
				}else{
					$(this).parent().parent().parent().siblings().find("span").hide();
				}
			});

			$(".Check input").change(function(){
				var length=$(this).parent().siblings(".Check").find("input:checked").length+$(this).is(":checked");
				var total=$(this).parent().siblings(".Check").length+1;
				$(this).parent().siblings(".AllCheck").find("input").prop("checked",length==total);
				if(length>0){
					$(this).parent().parent().parent().siblings().find("span").hide();
				}else{
					$(this).parent().parent().parent().siblings().find("span").show();
				}
			});
		});
		 $("#Content").load("${ctx}/bi/plInfo/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
             orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),entitys:""},function(){$("#loading").fadeOut(1000);});
		<!--$("#Content").load("${ctx}/bi/plInfo/list",{entitys:""},function(){$("#QueryBtn").click();});-->
		


	</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span><spring:message code='PLInfo'/></span>
			</h2>
		</div>
		
		<div class="m-l-md m-t-md m-r-md" style="clear:both;">
			<div class="controls">
				<select id="QEntity" class="input-large" style="width:150px;">
                    <option value="" selected>請選擇LegalCode</option>
					<c:forEach items="${entityList}" var="entitys">
						 <option value="${entitys}">${entitys}</option>
					</c:forEach>
				 </select>				 
				
				<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="width: 100px;" type="submit"><spring:message code='query'/></button>
			</div>
		</div>
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
