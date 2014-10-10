package strutter.action;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.BaseAction;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.config.annotation.WireActionForm;
import strutter.helper.ActionHelper;
import strutter.helper.PopulateHelper;

public class PlainDispatchAction extends BaseAction
{
	protected static Log log = LogFactory.getLog(PlainDispatchAction.class);

	static final String PREFIX = "do";

	protected HashMap methods = new HashMap();

	protected Class[] types =
	{
	        ActionMapping.class, ActionForm.class, HttpServletRequest.class,
	        HttpServletResponse.class
	};

	/**
	 * used for internal dispatching
	 */

	public ActionForward dispatch(String name) throws Exception
	{
		return dispatchMethod(ActionHelper.getMapping(), null, ActionHelper.getRequest(), ActionHelper.getResponse(), name);
	}

	
	private void inject(Object target, Object value, Class annotation) throws Exception
	{
		Field[] fieldList = target.getClass().getDeclaredFields();

        for (Field afield : fieldList) 
        {
            if(afield.isAnnotationPresent(annotation))
            {
            	afield.setAccessible(true);
            	afield.set(target, value);
            }
        } 
	}
	
	private Object object(Object source, String objname) throws Exception
	{
		Field field = source.getClass().getDeclaredField(objname);
		field.setAccessible(true);

		return field.get(this);
	}
	
	/**
	 * try to get method name: doView, do_view, view try to call with and in a
	 * second step without parameters
	 */
	protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm actionform, HttpServletRequest request, HttpServletResponse response, String name) throws Exception
	{
		if(name == null || name.equals(""))
		{
			if(mapping instanceof ActionMappingExtended)
				name = ((ActionMappingExtended)mapping).getUnspecified();
		}

		ActionMethodWrapper methodwrapper = (ActionMethodWrapper)methods.get(name);

		Object obj = this;

		String objname = null;
		String func = name;
		
		Class clazz = this.getClass();
		
		if(name.contains("."))
		{
			String parse[] = name.split("\\.");
			
			objname = parse[0];
			func = parse[1];
		
			obj = object(this, objname);
			
			clazz = obj.getClass();
			
			inject(obj, this, WireActionForm.class);
		}
		
		if(methodwrapper == null)
		{
			boolean parameter = true;

			Method method = null;

			try
			{
				method = getMethod(clazz, func, null);
				parameter = false;
			}
			catch(Exception e)
			{
				log.error("missing action: [" + name + "]");
				throw new Exception("missing action: [" + name + "]");
			}

			if(method == null)
			{
				try
				{
					method = getMethod(clazz, func, types);
				}
				catch(Exception e)
				{
				}
			}

			methodwrapper = new ActionMethodWrapper(method, parameter);
			methods.put(name, methodwrapper);
		}

		return methodwrapper.execute(obj, mapping, actionform, request, response);
	}

	public Method getMethod(Class clazz, String name, Class[] types) throws Exception
	{
		try
		{
			return clazz.getMethod(PREFIX + name.substring(0, 1).toUpperCase() + name.substring(1), types);
		}
		catch(Exception e)
		{
			try
			{
				return clazz.getMethod(PREFIX + "_" + name, types);
			}
			catch(Exception ee)
			{
				return clazz.getMethod(name, types);
			}
		}
	}

	public ActionForward execute(ActionMapping actionmapping, ActionForm actionform,
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception
	{
		// Get the parameter name.
		String parameter = actionmapping.getParameter();

		if(parameter == null)
		{
			String message = messages.getMessage("dispatch.handler", actionmapping.getPath());
			log.error(message);
			throw new ServletException(message);
		}

		// Get the method's name.
		String name = request.getParameter(parameter);

		if(this instanceof FormlessInterface)
		{
			// Make Action threadsafe!
			// Get existing Action if session scoped
			PlainDispatchAction action = (PlainDispatchAction)Utils.getActionFormFromSession(request);

			if(action == null) // No Session scope or first call
				action = (PlainDispatchAction)getClass().newInstance();

			action.setServlet(this.servlet);

			Map properties = PopulateHelper.populate(action, request);
 
			if(name == null && properties.get(parameter) != null)
				name = ((String[])properties.get(parameter))[0];

			// Store Action (Form)
			Utils.setActionForm(request, (Action)action);

			if(action instanceof InterceptorInterface)
				((InterceptorInterface)action).beforeExecute();

			ActionForward forward = action.dispatchMethod(actionmapping, null, request, response, name);
			
			if(action instanceof InterceptorInterface)
				((InterceptorInterface)action).afterExecute();

			return forward;
		}

		return dispatchMethod(actionmapping, actionform, request, response, name);
	}

	public void reset()
	{
	}
}
