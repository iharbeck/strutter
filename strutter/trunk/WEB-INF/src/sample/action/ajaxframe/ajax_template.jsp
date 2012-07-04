<%@page import="java.util.Date"%>
<html>
<body>
<div style="color:red">
<form action="ajaxframe.do" name="fra" method="get">
<table style="border:1px solid gray">
<tr><td>Requested:</td><td><%= new Date() %> DD</td></tr>
<tr><td>Firstname:</td><td>Tom</td></tr>
<tr><td>Lastname:</td><td>Tailer</td></tr>
<tr><td><input type="button" onclick="alert(forms.length);forms['xfra'].submit()"></td></tr>
</table>
</form>
</div>
</body>
</html>