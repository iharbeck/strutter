package struts.optional.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public interface ActionInterceptorInterface 
{
	public ActionForward beforeMethod(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response);
	public ActionForward afterMethod(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response);
}
