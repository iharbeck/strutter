<%@page import="sample.action.multicolum.SortAction"%>
<%@page import="strutter.helper.ActionHelper"%>
<HTML><HEAD><TITLE>Easy</TITLE>

<LINK href="style/style.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<div class=topheadermid>FORMLESS</div>

<DIV class=outertable style="padding:10px">

  <div type="messages" id="confirm" class="messages"/>
  <div type="errors" id="customer.firstname" class="errors"/>

<%
	SortAction form = (SortAction)ActionHelper.getForm();
%>

<style>


th.sorted_asc{
	background: url(arrow_down.gif) 0px no-repeat;
	background-position: right 4px;
}
th.sorted_desc{
	background: url(arrow_up.gif) 0px no-repeat;
	background-position: right 4px;
}

div.pos { float:right; margin-right:15px; margin-top: 3px; font-size: 8px; color: gray; font-weight: normal; font-family: sans-serif; }

table { border: 1px solid silver }
th, td { border: 1px solid silver; width: 60px; text-align: left; cursor: pointer; }

table#sorttab { border-collapse: collapse; }

</style>

<form action="sort.do" method="post" enctype="multipart/form-data">

<table id='sorttab' width="100%">
  <tr>
  	<th alt='Nachname' class='sorted_<%= form.lookupColumnOrder("Nachname") %>'>Nachname<div class="pos"><%= form.findPosition("Nachname")%></div></th>
  	<th alt='Vorname'  class='sorted_<%= form.lookupColumnOrder("Vorname")  %>'>Vorname <div class="pos"><%= form.findPosition("Vorname")%></div></th>
  	<th alt='Plz'      class='sorted_<%= form.lookupColumnOrder("Plz")      %>'>Plz     <div class="pos"><%= form.findPosition("Plz")%></div></th>
  	<th alt='Ort'      class='sorted_<%= form.lookupColumnOrder("Ort")      %>'>Ort     <div class="pos"><%= form.findPosition("Ort")%></div></th>
  	<th alt='Amount'   class='sorted_<%= form.lookupColumnOrder("Amount")   %>'>Amount  <div class="pos"><%= form.findPosition("Amount")%></div></th>
  </tr>
  <tr>
  	<td>x</td>
  	<td>x</td>
  	<td>x</td>
  	<td>x</td>
  	<td>x</td>
  </tr>
</table>

<%= form.buildOrderString() %>

</form>
</DIV>
</BODY></HTML>


<script src="https://ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-yui-compressed.js"></script>
<script>
	
	window.addEvent('domready', function() 
	{
		$$('#sorttab th').each
		(
			function(v) 
			{ 
				v.addEvent('click', function(event) { sorter(event, v.getProperty('alt')); } ); 
			}		
		);		
	});

	function sorter(e, i)
	{
		subberparameter('sort', {'column' : i, 'multi': e.control});
	}

</script>
