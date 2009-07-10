<%@page import="com.opensymphony.xwork2.ActionContext"%>


<html>
    <head>
        <title>Welcome</title>
    </head>
    <body>
    <form>
      
      <div type="messages" class="messages"/>
  	  <div type="errors" class="errors"/>
  
    <br>
    	<div type="resource" id="ingo"/> 
        <h1>Webwork is up and running...</h1><br>
        
        <%= ActionContext.getContext().getValueStack().getRoot().get(0) %><br>
        
        <a href="home!ingo.action">gogo</a><br>
   
        <input type="checkbox" name="checker" value="1">
        
        <input type="text" name="firstname" error="behind"> 

   		<br>
        <input type="text" name="number" erro="behind"> 
        
        <input type="text" name="lastname" error="behind"> 
    
       <input type="submit">
       
       </form>
    </body>
</html>
