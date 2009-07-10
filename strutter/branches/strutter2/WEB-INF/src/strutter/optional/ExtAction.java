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

import strutter.optional.helper.ActionHelper;


public abstract class ExtAction extends Action {
	
	public ActionHelper helper = ActionHelper.getInstance();
	
	public final ActionForward execute(ActionMapping actionmapping, ActionForm arg1, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		ActionHelper.init(actionmapping, request, response);
		
		System.out.println("Rollen:" + actionmapping.getRoles());

		ActionForward helperforward = helper.startInterceptors();
		
		if(helperforward != null) {
			helper.endInterceptors();
			return helperforward;
		}
		
		ActionForward forward = doexecute(actionmapping, arg1, request, response);
	
		helperforward = helper.endInterceptors();

		if(helperforward != null)
			forward = helperforward;
		
		return forward;
	}
	
	public abstract ActionForward doexecute(ActionMapping actionmapping, ActionForm arg1, HttpServletRequest request, HttpServletResponse arg3) throws Exception;
}
