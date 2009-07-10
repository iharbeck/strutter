<style>
  body { font-family: sans-serif}
</style>

<body>

<script>
document.write('<iframe style="display:none" name="target" onload="gonegone()"></iframe>');    
</script>

<div style="display: none;
			position: absolute; 
            top: 0; left: 0;
            padding: 3px; padding-bottom: 1px; padding-right: 4px;
            margin: 3px;
            background-color: white; 
            border-style: solid; 
            border-width: 1px; 
            border-color: gray;
            font-size: 10px; vertical-align: top" 
      id="processing">
<img src="img/progress.gif"> Processing Request
</div>

<form action="blank.do" target="target" name="te">
	firstname <input type="text" name="firstname"><br>
	lastname <input type="text" name="lastname"><br>
	<input type="button" value="send" onclick="gogo()">
</form>

</body>


<%= session.isNew() %>
<% session.setMaxInactiveInterval(10); %>
<%= session.getMaxInactiveInterval() %>
<%= System.currentTimeMillis() %>
<script>

<% 	if(session.getAttribute("struttersession") == null)
	{
		session.setAttribute("struttersession", session.getId());
%>
		window.name = '<%= session.getId() %>';
<%
    }  
%>
try{
blbl();
} catch(err) {
}


if(reusingSession())
   document.writeln("dub");

<%
  response.addCookie(new Cookie("strutter", "1"));
%>

if(!hasCookies())
   document.writeln("no cookie");

function reusingSession()
{
	return (window.name == "");
}

function hasCookies()
{
  return !(!document.cookie || document.cookie.indexOf("strutter=") == -1);
}

function getRequestObject() 
{
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	}
	else if (window.createRequest) {
		return window.createRequest();
	}
	else if (window.ActiveXObject) {
		return new ActiveXObject('Microsoft.XMLHTTP');
	}
}

function keepalive() 
{
	var xmlhttp = getRequestObject();

	xmlhttp.open("HEAD", "keepalive.jsp?" + Math.random(), true);
 	xmlhttp.onreadystatechange=function() 
 	{
	  	if (xmlhttp.readyState==4) {
	    //alert("File was last modified on - " + xmlhttp.getResponseHeader("Last-Modified"));
	    //alert(xmlhttp.getAllResponseHeaders());
	  	}
 	}
 	xmlhttp.send(null);
}

// request 5 sec vor session timeout
window.setInterval("keepalive()", <%= session.getMaxInactiveInterval()*1000-5000 %> ); 

function gogo() {
	sub=1;
	document.getElementById("processing").style.display="block";
	document.forms[0].submit();
	//document.close(); 
	//document.open(); 
	//document.write("<p>ingo"); 
}

function gonegone() 
{
	if(!sub)
		return;

	sub=0;

	try 
	{
		// FF
		//var ff = document.getElementsByName("target")[0].contentDocument.documentElement.innerHTML;
		// IE
		//var ff = document["target"].document.documentElement.outerHTML;
	
		var ff = window.frames["target"].document.documentElement.innerHTML;
	
	} catch(err) {
		alert(err);
	}
	
	//alert("got response");
	
	document.close(); 
	document.open(); 
	
	document.write(ff);
	document.close(); 
}

var sub=0;
</script>
