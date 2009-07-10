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

public class SampleAction extends ExtDispatchAction 
{
	private static Log log = LogFactory.getLog(SampleAction.class);

	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/sample_view.jsp");
		struts.addForward("/sample_preview.jsp");
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("view");
		
		return mapping.findForward("view");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("preview");
		
		return mapping.findForward("preview");
	}

	
	public ActionForward simulatemessage(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("simulatemessage");
		
		addMessage("confirm", "text.msg");
		addMessage("info", "text.info");

		if(hasMessages())
			return mapping.findForward("view");

		return mapping.findForward("view");
	}

	public ActionForward simulateerrors(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("simulateerrors");
		
		addError("text.msg");
		
		addError("text.param", new Object[] { "one", "two" });
		
		addError("customer.firstname", "error.missing");
		addError("customer.lastname",  "error.missing");
		
		if(hasErrors())
			return mapping.findForward("view");

		return mapping.findForward("view");
	}

	
	public ActionForward search(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("search");

		SampleActionForm form = (SampleActionForm)actionform;
		
		if(form.getFile() != null)
		{
			String filename = form.getFile().getFileName();
			log.debug("FILENAME: " + filename);
			form.setFilename(filename);

			//Utils.writeFile( "d:/uploads", form.getFile());
		}
		return mapping.findForward("view");
	}
}