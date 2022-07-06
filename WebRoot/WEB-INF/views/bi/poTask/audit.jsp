<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<script type="text/javascript" src="${ctx}/static/js/common/utility.js"></script>
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

		$("#roleCode").hide();
		$("#QDate").hide();
		$("#type").hide();
		$("#name").hide();
		$("#Query").hide();

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
							if ($("#taskType").val() == "FIT_PO_SBU_YEAR_CD_SUM" && e == 6) {
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
		$("#Content").load("${ctx}/bi/poTask/audit",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
			orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		statusType:$("#statusType").val(),role:$("#role").val(),id:$("#tId").val()},function(){$("#loading").fadeOut(1000);});
	}

	function refresh(){
		clickPage("1");
	}


	function submitTaskXQYM(e) {
		event.preventDefault();
		$("#loading").show();
		var id = $("#tId").val();
		var taskType = $("#taskType").val();
		var obj={
			id:id,
			taskType:taskType,
			roleCode:$("#roleCode").val()
		}
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/poTask/submitTask",
			async:false,
			dataType:"json",
			data:obj,
			success: function(data){
				layer.alert(data.msg);
				if(data.flag=="success"){
				$(e).hide();
				$(".file").hide();}
				$("#loading").hide();
			},
			error: function() {
				layer.alert("<spring:message code='connect_fail'/>");
			}
		});
	}


	function submitOneAuditXQ(e) {
		var name = $("#taskName").val();
		var id = $("#tId").val();
		var taskType = $("#taskType").val();
		$("#taskName2").val(name);
		$("#modal-audit").dialog({
			modal: true,
			title: "一级審核",
			height: 400,
			width: 350,
			position: "center",
			draggable: true,
			resizable: true,
			autoOpen: false,
			autofocus: false,
			closeText: "<spring:message code='close'/>",
			buttons: [
				{
					text: "<spring:message code='submit'/>",
					click: function () {
						var $dialog = $(this);
						var d = {};
						$("#loading").show();
						var t = $("#taskForm").serializeArray();
						$.each(t, function() {
							d[this.name] = this.value;
						});
						var flag=d.flag;
						var obj={
							id:id,
							status:flag,
							remark:d.remark,
							taskType:taskType,
							roleCode:$("#roleCode").val()
						}
						$.ajax({
							type:"POST",
							url:"${ctx}/bi/poTask/submitOneAudit",
							async:false,
							dataType:"json",
							data:obj,
							success: function(data){
								$dialog.dialog("destroy");
								layer.alert(data.msg);
								if(data.flag=="success"){
									$(e).hide();
									$(".file").hide();
								}
								$("#loading").hide();
								// refresh();
							},
							error: function(XMLHttpRequest, textStatus, errorThrown) {
								layer.alert("<spring:message code='connect_fail'/>");
							}
						});
					}
				},
				{
					text: "<spring:message code='close'/>",
					click: function () {
						$(this).dialog("destroy");
						$("#rolenameTip").hide();
					}
				}
			],
			close: function () {
				$(this).dialog("destroy");
				$("#rolenameTip").hide();
			}
		}).dialog("open");
	}
	function submitAuditXQ(e) {
		var name = $("#taskName").val();
		var id = $("#tId").val();
		var taskType = $("#taskType").val();
		$("#taskName2").val(name);
		$("#modal-audit").dialog({
			modal: true,
			title: "二级審核",
			height: 400,
			width: 350,
			position: "center",
			draggable: true,
			resizable: true,
			autoOpen: false,
			autofocus: false,
			closeText: "<spring:message code='close'/>",
			buttons: [
				{
					text: "<spring:message code='submit'/>",
					click: function () {
						var $dialog = $(this);
						$("#loading").show();
						var d = {};
						var t = $("#taskForm").serializeArray();
						$.each(t, function() {
							d[this.name] = this.value;
						});
						var flag=d.flag;
						var obj={
							id:id,
							status:flag,
							remark:d.remark,
							taskType:taskType
						}
						$.ajax({
							type:"POST",
							url:"${ctx}/bi/poTask/submitAudit",
							async:false,
							dataType:"json",
							data:obj,
							success: function(data){
								$dialog.dialog("destroy");
								layer.alert(data.msg);
								if(data.flag=="success"){
									$(e).hide();
									$(".file").hide();
									$("button[name='btnKeyUser']").hide();
								}
								$("#loading").hide();
							},
							error: function(XMLHttpRequest, textStatus, errorThrown) {
								layer.alert("<spring:message code='connect_fail'/>");
							}
						});
					}
				},
				{
					text: "<spring:message code='close'/>",
					click: function () {
						$(this).dialog("destroy");
						$("#rolenameTip").hide();
					}
				}
			],
			close: function () {
				$(this).dialog("destroy");
				$("#rolenameTip").hide();
			}
		}).dialog("open");
	}


	function cancelAudit(e) {
		var taskType = $("#taskType").val();
		var id = $("#tId").val();
		var obj={
			id:id,
			taskType:taskType
		}
		$.ajax({
			type:"POST",
			url:"${ctx}/bi/poTask/cancelAudit",
			async:false,
			dataType:"json",
			data:obj,
			success: function(data){
				// refresh();
				if(data.flag=="success") {
					debugger;
					$(e).hide();
					$(".file").hide();
					$("#btnKeyUser").hide();
				}
				$("#loading").hide();
				layer.alert(data.msg);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				layer.alert("<spring:message code='connect_fail'/>");
			}
		});
	}

	$(function () {
		$("#taskFileForm").fileupload({
			dataType: "json",
			url: "${ctx}/bi/poTask/upload",
			add: function (e, data) {
				$("#FileUpload").unbind();
				var filename = data.originalFiles[0]['name'];
				var acceptFileTypes = /(\.|\/)(xls|xlsx|XLS|XLSX|pdf)$/i;
				if (filename.length && !acceptFileTypes.test(filename)) {
					$(".tip").text("<spring:message code='click_select_excel'/>");
					layer.alert("<spring:message code='only_support_excel'/>");
					return;
				}
				if (data.originalFiles[0]['size'] > 1024 * 1024 * 30) {
					$(".tip").text("<spring:message code='click_select_excel'/>");
					layer.alert("<spring:message code='not_exceed_30M'/>");
					return;
				}
				$(".tip").text(filename);
				$(".upload-tip").attr("title", filename);
				$("#FileUpload").click(function () {
					$("#loading").show();
					data.submit();
				});
			},
			done: function (e, data) {
				$("#loading").delay(1000).hide();
				layer.alert(data.result.msg);
				$("#tableFile tr:eq(0)").append("<td>" +
						"<a  href='###' id='"+data.result.fileId+"' onclick=\"fileClick(this,'"+data.result.fileId+"/"+data.result.fileName+"')\">"+data.result.fileName+"&nbsp;&nbsp;&nbsp;</a>"+
						"</td>");
			},
			fail: function () {
				$("#loading").delay(1000).hide();
				layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
			},
			processfail: function (e, data) {
				$("#loading").delay(1000).hide();
				layer.alert("<spring:message code='upload'/><spring:message code='fail'/>");
			}
		});
		$(".upload-tip").click(function () {
			$(".input-file").trigger("click");
		});

		//最后加载判断是否显示上传文档
		if($("#tableButton tr:first td:eq(2) button[style='display: none;']").length<4){
			$(".file").show();
		}else{
			$(".file").hide();
		}
	})
	function fileClick(e,val) {
		//最后加载判断是否显示上传文档
		if($("#tableButton tr:first td:eq(2) button[style='display: none;']").length<4){
			var index = layer.confirm('刪除還是下載？', {
				btn: ['下載','刪除','关闭'], //按钮
				shade: false //不显示遮罩
			}, function(){
				//下載
				var tempwindow=window.open('_blank');
				tempwindow.location= "${ctx}/static/taskFile/"+val;
				<%--window.location.href = "${ctx}/static/taskFile/"+val;--%>
				layer.close(index);
			}, function(){
				layer.close(index);
				$("#loading").show();
				//刪除
				$.ajax({
					type:"POST",
					url: "${ctx}/bi/poTask/deleteUrl",
					async:true,
					dataType:"json",
					data:{fileId:$(e).attr("id")},
					success: function(data){
						$(e).parent('td').remove();
						$("#loading").hide();
						layer.alert(data.msg);
					},
					error: function(data) {
						$("#loading").hide();
						layer.alert(data.msg);
					}
				});
			}, function(){
				//关闭提示框
				layer.close(index);
			});
		}else{
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
	}
</script>
</head>
<body>
<div <c:choose>
	<c:when test="${tableName eq 'FIT_ACTUAL_PO_NPRICECD_DTL'}">style="width:310%;"</c:when>
	<c:when test="${tableName eq 'FIT_PO_CD_MONTH_DOWN'}">style="width:480%;"</c:when>
	<c:when test="${fn:length(columns) gt 15}">style="width:200%;"</c:when></c:choose>>
	<form id="taskFileForm" method="POST" enctype="multipart/form-data" class="form-horizontal">
		<input type="file" style="display:none;" class="input-file" multiple="false"/>
		<input style="display: none" id="taskId" name="taskId" value="${taskId}">
		<input style="display: none" id="role" name="role" value="${role}">
		<input style="display: none" id="statusType" name="statusType" value="${statusType}">
		<h3 style="margin-top: -35px;margin-bottom: 20px;">${taskName}</h3>
		<table id="tableButton" style="margin-top: -25px">
			<tr>
				<td class="file">
					<div title="<spring:message code='not_exceed_30M'/>，上傳文件格式（.xls/.xlsx/.pdf）">
						<div class="upload-tip" >
							<span class="tip"><c:if test="${languageS eq 'zh_CN'}">上傳附檔</c:if>
                                <c:if test="${languageS eq 'en_US'}">Upload the attached</c:if></span>
						</div>
					</div>
				</td>
				<td class="file">
					<button id="FileUpload"  class="btn search-btn btn-warning" type="button">
						<spring:message code='upload'/></button>
					<input style="display: none" id="tId" value="${taskId}">
					<input style="display: none" id="taskType" value="${taskType}">
					<input style="display: none" id="taskName" value="${taskName}">
				</td>
				<td>
					<button class="btn search-btn btn-warning"
							<c:if test="${user != 'N' || statusType != '0'}">style="display: none;"</c:if>
							onclick="submitTaskXQYM(this)"><spring:message code="submit"/> </button>
					<button  class="btn search-btn btn-warning"
					<c:if test="${user != 'C' || statusType != '1'}">style="display: none;"</c:if>
							 type="button"
							 onclick="submitOneAuditXQ(this)">
						<c:if test="${languageS eq 'zh_CN'}">初審</c:if>
						<c:if test="${languageS eq 'en_US'}">Praeiudicium</c:if>
					</button>
					<button  class="btn search-btn btn-warning"
					<c:if test="${user != 'Z' || statusType != '2'}">style="display: none;"</c:if>
							 type="button"
							 onclick="submitAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">終審</c:if>
						<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></button>
					<button  id="btnKeyUser" class="btn search-btn btn-warning"
					<c:if test="${user != 'TS' || statusType != '2'}">style="display: none;"</c:if>
							 type="button"
							 onclick="submitAuditXQ(this)"><c:if test="${languageS eq 'zh_CN'}">終審</c:if>
						<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></button>
				</td>
				<td style="margin-top: 10px">
<%--					<c:if test="${statusType == '1' || statusType == '2'}">--%>
<%--						<button name="btnKeyUser"  class="btn search-btn btn-warning" style="margin-left: -5px;--%>
<%--							"type="button" onclick="cancelAudit(this)">取消審批</button>--%>
<%--					</c:if>--%>
<%--					<c:if test="${statusType == '3' && taskType != 'FIT_PO_SBU_YEAR_CD_SUM'}">--%>
<%--						<button  class="btn search-btn btn-warning" style="margin-left: -5px;--%>
<%--							"type="button"--%>
<%--								 onclick="cancelAudit(this)">取消審批</button>--%>
<%--					</c:if>--%>

					<button name="btnKeyUser" class="btn search-btn btn-warning" style="margin-left: -5px;
					<c:if test="${user != 'K' }">display: none;</c:if>"
							 type="button"
							 onclick="cancelAudit(this)">取消審批</button>
					<button name="btnKeyUser"  class="btn search-btn btn-warning" style="margin-left: -5px;
					<c:if test="${user != 'TS' }">display: none;</c:if>"
							 type="button"
							 onclick="cancelAudit(this)">取消審批</button>


<%--					<button  class="btn search-btn btn-warning" style="margin-left: -5px;--%>
<%--						<c:if test="${user != 'TS' }">display: none;</c:if>"--%>
<%--							type="button"--%>
<%--							onclick="cancelAudit(this)">取消審批</button>--%>
				</td>
			</tr>
        </table>
		</p>
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
								<c:when test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'}">
									<c:choose>
										<c:when test="${column.comments eq '採購單位與需求單位提供最低單價之價差（NTD）'}">
											<th>採購單位與需求單位</br>提供最低單價之價差（NTD）</th>
										</c:when>
										<c:when test="${column.comments eq '投資(模具/設計/免費借用…)抵CD（NTD）'}">
											<th>投資(模具/設計/免費借用…)</br>抵CD（NTD）</th>
										</c:when>
										<c:when test="${column.comments eq '一次交易議價(成交價低于最低報價)（NTD）'}">
											<th>一次交易議價</br>(成交價低于最低報價)（NTD）</th>
										</c:when>
										<c:when test="${column.comments eq '回收(廢液/下腳料/包材...)（NTD）'}">
											<th>回收(廢液/下腳料/包材...)</br>（NTD）</th>
										</c:when>
										<c:otherwise>
											<th >${column.comments }</th>
										</c:otherwise>
									</c:choose>
								</c:when>
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
											<c:when test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==20|| tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==26|| tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==28}">
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
											<c:when test="${tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==20|| tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==26|| tableName=='FIT_ACTUAL_PO_NPRICECD_DTL'&&status.index==28}">
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
								<td style="border-right:1px solid #eee;background-color: beige">${mapping[21]+mapping[27]}</td>
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
					<c:if test="${languageS eq 'zh_CN'}">提交</c:if>
					<c:if test="${languageS eq 'en_US'}">Submit</c:if></td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '2'}">
				<td  style="border-right:1px solid #eee;">
					<c:if test="${languageS eq 'zh_CN'}">初審</c:if>
					<c:if test="${languageS eq 'en_US'}">Praeiudicium</c:if>
				</td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '3'}">
				<td  style="border-right:1px solid #eee;"><c:if test="${languageS eq 'zh_CN'}">终審</c:if>
					<c:if test="${languageS eq 'en_US'}">Final Judgment</c:if></td>
			</c:when>
			<c:when test="${taskLog.FLAG eq '-1'}">
				<td  style="border-right:1px solid #eee;">
					<c:if test="${languageS eq 'zh_CN'}">駁回</c:if>
					<c:if test="${languageS eq 'en_US'}">Turn Down</c:if>
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