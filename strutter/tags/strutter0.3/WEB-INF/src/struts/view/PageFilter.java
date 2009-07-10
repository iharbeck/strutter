package struts.view;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import struts.Utils;

public final class PageFilter extends AbstractPageFilter
{
	protected Object getForm(ServletRequest request) {
		// Identify the mapping for this request
		Object form = Utils.getActionForm((HttpServletRequest)request);
		
		return form;
	}
	
	public void init(FilterConfig f) {
	}

	public void destroy() {
	}
}

