package struts2.view;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;

import struts.view.AbstractPageFilter;
import struts2.easy.ActionPlugin;

import com.opensymphony.xwork.ActionContext;


public final class PageFilter extends AbstractPageFilter 
{
	public final static String ID = "name";

	protected Object getForm(ServletRequest request) {
		// First Element on the WW Stack is the Action itself
		return ActionContext.getContext().getValueStack().getRoot().get(0); 
	}

	public void init(FilterConfig fc) {
		
		ActionPlugin plugin = new ActionPlugin();
		
		plugin.init(fc);
		
	}

	public void destroy() {
	}
}

