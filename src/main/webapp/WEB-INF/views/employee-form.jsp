<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>员工信息</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    <script>const contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/static/js/employee.js" defer></script>
</head>
<body>
<div class="container narrow">
    <h2>员工信息</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/employees">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" value="${employee.id}">
        <div class="form-group">
            <label>编号</label>
            <input type="text" id="employeeCode" name="employeeCode" value="${employee.employeeCode}" readonly required>
        </div>
        <div class="form-group">
            <label>姓名</label>
            <input type="text" name="name" value="${employee.name}" required>
        </div>
        <div class="form-group">
            <label>性别</label>
            <label><input type="radio" name="gender" value="男" ${employee.gender == '男' ? 'checked' : ''} required> 男</label>
            <label><input type="radio" name="gender" value="女" ${employee.gender == '女' ? 'checked' : ''} required> 女</label>
        </div>
        <div class="form-group">
            <label>年龄</label>
            <input type="number" name="age" min="18" max="65" value="${employee.age}" required>
        </div>
        <div class="form-group">
            <label>手机号</label>
            <input type="tel" name="phone" pattern="^1[3-9]\\d{9}$" title="请输入合法的手机号" value="${employee.phone}" required>
        </div>
        <div class="form-group">
            <label>岗位</label>
            <select name="jobOptionId" id="jobOptionId" required>
                <option value="">请选择</option>
                <c:forEach var="option" items="${jobOptions}">
                    <option value="${option.id}" data-code="${option.value}" ${option.id == employee.jobOptionId ? 'selected' : ''}>${option.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>入职时间</label>
            <input type="month" name="hireDate" id="hireDate" value="${employee.hireDate != null ? fn:substring(employee.hireDate,0,7) : ''}" required>
        </div>
        <div class="form-group">
            <label>薪资</label>
            <input type="number" name="salary" step="0.01" min="0" value="${employee.salary}" required>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">保存</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/employees">返回</a>
        </div>
    </form>
</div>
</body>
</html>
