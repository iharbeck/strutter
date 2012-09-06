<style>

.borderred { border: 1px solid red !important }
.highlight { background-color: silver }

</style>


<form>

    <script>
       dwr.engine.setActiveReverseAjax(true);
    </script>     

    <input type="text" name="me"> 
    <input type="button" value="send" onclick="DWRAction.onWorker($N('me').value)">
    
    <div id='target' style="margin-top:20px; border: 1px solid gray; height:100px; width:400px"></div>

    <p>
    <hr>
    <p>
    
    <select name="combo" id="combo">
    </select> 
    <input type="button" value="Fill Combo" onclick="DWRAction.onFillcombo()">

    <input type="button" value="Add Rows" onclick="DWRAction.onAddrows()">

    <input type="button" value="Toggle" onclick="DWRAction.onToggle()">
    
    <p>
    <hr>
    <p>
    
    <input type="file" name="ff" id="ff">
    
    <p>
    <input type="button" value="filesend" onclick="DWRAction.onUpload($DWR('ff'))">

    <p>
    
    <input type="button" value="fileread" onclick="DWRAction.onDownload('JOE', function(data) { dwr.engine.openInDownload(data); } )">
    
    
    <input type="button" value="echo all" onclick="DWRAction.onEcho()">
    
    <table id="tab" style="width: 100%; border:1px solid green">
    </table>

</form>