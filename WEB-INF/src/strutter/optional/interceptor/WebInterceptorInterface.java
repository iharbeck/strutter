package strutter.optional.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public interface WebInterceptorInterface extends ActionInterceptorInterface
{
	public ActionForward beforeView(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response);
	public ActionForward afterView(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response);
}
