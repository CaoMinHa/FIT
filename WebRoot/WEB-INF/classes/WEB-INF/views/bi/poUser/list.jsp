<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<style>
	input {
		margin-bottom:0px !important;
	}
	.glyphicon-ok:before {
		content: "\e013";
	}
	*, *:after, *:before {
		-webkit-box-sizing: border-box;
		-moz-box-sizing: border-box;
		box-sizing: border-box;
	}
	:after, :before {
		-webkit-box-sizing: border-box;
		-moz-box-sizing: border-box;
		box-sizing: border-box;
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
	$('.selectpicker').selectpicker('render');
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	var date=$("#QDate").val();
	var entity=$("#QEntity").val();
	var tableName=$("#QTableName").val();
	$("#Content").load("${ctx}/bi/poUser/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
														orderBy:$("#OrderBy").val(),
		name:$("#name").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}
$('.selectpicker').selectpicker({
	noneSelectedText: '请选择',
	liveSearch: true,
	size:5   //设置select高度，同时显示5个值
});

function addDept(index) {
	var id=$('input[type=checkbox]')[index].value;
	var sid = $("#select_article").val();
	$("#loading").show();
	$('.selectpicker').selectpicker('val', '');
	$('.selectpicker').selectpicker('refresh');
	$.ajax({
		type:"GET",
		url:"${ctx}/bi/poUser/commodityAndSbu",
		dataType:"json",
		data:{userId:id},
		success: function (res) {
			var html="";
			var sbuHtml="";
			var data=res.map[0];
			$.each(res.cList, function (i) {
				if(data.COMMODITY_MAJOR!=null&&data.COMMODITY_MAJOR.toString().indexOf(res.cList[i].toString())!=-1){
					html += "<option selected>" + res.cList[i] + "</option>";
				}else{
					html += "<option>" + res.cList[i] + "</option>";
				}
			});
			$.each(res.sList, function (i) {
				if(data.SBU!=null&&data.SBU.toString().indexOf(res.sList[i].toString())!=-1){
					sbuHtml += "<option selected>" + res.sList[i] + "</option>";
				}else{
					sbuHtml += "<option>" + res.sList[i] + "</option>";
				}
			});
			$("#email").val(data.EMAIL)
			$('#select_c.selectpicker').html(html)
			$('#select_s.selectpicker').html(sbuHtml)
			$('.selectpicker').selectpicker('refresh');
			$("#loading").hide();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			console.log(222)
			layer.alert("<spring:message code='connect_fail'/>");
		}
	});
	$("#modal-dept").dialog({
		modal: true,
		title: "用戶維護",
		height: 400,
		width: 500,
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
					var that=$(this);
					$("#loading").show();
					var sbu=$("#select_s").val()!=null?$("#select_s").val().toString():"";
					var commodity=$("#select_c").val()!=null?$("#select_c").val().toString():""
					var email=$("#email").val();
					var obj={
						id:id,
						sbu:sbu,
						commodity:commodity,
						email:email
					}
					$.ajax({
						type:"POST",
						url:"${ctx}/bi/poUser/update",
						dataType:"json",
						data:obj,
						success: function (res) {
							$("#loading").hide();
							layer.alert(res.msg);
							that.dialog("destroy");
							refresh();

						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							$("#loading").hide();
							layer.alert("<spring:message code='connect_fail'/>");
						}
					});


				}
			},
			{
				text: "<spring:message code='close'/>",
				click: function () {
					that.dialog("destroy");
				}
			}
		],
		close: function () {
			$(this).dialog("destroy");
		}
	}).dialog("open");
}



</script>
</head>
<body>
<div style="width:95%;">
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center;width: 50px" >序号</th>
				<th style="text-align:center" >用戶賬號</th>
				<th style="text-align:center" >類型</th>
                <th style="text-align:center" >SBU</th>
				<th style="text-align:center" >物料大类</th>
                <th style="text-align:center" >郵箱</th>
				<th style="text-align:center"  >狀態</th>
				<th style="text-align:center" >創建人</th>
				<th style="text-align:center" >創建時間</th>
				<th style="text-align:center" style="white-space: nowrap; border-right:1px solid #eee;text-align: center;"><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
		<input id="roleId2" style="display: none" value="${roleId}" type="text">
		<input id="roleName2" style="display: none" value="${roleName}" type="text">
		<c:forEach items="${page.result}" var="mapping" varStatus="sort">
			<tr>
				<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq 0}">
							<td style="white-space: nowrap;border-right:1px solid #eee;">
								<input name="ID" type="checkbox"  value="${mapping[i]}"/>
							</td>
						</c:when>
						<c:otherwise>
							<td style="border-right:1px solid #eee;">${mapping[i]}</td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<td style="white-space: nowrap; border-right:1px solid #eee;">
					<a href="javascript:void(0);" class="role2User" onclick="addDept(${sort.index})">更新</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div id="modal-dept" style="display:none;">
		<form id="deptForm" class="form-horizontal">
			<input id="id" style="display: none;"/>
			<div class="control-group" style="margin:0px 0px 20px 10px;">
				<div class="pull-left" style=" display: inline-block">
					<i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
					物料大类：
					<select multiple class="selectpicker show-tick" style="outline: none;width:350px;" data-live-search="true" id="select_c" name="select_article">
					</select>
				</div>
				<div class="pull-left" style=" display: inline-block">
					<i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
					SBU 部门：
					<select  multiple class="selectpicker show-tick" style="outline: none;width:350px;margin-left: 20px" data-live-search="true" id="select_s" name="select_article">
					</select>
				</div>
				<div class="pull-left" style=" display: inline-block">
					<i class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i>
					用戶郵箱：
					<input id="email" style="height: 30px !important;" type="email" datatype="s3-30"/>
				</div>
			</div>

		</form>
	</div>


</div>

<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>