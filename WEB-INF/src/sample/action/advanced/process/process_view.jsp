<LINK href="style/style.css" type="text/css" rel="stylesheet">

<body>

<form action="process.do" method="post" name="te">
	<table>
	<tr><td>firstname</td><td><input type="text" name="firstname"></td></tr>
	<tr><td>lastname</td><td><input type="text" name="lastname"></td></tr>
	<tr><td><br></td></tr>
	<tr><td colspan=2>
	<input type="button" value="send" onclick="subbernowait('runner')">
	<input type="button" value="send regular" onclick="document.forms[0].submit()"><br><br><br>
	</td></tr>
	</table>
</form>

</body>

