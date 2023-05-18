<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
	.table th, .table td{
		padding:0px !important;
		vertical-align: initial !important;
	}
</style>
<head>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-select.css">
	<script src="${ctx}/static/js/bootstrap-select.js"></script>
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
	$("#Fenye input:first").bind("keypress",function(){
		if(event.keyCode == "13"){
			Page.jumpPage($(this).val());
			clickPage(Page.getPage());
		}
	});
	$('.selectpicker').selectpicker('render');

	$("#allCheck").click(function(){
		if ($("#allCheck").prop("checked") == true) {
			$(".userID").prop("checked", true);
		} else {
			$(".userID").prop("checked", false);
		}
	});
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var queryCondition=$("#QueryCondition").serialize();
	$("#Content").load("${ctx}/bi/poUploadOverdue/list",{
		pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),query:decodeURIComponent(queryCondition)
	},function(){$("#loading").fadeOut(1000);});
}

function allCheckTable(e) {
		if ($(e).prop("checked") == true) {
			$("#myModal input[type='checkbox']").prop("checked", true);
		} else {
			$("#myModal input[type='checkbox']").prop("checked", false);
		}
}

function affirmModal() {
	var valueName = '';
	var valueCode = '';
	debugger;
	$(".tableVal:checked").each(function () {
		valueCode += $(this).val() + ",";
		valueName += $(this).parent().text() + ",";
	})
	$("#tableVal").val(valueName.substring(0, valueName.length - 1));
	$("#tableName").val(valueName.substring(0, valueName.length - 1));
	$("#tableCode").attr('value',valueCode.substring(0, valueCode.length - 1));
	$("#myModal input[type='checkbox']").prop("checked", false);
	$("textarea,input").removeAttrs("id");
}

function closeModal() {
	$("#myModal input[type='checkbox']").prop("checked", false);
}

function refresh() {
	clickPage("1");
}
function modelShow(v) {
	$(v).attr('id','tableVal');
	$(v).next().attr('id','tableName');
	$(v).next().next().attr('id','tableCode');
	var a=","+$(v).next().val()+",";
	$(".tableVal").each(function () {
		if(a.indexOf($(this).val())!=-1){
			$(this).prop("checked", true);
		}
	})
	$('#myModal').modal('show');
}

$("a[name='update']").click(function(){
	var updateData="";
	$(this).parent().parent().parent().find("input").each(function(i){
		var columnName=$(this).attr("name");
		var value=$(this).val();
		updateData+=columnName+"="+value+"&";
	});
	updateData+="state="+$(this).parent().parent().parent().find("select").val();
	$("#loading").show();
	$.ajax({
		type:"POST",
		url:"${ctx}/bi/poUploadOverdue/allocate",
		async:true,
		dataType:"json",
		data:{updateData:updateData},
		success: function(data){
			layer.alert(data.msg);
			$("#loading").hide();
			toUser();
		},
		error: function() {
			$("#loading").hide();
			window.location.href="${ctx}/logout";
		}
	});
});
</script>
</head>
<body>
<div style="width:95%;">
	<table align="center" class="table table-hover" style="margin-left: 30px;">
		<thead>
			<tr>
				<th style="text-align:center;width: 5%;"><input id="allCheck" type="checkbox"/></th>
				<th style="text-align:center;width: 5%;">用戶賬號</th>
				<th style="text-align:center;width: 5%;" >用戶姓名</th>
                <th style="text-align:center;width: 20%;" >SBU</th>
				<th style="text-align:center;width: 20%;" >物料大類</th>
				<th style="text-align:center;width: 20%;" >上傳表單</th>
				<th style="text-align:center;width: 50px;" >開始期間</th>
				<th style="text-align:center;width: 5%;" >結束期間</th>
				<th style="text-align:center;"  >狀態</th>
				<th style="text-align:center;width: 5%;">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.result}" var="mapping" varStatus="sort">
			<tr>
				<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq 0}">
							<td style="border-right:1px solid #eee;">
								<input  class="userID" type="checkbox" style="margin-left: 40%;" name="USER_ID"  value="${mapping[i]}"/>
							</td>
						</c:when>
						<c:when test="${status.index eq 3 || status.index eq 4 }">
							<td style="border-right:1px solid #eee;">
								<textarea rows="1" style="width:95%;" readonly>${mapping[i]}</textarea>
							</td>
						</c:when>
						<c:when test="${status.index eq 5}">
							<td style="border-right:1px solid #eee;">
								<textarea rows="1" style="width:95%;" onfocus="modelShow(this)">${mapping[5]}</textarea>
								<input style="display: none" name="table_name" value="${mapping[5]}"/>
								<input style="display: none" name="table_code" value="${mapping[6]}"/>
							</td>
						</c:when>
						<c:when test="${status.index eq 6 }">
						</c:when>
						<c:when test="${status.index eq 7 }">
							<td style="border-right:1px solid #eee;">
								<input type="month"  name="period_start" style="width: 110px" value="${mapping[i]}"/>
							</td>
						</c:when>
						<c:when test="${status.index eq 8 }">
							<td style="border-right:1px solid #eee;">
								<input type="month" name="period_end" style="width: 110px" value="${mapping[i]}"/>
							</td>
						</c:when>
						<c:when test="${status.index eq 9 }">
							<td style="border-right:1px solid #eee;">
								<select name="state" style="width: 85px">
									<option value="N" <c:if test="${mapping[i]=='N'}">selected="selected"</c:if>>未分配</option>
									<option value="Y" <c:if test="${mapping[i]=='Y'}">selected="selected"</c:if>>已分配</option>
								</select>
							</td>
						</c:when>
						<c:otherwise>
							<td style="border-right:1px solid #eee;">${mapping[i]}</td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<td style="white-space: nowrap; border-right:1px solid #eee;text-align:center;">
					<span style="width: 1px;">
						<a href="javascript:void(0);" name="update"><spring:message code='update'/></a>
					</span>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>

<div id="Fenye" style="height: 50px"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />

<div class="modal fade" id="myModal" style="display: none;width: 250px;top: 20%;left: 60%;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					上傳表單
				</h4>
				<span>全選 <input onclick="allCheckTable(this)" type="checkbox"></span>
			</div>
			<div class="modal-body">
				<ul>
				<c:forEach items="${tableList }" var="poTable">
					<li><input type="checkbox" class="tableVal" value="${poTable.tableName }"/>${poTable.comments}</li>
				</c:forEach>
				</ul>
			</div>
			<div class="modal-footer">
				<button type="button" onclick="closeModal()" class="btn btn-default" data-dismiss="modal">
					<spring:message
							code="close"/>
				</button>
				<button type="button" onclick="affirmModal()" class="btn btn-primary" data-dismiss="modal">
					<spring:message
							code="submit"/></button>
			</div>
		</div>
	</div>
</div>
</body>
</html>