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
            $("#loading").show();
            var url="";
            switch (type) {
                case "pl":
                    url="${ctx}/bi/dataDisseminationEmail/plEmail";
                    break;
                case "rawdata":
                    url="${ctx}/bi/dataDisseminationEmail/rawdataEmail";
                    break;
                case "bs":
                    url="${ctx}/bi/dataDisseminationEmail/bsEmail";
                    break
                case "cf":
                    url="${ctx}/bi/dataDisseminationEmail/cfEmail";
                    break
                default:
                    break;
            }
            $.ajax({
                type: "POST",
                url: url,
                async: true,
                dataType: "json",
                success: function (data) {
                    $("#loading").hide();
                    layer.alert(data.msg);
                },
                error: function () {
                    $("#loading").hide();
                    layer.alert("郵件通知失敗,請聯係系統管理員。");
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
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('pl')">
                        損益表發佈郵件通知</button>
                </div>
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('rawdata')">
                        損益表 Rawdata數據郵件通知</button>
                </div>
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('bs')">
                        BU資產負債表發佈郵件通知</button>
                </div>
                <div class="m-l-md m-t-md m-r-md" style="clear:both;">
                    <button class="btn search-btn btn-primary m-l-md" type="button" onclick="plUpdateData('cf')">
                        BU現金流量表發佈郵件通知</button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
