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
            $("#checkboxQX").click(function () {
                if($("#checkboxQX").is(':checked')){
                    $("input[name='checkboxID']").prop("checked",true);
                }else{
                    $("input[name='checkboxID']").prop("checked",false);
                }
            })
        });

        //用于触发当前点击事件
        function clickPage(page) {
            $("#loading").show();
            $("#PageNo").val(page);
            var queryCondition=$("#QueryCondition").serialize();
            var date = $("#QDate").val();
            $("#Content").load("${ctx}/bi/plOfflineDataSupplement/list", {
                pageNo: $("#PageNo").val(), pageSize: $("#PageSize").val(),
                orderBy: $("#OrderBy").val(), orderDir: $("#OrderDir").val(),
                type: $("#tableType :checked").val(),queryCondition:decodeURIComponent(queryCondition)
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
<div <c:if test="${tableType eq 'cux_pl_fit_inter_manual'}">style="width:170%;"</c:if>>
    <table class="table table-condensed table-hover">
        <thead>
        <tr>
            <th><input id="checkboxQX" style="height:30px !important;margin-left: 6px;" type="checkbox"/></th>
            <c:forEach items="${columns }" var="column" varStatus="status">
                <th>${column}</th>
            </c:forEach>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.result}" var="mapping">
            <tr>
                <c:forEach var="i" begin="0" end="${fn:length(mapping)-index}" varStatus="status">
                    <c:choose>
                        <c:when test="${status.index eq 0}">
                            <td>
                                <input name="checkboxID" style="height:30px !important;margin-left: 10px;" type="checkbox" value="${mapping[i]}"/>
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
<div id="Fenye" <c:if test="${tableType eq 'cux_pl_fit_inter_manual'}">style="width:150%;"</c:if>></div>
<input type="hidden" id="PageNo" value="${fn:escapeXml(page.pageNo)}"/>
<input type="hidden" id="PageSize" value="${fn:escapeXml(page.pageSize)}"/>
<input type="hidden" id="OrderBy" value="${fn:escapeXml(page.orderBy)}"/>
<input type="hidden" id="OrderDir" value="${fn:escapeXml(page.orderDir)}"/>
</body>
</html>