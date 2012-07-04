<script>

function onReusingSession() {
	alert('warning: reusing session');
   document.writeln("!!!dublicate!!!");
}

function onNoCookies() {
   document.writeln("no cookies");
}

</script>

<style>
  body { font-family: sans-serif}
</style>

<body >
FIRST PAGE<p>

<a href="noreuse.do" target="_blank">NEW WINDOW</a>
</body>

