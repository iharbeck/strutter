
<form>


  <script>
     dwr.engine.setActiveReverseAjax(true);
  </script>     


    <div id='target' style="margin:20px; border:1px solid red; height:100px; width:400px">

    </div>


    <input type="text" name="me"> 
    <input type="button" value="send" onclick="DWR2Action.worker($N('me').value)">
    
    <p>
    
    <select name="combo" id="combo">
    </select> 
    
    <p>
    <hr>
    <p>
    
    <input type="file" name="ff" id="ff">
    
    <p>
    <input type="button" value="filesend" onclick="DWR2Action.upload($DWR('ff'))">

    <p>
    
    <input type="button" value="fileread" onclick="DWR2Action.download('JOE', function(data) { dwr.engine.openInDownload(data); } )">
    
    
    <input type="button" value="echo all" onclick="DWR2Action.echo()">
    
    <table id="tab">
    </table>

</form>