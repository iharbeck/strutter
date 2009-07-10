
<div class=topheadermid>Login Sample</div>

<DIV class=outertable style="padding:10px">

<LINK href="style/style.css" type="text/css" rel="stylesheet">

<form action="login.do" method="GET">
<input type="hidden" name="action" value="login">
<input type="hidden" name="refer"> 


	<table class="dialog">
		<tr>
		    <td width="80px">Username</td>
		    <td><input type="text" name="user" class="input"> admin</td>
		    
		    <script>
		      document.getElementsByName("user")[0].focus();
		    </script>
		    
		</tr>
		<tr>
		    <td>Password</td>
		    <td><input type="password" name="pass" class="input"> admin</td>
		</tr>
		
		<tr><td colspan=2><input type="submit" style="margin-top:10px" value="OK"></td></tr>
	
	</table>

</form>

</div>