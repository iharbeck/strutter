<center><b>HEAD 1</b></center>

<style>
 td.main {border:1px solid silver; padding: 10px}
</style>

<table border="0">
<tr>
 <td class="main" valign="top"><b><%= request.getAttribute("decorator_menu") %></b></td>
 <td class="main"><%= request.getAttribute("decorator_body") %></td>
</tr>
</table>

<center><b>FOOT</b></center>

<jsp:include page="../../include/include.jsp"></jsp:include>