<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

    <META name="decorator_name" content="main.jsp">
    <META name="decorator_menu" content="menueueue">
    
<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<div class=topheadermid>PREVIEW</div>

<DIV class=outertable style="padding:10px">

<form action="sample.do" method="post" enctype="multipart/form-data">

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
      <td><span type="resource" id="label.name"/></td>
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

<br>
<input type="button" onclick="subber('search')" value="Back">

</form>
</DIV>
</BODY></HTML>
