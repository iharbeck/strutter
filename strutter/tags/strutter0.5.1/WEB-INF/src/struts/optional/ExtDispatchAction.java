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

package struts.optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import struts.easy.ActionMappingExtended;



public class ExtDispatchAction extends DispatchAction {
	
	public ActionHelper helper = ActionHelper.getInstance();
	
	public ActionForward execute(ActionMapping actionmapping, ActionForm arg1, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		ActionForward forward = null;
		ActionForward helperforward = null;
		
		helperforward = helper.startExt((ActionMappingExtended)actionmapping, request, response);
		
		if(helperforward != null)
		{
			helper.endExt();
			return helperforward;
		}
		forward = super.execute(actionmapping, arg1, request, response);

		helperforward = helper.endExt();
		
		if(helperforward != null)
			forward = helperforward;
			
		return forward;
	}
	
	protected ActionForward unspecified(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws Exception {
		return view(arg0, arg1, arg2, arg3);
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		return mapping.findForward("view");
	}
}
