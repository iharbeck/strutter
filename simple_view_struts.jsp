<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style.css" type="text/css" rel="stylesheet">
<SCRIPT src="global.js" type="text/javascript"></SCRIPT>
</HEAD>
<BODY>

<div class=topheadermid>SIMPLE

<DIV class=outertable style="padding:10px">

 [
        <html:errors property="ff"/>
        
        <div type="error" id="ff" class="info"/>
        
        <div type="message" id="eintext" class="info"/>
 ]

[

<logic: messagesPresent message="true">
  <html:messages id="msg" message="false">
    <div class="errormessages">
      <bean:write name="msg"/>
    </div>
  </html:messages>
</logic:messagesPresent>
]

<br>
Label: <bean:message key="ingo"/>
<br>

<form action="simple.do" method="post" enctype="multipart/form-data"> 

<input type="hidden" id="action" name="action" value="search">

<table width="100%">
  <TR><TD colspan=4 bgcolor="silver"><b>Address</b></TD></TR>
  <tr>
      <td>Anrede</td>
      <td>
        <select name="customer.anrede">  <!--  tog -->
          <option value="" id="anreden" >ddd</option>
          <option value="1">xErster</option>              <!-- id="anreden" -->
          <option value="2">xZweiter</option>
          <option value="3">xDritter</option>
        </select> 
       
        <!-- multiple size=6 -->
        <input type="checkbox" name="tog" value="1">
        <input type="checkbox" name="tog" value="2">
        <input type="checkbox" name="tog" value="3">
        <input type="checkbox" name="tog" value="4">
	  </td>
	  <td rowspan=6 width="310" valign="top">
	  	<div type="text" class="info" id="memo"/>
	  </td>
  </tr>
  <tr>
      <td>Firstname</td>
      <td><INPUT type="text" name="customer.firstname" /> <html:errors property="number" /></td>
  </tr>
  <tr>
      <td>Lastname</td>
      <td><INPUT type="text" name="customer.lastname" /> <html:errors property="number" /></td>
  </tr>
  <tr>
      <td>Street</td>
      <td><INPUT type="text" name="customer.street" /> <html:errors property="number" /></td>
  </tr>
  <tr>
      <td>City</td>
      <td><INPUT type="text" name="customer.city" /> <html:errors property="number" /></td>
  </tr>
  <tr>   
      <td>Memo</td>
      <td><textarea rows="5" cols="40" name="memo"></textarea></td>
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
<input border=0 type=button class="defaultbutton" onclick="subber('view')" value="Continue">


</form>
</DIV>
</BODY></HTML>