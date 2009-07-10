<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<div class=topheadermid>Interceptor <span style="font-size: 9px">(please check log)</span></div>

<DIV class=outertable style="padding:10px">

  <div type="messages" id="confirm" class="messages"/>
  <div type="errors" id="customer.firstname" class="errors"/>

<form action="interceptor.do" method="post">

<table width="100%">
  <TR><TD colspan=4 bgcolor="silver"><b>Address</b></TD></TR>
  <tr>
      <td>Anrede</td>
      <td>
        <select name="customer.anrede" error="behind">
          <option id="anreden"/>
        </select>
	  </td>
	  <td rowspan=6 width="310" valign="top">
	  	<div type="text" class="info" id="memo"/>
	  </td>
  </tr>
  <tr>
      <td>Firstname</td>
      <td><INPUT type="text" name="customer.firstname" error="before">
      </td>
  </tr>
  <tr>
      <td>Lastname</td>
      <td><INPUT type="text" name="customer.lastname" error="before"></td>
  </tr>
  <tr>
      <td>Street</td>
      <td><INPUT type="text" name="customer.street" error="class"></td>
  </tr>
  <tr>
      <td>City</td>
      <td><INPUT type="text" name="customer.city" ></td>
  </tr>
  <tr>
      <td>Memo</td>
      <td><textarea rows="5" cols="40" name="memo" error="behind"></textarea></td>
  </tr>
</table>

<br>
<input type="button" onclick="subber('view')" value="Continue">


</form>
</DIV>
</BODY></HTML>
