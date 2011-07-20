<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Semblog Services</title>
	<link href="<c:url value="/resources/form.css" />" rel="stylesheet"  type="text/css" />		
	<link href="<c:url value="/resources/jqueryui/1.8/themes/base/jquery.ui.all.css" />" rel="stylesheet" type="text/css"/>
	<%-- <link href="<c:url value="/resources/jqueryui/1.8/themes/redmond/jquery-ui-1.8.13.custom.css" />" rel="stylesheet" type="text/css"/> --%>
	<link href="<c:url value="/resources/jqueryui/1.8/themes/flick/jquery-ui-1.8.14.custom.css" />" rel="stylesheet" type="text/css"/>	
</head>
<body>
<h1>Semblog Services</h1>

<div id="tabs">
	<ul>
		<li><a href="#home">Home</a></li>		
		<li><a href="#relatedPosts">Related Blog posts</a></li>
		<li><a href="#similarPosts">Similar Blog posts</a></li>
		<li><a href="#submitPosts">Submit New posts</a></li>
		<li><a href="#register">Register</a></li>
		<li><a href="#about">About Us</a></li>		
    </ul>
    <div id="home">
		<h2>Home</h2>
		<p>
			Welcome to Semblog Repository services. 
		</p>
		
	</div>	
	
	 <div id="relatedPosts">
		<h2>Related Posts</h2>		
		
		<form class="textFormRelated" action="<c:url value="/relatedPosts/param" />" method="post">
			<input id="searchUri" type="text" size="50" />
			<input id="relatedSumbit" type="submit" value="Search" />
		</form>
		<div id="busyDialog"></div>
		<h3>Results</h3>
		<div id="relatedPostsResults">
		</div>
	</div>
	
	<div id="similarPosts">
		<h2>Related Posts</h2>		
		
		<form class="textFormSimilar" action="<c:url value="/similarPosts/param" />" method="post">
			<input id="searchSimilarUri" type="text" size="50" />
			<input id="similarSumbit" type="submit" value="Search" />
		</form>
		
		<h3>Results</h3>
		<div id="similarPostsResults">
		</div>
	</div>
	
	<div id="submitPosts">
		<h2>Submit New Posts</h2>		
		
		<form class="textFormNew" action="<c:url value="/submitPosts/rest" />" method="put">
			<input id="newUri" type="text" size="50" />
			<input id="newSubmit" type="submit" value="Submit" />
		</form>	
		
		<div id="submitPostsResults">
		</div>
	</div>
	
	<div id="register">
		<h2>Register</h2>
		
		<p>Registration page</p>
	</div>
	<div id="about">
		<h2>About</h2>
		<p>
			About Semblog repository services	
		</p>		
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jqueryform/2.8/jquery.form.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jqueryui/1.8/jquery.ui.core.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jqueryui/1.8/jquery.ui.widget.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jqueryui/1.8/jquery.ui.tabs.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/json2.js" />"></script>
<script>
	MvcUtil = {};
	MvcUtil.showSuccessResponse = function (text, element) {
		MvcUtil.showResponse("success", text, element);
	};
	MvcUtil.showErrorResponse = function showErrorResponse(text, element) {
		MvcUtil.showResponse("error", text, element);
	};
	MvcUtil.showResponse = function(type, text, element) {
		var responseElementId = element.attr("id") + "Response";
		var responseElement = $("#" + responseElementId);
		if (responseElement.length == 0) {
			responseElement = $('<span id="' + responseElementId + '" class="' + type + '" style="display:none">' + text + '</span>').insertAfter(element);
		} else {
			responseElement.replaceWith('<span id="' + responseElementId + '" class="' + type + '" style="display:none">' + text + '</span>');
			responseElement = $("#" + responseElementId);
		}
		responseElement.fadeIn("slow");
	};
	MvcUtil.xmlencode = function(xml) {
		//for IE 
		var text;
		if (window.ActiveXObject) {
		    text = xml.xml;
		 }
		// for Mozilla, Firefox, Opera, etc.
		else {
		   text = (new XMLSerializer()).serializeToString(xml);
		}			
		    return text.replace(/\&/g,'&'+'amp;').replace(/</g,'&'+'lt;')
	        .replace(/>/g,'&'+'gt;').replace(/\'/g,'&'+'apos;').replace(/\"/g,'&'+'quot;');
	};
	
	
	


</script>	
<script type="text/javascript">

		
$(document).ready(function() {
	$("#tabs").tabs();	
	
	/* $("form[class=textForm]").submit(function(event) {
		var form = $(this);
		var resultsArea = $("#relatedPostsResults")
		$.ajax({ type: "POST", url: form.attr("action"), data: $("#searchUri").val(), dataType: "json", success: function(json) { MvcUtil.showSuccessResponse(JSON.stringify(json), resultsArea); }, error: function(xhr) { MvcUtil.showErrorResponse(xhr.responseText, resultsArea); }});
		return false;
	}); */
	
	$("form[class=textFormRelated]").submit(function(event) {
		var form = $(this);
		var resultsArea = $("#relatedPostsResults")
		$.ajax({ type: "POST", url: form.attr("action"), data: $("#searchUri").val(), success: function(text) { MvcUtil.showSuccessResponse(text, resultsArea); }, error: function(xhr) { MvcUtil.showErrorResponse(xhr.responseText, resultsArea); }});
		return false;
	});
	
	$("form[class=textFormSimilar]").submit(function(event) {
		var form = $(this);
		var resultsArea = $("#similarPostsResults")
		$.ajax({ type: "POST", url: form.attr("action"), data: $("#searchSimilarUri").val(), success: function(text) { MvcUtil.showSuccessResponse(text, resultsArea); }, error: function(xhr) { MvcUtil.showErrorResponse(xhr.responseText, resultsArea); }});
		return false;
	});
	
	$("form[class=textFormNew]").submit(function(event) {
		var form = $(this);
		var resultsArea = $("#submitPostsResults")
		$.ajax({ type: "PUT", url: form.attr("action"), data: $("#newUri").val(), success: function(text) { MvcUtil.showSuccessResponse(text, resultsArea); }, error: function(xhr) { MvcUtil.showErrorResponse(xhr.responseText, resultsArea); }});
		return false;
	});
	
	$("#readJson").submit(function() {
		var form = $(this);
		var button = form.children(":first");	
		$.ajax({ type: "POST", url: form.attr("action"), data: "{ \"foo\": \"bar\", \"fruit\": \"apple\" }", contentType: "application/json", dataType: "text", success: function(text) { MvcUtil.showSuccessResponse(text, button); }, error: function(xhr) { MvcUtil.showErrorResponse(xhr.responseText, button); }});
		return false;
	});

	$("#writeJson").click(function() {
		var link = $(this);
		$.ajax({ url: this.href, dataType: "json", success: function(json) { MvcUtil.showSuccessResponse(JSON.stringify(json), link); }, error: function(xhr) { MvcUtil.showErrorResponse(xhr.responseText, link); }});					
		return false;
	});

});
</script>
</body>
</html>