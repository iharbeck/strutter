package strutter.interceptor;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionForward;

/**
 * Events fired before and after HTML has been rendert
 * @author HARB05
 *
 */
public interface WebInterceptorInterface extends ActionInterceptorInterface
{
	public ActionForward beforeView() throws ServletException; 
	public ActionForward afterView() throws ServletException;
}
