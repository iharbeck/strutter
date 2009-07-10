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

import struts.easy.ActionConfig;
import struts.easy.ExtDispatchAction;

public class BlankAction extends ExtDispatchAction 
{
	private static Log log = LogFactory.getLog(BlankAction.class);

	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("view", "/blank_view.jsp");
	}

	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		Locale loc = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
		System.out.println("view ");
		
		return mapping.findForward("view");
	}
}