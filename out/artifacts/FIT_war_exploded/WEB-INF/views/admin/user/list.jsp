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
	
	$(".table-condensed a[enable]").click(function(){
		var id=$(this).parent().attr("userId");
		var enable=$(this).attr("enable");
		$.ajax({
			type:"POST",
			url:"${ctx}/admin/user/enable",
			async:true,
			dataType:"json",
			data:{id:id,enable:enable},
			success: function(data){
				if(data.flag=="success"){
					refresh();
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   		layer.alert("<spring:message code='connect_fail'/>");
		   	}
		});
	});
	
	$(".table-condensed a.edit").click(function(){
		var id=$(this).parent().attr("userId");
		
		$("#modal-user-edit").load("${ctx}/admin/user/detail",{id:id});
		
		$("#modal-user-edit").dialog({
			modal:true,
			title: "<spring:message code='user_detail'/>",
			height:600,
			width:700,
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
		            	userEditFormSubmit();
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
			var id=$this.parent().attr("userId");
			$.ajax({
				type:"POST",
				url:"${ctx}/admin/user/delete",
				async:true,
				dataType:"json",
				data:{id:id},
				success: function(data){
					if(data.flag=="success"){
						refresh();
					}
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		});
	});
	
	$(".table-condensed a.resetPwd").click(function(){
		var $this=$(this);
		var id=$this.parent().attr("userId");
		layer.confirm("确认重置密码?",{btn: ['确定', '取消'], title: "提示"},function(index){
			layer.close(index);
			$.ajax({
				type:"POST",
				url:"${ctx}/admin/user/resetPwd",
				async:true,
				dataType:"json",
				data:{id:id},
				success: function(data){
					if(data.flag=="success"){
						$this.css("background-color","#cdf426");
					}
					layer.alert(data.msg);
			   	},
			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			   		layer.alert("<spring:message code='connect_fail'/>");
			   	}
			});
		});
	});
	
});

//用于触发当前点击事件
function clickPage(page){
	$("#PageNo").val(page);
	var username=$("#QUsername").val();
	var corporationCode=$("#QCorporationCode").val();
	var type=$("#QType").val();
	$("#Content").load("${ctx}/admin/user/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
															 username:username,
															 corporationCode:corporationCode,
															 type:type});
}

function refresh(){
	clickPage("1");
}
</script>
</head>
<body>
	<div id="modal-user-edit" style="display:none;"></div>
	<table class="table table-condensed table-hover">
		<thead>
			<tr>
				<th><spring:message code='username'/></th>
				<th style="min-width:75px;"><spring:message code='user_type'/></th>
				<th style="text-align: center;"><span style="display:inline-table;width:20px;line-height:12px;background-color: #a0a59e;">&nbsp;</span><span style="color:#a0a59e;margin-right:10px;">--<spring:message code='readonly'/></span><span style="display:inline-table;width:20px;line-height:12px;background-color: #59ed11;">&nbsp;</span><span style="color:#59ed11;">--<spring:message code='updatable'/></span></th>
				<th style="min-width:140px;"><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="user" varStatus="status">
				<tr>
					<td style="border-right:1px solid #eee;">${user.username}</td>
					<td style="border-right:1px solid #eee;">
						${user.type.name}
						<c:if test="${user.type eq 'HFM' and not empty user.corporationCode}">(Tiptop)</c:if>
						<c:if test="${user.type eq 'HFM' and user.attribute eq 'single'}">(单体)</c:if>
						<c:if test="${user.type eq 'HFM' and user.attribute eq 'group'}">(集团)</c:if>
						<c:if test="${user.type eq 'HFM' and not empty user.ebs}">(EBS)</c:if>
					</td>
					<td style="border-right:1px solid #eee;">
						<c:choose>
							<c:when test="${user.type eq 'Budget'}">
								<span style="font-weight:bold;">SBU--></span>
								<c:forEach items="${fn:split(user.corporationCode,',') }" var="code" varStatus="status">
									<span style="font-size:16px;">${code}</span><c:if test="${not status.last}">、</c:if>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:if test="${not empty user.corporationCode}">
									<span style="font-weight:bold;">Tiptop法人编码--></span>
									<c:forEach items="${fn:split(user.corporationCode,',') }" var="code" varStatus="status">
										<c:choose>
											<c:when test="${fn:startsWith(code,'T_') }">
												<span style="color:#a0a59e;font-size:16px;">${fn:substring(code,2,-1) }</span><c:if test="${not status.last}">、</c:if>
											</c:when>
											<c:otherwise>
												<span style="color:#59ed11;font-size:16px;">${fn:substring(code,2,-1) }</span><c:if test="${not status.last}">、</c:if>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									</br>
								</c:if>
								<c:if test="${not empty user.entity}">
									<span style="font-weight:bold;">HFM公司编码--></span>
									<c:forEach items="${fn:split(user.entity,',') }" var="code" varStatus="status">
										<c:choose>
											<c:when test="${fn:startsWith(code,'T_') }">
												<span style="color:#a0a59e;font-size:16px;">${fn:substring(code,2,-1) }</span><c:if test="${not status.last}">、</c:if>
											</c:when>
											<c:otherwise>
												<span style="color:#59ed11;font-size:16px;">${fn:substring(code,2,-1) }</span><c:if test="${not status.last}">、</c:if>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									</br>
								</c:if>
								<c:if test="${not empty user.ebs}">
									<span style="font-weight:bold;">EBS公司编码--></span>
									<c:forEach items="${fn:split(user.ebs,',') }" var="code" varStatus="status">
										<c:choose>
											<c:when test="${fn:startsWith(code,'T_') }">
												<span style="color:#a0a59e;font-size:16px;">${fn:substring(code,2,-1) }</span><c:if test="${not status.last}">、</c:if>
											</c:when>
											<c:otherwise>
												<span style="color:#59ed11;font-size:16px;">${fn:substring(code,2,-1) }</span><c:if test="${not status.last}">、</c:if>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</c:if>
							</c:otherwise>
						</c:choose>
					</td>
					<td userId="${user.id }">
						<c:if test="${user.enable eq true}">
							<a class="m-r-md" href="javascript:void(0);" enable="false"><spring:message code='disable'/></a><a class="edit" href="javascript:void(0);"><spring:message code='edit'/></a>
							<br>
							<a href="javascript:void(0);" class="m-r-md delete"><spring:message code='delete'/></a><a href="javascript:void(0);" class="resetPwd"><spring:message code='reset_pwd'/></a>
						</c:if>
						<c:if test="${user.enable eq false}"><a href="javascript:void(0);" enable="true"><spring:message code='enable'/></a></c:if>
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