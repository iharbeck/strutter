<!-- style="display:none" -->
<iframe  style="display:none" width='200' height='200' name='abctarget' onload="processdone()" src="javascript:false;"></iframe>    

<style> body { font-family: sans-serif; } </style>

<div style="display: none; position: absolute; top: 0; left: 0; width: 100%; height: 100%;" id="processing">
	<div id="disser" style="position: absolute; overflow:auto;
	            top: 0; left: 0; height: 100%;
	            background-color: black; opacity:.20;filter: alpha(opacity=20); -moz-opacity: 0.20">
	</div>
	
	<div style="position: absolute; top: 5; left: 5; 
	            background-color: white;
	            border-style: solid; border-width: 2px; border-color: gray;
	            padding-left: 10; padding-right: 10;
	            ">
		<table border=0>            
		 <tr>
		     <td><img src="img/progress.gif" id="ani" style="margin-right: 15px"></td>
		     <td style="font-size: 12px;"><b>Processing... <span id="processtimer">0</span> sec</b><br>
		        <a href="#" style="color: black; text-decoration: none;" onclick="doKill()">[Cancel]</a>
		     </td>
		 </tr>
		</table>
	</div>
</div>

<form style="display:none" name="echo" action="strutter.do?echo" method="POST">
	<textarea name="data"></textarea>
</form>

<script>

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
	  data = window.frames["abctarget"].document.documentElement.innerHTML;
	  text = window.frames["abctarget"].document.body.innerHTML;
	
	  //window.frames["abctarget"].src="javascript:false;";	
	  window.frames["abctarget"].document.close();

	//if(data.search(/java.lang.InterruptedException/) > -1 || text == 'false' ) {
	if(killed == 1 || text == 'false' ) {
		return;
	}
	
	$N("data").value = data;
	
	theform = document.forms["echo"];
	theform.submit();
	
	return;	 
	
//	document.location.href = "blanko.jsp";
//   document.close(); 
//	document.open(); 
//	document.write(data);
//	document.close(); 
	
	} catch(err) {
	    alert(err);
		//return;
	}
}

<!-- prototype style -->
function $(name) {
	return document.getElementById(name);
}

function $N(name) {
	return document.getElementsByName(name)[0];
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
	
	theform.target="abctarget";
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

</script>