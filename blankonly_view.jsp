<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<%@page import="struts.Utils"%>
<%@page import="action.*"%>
<HTML>
  <HEAD><TITLE>Easy</TITLE>

<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style.css" type="text/css" rel="stylesheet">

<SCRIPT src="global.js" type="text/javascript"></SCRIPT>

<%
	BlankOnlyActionForm form = (BlankOnlyActionForm)Utils.getActionForm(request);
%>

</HEAD>
<BODY>


<STYLE TYPE="TEXT/CSS">
<!--
.gamegrid {font:10pt Arial}
.gamegrid TD {cursor:move}
.sub TD {font:5pt Arial; height:5px}
-->
</STYLE>
<SCRIPT LANGUAGE="JScript">
<!--

var DragEl,tempval,HotEl;

if(document.all){
document.onmousedown=DragStart;
document.onmouseup=DragEnd;
document.onmousemove=DoDrag;
}

function GetTable(tdel){
while(!(tdel.tagName=="TABLE")&&tdel)
	tdel=tdel.parentElement;
return tdel;
}

function ResetHotEl(){
if(!HotEl) return;
HotEl.style.backgroundColor="";
HotEl.style.color="black"
}

function DragStart(){
el=event.srcElement;
if(el.tagName=="TD" && el.className=="mover"){
	if(GetTable(el).className=="gamegrid"){
		DragEl=el;
		//alert(el.id);
		tempval=el.innerText;
		//el.innerText='';
		}
	}
}

function DragEnd(){
el=event.srcElement;
if(el.tagName=="TD" && el.className=="mover"){
	if(GetTable(el).className=="gamegrid"){
		if(DragEl){
		
		  alert(DragEl.id + " -> " + el.id);
		
			DragEl.innerText=el.innerText;
			el.innerText=tempval;
			DragEl.style.backgroundColor="";
			}
		}
	}
else
	if(DragEl&&tempval) DragEl.innerText=tempval;

ResetHotEl();

DragEl=null;
HotEl=null;
}

function DoDrag(){
el=event.srcElement;
if(el.tagName=="TD" && el.className=="mover"){
	if(GetTable(el).className=="gamegrid"){
		ResetHotEl();
		HotEl=el;
		HotEl.style.backgroundColor="navy";
    HotEl.style.color="white";
		}
	}
else 
	ResetHotEl();
}
//-->
</SCRIPT>


<div class=topheadermid><a href="cms.do?action=view">SAMPLE</a>

<DIV class=outertable style="padding:10px">

<form action="cms.do" method="get" enctype="multipart/form-data">

<input type="hidden" id="action" name="action" value="search">

<table width="100%">
  <TR><TD colspan=4 bgcolor="silver"><b>Result</b></TD></TR>
  <tr>
      <td width="100" valign="top">Menu</td>
	  <td valign="top">
	    <DIV onselectstart="return false;">
	  	<div type="text" id="result"/>
	  	</DIV>
	  </td>
  </tr>
</table>
<br>
<!-- 
<input border=0 type=button class="defaultbutton" onclick="subber('view')" value="Commit">
<input border=0 type=button class="defaultbutton" onclick="subber('list')" value="Seiten">
 -->
<input border=0 type=button class="defaultbutton" onclick="subber('generate')" value="Generate">
&nbsp;&nbsp;&nbsp;
 <select name="template"> 
   <option id="templates"></option>
 </select>
<input border=0 type=button class="defaultbutton" onclick="subber('newpage')" value="Neu">

</form>
</DIV>
</BODY></HTML>