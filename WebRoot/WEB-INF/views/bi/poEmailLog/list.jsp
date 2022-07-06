<%@page import="foxconn.fit.entity.base.EnumGenerateType"%>
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
		});

		//用于触发当前点击事件
		function clickPage(page){
			debugger
			$("#loading").show();
			$("#PageNo").val(page);
			$("#Content").load("${ctx}/bi/poEmailLog/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
				orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
				title:$("#title").val(),name:$("#name").val(),
				date:$("#Date").val(),dateEnd:$("#DateEnd").val()
			},function(){$("#loading").fadeOut(1000);});
		}

		function refresh(){
			clickPage("1");
		}

		//跳轉到詳細頁面
		function details(id){
			$("#loading").show();
			$("#name").hide();
			$("#title").hide();
			$("#DateEnd").hide();
			$("#Date").hide();
			$("#Query").hide();
			$("#Content").load("${ctx}/bi/poEmailLog/details",{id:id},
					function(){$("#loading").fadeOut(1000);});
		}


	</script>
</head>
<body>
<div style="width:100%;">
	<table align="center" class="table table-condensed table-hover" >
		<thead>
		<tr>
			<th style="text-align:center;width: 10%" >創建人</th>
			<th style="text-align:center;width: 12%" >創建時間</th>
			<th style="text-align:center;width: 23%" >郵件主題</th>
			<th style="text-align:center;width: 55%" >收件人</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.result}" var="mapping" varStatus="sort">
			<tr>
				<c:forEach var="i" begin="0" end="${fn:length(mapping)-index}" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq 2}">
							<td style="border-right:1px solid #eee">
								<a href="javascript:void(0);" class="update" onclick="details(${mapping[4]})">${mapping[i]}</a>
							</td>
						</c:when>
						<c:when test="${status.index eq 4 || status.index eq 5}"></c:when>
						<c:otherwise>
							<td style="border-right:1px solid #eee">${mapping[i]}</td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
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
</body>
</html>