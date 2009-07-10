<%@ taglib prefix="ww" uri="/webwork" %>

<%@page import="com.opensymphony.xwork.ActionContext"%>

<html>
    <head>
        <title>Welcome</title>
    </head>
    <body>
    <form>
    [
    <ww:if test="hasErrors()">
  <p style="color: red;">
  <b>Errors:</b>
  <ul>
  <ww:if test="hasActionErrors()">
  <ww:iterator value="actionErrors">
    <li style="color: red;"><ww:property/></li>
  </ww:iterator>
  </ww:if>
  <ww:if test="hasFieldErrors()">
  <ww:iterator value="fieldErrors">
    <ww:iterator value="value">
    <li style="color: red;"><ww:property/></li>
    </ww:iterator>
  </ww:iterator>
  </ww:if>
  </ul>
</ww:if>
    ]
    <br>
    	<ww:text name="ingo"/> 
        <h1>Webwork is up and running...</h1><br>
        <%= ActionContext.getContext().getValueStack().getRoot().get(0) %><br>
        
        <a href="home!ingo.action">gogo</a><br>
   
        <input type="checkbox" name="checker" value="1">
        
        <input type="text" name="firstname"> 
        <ww:fielderror>
        <ww:param>firstname</ww:param>
   		</ww:fielderror>
   		<br>
        <input type="text" name="number"> 
        <ww:fielderror>
        <ww:param>number</ww:param>
   		</ww:fielderror>
        
        <ww:fielderror>
        <ww:param>mynumber</ww:param>
   		</ww:fielderror>
        
        
        <input type="text" name="lastname"> 
        <ww:fielderror>
        <ww:param>lastname</ww:param>
   		</ww:fielderror>
    
       <input type="submit">
       
       </form>
    </body>
</html>
