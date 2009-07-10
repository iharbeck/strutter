function subbersingleclick(button, action, params) {
	button.disabled = true;
	subberparameter(action, params);
}

function subberparameter(action, params) 
{
  for (var i in params) 
  {
    var input = $N(i) || document.getElementById(i) ;
    
    if(!input)
    {
		input = document.createElement('input');
  
	    input.setAttribute('id', i);
	    input.setAttribute('name', i);
	    
	    input.setAttribute('type', 'hidden');
	    document.forms[0].appendChild(input);
	}
    input.setAttribute('value', params[i]);
  }

  subber(action);
}

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

function setAction(action) { $N('##actionname##').value=action; }

function elementform(obj) {
	var parent = obj.getParent();
	if(parent.getTag() == "form") {
		return parent;
	} else {
		return this.elementform(parent);
	}
}

function getForm(element)
{
	if(isform(element))
		return element;

	while(element.parentNode)
	{		
		element = element.parentNode;
		
		if(isform(element))
			return element;
	}
	return null;
}

function isform(element) {
	return (element.nodeName && element.nodeName.toUpperCase()=="FORM");
}

function submit(theform, action) {
	var unknown;
	
	if(!theform)
		theform = document.forms[0];
	else
		theform = getForm(theform);

	if(action != 'undefined' && action != unknown && !action)
		setAction(action);
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

//Document of Frame
function $DWR(name) {
    return dwr.util.getValue(name);
}

function processtime()
{
	$("processtimer").innerHTML = counter++;
}

function process(theform)
{
	if(!theform)
		theform = document.forms[0];
	else
		theform = getForm(theform);

	killed = 0;
	counter=0;
	processtime();
	counterinterval = setInterval('processtime()',1000);

 	$("ani").src = 'strutter.do?img&name=img/progress.gif';
	$("processing").style.display="block";
	$("disser").style.width=document.body.clientWidth;

	theform.target="struttertarget";
	theform.submit();
}

var killed = 0;

<!-- AJAX stuff -->
function doKill()
{
	$("ani").src = 'strutter.do?img&name=img/progress.gif';
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

	xmlhttp.open("GET", noCache(url), true);
 	xmlhttp.send(null);
}


function keepalive()
{
 	sendRequestGET("strutter.do?keepalive");
}

function addkeepalive() {
	// request 10 sec vor session timeout
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
}

function ajaxinclude(target, url) 
{
	var page_request = getRequestObject();
	
	page_request.open('GET', noCache(url), false);
	page_request.send(null);

	if (page_request.status==200 || window.location.href.indexOf("http")==-1) {
		
		$(target).innerHTML = page_request.responseText;
		evalScript( $(target).innerHTML );
	}
}

function evalScript(scripts)
{	try
	{	if(scripts != '')	
		{	var script = "";
			scripts = scripts.replace(/<script[^>]*>([\s\S]*?)<\/script>/gi, function() {
	       	                         if (scripts !== null) script += arguments[1] + '\n';
 	        	                        return '';});
			if(script) (window.execScript) ? window.execScript(script) : window.setTimeout(script, 0);
		}
		return false;
	}
	catch(e)
	{	alert(e)
	}
}

function noCache(uri) 
{
	return uri.concat(/\?/.test(uri)?"&":"?","noCache=",(new Date).getTime(),".",Math.random()*1234567)
}

