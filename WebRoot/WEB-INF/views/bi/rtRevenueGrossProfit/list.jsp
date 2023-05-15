<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglibs.jsp" %>
<html>
<head>
    <script type="text/javascript">
        var Page;
        $(function () {
            Page = $("#Fenye").myPagination({
                currPage: eval('${fn:escapeXml(page.pageNo)}'),
                pageCount: eval('${fn:escapeXml(page.totalPages)}'),
                pageNumber: 5,
                panel: {
                    tipInfo_on: true,
                    tipInfo: '跳{input}/{sumPage}页',
                    tipInfo_css: {
                        width: "20px",
                        height: "20px",
                        border: "2px solid #f0f0f0",
                        padding: "0 0 0 5px",
                        margin: "0 5px 20px 5px",
                        color: "red"
                    }
                },
                ajax: {
                    on: false,
                    url: "",
                    pageCountId: 'pageCount',
                    param: {on: true, page: 1},
                    dataType: 'json',
                    onClick: clickPage,
                    callback: null
                }
            });

            $("#Fenye>input:first").bind("blur", function () {
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
        function clickPage(page) {
            $("#loading").show();
            $("#PageNo").val(page);
            var queryCondition=$("#QueryCondition").serialize();
            $("#Content").load("${ctx}/bi/rtRevenueGrossProfit/list", {
                pageNo: $("#PageNo").val(), pageSize: $("#PageSize").val(),
                orderBy: $("#OrderBy").val(), orderDir: $("#OrderDir").val(),queryCondition:decodeURIComponent(queryCondition)
            }, function () {
                $("#loading").fadeOut(1000);
            });
        }
        function refresh() {
            Page.jumpPage($(this).val());
            clickPage(Page.getPage());
        }

    </script>
</head>
<body>
<div style="width: 220%">
    <table class="table table-condensed table-hover">
        <thead>
        <tr>
            <th>上傳日期</th>
            <th>年月</th>
            <th>體系</th>
            <th>Product Category</th>
            <th>BU</th>
            <th>SBU</th>
            <th>SSBU</th>
            <th>Segment</th>
            <th>產品歸類</th>
            <th>主產業</th>
            <th>次產業</th>
            <th>策略</th>
            <th>屏網雲</th>
            <th>內交/外售</th>
            <th>品牌客戶群組</th>
            <th>品牌客戶（英文）</th>
            <th>品牌客戶（中文）</th>
            <th>客戶所在地區</th>
            <th>Product Family</th>
            <th>Product Series</th>
            <th>M-2106銷量MPCS-實際</th>
            <th>M-2106折算營收MUSD-實際</th>
            <th>M-2106折算毛利MUSD-實際</th>
            <th>匯率</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${page.result}" var="mapping">
                <tr>
                    <td style="border-right:1px solid #eee;text-align:left;">${mapping[0]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[1]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[2]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[3]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[4]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[5]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[6]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[7]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[8]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[9]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[10]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[11]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[12]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[13]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[14]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[15]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[16]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[17]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[18]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[19]}</td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[20]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[21]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[22]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[23]}" pattern="#,##0.##"></fmt:formatNumber></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
<div id="Fenye"></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}"/>
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}"/>
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}"/>
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}"/>
</body>
</html>