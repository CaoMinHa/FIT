<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<html>
<head>
	<style type="text/css">
		.theadCss th{
			border-left: 1px solid #e3e0e0;
			text-align:center;
		}
	</style>
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
	
	
	
	$(".table-condensed a.delete").click(function(){
	/*$("#Download").click(function(){*/
		var $this=$(this);
		layer.confirm("<spring:message code='confirm'/>?",{btn: ['<spring:message code='confirm'/>', '<spring:message code='cancel'/>'], title: "<spring:message code='tip'/>"},function(index){
			layer.close(index);
			/*var id=$this.parent().attr("mappingId");
	        if(!$("#QYear").val()){
				layer.alert("請選擇年份！(Please select a year)");
				 return;
			  }*/
			var row = $("#Download").closest('tr');
			var entity1 =  row.find('td:eq(1)').text();
			var year1 =  row.find('td:eq(3)').text();
			var scenario1 =  row.find('td:eq(4)').text();
			var version1 =  row.find('td:eq(5)').text();			
	        $("#loading").show();
		    $.ajax({
			  type:"POST",
			  url:"${ctx}/bi/signedINVBudget/download",
			  async:true,
			  dataType:"json",
			  /*data:{id:id,years: $("#QYear").val()},*/
			  data:{entitys:entity1,years:year1,scenarios:scenario1,versions:version1},
			  success: function(data){  
				if (data.flag == "success") {
					window.location.href = "${ctx}/static/download/" + data.fileName;
				} else {
					layer.alert(data.msg);
				}
				$("#loading").hide();
			   },
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				$("#loading").hide();
				layer.alert("<spring:message code='connect_fail'/>");
			    }
		   });
	    });
		//refresh();
	});
});



//用于触发当前点击事件
function clickPage(page){
	$("#PageNo").val(page);
	if(!$("#QScenarios").val()){
		layer.alert("請選擇場景！(Please select a scene)");
		return;
	}
	$("#loading").show();
	var entity="";
	$("input[name=entitys]:checked").each(function(i,dom){
		entity+=$(dom).val()+",";
	});
	$("#Content").load("${ctx}/bi/signedINVBudget/list",{pageNo:$("#PageNo").val(),pageSize:$("#PageSize").val(),
		orderBy:$("#OrderBy").val(),orderDir:$("#OrderDir").val(),
		scenarios:$("#QScenarios").val(),
		entitys:entity.substring(0,entity.length-1),year:$("#QYear").val()},function(){$("#loading").fadeOut(1000);});
}

function refresh(){
	clickPage("1");
}

function addDept(index) {
	var $this=$(this);
	//var id=$('input[type=checkbox]')[index].value;
	var id = $('input[name="ID"]')[index].value;
	$("#loading").show();
	layer.confirm("<spring:message code='confirm'/>?",{btn: ['<spring:message code='confirm'/>', '<spring:message code='cancel'/>'], title: "<spring:message code='tip'/>"},function(index){
		layer.close(index);
        if(!$("#QYear").val()){
			layer.alert("請選擇年份！(Please select a year)");
			 return;
		  }
        $("#loading").show();
	    $.ajax({
		  type:"POST",
		  url:"${ctx}/bi/signedINVBudget/download",
		  async:true,
		  dataType:"json",
		  data:{id:id,years: $("#QYear").val()},
		  //data:{entitys:entity1,years:year1,scenarios:scenario1,versions:version1},
		  success: function(data){  
			if (data.flag == "success") {
				window.location.href = "${ctx}/static/download/" + data.fileName;
			} else {
				layer.alert(data.msg);
			}
			$("#loading").hide();
		   },
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			$("#loading").hide();
			layer.alert("<spring:message code='connect_fail'/>");
		    }
	   });
    },function(index){
    	layer.close(index);
    	$("#loading").hide();
    	});
    
	//refresh();
}

</script>
</head>
<body>
<div style="width:100%;">
	<c:choose>	
		<c:when test="${scenarios eq 'Budget'}">
			<table class="table table-condensed table-hover">
				<c:if test="${languageS eq 'en_US'}">
					<thead class="theadCss">
					<tr>
					    <th>Opertion</th>
						<th>Year</th>
						<th>Doc_number</th>
						<th>Request_date</th>
						<th>SBU_code</th>
						<th>SBU_name</th>
						<th>Department_code</th>
						<th>Department_name</th>
					</tr>
					</thead>
				</c:if>
				<c:if test="${languageS eq 'zh_CN'}">
					<thead class="theadCss">
					<tr>
					    <th>操作</th>
						<th>年份</th>
						<th>簽核單號</th>
						<th>申請日期</th>
						<th>SBU/機能</th>
						<th>SBU/機能名稱</th>
						<th>部門編碼</th>
						<th>部門名稱</th>
					</tr>
					</thead>
				</c:if>
				<tbody>
				<c:forEach items="${page.result}" var="mapping" varStatus="sort">
					 <tr>
					    <td style="white-space: nowrap;border-right:1px solid #eee;display: none">
								<input  name="ID" type="checkbox"  value="${mapping.id}"/>
						</td>
						<td style="border-right:1px solid #eee;text-align:left;">
						<!--<td style="border-right:1px solid #eee;text-align:left;"> 
						    <button id="${mapping.id}" class="btn search-btn" style="width: 80px;" type="button" ><spring:message code='download'/></button> 
						    <a href="javascript:void(0);" class="m-r-md delete" ><spring:message code='delete'/></a>  -->
						     <!--   <input type='button' id="button_${mapping.id}" value="${mapping.id}" class="btn search-btn" style="width: 100px;"/>	    -->
						    <a href="javascript:void(0);" class="role2User" onclick="addDept(${sort.index})">下載</a>
						</td>
						<td style="border-right:1px solid #eee;text-align:center;">${mapping.year}</td>
						<td style="border-right:1px solid #eee;text-align:center;">${mapping.docNumber}</td>
						<td style="border-right:1px solid #eee;text-align: center;">${mapping.dateV}</td>
						<td style="border-right:1px solid #eee;text-align: center;">${mapping.sbuCode}</td>
						<td style="border-right:1px solid #eee;text-align: center;">${mapping.sbuName}</td>
						<td style="border-right:1px solid #eee;text-align: center;">${mapping.departmentCode}</td> 
						<td style="border-right:1px solid #eee;text-align: center;">${mapping.departmentName}</td>   
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:when>
	</c:choose>

</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}" />
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}" />
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}" />
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}" />
</body>
</html>