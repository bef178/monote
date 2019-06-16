<%@ page language="java"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="moo.UserAuth"%>
<%
    UserAuth userAuth = (UserAuth) session.getAttribute("userAuth");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
</head>
<body>
	User:<%=userAuth.getUser()%>
</body>
</html>
