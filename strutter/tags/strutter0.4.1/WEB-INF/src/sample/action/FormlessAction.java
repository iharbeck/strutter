package sample.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sample.object.Address;
import struts.easy.ActionConfig;
import struts.easy.FormlessDispatchAction;

public class FormlessAction extends FormlessDispatchAction 
{
	private static Log log = LogFactory.getLog(FormlessAction.class);
	
	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/formless_view.jsp");
	}

	String [] anreden = new String[] { "Herr", "Frau", "Dr." };
	
	
	Address customer = new Address();
	String memo;
	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception 
	{
		log.debug("view");
		
		return mapping.findForward("view");
	}

	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Address getCustomer() {
		return customer;
	}

	public void setCustomer(Address customer) {
		this.customer = customer;
	}

	public String[] getAnreden() {
		return anreden;
	}

	public void setAnreden(String[] anreden) {
		this.anreden = anreden;
	}

}