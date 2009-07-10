
function subber(action) {
    setAction(action);
    submit();
}

function subberconfirmed(action, message) {
    setAction(action);
	if(confirm(message))
	   submit();
}

function subbernowait(action) {
    setAction(action);
    process();
}

function setAction(action) { $N('action').value=action; }

function submit(theform) {
	if(!theform)
		theform = document.forms[0];
    theform.submit();
}

var counter=-1;
var counterinterval=0;

function processdone()
{
	if(counter == -1)
		return;

	var data = '';
	var text = 'false';

	if(counterinterval)
		clearInterval(counterinterval);

	try {
		$("processing").style.display="none";
	} catch(err) {
	}

	try {
		data = $D("struttertarget").documentElement.innerHTML;
		//text = $D("struttertarget").body.innerHTML;

		//window.frames["struttertarget"].src="javascript:false;";
		//$D("struttertarget").close();

		//if(data.search(/java.lang.InterruptedException/) > -1 || text == 'false' ) {
		if(killed == 1 || text == 'false' ) {
			return;
		}

		$N("struttercache").value = data;

		submit("strutterecho");

		return;

	} catch(err) {
	    alert(err);
	}
}

<!-- prototype style -->
// Element by ID
function $(name) {
	return document.getElementById(name);
}

// First Element by Name
function $N(name) {
	return document.getElementsByName(name)[0];
}

// Document of Frame
function $D(name) {
    return window.frames[name].document
}

function processtime()
{
	$("processtimer").innerHTML = counter++;
}

function process(theform)
{
	if(!theform)
		theform = document.forms[0];

	killed = 0;
	counter=0;
	processtime();
	counterinterval = setInterval('processtime()',1000);

 	$("ani").src = 'img/progress.gif';
	$("processing").style.display="block";
	$("disser").style.width=document.body.clientWidth;

	theform.target="struttertarget";
	theform.submit();
}

var killed = 0;

<!-- AJAX stuff -->
function doKill()
{
	$("ani").src = 'img/cancle.gif';
	sendRequestGET("strutter.do?killer");
	clearInterval(counterinterval);
	killed = 1;
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

function sendRequestGET(url)
{
	var xmlhttp = getRequestObject();

	xmlhttp.open("GET", url, true);
 	xmlhttp.send(null);
}


function keepalive()
{
	var xmlhttp = getRequestObject();

	xmlhttp.open("HEAD", "strutter.do?keepalive&" + Math.random(), true);
 	xmlhttp.onreadystatechange=function()
 	{
	  	if (xmlhttp.readyState==4) {
	    //alert("File was last modified on - " + xmlhttp.getResponseHeader("Last-Modified"));
	    //alert(xmlhttp.getAllResponseHeaders());
	  	}
 	}
 	xmlhttp.send(null);
}

function addkeepalive() {
	// request 5 sec vor session timeout
	window.setInterval("keepalive()", ##sessiontimeout## );
}

function setFocus() {
  if (document.forms.length > 0) {
    var field = document.forms[0];
    for (i = 0; i < field.length; i++) {
      if ((field.elements[i].type == "text") || (field.elements[i].type == "textarea")
      || (field.elements[i].type.toString().charAt(0) == "s")) {
        document.forms[0].elements[i].focus();
        break;
      }
    }
  }
}

function isTopWindow() { return (top.window == window); }

function setWindowName(windowname) {
    if(isTopWindow())
        window.name = windowname;
}

function reusingSession() {
	return (isTopWindow() && (window.name == ""));
}

function hasCookies() {
  	return (document.cookie || document.cookie.indexOf("strutter=") >= 0);
}

function strutterloaded() {
	// NEW EVENTS
	try { if(reusingSession()) { onReusingSession(); } } catch(err) {}
	try { if(!hasCookies()) { onNoCookies(); } } catch(err) {}
	addkeepalive();
}

