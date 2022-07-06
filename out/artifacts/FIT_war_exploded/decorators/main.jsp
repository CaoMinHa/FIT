<%@page import="foxconn.fit.entity.base.EnumScenarios"%>
<%@page import="foxconn.fit.entity.base.EnumMenu"%>
<%@page import="foxconn.fit.util.SecurityUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
	prefix="decorator"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="logon_title"/></title>
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-combined.min.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-ui-1.10.3/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/bootstrap-toggle/bootstrap-toggle.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/myPagination6.0/page.css"></link>
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/poshytip/tip-yellowsimple/tip-yellowsimple.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/viewer/viewer.min.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/validform/css/style.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/validform/css/password.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/validform/css/datePicker.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/icheck/square/green.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/main.css">

<script type="text/javascript" src="${ctx}/static/js/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery/jquery.md5.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery/validate/jquery.validate.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery/validate/validate.extend.js"></script>
<script type="text/javascript" src="${ctx}/static/js/bootstrap-toggle/bootstrap-toggle.js"></script>
<script type="text/javascript" src="${ctx}/static/js/poshytip/jquery.poshytip.js"></script>
<script type="text/javascript" src="${ctx}/static/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="${ctx}/static/js/myPagination6.0/jquery.myPagination6.0.js"></script>
<script type="text/javascript" src="${ctx}/static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/icheck/icheck.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery.fileupload/jquery.ui.widget.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery.fileupload/jquery.blueimp-gallery.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery.fileupload/jquery.iframe-transport.js"></script>
<!-- 用于兼容IE9 -->
<script type="text/javascript" src="${ctx}/static/js/jquery.fileupload/jquery.fileupload.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery.fileupload/jquery.fileupload-process.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery.fileupload/jquery.fileupload-validate.js"></script>
<script type="text/javascript" src="${ctx}/static/js/viewer/viewer.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/validform/Validform_v5.3.2_min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/validform/plugin/passwordStrength/passwordStrength-min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-ui-1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-ui-1.10.3/jquery.datePicker-min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-ui-1.10.3/jquery.ui.datepicker-zh-CN.js"></script>
<script type="text/javascript" src="${ctx}/static/js/layer/layer.js"></script>
<script type="text/javascript" src="${ctx}/static/js/FileSaver.js"></script>
<script type="text/javascript" src="${ctx}/dwr/engine.js"></script>
<style type="text/css">
.content-body{
    max-width: 500%;
    height: 100%;
}
.two_bar {
	display:none;
}
.top_bar .two_bar li {
	list-style:none;
}
.one_bar {
	display:block;
	text-align:center;
	font-size:16px;
	background-color: #2e363f;
	color:#ffffff;
	border-bottom:1px solid #ffffff;
	border-radius:10px;
	line-height:38px;
	overflow:hidden;
	height:38px;
	margin-bottom: 5px !important;
}
.two_bar li  {
	display:block;
	text-align:center;
	font-size:16px;
	background-color: #2e363f;
	color:#ffffff;
	border-radius:10px;
	line-height:38px;
	overflow:hidden;
	height:35px !important;
}
.top_bar a{
	color: #bebebe;
}
.two_bar a:link,a:visited,a:hover,a:active {
	text-decoration:none;
}
.hover{
	background-color:#2086ee !important;
}

</style>
<script type="text/javascript">
	$(document).ready(function(){
		if($("#menuBI").val()=="Y"){
			$(".one_bar").click(function(){
				$(".nav-body ul").removeAttrs("style");
				$(this).next().slideToggle();
				$(this).parent().siblings().children("ul").slideUp();
			});
			$(".two_bar li").hover(function() {
				$(this).addClass("hover");
			}, function() {
				$(this).removeClass("hover");
			});
			$(".two_bar:first").show();
		}
	});
	$(function() {
	var length=88/($(".js_menu>li").length-1);
	$(".js_menu>li:gt(0)").css("height",((length>9)?9:length)+"%");
	
    $(".js_menu>li>a").bind("click",function(event,init){
		$(".js_menu>li>a").parent().removeClass("active");
	    $(this).parent().addClass("active");
   		var url=$(this).attr("url")+"?time="+new Date().getTime();
   		if(init){
   			$("#mainFrame").load(url);
   		}else{
    		$("#loading").show();
    		setTimeout(function() {
		    	$("#mainFrame").load(url,function(){
	    			$("#loading").hide();
		    	});
    		}, 1000)
   		}
    });
    
	$(".dropdown-menu a[link]").bind("click",function(){
		$(".nav>li>a[linked='"+$(this).attr("link")+"']").trigger("click");
	});


	if($.trim($("#userNameCD").val())=="POAdmin"){
		$(".userMenu a").click();
		$("#navMenu li:gt(0)").hide();
		$(".userMenu").show();
	}

    dwr.engine.setActiveReverseAjax(true);
    dwr.engine.setNotifyServerOnPageUnload(true);
    dwr.engine.setErrorHandler(function(message, ex){});
    
    $("#modifyPwd").click(function(){
    	var pwdFormValid=$("#pwdForm").Validform({
			tiptype:2,
			datatype:{
				"pwd" : /^\w{6,16}$/
			},
			usePlugin:{
				passwordstrength:{
					minLen:6,//设置密码长度最小值，默认为0;
					maxLen:18,//设置密码长度最大值，默认为30;
					trigger:function(obj,error){
						if(error){
							obj.parent().siblings(".Validform_checktip").show();
							obj.parent().siblings(".passwordStrength").hide();
						}else{
							obj.parent().siblings(".Validform_checktip").hide();
							obj.parent().siblings(".passwordStrength").show();	
						}
					}
				}
			},
		});
    	
    	$("#modal-modify-pwd").dialog({
			modal:true,
			title: "<spring:message code='modify.password'/>",
			height:450,
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
			            	var $dialog=$(this);
			            	if(pwdFormValid.check()){
			            		var password=$.md5($("#password").val());
			    				var newPassword=$.md5($("#newPassword").val());
			    				
			            		$.ajax({
			        				type:"POST",
			        				url:"${ctx}/admin/user/pwdModify",
			        				async:false,
			        				dataType:"json",
			        				data:{password:password,newPassword:newPassword},
			        				success: function(data){
			        					if(data.flag=="success"){
			        						$dialog.dialog("destroy");
			        						layer.alert("<spring:message code='modify_pwd_succ'/>");
			        					}else{
			        						layer.alert(data.msg);
			        					}
			        			   	},
			        			   	error: function(XMLHttpRequest, textStatus, errorThrown) {
			        			   		layer.alert("<spring:message code='connect_fail'/>");
			        			   	}
			        			});
			            	}
			            }
			          }
			        ],
			close:function(){
				$(this).dialog("destroy");
			}
		}).dialog("open");
    });
    
    if("${isAdmin}"=="true"){
    	$("#DataSynchronizeSetup").change(function(){
        	$("#TaskSave").show();
        });
        
        $("#TaskSave").click(function(){
    		var value=$("#DataSynchronizeSetup").val();
        	
        	$.ajax({
        		type:"POST",
        		url:"${ctx}/admin/parameter/update",
        		async:true,
        		data:{value:value},
        		dataType:"json",
        		success: function(data){
        			if(data.flag=="success"){
        				$("#TaskSave").hide();
        			}else{
        				layer.alert(data.msg);
        			}
        	   	},
        	   	error: function(XMLHttpRequest, textStatus, errorThrown) {
        	   		window.location.href="${ctx}/logout";
        	   	}
        	});
        });
        
        $.ajax({
    		type:"POST",
    		url:"${ctx}/hfm/schedule/listSynchronizeTask",
    		async:true,
    		dataType:"json",
    		success: function(data){
    			if(data.flag=="success" && data.idList){
    				$("#DataSynchronizeSetup").empty();
    				$.each(data.idList, function(i, n){
    					$("#DataSynchronizeSetup").append("<option value='"+n+"'>"+data.nameList[i]+"</option>");
    				});
    				
    				var taskId="${taskId}";
    				if(taskId==""){
    					$("#TaskSave").show();
    				}else{
    					$("#DataSynchronizeSetup option[value="+taskId+"]").attr("selected","selected");
    				}
    			}else{
    				layer.alert(data.msg);
    			}
    	   	},
    	   	error: function(XMLHttpRequest, textStatus, errorThrown) {
    	   		window.location.href="${ctx}/logout";
    	   	}
    	});
    }
		if($("#menuBI").val()=="Y") {
			if($("#task").val()=="Y"){
				$("a[url='/fit/bi/poTask/index']").click();
			}else if("${detailsTsak}" == "Y"){
				$("a[url='/fit/bi/poTask/index']").click();
			}else if("${detailsTsak}"=="ok"){
				$("a[url='/fit/bi/poIntegration/index']").click();
			}else{
				$(".two_bar li a:first").click();
			}
		}else{
			$(".nav>li>a:first").click();
		}
})
	 
function updateTask(id,status,time){
	var taskId=$("#"+id);
	if(taskId.length>0){
		var cls=("fail"==status)?"error":"success";
		var text=("fail"==status)?"<spring:message code='fail'/>":"<spring:message code='success'/>";
		taskId.siblings(".status").text(text).siblings(".endTime").text(time).parent().removeClass().addClass(cls);
	}
}
</script>
</head>
<body>
<input type="hidden" id="task" value="${task}">
<input type="hidden" id="menuBI" value="${menuBI}">
	<div id="loading" style="display:none;position: absolute;width:100%;height:100%;z-index:9999;background-color: #f1eded;opacity:0.6;">
		<img style="border-radius:50%;position: absolute;left: 50%;top: 50%;width: 100px;height: 100px;margin-top: -50px;margin-left: -50px;" alt="" src="${ctx}/static/image/loading.gif"></div>
	<div id="MessageDialog" style="display:none;"></div>
	<div id="modal-modify-pwd" style="display:none;">
		<form id="pwdForm" class="form-horizontal" autocomplete="off">
			<div class="control-group">
				<label class="control-label"><i
					class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i><spring:message code="old_password"/></label>
				<div class="controls">
					<div class="pull-left">
						<input id="password" type="password" datatype="pwd" nullmsg="<spring:message code='please_input'/>"
							errormsg="<spring:message code='w6_16'/>" style="font-size:16px;" />
					</div>
					<div class="Validform_checktip"></div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><i
					class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i><spring:message code="new_password"/></label>
				<div class="controls">
					<div class="pull-left">
						<input id="newPassword" name="newPassword" type="password" 
							plugin="passwordStrength" datatype="pwd" errormsg="<spring:message code='w6_16'/>" nullmsg="<spring:message code='please_input'/>"
							style="font-size:16px;" />
					</div>
					<div class="Validform_checktip"></div>
					<div class="passwordStrength Validform_checktip" style="display:none;">
						<b><spring:message code='pwd_strength'/>：</b> <span><spring:message code='weak'/></span> <span><spring:message code='moderate'/></span> <span class="last"><spring:message code='strong'/></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><i
					class="icon-asterisk need m-r-sm" title="<spring:message code='required'/>"></i><spring:message code="repeat_new_password"/></label>
				<div class="controls">
					<div class="pull-left">
						<input type="password" datatype="*" recheck="newPassword" nullmsg="<spring:message code='please_input'/>"
	             			errormsg="<spring:message code='not_match'/>" style="font-size:16px;"/>
					</div>
					<div class="Validform_checktip"></div>
				</div>
			</div>
		</form>
	</div>
	<div>
		<div style="width:15%;display: inline-block;">
			<security:authorize access="authenticated">
				<input type="hidden" id="userNameCD" value="<security:authentication property='principal.username'/>">
			</security:authorize>
			<nav class="navbar navbar-static-left">
				<ul class="nav js_menu" id="navMenu">
					<li class="nav-header">
						<img style="width:100%;height:100%;border:none;" src="${ctx }/static/image/logo/logo.png"/>
					</li>
					<security:authorize access="hasRole('ROLE_ADMIN')">
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/admin/userLog/index">
								<div class="div-middle">
									<span>用戶操作日誌</span>
								</div>
							</a>
						</li>
						<li class="nav-body userMenu">
							<a href="javascript:void(0);" url="${ctx}/admin/user/index">
								<div class="div-middle">
									<span><spring:message code='user_manage'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/admin/dimension/index">
								<div class="div-middle">
									<span><spring:message code='dimension'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/admin/table/index">
								<div class="div-middle">
									<span>明細表管理</span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/admin/mappingRelation/index">
								<div class="div-middle"> 
									<span><spring:message code='mapping_relation'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/admin/planning/index">
								<div class="div-middle"> 
									<span>預算數據管理</span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/admin/masterData/index">
								<div class="div-middle"> 
									<span><spring:message code='masterDataPlatform'/></span>
								</div>
							</a>
						</li>
					</security:authorize> 
					<security:authorize access="hasRole('ROLE_HFM')">
						<%-- <li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/apBalanceInvoice/index">
								<div class="div-middle">
									<span><spring:message code='AP_balance_invoice'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/apBalanceStorage/index">
								<div class="div-middle">
									<span><spring:message code='AP_balance_storage'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/apPayment/index">
								<div class="div-middle">
									<span><spring:message code='AP_payment'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/apTradeInvoice/index">
								<div class="div-middle"> 
									<span><spring:message code='AP_trade_invoice'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/apTradeStorage/index">
								<div class="div-middle"> 
									<span><spring:message code='AP_trade_storage'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/arBalanceInvoice/index">
								<div class="div-middle">
									<span><spring:message code='AR_balance_invoice'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/arBalanceSale/index">
								<div class="div-middle">
									<span><spring:message code='AR_balance_sale'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/arReceive/index">
								<div class="div-middle">
									<span><spring:message code='AR_receive'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/arTradeInvoice/index">
								<div class="div-middle"> 
									<span><spring:message code='AR_trade_invoice'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/arTradeSale/index">
								<div class="div-middle"> 
									<span><spring:message code='AR_trade_sale'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/fixedAssets/index">
								<div class="div-middle"> 
									<span><spring:message code='fixed_assets'/></span>
								</div>
							</a>
						</li> 
						--%>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/generalLedger/index">
								<div class="div-middle"> 
									<span><spring:message code='general_ledger'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/coaMapping/index">
								<div class="div-middle"> 
									<span><spring:message code='coa_mapping'/></span>
								</div>
							</a>
						</li>
						<%--
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/customerMapping/index">
								<div class="div-middle"> 
									<span><spring:message code='customer_mapping'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/supplierMapping/index">
								<div class="div-middle"> 
									<span><spring:message code='supplier_mapping'/></span>
								</div>
							</a>
						</li>
						--%>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/audit/index">
								<div class="div-middle"> 
									<span><spring:message code='auditPackage'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/reconciliation/index">
								<div class="div-middle"> 
									<span><spring:message code='reconciliation'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/mappingRelation/index">
								<div class="div-middle"> 
									<span><spring:message code='mapping_relation'/></span>
								</div>
							</a>
						</li>
						<li class="nav-body">
							<a href="javascript:void(0);" url="${ctx}/hfm/package/index">
								<div class="div-middle"> 
									<span>HFM Package</span>
								</div>
							</a>
						</li>
					</security:authorize>
					<security:authorize access="hasRole('ROLE_BI')">
						<c:forEach items="${menuList}" var="menu">
							<div class="top_bar">
							<li class="nav-body">
								<div class="one_bar" >
									<div class="div-middle">
										<span style="color: #fff;font-size: 18px">
											<c:choose>
												<c:when test="${languageS == 'en_US'}">
													${menu.menuNameE }
												</c:when>
												<c:when test="${languageS == 'zh_CN'}">
												${menu.menuName }
												</c:when>
											</c:choose>
										</span>
									</div>
								</div>
								<ul class="two_bar js_menu">
									<c:forEach items="${menu.list}" var="menulist">
										<li>
											<a href="javascript:void(0);" url='${ctx}/bi/${menulist.menuCode }/index'>
												<c:choose>
													<c:when test="${languageS == 'en_US'}">
														${menulist.menuNameE }
													</c:when>
													<c:when test="${languageS == 'zh_CN'}">
														${menulist.menuName }
													</c:when>
												</c:choose>
											</a>
										</li>
									</c:forEach>
								</ul>
							</li>
							</div>
						</c:forEach>
					</security:authorize>

					<security:authorize access="hasRole('ROLE_Budget')">
						<c:forEach items="<%=SecurityUtils.getMenus() %>" var="menu">
							<li class="nav-body">
								<a href="javascript:void(0);" url='${ctx}/budget/${menu }/index'>
									<div class="div-middle">
										<span><spring:message code='${menu }'/></span>
									</div>
								</a>
							</li>
						</c:forEach>
							<li>
								<a href="javascript:void(0);" url='${ctx}/budget/fileDownload/index'>
									<div class="div-middle">
											<span>
												<c:if test="${languageS eq 'zh_CN'}">文件下載</c:if>
												<c:if test="${languageS eq 'en_US'}">File Download</c:if>
											</span>
									</div>
								</a>
							</li>
					</security:authorize> 
				</ul>
			</nav>
		</div>
		<div class="main-wrapper bg-white">
			<div class="row navbar-static-top"
				style="position:relative;margin-left:0;">
				<page:applyDecorator name="header" />
			</div>
			<div id="mainFrame" class="wrapper-content" style="height:91%;width:100%;overflow:scroll;position:relative;"></div>
		</div>
	</div>
</body>
</html>