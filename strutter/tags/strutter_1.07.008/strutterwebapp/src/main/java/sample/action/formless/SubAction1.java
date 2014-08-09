package sample.action.formless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import sample.dao.Address;
import strutter.config.annotation.WireActionForm;
import strutter.helper.ActionHelper;

public class SubAction1 
{
	private static Log log = LogFactory.getLog(FormlessAction.class);

	@WireActionForm
	FormlessAction form;
	
	public ActionForward doUpdate() throws Exception
	{
		log.info("called: sub action" + this.getClass());
		log.info("wired: " + form);
		
		log.info(form.getCustomer().getFirstname());
		
		Address adr = form.getCustomer();
		
		adr.setFirstname(adr.getFirstname().toUpperCase());
		
		return ActionHelper.findForward("view");
	}
}
