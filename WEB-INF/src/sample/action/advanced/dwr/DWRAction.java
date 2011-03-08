package sample.action.advanced.dwr;

import org.apache.struts.action.ActionForward;
import org.directwebremoting.Browser;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.ui.browser.Document;
import org.directwebremoting.ui.browser.Window;
import org.directwebremoting.ui.dwr.Util;

import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.config.tags.ConfigRemotingInterface;
import strutter.helper.ActionHelper;

public class DWRAction extends FormlessDispatchAction implements ConfigInterface, ConfigRemotingInterface
{
	public void config(ActionConfig struts) {
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
		struts.addForward("view", "dwr.jsp");		
	}

	public ActionForward doView() throws Exception
	{
		return ActionHelper.findForward("view");
	}
	
	String[] values = new String[] { "erster", "zweiter", "dritter" };
	
	public void worker(String str) {
		
		Document.setTitle(str);

		Util.setValue("target", "You send: " + str);
		Util.removeAllOptions("combo");
		Util.addOptions("combo", values);
	}
	
	public void upload(FileTransfer data) 
	{
		String info = "Received: " + data.getFilename() + "<br>Size: " + data.getSize();
		
		Util.setValue("target", info);	
		Util.setClassName("target", "rumrum");
		
		Util.addRows("tab", new String[][] {  {"<div style='border:1px solid gray'>1</div>", "2"},  {"3", "4"} } , "{escapeHtml:false}");
	}
	
	public FileTransfer download(String data) 
	{
		return new FileTransfer("test.html", "text/html", "<h1>DOWN</h1>".getBytes());
	}
	
	public void echo() {
		// ECHO to specific Page!
		Browser.withPage("/strutter/dwr.do", 
				new Runnable() { 
			        public void run() { 
			        	Window.alert("hello"); 
			        }; 
			    } 
		);
	}
	
	
}