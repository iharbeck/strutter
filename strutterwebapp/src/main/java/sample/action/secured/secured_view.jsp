<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<div class=topheadermid>SECURED

<DIV class=outertable style="padding:10px">

  <div type="messages" id="confirm" class="messages"/>
  <div type="errors" id="customer.firstname" class="errors"/>

<form action="secured.do" method="post"> 

<table width="100%">
  <tr>
  	<td>Remoteuser</td>
  	<td><%= request.getRemoteUser() %></td>
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
