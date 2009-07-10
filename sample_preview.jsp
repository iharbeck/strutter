<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style.css" type="text/css" rel="stylesheet">
<SCRIPT src="global.js" type="text/javascript"></SCRIPT>
</HEAD>
<BODY>

<div class=topheadermid>SAMPLE

<DIV class=outertable style="padding:10px">

<form action="sample.do" method="post" enctype="multipart/form-data">

<input type="hidden" id="action" name="action" value="search">

<table width="100%">
  <TR><TD colspan=4 bgcolor="silver"><b>Address</b></TD></TR>
  <tr>
      <td>Anrede</td>
      <td>
        <div type="text" id="customer.anrede"/> 
	  </td>
	  <td rowspan=6 width="310" valign="top">
	  	<div type="text" class="info" id="memo"/>
	  </td>
  </tr>
  <tr>
      <td>Firstname</td>
      <td><div type="text" id="customer.firstname" /></td>
  </tr>
  <tr>
      <td>Lastname</td>
      <td><div type="text" id="customer.lastname" /></td>
  </tr>
  <tr>
      <td>Street</td>
      <td><div type="text" id="customer.street" /></td>
  </tr>
  <tr>
      <td>City</td>
      <td><div type="text" id="customer.city" /></td>
  </tr>
</table>

<table width="100%">
  <TR><TD colspan=2 bgcolor="silver"><b>Details</b></TD></TR>
  <tr><td>
	<fieldset>
	<legend>Radiobutton</legend>
	<table>
	<tr><td>Radio 1</td><td><input type="radio" name="rposition" value="1"></td></tr>
	<tr><td>Radio 2</td><td><input type="radio" name="rposition" value="2"></td></tr>
	<tr><td>Radio 3</td><td><input type="radio" name="rposition" value="3"></td></tr>
	<tr><td>Radio 4</td><td><input type="radio" name="rposition" value="4"></td></tr>
	</table>
	</fieldset> 
</td><td>
	<fieldset>
	<legend>Checkboxen</legend>
	<table>
	<tr><td>ISO9000</td><td><input type="checkbox" name="c1iso" value="1"></td></tr>
	<tr><td>ISO9001</td><td><input type="checkbox" name="c2iso" value="2"></td></tr>
	<tr><td>ISO9002</td><td><input type="checkbox" name="c3iso" value="3"></td></tr>
	<tr><td>ISO9003</td><td><input type="checkbox" name="c4iso" value="4"></td></tr>
	</table>
	</fieldset> 
</td></tr>
</table>

<table width="100%">
  	<TR><TD colspan=2 bgcolor="silver"><b>Extended</b></TD></TR>
	<tr>
	   <td>Upload <input type="file" name="file"> </td>
	   <td><div type="text" id="filename"/></td>
	</tr>
</table>
<br>
<input border=0 type=button class="defaultbutton" onclick="subber('view')" value="back">


</form>
</DIV>
</BODY></HTML>