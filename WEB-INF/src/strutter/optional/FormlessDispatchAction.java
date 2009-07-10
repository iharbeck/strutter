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

/**
 * Handle Action with internal Form handling
 */

package strutter.optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.MultipartRequestHandler;

import strutter.Utils;
import strutter.optional.helper.ActionHelper;
import strutter.optional.helper.FormlessHelper;
import strutter.optional.helper.FormlessInterface;

public class FormlessDispatchAction extends ExtDispatchAction implements FormlessInterface
{
	private static Log log = LogFactory.getLog(FormlessDispatchAction.class);

	MultipartRequestHandler multipartRequestHandler;

	public ActionForward execute(ActionMapping actionmapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		ActionHelper.init(actionmapping, request, response);

		//Get existing Action
		//TODO: integrate Spring
		FormlessDispatchAction action = (FormlessDispatchAction) Utils.getActionFormFromSession(request);

		if(action == null)	// No Session scope or first call
			action = (FormlessDispatchAction) this.getClass().newInstance();

		action.servlet = this.servlet;

		ActionForward forward = null;

		FormlessHelper.populate(actionmapping, action, actionmapping.getPrefix(), actionmapping.getSuffix(), request);

		//TODO: Add call to validate


		// Store Action (Form)
		Utils.setActionForm(request, action);


		//Message/Error handler
		ActionForward helperforward = action.helper.startInterceptors();

		if(helperforward != null) {
			action.helper.endInterceptors();
			return helperforward;
		}

		// Get the parameter. This could be overridden in subclasses.
        String parameter = action.getParameter(actionmapping, null, request, response);

        if(!request.getParameterMap().containsKey(parameter))
        {
        	parameter = "action";
        	//throw new Exception("Strutter: parameter [" + parameter + "] is missing");
        }
        // Get the method's name. This could be overridden in subclasses.
        String name = action.getMethodName(actionmapping, null, request, response, parameter);

        forward = action.dispatchMethod(actionmapping, null, request, response, name);

        if(forward == null) {
        	log.error("Can't find action in parameter: " + name + "");
        }
        // Message/Error handler
        helperforward = action.helper.endInterceptors();

        if(helperforward != null)
        	forward = helperforward;

		return forward;
	}


	public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		reset();
	}

	public void reset() {
	}

	public MultipartRequestHandler getMultipartRequestHandler() {
		return multipartRequestHandler;
	}

	public void setMultipartRequestHandler(MultipartRequestHandler multipartRequestHandler) {
		this.multipartRequestHandler = multipartRequestHandler;
	}
}