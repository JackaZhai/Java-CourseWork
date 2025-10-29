<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员登陆</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>
<div class="container narrow">
    <h2>管理员登陆</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username">账号</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary">登陆</button>
    </form>
</div>
</body>
</html>
