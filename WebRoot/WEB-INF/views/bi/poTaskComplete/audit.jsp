<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<script type="text/javascript" src="${ctx}/static/js/common/utility.js"></script>
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

		$("#taskDetails tr:last td").css("text-align","right");
		$("#taskDetails tbody tr").each(function(i){
			if(i%2!=0){
				$(this).css("background-color","#eceaea");
			}
			$(this).children('td').each(function(e){
				if(e>3){
					var txt = $(this).text();
					if(txt!=null && txt.length>0) {
						if(txt.trim().substring(0,1)=="."){
							txt="0"+txt.trim();
						}
						if (/^\-?[0-9]+(.[0-9]+)?$/.test(txt)){
							$(this).css("text-align", "right");
							if ($("#taskType").val() == "FIT_PO_SBU_YEAR_CD_SUM" && e == 14) {
								$(this).text(RetainedDecimalPlaces(Number(txt),6));
							} else {
								$(this).text(RetainedDecimalPlaces(Number(txt),2));
							}
						}
					}
				}
			});
		})
		$("#taskDetails tr:last").css("background-color","#e4f2fd");
	});


	//用于触发当前点击事件
	function clickPage(page){
		$("#loading").show();
		$("#PageNo").val(page);
		$("#Content").load("${ctx}/bi/poTaskComplete/audit",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
			orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		statusType:$("#statusType").val(),role:$("#role").val(),id:$("#tId").val()},function(){$("#loading").fadeOut(1000);});
	}

	function refresh(){
		clickPage("1");
	}
	function fileClick(e,val) {
		//最后加载判断是否显示上传文档
		var index = layer.confirm('下載文件', {
			btn: ['下載','关闭'], //按钮
			shade: false //不显示遮罩
		}, function(){
			//下載
			<%--window.location.href = "${ctx}/static/taskFile/"+val;--%>
			var tempwindow=window.open('_blank');
			tempwindow.location= "${ctx}/static/taskFile/"+val;
			layer.close(index);
		}, function(){
			//关闭提示框
			layer.close(index);
		});
	}

</script>
</head>
<body>
<div <c:choose>
	<c:when test="${tableName eq 'FIT_ACTUAL_PO_NPRICECD_DTL'}">style="width:250%;"</c:when>
	<c:when test="${tableName eq 'FIT_PO_CD_MONTH_DOWN'}">style="width:480%;"</c:when>
	<c:when test="${fn:length(columns) gt 15}">style="width:200%;"</c:when></c:choose>>
	<form id="taskFileForm" method="POST" enctype="multipart/form-data">
		<table id="tableFile">
			<tr>
				<c:forEach items="${fileList}" var="file">
					<td>
						<a href="###" id="${file.FILEID}" onclick="fileClick(this,'${file.FILEID}/${file.FILENAME}')">${file.FILENAME}&nbsp;&nbsp;&nbsp;</a>
					</td>
				</c:forEach>
			</tr>
		</table>
	</form>
                <table id="taskDetails" class="table table-condensed table-hover">
                    <thead>
                    <tr>
						<c:forEach items="${columns }" var="column" varStatus="status">
							<c:choose>
								<c:when test="${tableName=='FIT_PO_CD_MONTH_DOWN'}">
									<c:choose>
										<c:when test="${column.comments eq 'CD比率%（年度目标）'}">
											<th>CD比率%</br>（年度目标）</th>
										</c:when>
										<c:when test="${column.comments eq 'CPO核准比例%（年度目标）'}">
											<th>CPO核准比例%</br>（年度目标）</th>
										</c:when>
										<c:when test="${column.comments eq 'CD比率%（年度预估）'}">
											<th>CD比率%</br>（年度预估）</th>
										</c:when>
										<c:otherwise>
											<th >${column.comments }</th>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<th >${column.comments }</th>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:if test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'}">
							<th>CD合計(NTD)</th>
						</c:if>
                    </tr>
                    </thead>
                    <tbody>
					<c:forEach items="${page.result}" var="mapping" varStatus="sort">
						<tr>
							<c:choose>
								<c:when test="${sort.index eq fn:length(page.result)-1}">
									<c:forEach var="i" begin="0" end="${fn:length(mapping)-1}" varStatus="status">
										<c:choose>
											<c:when test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==22|| tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==14}">
												<td style="border-right:1px solid #eee;background-color: beige">${mapping[i]}</td>
											</c:when>
											<c:when test="${tableName =='FIT_PO_CD_MONTH_DOWN' && status.index >fn:length(mapping)-4}">
												<td style="border-right:1px solid #eee;background-color: #f5f5dc">${mapping[i]}</td>
											</c:when>
											<c:when test="${tableName =='FIT_PO_SBU_YEAR_CD_SUM' && status.index ==fn:length(mapping)-13}">
												<td style="border-right:1px solid #eee;background-color: #f5f5dc">${mapping[i]}</td>
											</c:when>
											<c:otherwise>
												<td style="border-right:1px solid #eee;">${mapping[i]}</td>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<c:forEach var="i" begin="0" end="${fn:length(mapping)-index}" varStatus="status">
										<c:choose>
											<c:when test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==22||tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==14}">
												<td style="border-right:1px solid #eee;background-color: beige">${mapping[i]}</td>
											</c:when>
											<c:when test="${tableName =='FIT_PO_CD_MONTH_DOWN' && status.index >fn:length(mapping)-4}">
												<td style="border-right:1px solid #eee;background-color: #f5f5dc">${mapping[i]}</td>
											</c:when>
                                            <c:when test="${tableName =='FIT_PO_SBU_YEAR_CD_SUM' && fn:escapeXml(page.pageNo) eq 1 && status.index ==fn:length(mapping)-13}">
                                                <td style="border-right:1px solid #eee;background-color: #f5f5dc">${mapping[i]}</td>
                                            </c:when>
                                            <c:when test="${tableName =='FIT_PO_SBU_YEAR_CD_SUM' && fn:escapeXml(page.pageNo) gt 1 && status.index ==fn:length(mapping)-14}">
                                                <td style="border-right:1px solid #eee;background-color: #f5f5dc">${mapping[i]}</td>
                                            </c:when>
											<c:otherwise>
												<td style="border-right:1px solid #eee;">${mapping[i]}</td>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</c:otherwise>
							</c:choose>
							<c:if test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'}">
								<td style="border-right:1px solid #eee;background-color: beige">${mapping[14]+mapping[22]}</td>
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
<c:if test="${fn:length(taskLogList) gt 0}">
	<h3>
		<c:if test="${languageS eq 'zh_CN'}">審批日志</c:if>
		<c:if test="${languageS eq 'en_US'}">Approval log</c:if>
	</h3><br>
<table style="margin-top: -25px" class="table table-condensed table-hover">
	<thead>
		<tr>
			<th style="width: 10%">操作人</th>
			<th style="width: 15%">操作時間</th>
			<th style="width: 10%">狀態</th>
			<th style="width: 70%">審批意見</th>
		</tr>
	</thead>
	<tbody>
<c:forEach items="${taskLogList}" var="taskLog">
	<tr>
		<td>${taskLog.CREATE_USER}</td>
		<td>${taskLog.CREATE_TIME}</td>
		<c:choose>
			<c:when test="${taskLog.FLAG eq '1'}">
				<td style="border-right:1px solid #eee;">
					<spring:message code='submit'/></td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '2'}">
				<td  style="border-right:1px solid #eee;">
					<spring:message code='praeiudicium'/>
				</td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '3'}">
				<td  style="border-right:1px solid #eee;"><spring:message code='finish'/></td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '-1'}">
				<td  style="border-right:1px solid #eee;">
					<c:if test="${languageS eq 'zh_CN'}">駁回</c:if>
					<c:if test="${languageS eq 'en_US'}">Turn Down</c:if>
				</td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '-2'}">
				<td  style="border-right:1px solid #eee;">
					<c:if test="${languageS eq 'zh_CN'}">用戶取消</c:if>
					<c:if test="${languageS eq 'en_US'}">User cancelled</c:if>
				</td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '-3'}">
				<td  style="border-right:1px solid #eee;">
					<c:if test="${languageS eq 'zh_CN'}">管理員取消</c:if>
					<c:if test="${languageS eq 'en_US'}">Administrator cancel</c:if>
				</td>
			</c:when>
		</c:choose>
		<td>${taskLog.REMARK}</td>
	</tr>
</c:forEach>
	</tbody>
</table>
</c:if>
</body>
</html>