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

package strutter.optional.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import strutter.optional.ActionHelper;

public class TokenInterceptor implements WebInterceptorInterface 
{
	public ActionForward beforeMethod(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) 
	{
		String token = request.getParameter("TOKEN");
		
		if(token == null)
			return null;
		
		HttpSession session = request.getSession();
		ActionForward fw = null;
		
		if(session.getAttribute(token) != null)
		{
			ActionHelper helper = ActionHelper.getInstance();
			helper.addError("strutter.invalidtoken");
			
			fw = mapping.findForward("invalidtoken");
		}

		session.setAttribute(token, new Object());
		
		return fw;
	}

	public ActionForward afterMethod(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	public ActionForward afterView(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) 
	{
		return null;
	}

	public ActionForward beforeView(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) 
	{
		return null;
	}
}
