<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglibs.jsp" %>
<html>
<head>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <script type="text/javascript">
        function plUpdateData(type) {
            var url="";
            var date="";
            switch (type) {
                case "pl":
                    if(!$("#plDate").val()){
                        layer.alert("請選擇年月！");
                        return;
                    }
                    date=$("#plDate").val();
                    url="${ctx}/bi/dataDisseminationEmail/plEmail";
                    break;
                case "rawdata":
                    if(!$("#rawdataDate").val()){
                        layer.alert("請選擇年月！");
                        return;
                    }
                    date=$("#rawdataDate").val();
                    url="${ctx}/bi/dataDisseminationEmail/rawdataEmail";
                    break;
                case "bs":
                    if(!$("#bsDate").val()){
                        layer.alert("請選擇年月！");
                        return;
                    }
                    date=$("#bsDate").val();
                    url="${ctx}/bi/dataDisseminationEmail/bsEmail";
                    break
                case "cf":
                    if(!$("#cfDate").val()){
                        layer.alert("請選擇年月！");
                        return;
                    }
                    date=$("#cfDate").val();
                    url="${ctx}/bi/dataDisseminationEmail/cfEmail";
                    break
                default:
                    break;
            }
            $("#loading").show();
            $.ajax({
                type: "POST",
                url: url,
                async: true,
                dataType: "json",
                data:{date:date},
                success: function (data) {
                    layer.confirm(data.msg,{btn: ['關閉'], title: "提示"},function(index){
                        layer.close(index);
                        $("#loading").hide();
                        $("a[url='/fit/bi/dataDisseminationEmail/index']").click();
                    });
                },error: function () {
                    $("#loading").hide();
                }
            });
        };
    </script>
</head>
<body>
<div class="row-fluid bg-white content-body">
    <div class="span12">
        <div class="page-header bg-white">
            <h2>
                <span>三表發佈郵件通知</span>
            </h2>
        </div>
        <div class="m-l-md m-t-md m-r-md">
            <div class="controls">
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <select id="plDate" class="input-large" style="width:110px;">
                        <option value=""><spring:message code='please_select'/><spring:message code='date'/></option>
                        <c:forEach items="${plDate}" var="date">
                            <option value="${date}" >${date}</option>
                        </c:forEach>
                    </select>
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('pl')">
                        損益表發佈郵件通知</button>
                </div>
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <select id="rawdataDate" class="input-large" style="width:110px;">
                        <option value=""><spring:message code='please_select'/><spring:message code='date'/></option>
                        <c:forEach items="${rawdataDate}" var="date">
                            <option value="${date}" >${date}</option>
                        </c:forEach>
                    </select>
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('rawdata')">
                        損益表 Rawdata數據郵件通知</button>
                </div>
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <select id="bsDate" class="input-large" style="width:110px;">
                        <option value=""><spring:message code='please_select'/><spring:message code='date'/></option>
                        <c:forEach items="${bsDate}" var="date">
                            <option value="${date}" >${date}</option>
                        </c:forEach>
                    </select>
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('bs')">
                        BU資產負債表發佈郵件通知</button>
                </div>
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <select id="cfDate" class="input-large" style="width:110px;">
                        <option value=""><spring:message code='please_select'/><spring:message code='date'/></option>
                        <c:forEach items="${cfDate}" var="date">
                            <option value="${date}" >${date}</option>
                        </c:forEach>
                    </select>
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('cf')">
                        BU現金流量表發佈郵件通知</button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
