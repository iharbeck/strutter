<%@page import="strutter.helper.ActionHelper"%>

<HTML>
<HEAD>
	<TITLE>Easy</TITLE>
	<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META name="decorator_name" content="main.jsp">
	<META name="decorator_menu" content="menueueue">
	<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>
<%
    Object form = ActionHelper.getForm();
%>

<div class="topheadermid">SAMPLE2</div>

<DIV class="outertable" style="padding:10px">

  <div type="messages" class="messages"></div>
  <div type="errors" class="errors"></div>

<form action="sample.do" method="post" enctype="multipart/form-data">

<table width="100%">
  <TR><TD colspan="4" bgcolor="silver"><b>Address</b></TD></TR>
  <tr>
      <td>Anrede</td>
      <td>
        <select style="" name="customer.anrede" error="behind">  <!--  tog -->
          <option id="anreden"/>
          <option value="" >ddd</option>
          <option value="1">xErster</option>              <!-- id="anreden" -->
          <option value="2">xZweiter</option>
          <option value="3">xDritter</option>
        </select>

        <!-- multiple size=6 -->
        <input type="checkbox" name="tog" CHECKED="CHECKED" disabled="disabled" value="1">
        <input type="checkbox" name="tog" value="2">
        <input type="checkbox" name="tog" value="3">
        <input type="checkbox" name="tog" value="4">
	  </td>
	  <td rowspan="6" width="310" valign="top">


	  	<table width="100%">
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
		<tr><td>LONG: <span type="resource" id="button.label" /></td></tr>
		<tr><td>SHORT: #R{button.label}</td></tr>
		<tr><td>WRONG: #R{unknown}</td></tr>
		</table>




	  </td>
  </tr>
  
  <tr>
      <td>#R{label.name}</td>
      <td><INPUT type="text" name="customer.firstname" error="behind"/> </td>
  </tr>
  
  
  <tr>
      <td>Lastname</td>
      <td><INPUT type="text" name="customer.lastname" readonly="readonly" error="class"/> </td>
  </tr>
  <tr>
      <td>Street</td>
      <td><INPUT type="text" name="customer.street" error="behind"/> </td>
  </tr>
  <tr>
      <td>City</td>
      <td><INPUT type="text" name="customer.city" error="behind"/> </td>
  </tr>
  <tr>
      <td>Memo</td>
      <td><textarea rows="5" cols="40" name="memo"></textarea></td>
  </tr>
</table>


<table width="100%">
  	<TR><TD colspan="2" bgcolor="silver"><b>Extended</b></TD></TR>
	<tr>
	   <td>Upload <input type="file" name="file"> </td>
	   <td><div type="text" id="filename"></div></td>
	</tr>
</table>
<br>
<input type="submit" onclick="subber('search')" value="OK">
<input type="button" onclick="subber('preview')" value="Next page">
<input type="button" onclick="subber('simulateerrors')" value="Simulate Errors">
<input type="button" onclick="subber('simulatemessage')" value="Simulate Message">

<input type="button" class="delete" value="löschen">

<input type="button" value="$button.label"> <!-- deprecated -->
<input type="button" value="#R{button.label}">

</form>
</DIV>

</BODY>
</HTML>

