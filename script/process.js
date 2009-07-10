//style="display:none"
document.write('<iframe  width="200" height="200" name="target" onload="processdone()" src="javascript:false;"></iframe>');    
document.write('<style> body { font-family: sans-serif; } </style>');

document.write('<div style="display: none; position: absolute; top: 0; left: 0; width: 100%; height: 100%;" id="processing">');
document.write('	<div id="disser" style="position: absolute; overflow:auto;');
document.write('	            top: 0; left: 0; height: 100%;');
document.write('	            background-color: black; opacity:.20;filter: alpha(opacity=20); -moz-opacity: 0.20">');
document.write('	</div>');
	
document.write('	<div style="position: absolute; top: 5; left: 5; ');
document.write('	            background-color: white;');
document.write('	            border-style: solid; border-width: 2px; border-color: gray;');
document.write('	            padding-left: 10; padding-right: 10;');
document.write('	            ">');
document.write('		<table border=0>');            
document.write('		 <tr>');
document.write('		     <td><img src="img/progress.gif" id="ani" style="margin-right: 15px"></td>');
document.write('		     <td style="font-size: 12px;"><b>Processing... <span id="processtimer">0</span> sec</b><br>');
document.write('		        <a href="#" style="color: black; text-decoration: none;" onclick="doKill()">[Cancel]</a>');
document.write('		     </td>');
document.write('		 </tr>');
document.write('		</table>');
document.write('	</div>');
document.write('</div>');
	
function processdone() 
{
	var data = '';	
	var text = 'false';

	$("processing").style.display="none";
	
	try {
	  data = window.frames["target"].document.documentElement.innerHTML;
	  text = window.frames["target"].document.body.innerHTML;
	} catch(err) {
		return;
	}
	
	window.frames["target"].src="javascript:false;";	

	if(data.search(/java.lang.InterruptedException/) > -1 || text == 'false' ) {
		return;
	}	 
	
	document.close(); 
	document.open(); 
	alert(data);
	document.write(data);
	document.close(); 
	
}

<!-- prototype style -->
function $(name) {
	return document.getElementById(name);
}

var counter=0;
var conterinterval;

function processtime() 
{
	$("processtimer").innerHTML = counter++;
}

function process(theform) 
{
	if(!theform)
		theform = document.forms[0];

	counter=0;
	processtime();
	counterinterval = setInterval('processtime()',1000);
	
 	$("ani").src = 'img/progress.gif';			
	$("processing").style.display="block";
	$("disser").style.width=document.body.clientWidth;
	
	theform.target="target";
	theform.submit();
}

<!-- AJAX stuff -->
function doKill() 
{
	$("ani").src = 'img/cancle.gif';
	sendRequestGET("strutter.do?strutter=killer"); 		
	clearInterval(counterinterval);
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

