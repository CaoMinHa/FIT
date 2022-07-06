<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<div class="pull-left" style="margin-left:20px;;line-height: 60px;text-align:center;">
	<div style="font-size:15px;">
		<span class="label" style="font-size:14px;line-height:20px;margin-right:5px;"><spring:message code="language"/></span>
		<a href="?lang=zh_CN" style="margin-right:10px;"><spring:message code="language.cn"/></a>
		<a href="?lang=en_US"><spring:message code="language.en"/></a>
	</div>
</div>
<security:authorize access="hasRole('ROLE_ADMIN')">
	<div class="pull-left m-l-md" style="font-size:15px;line-height: 60px;display:inline-block;text-align: center;">
		<spring:message code='dataSynchronizeSetup'/>:
		<select id="DataSynchronizeSetup" style="width:130px;color:#9f9a9a;font-weight:bold;margin-bottom:0;">
		</select>
		<button id="TaskSave" style="display:none;" class="btn btn-small btn-primary" type="button"><spring:message code="save"/></button>
	</div>
</security:authorize>
<ul class="nav-top-links pull-right">
	<security:authorize access="authenticated">
		<li style="line-height: 60px;">
			<a class="m-l-sm" data-toggle="dropdown" href="#">
				<span style="font-size:22px;">
					<security:authorize access="authenticated">
						<security:authentication property="principal.username" />
					</security:authorize> 
				</span>
				<span class="icon-caret-down" style="font-size:20px;"></span>
			</a>
			<ul class="dropdown-menu">
				<li>
					<a id="modifyPwd" href="javascript:void(0);"> <b class="fa fa-sign-out"></b>
						<spring:message code="modify.password"/>
					</a>
					<a href="${ctx}/logout"> <b class="fa fa-sign-out"></b>
						<spring:message code="logout"/>
					</a>
				</li>
			</ul>
		</li>
	</security:authorize>
</ul>