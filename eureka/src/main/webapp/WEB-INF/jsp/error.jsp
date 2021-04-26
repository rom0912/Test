<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<!--   META	 -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Imagetoolbar" content="no" />
<meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">

<title>eureka</title>

<link rel="stylesheet" href="/css/bootstrap.min.css">
<link rel="stylesheet" href="/css/NotosansKR.css">
</head>
<script type="text/javascript" src="/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript">
$(function(){
	var code = "${code}";
	
	if(parent.opener){
		$("#title").html(code);
		var contents = "";
		contents +="<br/>Code      : ${code}";
		contents +="<br/>Message : ${message}";
		
		$("#contents").html(contents);
	}else{
		$("#title").html(code);
		var contents = "";
		
		contents +="<br/>Code      : ${code}";
		contents +="<br/>Message : ${message}";
		
		$("#contents").html(contents);
	}

	var result = "";

	$.each(jQuery.browser, function(i, val) {
			result += i + ":" + val + "\n";
	});
});

</script>
<style>
#wrap {
	position: relative;
	margin: 0 auto;
	padding: 0;
	text-align: left;
	background-color: #FFF;
}

#error-wrap {
	position: absolute;
	left: 40%;
	padding-left: 100px;
	padding-top: 210px;
	margin-top: -20px;
	background-repeat: no-repeat;
	background-image: url(../img/error_img.png);
	background-position: top left;
}

#error-wrap .title {
	font-size: 32px;
	font-weight: bold;
	line-height: 0.81;
	color: #1d2226;
}

#error-wrap .desc {
	margin-top: 16px;
	width: 400px;
	font-size: 16px;
	line-height: 1.63;
	color: #1d2226;
}
</style>
<body>
	<div id="wrap">
		<div id="error-wrap">
			<h1 class="title" id="title"></h1>
			<p class="desc" id="contents"></p>
		</div>
	</div>
</body>
</html>