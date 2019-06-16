<%@ page language="java"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.Date"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Hello</title>
</head>
<body>
	<p>
		This is a jsp
		<%=getClass().getName()%>.
	</p>
	<p>
		Working directory:
		<%=System.getProperty("user.dir")%>.
	</p>
	<p><%=new Date()%></p>

	<jsp:include page="/fragments/login-form.html"></jsp:include>
</body>
</html>
