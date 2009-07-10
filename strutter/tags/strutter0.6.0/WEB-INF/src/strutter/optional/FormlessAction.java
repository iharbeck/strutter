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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.MultipartRequestHandler;

import strutter.Utils;
import strutter.optional.helper.ActionHelper;
import strutter.optional.helper.FormlessHelper;
import strutter.optional.helper.FormlessInterface;

public abstract class FormlessAction extends Action implements FormlessInterface
{
	public ActionHelper helper = ActionHelper.getInstance();

	MultipartRequestHandler multipartRequestHandler;

	public final ActionForward execute(ActionMapping actionmapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		ActionHelper.init(actionmapping, request, response);

		//Get existing Action
		//TODO: Maybe integrate Spring
		FormlessAction action = (FormlessAction)Utils.getActionFormFromSession(request);

		if(action == null)	// No Session scope or first call
			action = (FormlessAction) this.getClass().newInstance();

		action.servlet = this.servlet;



		FormlessHelper.populate(actionmapping, action, actionmapping.getPrefix(), actionmapping.getSuffix(), request);


		//TODO: Maybe add call to validate


		// Store Action (Form)
		Utils.setActionForm(request, action);

		//Message/Error handler
		ActionForward helperforward = action.helper.startInterceptors();

		if(helperforward != null) {
			action.helper.endInterceptors();
			return helperforward;
		}

		ActionForward forward = action.execute();

        // Message/Error handler
        helperforward =  action.helper.endInterceptors();

        if(helperforward != null)
        	forward = helperforward;

		return forward;
	}

	public abstract ActionForward execute() throws Exception;

	public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		reset();
	}

	public void reset() { }

	public MultipartRequestHandler getMultipartRequestHandler() {
		return multipartRequestHandler;
	}

	public void setMultipartRequestHandler(
			MultipartRequestHandler multipartRequestHandler) {
		this.multipartRequestHandler = multipartRequestHandler;
	}
}