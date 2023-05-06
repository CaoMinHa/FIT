<%@page import="foxconn.fit.entity.base.EnumGenerateType" %>
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
            $("#Content").load("${ctx}/bi/rtDynamicPrediction/list", {
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
<div>
    <table class="table table-condensed table-hover">
        <thead>
        <tr>
            <th style="width:150px !important;">年</th>
            <th style="width:150px !important;">SBU</th>
            <th>JAN</th>
            <th>FEB</th>
            <th>MAR</th>
            <th>APR</th>
            <th>MAY</th>
            <th>JUN</th>
            <th>JUL</th>
            <th>AUG</th>
            <th>SEP</th>
            <th>OCT</th>
            <th>NOV</th>
            <th>DEC</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${page.result}" var="mapping">
                <tr>
                    <td style="border-right:1px solid #eee;text-align:left;">${mapping[0]}</td>
                    <td style="border-right:1px solid #eee;text-align: left;">${mapping[1]}</td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[2]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[3]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[4]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[5]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[6]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[7]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[8]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[9]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[10]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[11]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[12]}" pattern="#,##0.##"></fmt:formatNumber></td>
                    <td style="border-right:1px solid #eee;text-align: right;"><fmt:formatNumber value="${mapping[13]}" pattern="#,##0.##"></fmt:formatNumber></td>
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