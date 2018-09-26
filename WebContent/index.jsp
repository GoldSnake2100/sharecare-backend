<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>XinQiang</title>
</head>
<body>
	<%
		Date d = new Date();
	%>
	<h1>
		<%=d.toString()%>
		<br>
		ShareCare server is running well!		
	</h1>
</body>
</html>