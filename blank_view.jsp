
<%@page import="strutter.Utils"%>

<HTML>
  <HEAD>
    <TITLE>BLANK</TITLE>

	<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<LINK href="style/style.css" type="text/css" rel="stylesheet">
	
	<SCRIPT src="script/global.js" type="text/javascript"></SCRIPT>
  </HEAD>
<BODY>

<%
	Object form = (Object)Utils.getActionForm(request);
%>

<DIV class=topheadermid>BLANK</DIV>

<DIV class=outertable style="padding:10px">

<form action="blank.do" method="post" enctype="multipart/form-data">
<input type="hidden" name="action__">

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