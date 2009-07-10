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

package struts.easy;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

public class ExtDispatchAction extends DispatchAction {
	
	Locale locale = null;
	public ActionForward execute(ActionMapping arg0, ActionForm arg1, HttpServletRequest request, HttpServletResponse arg3) throws Exception 
	{
		locale = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);

		startExt(request);
		ActionForward forward = super.execute(arg0, arg1, request, arg3);
		endExt();
		
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

	void startExt(HttpServletRequest request)
	{
		this.request = request;
		errormsgs = getErrors(request);
		infomsgs  = getMessages(request);
	}

	void endExt()
	{
		addErrors(request, errormsgs);
		addMessages(request, infomsgs);
	}
	
	protected HttpServletRequest request;
	ActionMessages errormsgs;
	ActionMessages infomsgs;
	
	public void addError(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		errormsgs.add(alias, msg);
	}

	public void addError(String text)
	{
		ActionMessage msg = new ActionMessage(text);
		errormsgs.add("", msg);
	}

	public void addError(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		errormsgs.add( alias, msg);
	}

	public void addError(String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		errormsgs.add( "", msg);
	}

	public void addMessage(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		infomsgs.add(alias, msg);
	}
	
	public void addMessage(String text)
	{
		ActionMessage msg = new ActionMessage(text);
		infomsgs.add("", msg);
	}
	
	public void addMessage(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		infomsgs.add( alias, msg);
	}

	public void addMessage(String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		infomsgs.add( "", msg);
	}

	
	
	public boolean hasErrors()
	{
		return !getErrors(request).isEmpty() || !errormsgs.isEmpty();
	}
	
	public boolean hasMessages()
	{
		return !getMessages(request).isEmpty() || !infomsgs.isEmpty();
	}
	
}
