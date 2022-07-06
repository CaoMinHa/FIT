<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<style type="text/css">
		.modal-backdrop {
			position: initial!important;
		}
		.modal-header{
			background-color: #f5f5f5!important;
		}
	</style>
	<script type="text/javascript">
		function fileClick(e,val) {
			var index = layer.confirm('下載文件', {
				btn: ['下載','关闭'], //按钮
				shade: false //不显示遮罩
			}, function(){
				debugger
				//下載
				var tempwindow=window.open('_blank');
				var url=$("#fileAddress").val().substring(0,$("#fileAddress").val().length-1);
				tempwindow.location= "${ctx}/static/download/"+url+"/"+val;
				layer.close(index);
			}, function(){
				//关闭提示框
				layer.close(index);
			});
		}
	</script>

</head>
<body>
<div class="row-fluid bg-white content-body">
	<div class="p-l-md p-r-md p-b-md">
		<div style="width:95%;">
			<form style="text-align: center;">
				<div>
					<label style="display: inline!important;">用戶分組：</label>
					<textarea style="width: 700px;" readonly type="text">${poEmailLog.emailTeam}</textarea>
				</div>
				<div>
					<label style="display: inline!important;" >郵件主題：</label>
					<input id="emailTitle"  readonly style="width: 700px;" type="text" value="${poEmailLog.emailTitle}">
				</div>
				<div>
					<label style="display: inline!important;">郵件内容：</label>
					<textarea style="width: 700px" id="emailContent" readonly rows="15">${poEmailLog.emailContent}</textarea>
				</div>
				<div style="margin-right: 500px;">
					<label style="display: inline!important;">截止时间：</label>
					<input id="endDate" style="width: 200px;text-align:center;" readonly value="${poEmailLog.endDate}">
				</div>
				<div style="text-align: center;">
					<input id="fileAddress"  style="display: none" value="${poEmailLog.fileAddress}">
					<div>
						<c:forEach items="${fileList}" var="mapping">
							<span></span>
						</c:forEach>
						<c:forEach items="${fileList}" var="file">
						    <span>
								<a href="###" onclick="fileClick(this,'${file}')">${file}</a>
							</span>
							<br>
						</c:forEach>

					</div>
				</div>
			</form>
		</div>
	</div>
</div>
</body>
</html>
