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
            $(".table tbody tr").each(function(i){
                if(i%2!=0){
                    $(this).css("background-color","#eceaea");
                }
                $(this).children('td').each(function(){
                    var txt = $(this).text();
                    if (/^\-?[0-9]+(.[0-9]+)?$/.test(txt)){
                        $(this).css("text-align", "right");
                    }
                });
            })
        });

        //用于触发当前点击事件
        function clickPage(page) {
            $("#loading").show();
            $("#PageNo").val(page);
            var date = $("#QDate").val();
            var tableName = $("#QTableName").val();
            var queryCondition=$("#QueryCondition").serialize();
            $("#Content").load("${ctx}/bi/rgpIntegration/list", {
                pageNo: $("#PageNo").val(), pageSize: $("#PageSize").val(),
                orderBy: $("#OrderBy").val(), orderDir: $("#OrderDir").val(),
                date: date, tableName: tableName, poCenter: $("#QpoCenter").val(),queryCondition:decodeURIComponent(queryCondition)
            }, function () {
                $("#loading").fadeOut(1000);
            });
        }

        function refresh() {
            clickPage("1");
        }
        $("#selectAll").click(function(){
            if ($("#selectAll").prop("checked") == true) {
                $(":checkbox[name='columnName']").prop("checked", true);
            } else {
                $(":checkbox[name='columnName']").prop("checked", false);
            }
        });
    </script>
</head>
<body>
<div <c:if test="${hidden eq 1}">hidden="hidden"</c:if>
        <c:choose>
            <c:when test="${fn:length(columns) gt 30}">style="width:500%;" </c:when>
            <c:when test="${fn:length(columns) gt 25}">style="width:400%;" </c:when>
            <c:when test="${fn:length(columns) gt 20}">style="width:300%;" </c:when>
            <c:otherwise>style="width:200%;"</c:otherwise>
        </c:choose>>
    <table class="table table-condensed table-hover">
        <thead>
        <tr>
            <th>序号</th>
            <c:forEach items="${columns }" var="column" varStatus="status">
                <th>${column.comments }</th>
            </c:forEach>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.result}" var="mapping">
            <tr>
                <c:forEach var="i" begin="0" end="${fn:length(mapping)-index}" varStatus="status">
                    <c:choose>
                        <c:when test="${status.index eq 0}">
                            <td style="white-space: nowrap;border-right:1px solid #eee;">
                                <input name="ID" type="checkbox" value="${mapping[i]}"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td style="border-right:1px solid #eee;">${mapping[i]}</td>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<div id="Fenye" <c:if test="${hidden eq 1}">hidden="hidden"</c:if>></div>
<div <c:if test="${hidden eq 2}">hidden="hidden"</c:if>>
    <table>
        <thead>
            <tr>
                <td colspan="5">
                    <input type="checkbox" checked style="margin-left: 10px" id="selectAll">全選</input>
                </td>
            </tr>
        </thead>
        <tbody>
    <c:forEach items="${columns }" var="column" varStatus="status">
        <c:if test="${status.index%5 eq 0}">
            <tr>
        </c:if>
        <td>
            <input style="margin-left: 10px" type="checkbox" name="columnName" checked value="${column.columnName }">${column.comments }
        </td>
            </c:forEach>
        </tbody>
    </table>
</div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}"/>
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}"/>
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}"/>
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}"/>
</body>
</html>