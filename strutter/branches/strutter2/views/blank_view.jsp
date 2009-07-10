<%@page import="strutter.Utils"%>

<%@page import="strutter.optional.helper.ActionHelper"%>
<HTML>
  <HEAD>
    <TITLE>BLANK</TITLE>

	<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<LINK href="style/style.css" type="text/css" rel="stylesheet">
  </HEAD>
<BODY>

<%
Object form = (Object)ActionHelper.getForm();
%>

<DIV class=topheadermid>BLANK</DIV>

<DIV class=outertable style="padding:10px">

<form action="blank.do" method="post" enctype="multipart/form-data">

<table width="100%">
  <tr>
      <td class="formlabel">Field 1</td><td><input type="text" name="field1"></td>
  </tr>
</table>

<p>

<input type=button class="defaultbutton" onclick="subber('view')" value="Commit">

</form>
</DIV>
</BODY>
</HTML>