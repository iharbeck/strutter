package strutter.action;

import java.lang.reflect.Method;
import java.util.HashMap;

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
import strutter.helper.ActionHelper;
import strutter.helper.PopulateHelper;

public class PlainDispatchAction extends BaseAction
{
	protected static Log log = LogFactory.getLog(PlainDispatchAction.class);
	
	static final String PREFIX = "do";
	
	protected Class clazz     = this.getClass();
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
	
	/**
	 * try to get method name: doView, do_view, view
	 * try to call with and in a second step without parameters
	 */
	protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm actionform, HttpServletRequest request, HttpServletResponse response, String name) throws Exception
	{
      if(name == null || name.equals(""))
      {
    	 if(mapping instanceof ActionMappingExtended)
    	  	name = ((ActionMappingExtended) mapping).getUnspecified();
	  }
	
      ActionMethodWrapper methodwrapper = (ActionMethodWrapper)methods.get(name);
      
      if(methodwrapper == null) 
      {
    	  boolean parameter = true;

    	  Method method = null;
    	  
    	  try {
    		  method = getMethod(name, types);
    	  } catch (Exception e) {}

    	  if(method == null) {
    		  try {
    			  method = getMethod( name, null);
    		  } catch (Exception e) {
    			  log.error("missing action: [" + name + "]");
    			  throw new Exception("missing action: [" + name + "]");
    		  }
    		  parameter = false;
    	  }
          
		  methodwrapper = new ActionMethodWrapper(method, parameter);
		  methods.put(name, methodwrapper);    		  
      }
      
      return methodwrapper.execute(this, mapping, actionform, request, response);
    }
	
	public Method getMethod(String name, Class[] types) throws Exception 
	{
		try {
			return clazz.getMethod(PREFIX + name.substring(0, 1).toUpperCase() + name.substring(1), types);
		} catch (Exception e) {
			try {
	  		  return clazz.getMethod(PREFIX + "_" + name, types);
	  	  	} catch (Exception ee) {
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

        if (parameter == null) {
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
			PlainDispatchAction action = (PlainDispatchAction) Utils.getActionFormFromSession(request);
	
			if(action == null)	// No Session scope or first call
				action = (PlainDispatchAction) getClass().newInstance(); 
	
			action.setServlet(this.servlet);
	
			HashMap properties = PopulateHelper.populate(action, actionmapping.getPrefix(), actionmapping.getSuffix(), request);
	
			if(name == null && properties.get(parameter) != null)
				name = ((String[])properties.get(parameter))[0];
	    	
			// Store Action (Form)
			Utils.setActionForm(request, (Action)action);
			return action.dispatchMethod(actionmapping, null, request, response, name);
		}

		return dispatchMethod(actionmapping, actionform, request, response, name);

	}

	public void reset() 
	{
	}
}
