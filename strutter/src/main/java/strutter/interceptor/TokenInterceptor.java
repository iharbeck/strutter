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

package strutter.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForward;

import strutter.helper.ActionHelper;
import strutter.helper.ActionHelperData;

public class TokenInterceptor implements WebInterceptorInterface
{
	public ActionForward beforeMethod() throws ServletException
	{
		ActionHelperData ahd = ActionHelper.getActionHelperData();
		
		String token = ActionHelper.getParameter("TOKEN");

		if(token == null)
			return null;

		HttpSession session = ActionHelper.getSession();
		ActionForward fw = null;

		if(session.getAttribute(token) != null)
		{
			ActionHelper.addError("strutter.invalidtoken");

			fw = ActionHelper.findForward("invalidtoken");
		}

		session.setAttribute(token, new Object());

		return fw;
	}

	public ActionForward afterMethod()
	{
		return null;
	}

	public ActionForward afterView()
	{
		return null;
	}

	public ActionForward beforeView()
	{
		return null;
	}
}
