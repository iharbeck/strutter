<%@page import="strutter.helper.ActionHelper"%>

<HTML><HEAD><TITLE>Easy</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<div class=topheadermid>AJAX SAMPLE FRAME</div>


  <div type="messages" class="messages"/>
  <div type="errors" class="errors"/>

<form action="ajaxmain.do" name="mfra" method="post" enctype="multipart/form-data">


<p>

<input type=button onclick="ajaxinclude('subpage', 'ajaxframe.do')" value="OK">

</form>

<form name="xfra" action="ajaxframe.do">
<div id='subpage' style='border:1px solid gray; padding: 5px; width: 400px; height: 200px'></div>
</form>
</BODY></HTML>
