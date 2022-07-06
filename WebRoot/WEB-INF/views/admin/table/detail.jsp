<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<head>
<style type="text/css">
#tableEditForm .table-condensed th, .table-condensed td{padding:0px 5px !important;}
#tableEditForm .control-label{width:90px !important;}
#tableEditForm .controls{margin-left:110px !important;}
#tableEditForm .btn{padding:0 12px;}
</style>
<script type="text/javascript">
$(function() {
	$("#tableEditForm input[type=checkbox][data-toggle=toggle]").bootstrapToggle();
});

function tableEditFormSubmit(){
	var data="tableName="+$("#tableEditForm #tableName").val();
	$("#tableEditForm input[name=locked]").each(function(i){
		data+="&locked="+$(this).prop('checked');
	});
	
	$.ajax({
		type:"POST",
		url:"${ctx}/admin/table/update",
		async:true,
		dataType:"json",
		data:data,
		success: function(data){
			if(data.flag=="success"){
				$("#modal-table-edit").dialog("destroy");
				refresh();
			}
			layer.alert(data.msg);
	   	},
	   	error: function(XMLHttpRequest, textStatus, errorThrown) {
	   		layer.alert("<spring:message code='connect_fail'/>");
	   	}
	});
}
</script>
</head>
<body>
<form id="tableEditForm" class="form-horizontal">
	<div class="control-group">
		<label class="control-label">明细表名称</label>
		<div class="controls">
			<div class="pull-left">
				<input id="tableName" type="hidden" value="${auditTable.tableName }"/>
				<input type="text" value="${auditTable.tableName }" readonly="readonly"/>
			</div>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">明细表描述</label>
		<div class="controls">
			<div class="pull-left">
				<input type="text" value="${auditTable.comments }" readonly="readonly"/>
			</div>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">KEY</label>
		<div class="controls">
			<div class="pull-left">
				<table class="table table-condensed table-hover">
					<thead>
						<tr>
							<th>序号</th>
							<th>列名</th>
							<th>列类型</th>
							<th>列描述</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${auditTable.keys }" var="key">
							<tr>
								<td style="border-right:1px solid #eee;">${key.serial}</td>
								<td style="border-right:1px solid #eee;">${key.columnName}</td>
								<td style="border-right:1px solid #eee;">${key.dataType}</td>
								<td style="border-right:1px solid #eee;">${key.comments}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">列信息</label>
		<div class="controls">
			<div class="pull-left">
				<table class="table table-condensed table-hover">
					<thead>
						<tr>
							<th>序号</th>
							<th>列名</th>
							<th>列类型</th>
							<th>列描述</th>
							<th>是否可为空</th>
							<th>是否锁定</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${auditTable.columns }" var="column">
							<tr>
								<td style="border-right:1px solid #eee;">${column.serial}</td>
								<td style="border-right:1px solid #eee;">${column.columnName}</td>
								<td style="border-right:1px solid #eee;">${column.dataType}</td>
								<td style="border-right:1px solid #eee;">${column.comments}</td>
								<td style="border-right:1px solid #eee;">
									<c:choose>
										<c:when test="${column.nullable eq true}"><span style="color:#5bb75b;">是</span></c:when>
										<c:otherwise><span style="color:#f89406;">否</span></c:otherwise>
									</c:choose>
								</td>
								<td style="border-right:1px solid #eee;">
									<input name="locked" type="checkbox" <c:if test="${column.locked eq true}">checked="true"</c:if> data-toggle="toggle" data-on="是" data-off="否" data-onstyle="success" data-offstyle="warning">
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</form>
</body>
</html>