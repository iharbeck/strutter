<%@page import="strutter.optional.helper.ActionHelper"%>
<%@page import="sample.action.advanced.HelperAction"%>

<%
// Access the Form
HelperAction form = (HelperAction)ActionHelper.getForm();
%>

simple String access to variable (test): <%=ActionHelper.getString("test")%>
<br>
simple Object access to variable (test): <%=ActionHelper.getObject("test")%><br>

