<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>选项信息</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body>
<div class="container narrow">
    <h2>选项信息</h2>
    <form method="post" action="${pageContext.request.contextPath}/options">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" value="${optionItem.id}">
        <div class="form-group">
            <label>名称</label>
            <input type="text" name="name" value="${optionItem.name}" required>
        </div>
        <div class="form-group">
            <label>范畴</label>
            <input type="text" name="category" value="${optionItem.category}" required>
        </div>
        <div class="form-group">
            <label>值</label>
            <input type="text" name="value" value="${optionItem.value}" required>
        </div>
        <div class="form-group">
            <label>备注</label>
            <textarea name="remark" rows="3">${optionItem.remark}</textarea>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">保存</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/options">返回</a>
        </div>
    </form>
</div>
</body>
</html>
