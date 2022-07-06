<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="logon_title"/></title>
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/bootstrap-combined.min.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/main.css">
<link rel="stylesheet" href="${ctx}/static/js/poshytip/tip-yellowsimple/tip-yellowsimple.css" type="text/css" />

<script type="text/javascript" src="${ctx}/static/js/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery/jquery.md5.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery/validate/jquery.validate.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery/validate/validate.extend.js"></script>
<script type="text/javascript" src="${ctx}/static/js/poshytip/jquery.poshytip.js"></script>


<script type="text/javascript">
$(function(){
	$("#loginForm").validate({
		submitHandler: function(form) {
			$("#password").val($.md5($("#loginForm #j_password").val()));
			form.submit();
			
			return false;
		},
		onkeyup:function(){
		},
		rules: {
			username:{
				required:true
			},
			j_password:{
				required:true
			}/* ,
			verifyCode:{
				required:true,
				rangelength:[4,4],
				checkVerifyCode: true
			} */
		},
		messages: {
			username:{
				required:"<spring:message code='please_input'/>"
			},
			j_password:{
				required:"<spring:message code='please_input'/>"
			}/* ,
			verifyCode:{
				required:"<spring:message code='please_input'/>",
				rangelength:"<spring:message code='limit_length_4'/>"
			} */
		},
		unhighlight:function(element, errorClass, validClass){
			if (element.type === 'radio') {
				this.findByName(element.name).removeClass(errorClass).addClass(validClass);
			} else {
				$(element).removeClass(errorClass).addClass(validClass);
			}
			$(element).removeData("init").poshytip('destroy');
		},
		errorPlacement: function (error, element) { //指定错误信息位置
        	if(!element.data("init")){
        		var alignX="right";
        		var alignY="center";
        		/* if(element.attr("id")=="verifyCode"){
        			alignX="center";
        			alignY="bottom";
        		} */
        		
	            element.poshytip({
	                className: 'tip-yellowsimple',
	                content: error,
	                alignTo: 'target',
	                alignX: alignX,
	                alignY: alignY,
	                offsetX: 5,
	                offsetY: 0,
	                showOn:'none',
	                allowTipHover:false
	            });
                element.poshytip('show');
	            element.data("init","true");
        	}else{
            	element.poshytip('update',error);
			}
		}
	});
	
	/* jQuery.validator.addMethod('checkVerifyCode', function(value, element, param) {
		var flag=false;
		$.ajax({
			type:"POST",
			url:"${ctx}/checkVerifyCode",
			dataType:"text",
			async: false,
			data:{verifyCode:value},
			success: function(data){
				if(data==""){
					flag=true;
				}else if(data=="expired"){
					$.validator.messages['checkVerifyCode'] = "<spring:message code='verifyCode_expired'/>";
					changeVerifyCode();
				}else{
					$.validator.messages['checkVerifyCode'] = "<spring:message code='verifyCode_error'/>";
				}
		   	},
		   	error: function(XMLHttpRequest, textStatus, errorThrown) {
		   	}
		});
		return flag;
	}, $.validator.format("<spring:message code='verifyCode_error'/>")); */
	
});

/* function changeVerifyCode(){
	$("#verify").attr("src","${ctx}/getVerifyCode?width=100&height=34&time=" + new Date().getTime());
} */
</script>
</head>
<body class="bg-gray">
<div style="text-align: center;padding-top:15px;font-size:15px;">
	<span class="label" style="font-size:14px;line-height:20px;margin-right:15px;"><spring:message code="language"/></span>
	<a href="?lang=zh_CN" style="margin-right:15px;"><spring:message code="language.cn"/></a>
	<a href="?lang=en_US"><spring:message code="language.en"/></a>
</div>
<div class="login text-center">
	<div style="position: relative;top: 30%;height: 40%;">
		<h2><spring:message code="welcome"/></h2>
		<p style="color: #ed1313;font-weight: bold;font-size: 2rem;"><spring:message code="${error}"/></p>
		<form id="loginForm" action="${ctx}/login" method="post" autocomplete="off">
			<div class="control-group">
				<div class="controls">
					<input type="text" class="col-sm-12" id="username" name="username" placeholder="<spring:message code='username'/>"/>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<input type="password" class="col-sm-12" id="j_password" name="j_password" placeholder="<spring:message code='password'/>"/>
					<input type="password" id="password" name="password" readonly="readonly" autocomplete="off" style="display:none;"/>
				</div>
			</div>
			<%-- <div class="control-group">
				<div class="controls">
					<div>
						<input type="text" class="col-sm-5" id="verifyCode" name="verifyCode" placeholder="<spring:message code='verifyCode'/>"/>
					</div>
					<div>
						<a href="javascript:changeVerifyCode()" class="col-sm-5" style="padding-right:0px;float:right;"><img id="verify" src="${ctx}/getVerifyCode?width=100&height=34" title="<spring:message code='click.refresh'/>"/></a>
					</div>
				</div>
			</div> --%>
			<div class="control-group">
				<div class="controls">
					<button type="submit" class="btn btn-success btn-block full-width m-b-lg"><spring:message code="logon"/></button>
				</div>
			</div>
		</form>
		<p class="copyright font-xs m-t-lg">Copyright ©2018 All rights reseverd by foxconn</p>
	</div>
</div>
</body>
</html>	
