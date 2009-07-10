
<body>

<form action="blank.do" method="post" name="te">
	<table>
	<tr><td>firstname</td><td><input type="text" name="firstname"></td></tr>
	<tr><td>lastname</td><td><input type="text" name="lastname"></td></tr>
	<tr><td><br></td></tr>
	<tr><td colspan=2>
	<input type="button" value="send" onclick="process()">
	<input type="button" value="send regular" onclick="document.forms[0].submit()"><br><br><br>
	</td></tr>
	</table>
</form>

</body>

