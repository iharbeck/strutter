package strutter.action;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.helper.ActionHelper;
import strutter.helper.PopulateHelper;

public class PlainDispatchAction extends DispatchAction
{
	static final String PREFIX = "do_";
	
	/**
	 * used for internal dispatching
	 */
	
	public ActionForward dispatch(String name) throws Exception 
	{
		return dispatchMethod(ActionHelper.getMapping(), null, ActionHelper.getRequest(), ActionHelper.getResponse(), name);
	}
	
	/**
	 * try to get method name like do_view then view
	 * try to call without and in a second step with parameters
	 */
	protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm actionform, HttpServletRequest request, HttpServletResponse response, String name) throws Exception
	{
      if(name == null || name.equals(""))
      {
    	 if(mapping instanceof ActionMappingExtended)
    	  	name = ((ActionMappingExtended) mapping).getUnspecified();
    	 else
    	    return unspecified(mapping, actionform, request, response);
	  }
	
      ActionMethodWrapper methodwrapper = (ActionMethodWrapper)methods.get(name);
      
      if(methodwrapper == null) 
      {
    	  Method method = null;
    	  try {
    		  method = clazz.getMethod(PREFIX + name, new Class[] {ActionMapping.class, ActionForm.class, HttpServletRequest.class, HttpServletResponse.class});
    	  } catch (Exception e) {
    		  try {
        		  method = clazz.getMethod(name, new Class[] {ActionMapping.class, ActionForm.class, HttpServletRequest.class, HttpServletResponse.class});
        	  } catch (Exception ee) {
        	  }  
    	  }
    	  
    	  boolean parameter = true;

    	  if(method == null) {
    		  try {
    			  method = clazz.getMethod(PREFIX + name, null);
    			  parameter = false;
    		  } catch (Exception e) {
        		  try {
        			  method = clazz.getMethod(name, null);
        			  parameter = false;
        		  } catch (Exception ee) {
        			  log.error("missing action: [" + name + "]");
        			  throw new Exception("missing action: [" + name + "]");
        		  }	  
			}
    	  }
          
    	  if(method != null) {
    		  methodwrapper = new ActionMethodWrapper(method, parameter);
    		  methods.put(name, methodwrapper);    		  
    	  }
      }
      
      return methodwrapper.execute(this, mapping, actionform, request, response);
    }
	
	public ActionForward execute(ActionMapping actionmapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		if(!(this instanceof FormlessInterface))
			return super.execute(actionmapping, actionform, request, response);

		//Get existing Action
		PlainDispatchAction action = (PlainDispatchAction) Utils.getActionFormFromSession(request);

		if(action == null)	// No Session scope or first call
			action = (PlainDispatchAction) this.getClass().newInstance();  // clone(); //

		action.setServlet(this.servlet);

		PopulateHelper.populate(action, actionmapping.getPrefix(), actionmapping.getSuffix(), request);

		//TODO: Add call to validate

		// Store Action (Form)
		Utils.setActionForm(request, (Action)action);

		// Get the parameter name. 
        String parameter = action.getParameter(actionmapping, null, request, response);

        // Get the method's name. 
        String name = action.getMethodName(actionmapping, null, request, response, parameter);

        // Call Actionmethod
        ActionForward forward = action.dispatchMethod(actionmapping, null, request, response, name);

        if(forward == null) 
        	log.error("Forward is null");
        
		return forward;
	}


	protected ActionForward unspecified(ActionMapping mapping, ActionForm actionform, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		return dispatchMethod(mapping, actionform, request, response, "view");
	}

	public ActionForward view() throws Exception
	{
		return ActionHelper.findForward("view");
	}
	
	public void reset() 
	{
	}
}
