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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import sample.object.CounterBean;
import struts.Utils;
import struts.easy.ActionConfig;
import struts.easy.ExtDispatchAction;

public class SampleAction extends ExtDispatchAction 
{
	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("view", "/sample_view.jsp");
		struts.addForward("preview", "/sample_preview.jsp");
	}
	
	private static Log log = LogFactory.getLog(SampleAction.class);

	
	/** Spring managed bean reference */
    private CounterBean counterBean;
    private String mypar;

    /**
     * IoC setter for the spring managed CounterBean.
     *
     * @param counterBean
     */
    public void setCounterBean(CounterBean counterBean) {
        this.counterBean = counterBean;
    }
	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		Locale loc = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
		System.out.println("view" + mypar + counterBean);
		
		return mapping.findForward("view");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		Locale loc = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
		System.out.println("preview");
		
		
		return mapping.findForward("preview");
	}
	
//	private WebApplicationContext wac;
//	public void setServlet(ActionServlet actionServlet) {
//		super.setServlet(actionServlet);
//		ServletContext servletContext = actionServlet.getServletContext();
//
//		wac = WebApplicationContextUtils.
//			  getRequiredWebApplicationContext(servletContext);
//
//		System.out.println( wac.getBean("counterBean") );
//		 
//		org.springframework.orm.hibernate3.SessionFactoryUtils.getSession();
//	}

	
	public ActionForward search(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		Locale loc = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);

		System.out.println("search");

		
		
		
		
		ActionMessages messages = new ActionMessages();
		messages.add("ff", new ActionMessage("ingo"));

		//saveMessages(request, messages);
		saveErrors(request, messages);
		
		SampleActionForm form = (SampleActionForm)actionform;
		
		if(form.getFile() != null)
		{
			String filename = form.getFile().getFileName();
			System.out.println("FILENAME: " + filename);
			form.setFilename(filename);

			Utils.writeFile("d:/uploads", form.getFile());
		}
		return mapping.findForward("view");
	}

	public String getMypar() {
		return mypar;
	}

	public void setMypar(String mypar) {
		this.mypar = mypar;
	}

}