<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
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
	margin:0 20%;
	color:#ffffff;
	background-image: linear-gradient(to bottom, #fbb450, #f89406);
	background-color: #f89406 !important;
}
.datetimepicker{width:220px;}
</style>
<script type="text/javascript">
$(function() {
	$("#Content").load("${ctx}/admin/userLog/list",{orderBy:"operatorTime",orderDir:"desc"});
	$("#QueryBtn").click(function(){
		clickPage(1);
	});
	
	$("#QStartTime").datetimepicker({
	    format: "yyyy-mm-dd hh:ii:ss",
	    todayHighlight:true,
	    language:"zh-CN",
	    autoclose:true,
	    clearBtn:true,
	}).on("changeDate",function(e){
		$("#QEndTime").datetimepicker("setStartDate",$("#QStartTime").val());
	});
	
	$("#QEndTime").datetimepicker({
	    format: "yyyy-mm-dd hh:ii:ss",
	    startDate:new Date(),
	    todayHighlight:true,
	    language:"zh-CN",
	    todayBtn: true,
	    clearBtn:true,
	    autoclose:true
	}).on("changeDate",function(e){
		$("#QStartTime").datetimepicker("setEndDate",$("#QEndTime").val());
	});
});
</script>
</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="span12">
		<div class="page-header bg-white">
			<h2>
				<span>用户操作日志</span>
			</h2>
		</div>
        <div class="m-l-md m-t-md m-r-md" style="clear:both;">
	        <div class="controls text-center">
	           	<input id="QMethod" list="methodList" placeholder="方法" type="text" class="input-xlarge"/>
	           	<datalist id="methodList">
	           		<c:forEach items="${methodList }" var="method">
	           			<option value="${method }"/>
	           		</c:forEach>
	           	</datalist>
	           	<input id="QUsername" list="userList" placeholder="操作人" type="text" style="width:150px;" class="input-xlarge"/>
	           	<datalist id="userList">
	           		<c:forEach items="${userList }" var="username">
	           			<option value="${username }"/>
	           		</c:forEach>
	           	</datalist>
	           	<select id="QStatus" style="height:38px;width:100px;">
	           		<option value="">状态</option>
	           		<c:forEach items="${statusList }" var="status">
	           			<option value="${status }">${status }</option>
	           		</c:forEach>
	           	</select>
	           	<input id="QStartTime" class="" type="text" readonly="readonly" placeholder="开始时间"/>
	           	<input id="QEndTime" class="" type="text" readonly="readonly" placeholder="结束时间"/>
	        	<button id="QueryBtn" class="btn search-btn btn-warning m-l-md" style="margin:0 0 0 5%" type="submit"><spring:message code='query'/></button>
	        </div>
		</div>		
		<div class="p-l-md p-r-md p-b-md" id="Content"></div>
	</div>
</div>
</body>
</html>
