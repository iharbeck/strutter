package sample.action;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.LabelValueBean;

import sample.object.Address;
import struts.easy.ActionConfig;
import struts.easy.FormlessDispatchAction;

public class SimpleAction extends FormlessDispatchAction 
{
	private static Log log = LogFactory.getLog(SimpleAction.class);

	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("view", "/simple_view.jsp");
	}

	Address customer = new Address();
	
	//String [] anreden = new String[] { "Herr", "Frau", "Dr." };
	LabelValueBean [] anreden = new LabelValueBean[] { 
			new LabelValueBean("Herr", "11"), 
			new LabelValueBean("Frau", "22"), 
			new LabelValueBean("Dr.",  "33")
	};
	
	
	String rposition;

	String[] tog = new String[10]; 
	
	String c1iso;
	String c2iso;
	String c3iso;
	String c4iso;
	
	String memo;
	
	FormFile file = null;
	String   filename = "";

	
	
	protected ActionForward unspecified(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws Exception 
	{
		return view(arg0, arg1, arg2, arg3);
	}

	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception 
	{
		Locale loc = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
		System.out.println("view ");
		
		addError("", "ingo");
		addError("", "ingo");
		
		addError("ff", "some", new Object[] { "1", "2" });
		addError("ff", "some", new Object[] { "3", "4" });
		addError("ff", "ingo");
		
		addError("customer.firstname", "missing");
		
		addMessage("confirm", "msgeins");
		addMessage("confirm", "msgzwei");
		addMessage("confirm", "msgdrei");
		
		addMessage("info", "msg_eins");
		addMessage("info", "msg_zwei");
		addMessage("info", "msg_drei");
		

		return mapping.findForward("view");
	}

	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
		c1iso = "";
		c2iso = "";
		c3iso = "";
		c4iso = "";
		tog   = new String[10]; 
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getRposition() {
		return rposition;
	}

	public void setRposition(String rgolgo) {
		this.rposition = rgolgo;
	}


	public String getC1iso() {
		return c1iso;
	}

	public void setC1iso(String c1iso) {
		this.c1iso = c1iso;
	}

	public String getC2iso() {
		return c2iso;
	}

	public void setC2iso(String c2iso) {
		this.c2iso = c2iso;
	}

	public String getC3iso() {
		return c3iso;
	}

	public void setC3iso(String c3iso) {
		this.c3iso = c3iso;
	}

	public String getC4iso() {
		return c4iso;
	}

	public void setC4iso(String c4iso) {
		this.c4iso = c4iso;
	}


	public LabelValueBean[] getAnreden() {
		return anreden;
	}

	public void setAnreden(LabelValueBean[] anreden) {
		this.anreden = anreden;
	}

	public Address getCustomer() {
		return customer;
	}

	public void setCustomer(Address customer) {
		this.customer = customer;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String[] getTog() {
		return tog;
	}

	public void setTog(String[] tog) {
		this.tog = tog;
	}
}