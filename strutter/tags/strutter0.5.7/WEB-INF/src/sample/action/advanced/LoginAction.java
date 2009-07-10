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

package sample.action.advanced;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import strutter.config.ActionConfig;
import SSO.BasicLoginAction;

public class LoginAction extends BasicLoginAction 
{
	private static Log log = LogFactory.getLog(LoginAction.class);

	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.setScope(ActionConfig.SCOPE_SESSION);
		struts.addForward("/login_view.jsp");
	}
	
	public ActionForward login(ActionMapping mapping, ActionForm actionform, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		LoginActionForm form = (LoginActionForm) actionform;

		return super.login(mapping, actionform, request, response);
	}
}