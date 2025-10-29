<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>员工信息管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>
<div class="container">
    <div class="top-bar">
        <h2>员工信息管理</h2>
        <div>
            <a class="btn" href="${pageContext.request.contextPath}/employees?action=create">新增员工</a>
            <a class="btn" href="${pageContext.request.contextPath}/options">岗位选项管理</a>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/logout">退出</a>
        </div>
    </div>

    <form class="filter" method="get" action="${pageContext.request.contextPath}/employees">
        <div class="filter-row">
            <label>姓名：<input type="text" name="name" value="${fn:escapeXml(name)}"></label>
            <label>手机号：<input type="text" name="phone" value="${fn:escapeXml(phone)}"></label>
            <label>性别：
                <select name="gender">
                    <option value="">全部</option>
                    <option value="男" ${gender == '男' ? 'selected' : ''}>男</option>
                    <option value="女" ${gender == '女' ? 'selected' : ''}>女</option>
                </select>
            </label>
            <label>岗位：
                <select name="jobOptionId">
                    <option value="">全部</option>
                    <c:forEach var="option" items="${jobOptions}">
                        <option value="${option.id}" ${option.id == jobOptionId ? 'selected' : ''}>${option.name}</option>
                    </c:forEach>
                </select>
            </label>
        </div>
        <div class="filter-row">
            <label>入职时间：
                <input type="month" name="hireDateFrom" value="${hireDateFrom}"> -
                <input type="month" name="hireDateTo" value="${hireDateTo}">
            </label>
            <label>薪资范围：
                <input type="number" step="0.01" name="salaryMin" value="${salaryMin}"> -
                <input type="number" step="0.01" name="salaryMax" value="${salaryMax}">
            </label>
            <button type="submit" class="btn btn-primary">查询</button>
        </div>
    </form>

    <table>
        <thead>
        <tr>
            <th>编号</th>
            <th>姓名</th>
            <th>性别</th>
            <th>年龄</th>
            <th>手机号</th>
            <th>入职时间</th>
            <th>岗位</th>
            <th>薪资</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="employee" items="${pageResult.records}">
            <tr>
                <td>${employee.employeeCode}</td>
                <td>${employee.name}</td>
                <td>${employee.gender}</td>
                <td>${employee.age}</td>
                <td>${employee.phone}</td>
                <td><c:out value="${employee.hireDate}"/></td>
                <td>${employee.jobName}</td>
                <td>${employee.salary}</td>
                <td>
                    <a class="btn btn-link" href="${pageContext.request.contextPath}/employees?action=edit&id=${employee.id}">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/employees" class="inline">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${employee.id}">
                        <button type="submit" class="btn btn-link danger" onclick="return confirm('确定删除该员工吗？');">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pageResult.records}">
            <tr>
                <td colspan="9" class="text-center">暂无数据</td>
            </tr>
        </c:if>
        </tbody>
    </table>

    <div class="pagination">
        <c:set var="totalPages" value="${pageResult.totalPages}"/>
        <c:forEach begin="1" end="${totalPages}" var="p">
            <c:url var="pageUrl" value="/employees">
                <c:param name="page" value="${p}"/>
                <c:param name="name" value="${name}"/>
                <c:param name="phone" value="${phone}"/>
                <c:param name="gender" value="${gender}"/>
                <c:param name="jobOptionId" value="${jobOptionId}"/>
                <c:param name="hireDateFrom" value="${hireDateFrom}"/>
                <c:param name="hireDateTo" value="${hireDateTo}"/>
                <c:param name="salaryMin" value="${salaryMin}"/>
                <c:param name="salaryMax" value="${salaryMax}"/>
            </c:url>
            <a class="page-link ${p == pageResult.page ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
        </c:forEach>
    </div>
</div>
</body>
</html>
