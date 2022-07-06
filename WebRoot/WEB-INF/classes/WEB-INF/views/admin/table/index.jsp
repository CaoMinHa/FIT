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
<script type="text/javascript">
$(function() {
	$("#Content").load("${ctx}/admin/table/list",{orderBy:"tableName",orderDir:"asc"});
	$("#AddBtn").click(function(){
		var tableName=$("#tableName").val();
		if(tableName.length>0){
			$("#loading").show();
			$.ajax({
				type:"POST",
				url:"${ctx}/admin/table/add",
				async:true,
				dataType:"json",
				data:{tableName:tableName},
				success: function(data){
					$("#loading").hide();
					if(data.flag=="success"){
						refresh();
					}
					layer.alert(data.msg);
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		$("#loading").hide();
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		}
	});
});
</script>
</head>
<body>
	<div class="content">
		<div class="container-fluid">
			<div class="row-fluid bg-white">
				<div class="span12">
					<div class="page-header bg-white">
						<h2><span>明细表管理</span></h2>
					</div>
					<div class="m-l-md m-t-md m-r-md">
                    <div class="controls text-center">
                       <input id="tableName" type="text" placeholder="请输入明细表名称" class="input-xlarge" style="width: 240px;">
                       <button id="AddBtn" class="btn search-btn btn-warning m-l-md" type="button"><spring:message code='add'/></button>
                    </div>
					<div class="p-l-md p-t-md p-r-md p-b-md" id="Content"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
