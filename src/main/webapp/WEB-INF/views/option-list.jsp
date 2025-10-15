<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>选项管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>
<div class="container">
    <div class="top-bar">
        <h2>选项管理</h2>
        <div>
            <a class="btn" href="${pageContext.request.contextPath}/options?action=create">新增选项</a>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employees">返回员工列表</a>
        </div>
    </div>

    <form class="filter" method="get" action="${pageContext.request.contextPath}/options">
        <div class="filter-row">
            <label>名称：<input type="text" name="name" value="${name}"></label>
            <label>范畴：<input type="text" name="category" value="${category}"></label>
            <button type="submit" class="btn btn-primary">查询</button>
        </div>
    </form>

    <table>
        <thead>
        <tr>
            <th>名称</th>
            <th>范畴</th>
            <th>值</th>
            <th>备注</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="option" items="${pageResult.records}">
            <tr>
                <td>${option.name}</td>
                <td>${option.category}</td>
                <td>${option.value}</td>
                <td>${option.remark}</td>
                <td>
                    <a class="btn btn-link" href="${pageContext.request.contextPath}/options?action=edit&id=${option.id}">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/options" class="inline">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${option.id}">
                        <button type="submit" class="btn btn-link danger" onclick="return confirm('确定删除该选项吗？');">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pageResult.records}">
            <tr>
                <td colspan="5" class="text-center">暂无数据</td>
            </tr>
        </c:if>
        </tbody>
    </table>

    <div class="pagination">
        <c:set var="totalPages" value="${pageResult.totalPages}"/>
        <c:forEach begin="1" end="${totalPages}" var="p">
            <c:url var="pageUrl" value="/options">
                <c:param name="page" value="${p}"/>
                <c:param name="name" value="${name}"/>
                <c:param name="category" value="${category}"/>
            </c:url>
            <a class="page-link ${p == pageResult.page ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
        </c:forEach>
    </div>
</div>
</body>
</html>
