
<form>


  <script>
     dwr.engine.setActiveReverseAjax(true);
  </script>     


    <div id='target' style="margin:20px; border:1px solid red; height:100px; width:400px">

    </div>


    <input type="text" name="me"> 
    <input type="button" value="send" onclick="DWRAction.worker($N('me').value)">
    
    <p>
    
    <select name="combo" id="combo">
    </select> 
    
    <p>
    <hr>
    <p>
    
    <input type="file" name="ff" id="ff">
    
    <p>
    <input type="button" value="filesend" onclick="DWRAction.upload($DWR('ff'))">

    <p>
    
    <input type="button" value="fileread" onclick="DWRAction.download('JOE', function(data) { dwr.engine.openInDownload(data); } )">
    
    
    <input type="button" value="echo all" onclick="DWRAction.echo()">
    
    <table id="tab">
    </table>

</form>