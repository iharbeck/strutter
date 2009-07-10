package sample.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import struts.easy.ActionConfig;
import struts.easy.ExtDispatchAction;

public class BlankAction extends ExtDispatchAction 
{
	private static Log log = LogFactory.getLog(BlankAction.class);

	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/blank_view.jsp");
	}

	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("view");
		
		return mapping.findForward("view");
	}
}