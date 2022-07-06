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
	
	$(".table-condensed a.edit").click(function(){
		var tableName=$(this).parent().attr("tableName");
		$("#modal-table-edit").load("${ctx}/admin/table/detail",{tableName:tableName});
		$("#modal-table-edit").dialog({
			modal:true,
			title: "明细表详情",
			height:700,
			width:1100,
			position:"top",
			draggable: true,
			resizable: true,
			autoOpen:false,
			autofocus:false,
			closeText:"<spring:message code='close'/>",
			buttons: [
				  {
				    text: "<spring:message code='submit'/>",
				    click: function() {
				    	tableEditFormSubmit();
				    }
				  },
		          {
		            text: "<spring:message code='close'/>",
		            click: function() {
		              	$(this).dialog("destroy");
		            }
		          }
		        ],
			close:function(){
				$(this).dialog("destroy");
			}
		}).dialog("open");
	});
	
	$(".table-condensed a.delete").click(function(){
		var $this=$(this);
		layer.confirm("确认删除?",{btn: ['确定', '取消'], title: "提示"},function(index){
			layer.close(index);
			$("#loading").show();
			var tableName=$this.parent().attr("tableName");
			$.ajax({
				type:"POST",
				url:"${ctx}/admin/table/delete",
				async:true,
				dataType:"json",
				data:{tableName:tableName},
				success: function(data){
					$("#loading").hide();
					if(data.flag=="success"){
						refresh();
					}
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		$("#loading").hide();
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		});
	});
	
	$(".table-condensed a.refresh").click(function(){
		$("#loading").show();
		var tableName=$(this).parent().attr("tableName");
		$.ajax({
			type:"POST",
			url:"${ctx}/admin/table/refresh",
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
	});
	
});

//用于触发当前点击事件
function clickPage(page){
	$("#PageNo").val(page);
	$("#Content").load("${ctx}/admin/table/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
													orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val()});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
	<div id="modal-table-edit" style="display:none;"></div>
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th>明细表名称</th>
				<th>明细表描述</th>
				<th>锁定列</th>
				<th><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="table" varStatus="status">
				<tr>
					<td style="border-right:1px solid #eee;">${table.tableName}</td>
					<td style="border-right:1px solid #eee;">${table.comments}</td>
					<td style="border-right:1px solid #eee;">
						<c:forEach items="${table.columns }" var="column">
							<c:if test="${column.locked eq true }">
								<span style="color:#2be86a;font-size:16px;padding-right:10px;">${column.columnName}</span>
							</c:if>
						</c:forEach>
					</td>
					<td tableName="${table.tableName }">
						<a class="m-r-md edit" href="javascript:void(0);">编辑</a><a href="javascript:void(0);" class="m-r-md delete">删除</a><a href="javascript:void(0);" class="refresh">更新</a>
					</td>
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