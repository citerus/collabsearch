<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Collaborative Search Login</title>
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
	
	.outer-login-div {
		/* background-image: -moz-linear-gradient(center top , #66BBDD 11%, #E2F0F5 100%) !important; */
	}
	
	.inner-login-box {
		-webkit-border-radius: 10px;
		-moz-border-radius: 10px;
		border-radius: 10px 10px 10px 10px;
		box-shadow: 0px 0px 1px 0px rgba(0, 0, 0, 0.5);
		-moz-border-bottom-colors: none;
	    -moz-border-left-colors: none;
	    -moz-border-right-colors: none;
	    -moz-border-top-colors: none;
	    border-image: none;
	    border-style: none solid;
    	border-width: medium 1px;
    	background-image: url("../mytheme/transp_background.png");
/*     	background: none repeat scroll 0 0 transparent; */
	}
	
	.outer-login-box {
		border-radius: 10px 10px 10px 10px;
		background: none repeat scroll 0 0 white;
		width: 30%;
	}
	
	form {
		background: none repeat scroll 0 0 transparent;
	}
</style>
</head>
<body>
	<div align="center" class="outer-login-div">
	<h1>Missing People</h1>
	<h2>Inloggning för admininstrationsgränssnitt</h2>
		<div class="outer-login-box">
			<div class="inner-login-box">
				<form action="/collabsearch-adminui/j_spring_security_check" method="post">
				<table>
					<tr>
						<td>Användare</td>
						<td><input name="j_username" style="width: 100%"></td>
					</tr>
					<tr>
						<td>Lösenord</td>
						<td><input type="password" name="j_password" style="width: 100%; "/></td>
					</tr>
					<tr>
						<td></td>
						<td><div align="right"><input type="submit" value="Logga in"></div></td>
					</tr>
				</table>
				</form>
			</div>
		</div>
	</div>
</body>
</html>