<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login error</title>
<link href="/collabsearch-adminui/VAADIN/themes/mytheme/favicon.ico" type="image/vnd.microsoft.icon" rel="shortcut icon">
<link href="/collabsearch-adminui/VAADIN/themes/mytheme/favicon.ico" type="image/vnd.microsoft.icon" rel="icon">
<style type="text/css">
	html, body {
		height:100%;
		margin:0;
		color: #0000AB;
	}
	
	body {
		font-family: 'Helvetica',Helvetica,sans-serif;

		background-image: linear-gradient(top, #66BBDD 11%, #E2F0F5 100%);
		background-image: -o-linear-gradient(top, #66BBDD 11%, #E2F0F5 100%);
		background-image: -moz-linear-gradient(top, #66BBDD 11%, #E2F0F5 100%) !important;
		background-image: -webkit-linear-gradient(top, #66BBDD 11%, #E2F0F5 100%);
		background-image: -ms-linear-gradient(top, #66BBDD 11%, #E2F0F5 100%);
		background-image: -webkit-gradient(
			linear,
			left top,
			left bottom,
			color-stop(0.11, #66BBDD),
			color-stop(1, #E2F0F5)
		);
	}
	
	.login-error {
		color: red;
	}
</style>
</head>
<body>
	<h1 align="center" class="login-error">Fel vid inloggning</h1>
	<div align="center"><a href="<c:url value="login"/>" > Tillbaka </a></div>
</body>
</html>