package strutter.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ActionMethodWrapper
{
	boolean parameter = false;
	Method method;

	public ActionMethodWrapper(Method method, boolean parameter) {
		this.method = method;
		this.parameter = parameter;
	}
	
	public ActionForward execute(Object action, ActionMapping mapping, ActionForm actionform, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		ActionForward forward = null;
		try {
			if(parameter) {
				forward = (ActionForward)method.invoke(action, new Object[] {mapping, actionform, request, response});
			} else {
				forward = (ActionForward)method.invoke(action, null);
			}
		} catch (ClassCastException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();

            if (t instanceof Exception) {
                throw ((Exception) t);
            } else {
                throw new ServletException(t);
            }
		}
		return forward;
	}
}
