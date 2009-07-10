/*
 * Copyright 2006 Ingo Harbeck.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import strutter.config.ActionConfig;
import strutter.optional.ExtDispatchAction;

/**
 * to be continued
 * @author harb05
 *
 */
public class SpringAction extends ExtDispatchAction 
{
	private WebApplicationContext wac;

	// http://www.javalobby.org/java/forums/t20533.html
	// org.springframework.orm.hibernate3.SessionFactoryUtils.getSession();
	// Hibernate.initialize()
	

	public void setServlet(ActionServlet actionServlet) {
		super.setServlet(actionServlet);
		ServletContext servletContext = actionServlet.getServletContext();

		wac = WebApplicationContextUtils.
			  getRequiredWebApplicationContext(servletContext);
	}
	
	public WebApplicationContext getWac()
	{
		ServletContext servletContext = getServlet().getServletContext();

		return WebApplicationContextUtils.
		  getRequiredWebApplicationContext(servletContext);
	}
	
	public Object getService(String name) {
		return getWac().getBean(name);
	}

	public Object getAutowired(Object bean) {
		int AUTOWIREMODE = 1;
		boolean DEPENDENCYCHECK = false;
		
		getWac().getAutowireCapableBeanFactory()
					   .autowireBeanProperties(
							bean, AUTOWIREMODE, DEPENDENCYCHECK);
		
		return bean;
	}

	
	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/spring_view.jsp");
	}
	
	private static Log log = LogFactory.getLog(SpringAction.class);

	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug(this);
		
		return mapping.findForward("view");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("preview");
		
		return mapping.findForward("preview");
	}
	
	public ActionForward search(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("search");
		
		return mapping.findForward("view");
	}
}