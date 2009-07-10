<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@page import="struts.Utils"%>
<%@page import="sample.action.*"%>

<HTML>
  <HEAD><TITLE>Easy</TITLE>

<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style.css" type="text/css" rel="stylesheet">

<SCRIPT src="global.js" type="text/javascript"></SCRIPT>

</HEAD>
<BODY>

<%
	BlankActionForm form = (BlankActionForm)Utils.getActionForm(request);
%>

<div class=topheadermid>BLANK

<DIV class=outertable style="padding:10px">

<form action="blank.do" method="post" enctype="multipart/form-data">

<input type="hidden" id="action" name="action" value="search">

<table width="100%">
  <TR><TD colspan=4 bgcolor="silver"><b>Address</b></TD></TR>
  <tr>
      <td>Value</td>
      <td>
        <div type="text" id="customer.anrede"/> 
	  </td>
	  <td rowspan=6 width="310" valign="top">
	  	<div type="text" class="info" id="memo"/>
	  </td>
  </tr>

</table>

<input border=0 type=button class="defaultbutton" onclick="subber('view')" value="Commit">

</form>
</DIV>
</BODY></HTML>