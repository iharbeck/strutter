/*
 * Copyright 2006 Ingo Harbeck.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package strutter.optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import strutter.Utils;
import strutter.config.ActionMappingExtended;

public class FormlessDispatchAction extends ExtDispatchAction 
{
	private static Log log = LogFactory.getLog(FormlessDispatchAction.class);
	
	public final ActionForward execute(ActionMapping actionmapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception 
	{
		//Get existing Action
		//TODO: integrate Spring
		FormlessDispatchAction action = (FormlessDispatchAction)Utils.getActionFormFromSession(request);
		
		if(action == null)	// No Session scope or first call
			action = (FormlessDispatchAction) this.getClass().newInstance();

		ActionForward forward = null;
		
		action.reset(actionmapping, request);
		
		//BeanUtils.populate(this, request.getParameterMap());
		
		FormlessHelper.populate(action, actionmapping.getPrefix(), actionmapping.getSuffix(), request);
		
		
		//TODO: Add call to validate
		
		
		// Store Action (Form)
		Utils.setActionForm(request, action);

		
		//Message/Error handler
		ActionForward helperforward = action.helper.startExt((ActionMappingExtended)actionmapping, request, response); 

		if(helperforward != null) {
			action.helper.endExt();
			return helperforward;
		}

		// Get the parameter. This could be overridden in subclasses.
        String parameter = action.getParameter(actionmapping, null, request, response);

        // Get the method's name. This could be overridden in subclasses.
        String name = action.getMethodName(actionmapping, null, request, response, parameter);

        forward = action.dispatchMethod(actionmapping, null, request, response, name);
        
        
        // Message/Error handler
        helperforward =  action.helper.endExt();
        
        if(helperforward != null)
        	forward = helperforward;
        
		return forward;
	}

	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
	}
}