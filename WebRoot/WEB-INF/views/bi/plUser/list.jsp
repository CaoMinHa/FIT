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
	.table-condensed td{
		padding:2px 3px !important;
	}
	.table-condensed th{
		padding:4px 5px !important;
	}
	.modal-backdrop {
		position: initial!important;
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
});

//用于触发当前点击事件
function clickPage(page){
	$("#loading").show();
	$("#PageNo").val(page);
	$("#Content").load("${ctx}/bi/plUser/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
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

		$("#modal-user-edit").load("${ctx}/bi/plUser/detail",{id:id});
		
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
}



</script>
</head>
<body>
<div style="width:95%;">
    <div id="modal-user-edit" style="display:none;"></div>
	<table align="center" class="table table-condensed table-hover" >
		<thead>
			<tr>
				<th style="text-align:center" >用戶賬號</th>
				<th style="text-align:center" >用戶姓名</th>
				<th style="text-align:center" >類型</th>
                <th style="text-align:center" >leagal</th>
				<th style="text-align:center"  >狀態</th>
				<th style="text-align:center" >創建人</th>
				<th style="text-align:center" >創建時間</th>
				<th style="text-align:center" >更新時間</th>
				<th style="text-align:center" style="white-space: nowrap; border-right:1px solid #eee;text-align: center;"><spring:message code='operation'/></th>
			</tr>
		</thead>
		<tbody>
		<!-- <input id="roleId2" style="display: none" value="${roleId}" type="text">
		<input id="roleName2" style="display: none" value="${roleName}" type="text"> -->
		<c:forEach items="${page.result}" var="mapping" varStatus="sort">
			<tr>
				<c:forEach var="i" begin="0" end="${fn:length(mapping)-index }" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq 0}">
							<td style="white-space: nowrap;border-right:1px solid #eee;display: none;">
								<input  name="ID" type="checkbox"  value="${mapping[i]}"/>
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


</div>

<div id="Fenye" style="height: 50px"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="12" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>