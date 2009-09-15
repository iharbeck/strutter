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

package strutter.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import strutter.Utils;
import strutter.helper.ActionHelper;
import strutter.helper.PopulateHelper;


public abstract class PlainAction extends Action 
{
	public ActionForward execute(ActionMapping actionmapping, ActionForm arg1, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		if(this instanceof FormlessInterface)
			return execute_formless();
		else
			return execute();
	}

	public abstract ActionForward execute() throws Exception;
	
	
	public ActionForward execute_formless() throws Exception
	{
//		if(ActionHelper.getMapping().getName() != null)
//			return super.execute(actionmapping, actionform, request, response);
		
		//Get existing Action
		//TODO: Maybe integrate Spring
		PlainAction action = (PlainAction)Utils.getActionFormFromSession(ActionHelper.getRequest());

		if(action == null)	// No Session scope or first call
			action = (PlainAction) this.clone(); // getClass().newInstance();

		action.servlet = this.servlet;

		PopulateHelper.populate(
			action, 
			ActionHelper.getMapping().getPrefix(), 
			ActionHelper.getMapping().getSuffix(), 
			ActionHelper.getRequest());

		//TODO: Maybe add call to validate


		// Store Action (Form)
		Utils.setActionForm(ActionHelper.getRequest(), action);

		ActionForward forward = action.execute();

		return forward;
	}

	public void reset() 
	{
	}
}