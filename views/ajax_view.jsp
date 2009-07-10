<%@page import="strutter.helper.ActionHelper"%>

<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<div class=topheadermid>AJAX SAMPLE</div>


  <div type="messages" class="messages"/>
  <div type="errors" class="errors"/>

<form action="ajax.do" method="post" enctype="multipart/form-data">

<div id='subpage' style='border:1px solid red; padding: 5px'></div>

<p>

<input type=button onclick="ajaxinclude('subpage', 'ajax.do?action=template')" value="OK">

</form>


</BODY></HTML>
