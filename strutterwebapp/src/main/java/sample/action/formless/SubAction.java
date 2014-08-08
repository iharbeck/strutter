package sample.action.formless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import strutter.helper.ActionHelper;

public class SubAction 
{
	private static Log log = LogFactory.getLog(FormlessAction.class);

	public ActionForward doUpdate() throws Exception
	{
		log.debug("sub action");
		
		FormlessAction form = (FormlessAction)ActionHelper.getForm();
		
		log.debug(form);
		log.debug(form.getCustomer().getFirstname());
		
		return ActionHelper.findForward("view");
	}
	
}
