package sample.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sample.object.CounterBean;
import struts.easy.ActionConfig;
import struts.easy.ExtDispatchAction;

public class SpringAction extends ExtDispatchAction 
{
	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/spring_view.jsp");
	}
	
	private static Log log = LogFactory.getLog(SpringAction.class);

	
	/** Spring managed bean reference */
    private CounterBean counterBean;
    private String mypar;

    public String getMypar() {
		return mypar;
	}

	public void setMypar(String mypar) {
		log.debug(this);
		this.mypar = mypar;
	}

	public CounterBean getCounterBean() {
		return counterBean;
	}

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
		log.debug(this);
		log.debug("view s " + mypar + " " + counterBean);
		
		return mapping.findForward("view");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("preview");
		
		return mapping.findForward("preview");
	}
	
//	private WebApplicationContext wac;
//	public void setServlet(ActionServlet actionServlet) {
//		super.setServlet(actionServlet);
//		ServletContext servletContext = actionServlet.getServletContext();
//
	// this.getServlet().getServletContext()
	// wac.getAutowireCapableBeanFactory().autowireBeanProperties(
	//		thebean, this.autowireMode, this.dependencyCheck);

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
		log.debug("search");
		
		return mapping.findForward("view");
	}
}