package sample.action.dwr;

import java.util.ArrayList;

import org.apache.struts.action.ActionForward;
import org.directwebremoting.Browser;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.ui.browser.Document;
import org.directwebremoting.ui.browser.Window;
import org.directwebremoting.ui.dwr.Util;

import com.sun.org.apache.bcel.internal.generic.NEW;

import sample.dao.Address;
import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.config.tags.ConfigRemotingInterface;
import strutter.helper.ActionHelper;

public class DWR2Action extends FormlessDispatchAction implements ConfigInterface, ConfigRemotingInterface
{
	public void config(ActionConfig struts)
	{
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
		struts.addForward("view", "dwr2.jsp");
		struts.addRemoting(Address.class);
	}

	ArrayList list = new ArrayList();

	public ActionForward doView() throws Exception
	{
		return ActionHelper.findForward("view");
	}

	public String dodoMeMe(String address)
	{

		address = "ingo";

		return address;
	}

	public Address getMe()
	{

		Address address = new Address();
		address.setFirstname("ingo");

		return address;
	}

	public Address dodoMe(Address address)
	{

		address.setFirstname(address.getFirstname() + "_ingo");

		return address;
	}

	String[] values = new String[] { "erster", "zweiter", "dritter" };

	public void worker(String str)
	{

		list.add(str);

		Document.setTitle(str + ActionHelper.getSession() + ActionHelper.getUsername());

		Util.setValue("target", "You send: " + str + " : " + list.size());
		Util.removeAllOptions("combo");
		Util.addOptions("combo", values);
	}

	public void upload(FileTransfer data)
	{
		String info = "Received: " + data.getFilename() + "<br>Size: " + data.getSize();

		Util.setValue("target", info);
		Util.setClassName("target", "rumrum");

		Util.addRows("tab", new String[][] { { "<div style='border:1px solid gray'>1</div>", "2" }, { "3", "4" } }, "{escapeHtml:false}");
	}

	public FileTransfer download(String data)
	{
		return new FileTransfer("test.html", "text/html", "<h1>DOWN</h1>".getBytes());
	}

	public void echo()
	{
		// ECHO to specific Page!
		Browser.withPage("/strutter/dwr.do",
		        new Runnable()
		        {
			        public void run()
			        {
				        Window.alert("hello");
			        };
		        }
		        );
	}

}
