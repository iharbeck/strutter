package strutter.interceptor;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionForward;

/**
 * Events fired before and after the action has been executed
 * 
 * @author HARB05
 * 
 */

public interface ActionInterceptorInterface
{
	public ActionForward beforeMethod() throws ServletException;

	public ActionForward afterMethod() throws ServletException;
}
