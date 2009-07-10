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

package strutter.optional.helper;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.optional.interceptor.ActionInterceptorInterface;
import strutter.view.tag.TagHelper;

public class ActionHelper
{
	private static class ThreadLocalActionHelper extends ThreadLocal {

		private HttpServletRequest  request;
		private HttpServletResponse response;
		private HttpSession         session;
		private ActionMessages 	    errormsgs;
		private ActionMessages 	    infomsgs;
		private Locale 			    locale = null;
		private ActionMapping       mapping;

		public Object initialValue() {
	      return new ThreadLocalActionHelper();
	    }
	}

	private static final ThreadLocalActionHelper helper = new ThreadLocalActionHelper();

	public final boolean hasObject(String name) 
	{
		Object form = Utils.getActionForm(me().request);
		return (TagHelper.getFormObject(form, name) != null);
	}
	
	public final static String getString(String name)
	{
		Object form = Utils.getActionForm(me().request);
		return TagHelper.getFormValue(form, name);
	}
	
	public final static Object getObject(String name)
	{
		Object form = Utils.getActionForm(me().request);
		return TagHelper.getFormObject(form, name);
	}
	
	public final static Object getForm()
	{
		return Utils.getActionForm(me().request);
	}
	
	public final static String getParameter(String name)
	{
		return me().request.getParameter(name);
	}

	//singleton
	static ActionHelper instance = new ActionHelper();

	public static synchronized ActionHelper getInstance() {
		return instance;
	}

	public ActionHelper() {
	}

	public static void init(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response)
	{
		ThreadLocalActionHelper me = ActionHelper.me();
		me.request   = request;
		me.response  = response;
		me.session   = request.getSession(true);
		me.locale    = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);

		me.errormsgs = getMsg(request, Globals.ERROR_KEY);
		me.infomsgs  = getMsg(request, Globals.MESSAGE_KEY);

		me.mapping   = mapping;
	}

	protected static ActionMessages getMsg(HttpServletRequest request, String alias)
	{
		ActionMessages errors =
            (ActionMessages) request.getAttribute(alias);
        if (errors == null) {
            errors = new ActionMessages();
        }
        return errors;
    }

	public ActionMessages getErrormsgs() {
		return me().errormsgs;
	}

	public ActionMessages getInfomsgs() {
		return me().infomsgs;
	}

	public Locale getLocale() {
		return me().locale;
	}

	public HttpServletRequest getRequest() {
		return me().request;
	}

	public HttpServletResponse getResponse() {
		return me().response;
	}

	public HttpSession getSession() {
		return me().session;
	}

	public ActionMapping getMapping() {
		return ActionHelper.me().mapping;
	}

	public ActionForward getForward(String forward) {
		return me().mapping.findForward(forward);
	}


	public ActionForward startInterceptors() throws IOException, ServletException
	{
		//ActionMapping is extended to provide interceptor interface
		if(me().mapping instanceof ActionMappingExtended)
		{
			for(int i=0; i < ((ActionMappingExtended)me().mapping).getInterceptors().size(); i++)
			{
			    ActionInterceptorInterface interceptor =	(ActionInterceptorInterface) ((ActionMappingExtended)me().mapping).getInterceptors().get(i);
			    ActionForward forward = interceptor.beforeMethod(me().mapping, me().request, me().response);

			    if(forward != null) {
			    	return forward;
			    }
			}
		}
		return null;
	}

	private static final ThreadLocalActionHelper me() {
		return (ThreadLocalActionHelper)helper.get();
	}

	public static final void remove() {
		try {
			ActionHelper.me().remove();
		} catch(Exception e) {
		}
	}

	public ActionForward endInterceptors()
	{
		try {
			addErrors();
			addMessages();

			if(me().mapping instanceof ActionMappingExtended)
			{
				for(int i=0; i < ((ActionMappingExtended)me().mapping).getInterceptors().size(); i++)
				{
				    ActionInterceptorInterface interceptor =	(ActionInterceptorInterface) ((ActionMappingExtended)me().mapping).getInterceptors().get(i);
				    ActionForward forward = interceptor.afterMethod(me().mapping, me().request, me().response);

				    if(forward != null) {
				    	return forward;
				    }
				}
			}

		} finally {
			// Nur in JDK1.5
			//mem.remove();

			// Threadlocal wird nicht vom Filter aufgräumt
			// Steht damit auch nicht in der JSP zur Verfügung
			if(getRequest().getAttribute("STRUTTERVIEW") == null)
				remove();
		}
		return null;
	}

	protected void addErrors()
	{
		HttpServletRequest request = me().request;
		ActionMessages errors = me().errormsgs;

		if (errors == null)
			return;

		// get any existing errors from the request, or make a new one
		ActionMessages requestErrors = (ActionMessages)request.getAttribute(Globals.ERROR_KEY);
		if (requestErrors == null){
			requestErrors = new ActionMessages();
		}
		// add incoming errors
		try {
			if(requestErrors != errors)
				requestErrors.add(errors);
		} catch(Exception e) {
			System.out.println("CONCURRBUG");
		}


		// if still empty, just wipe it out from the request
		if (requestErrors.isEmpty()) {
			request.removeAttribute(Globals.ERROR_KEY);
			return;
		}

		// Save the errors
		request.setAttribute(Globals.ERROR_KEY, requestErrors);
	}

	public String getRessource(String key)
	{
		MessageResources resources = (MessageResources) me().request.getAttribute(Globals.MESSAGES_KEY);

		return resources.getMessage(getLocale(), key);
	}

	protected void addMessages()
	{
		HttpServletRequest request = me().request;
		ActionMessages messages    = me().infomsgs;

		if (messages == null)
			return;

		// get any existing messages from the request, or make a new one
		ActionMessages requestMessages = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
		if (requestMessages == null){
			requestMessages = new ActionMessages();
		}

		// add incoming messages
		try {
			if(requestMessages != messages)
				requestMessages.add(messages);
		} catch(Exception e) {
			System.out.println("CONCURRBUG");
		}

		// if still empty, just wipe it out from the request
		if (requestMessages.isEmpty()) {
			request.removeAttribute(Globals.MESSAGE_KEY);
			return;
		}

		// Save the messages
		request.setAttribute(Globals.MESSAGE_KEY, requestMessages);
	}

	public void addError(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getErrormsgs().add(alias, msg);
	}

	public void addError(String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getErrormsgs().add("", msg);
	}

	public void addError(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getErrormsgs().add( alias, msg);
	}

	public void addError(String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getErrormsgs().add( "", msg);
	}

	public void addMessage(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getInfomsgs().add(alias, msg);
	}

	public void addMessage(String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getInfomsgs().add("", msg);
	}

	public void addMessage(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getInfomsgs().add( alias, msg);
	}

	public void addMessage(String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getInfomsgs().add( "", msg);
	}


	public boolean hasErrors()
	{
		return !getErrormsgs().isEmpty();
	}

	public boolean hasMessages()
	{
		return !getInfomsgs().isEmpty();
	}



	public boolean isUserInRole(String role)
	{
		return getRequest().isUserInRole(role);
	}

	public void validate(boolean condition, String property, String alias)
	{
		if(condition)
			addError(property, alias);
	}

	public void validate(boolean condition, String alias)
	{
		if(condition)
			addError(alias);
	}

	public void setLocale(String language) {
		Utils.setLocale(getSession(), language);
	}

	/**
	 * Get session data in getter function
	 *
	 * return getGlobal("debnr", "0");
	 * @param name
	 * @param def
	 * @return
	 */
	public String getGlobal(String name, String def) {
		String val = (String)getSession().getAttribute(name);
		return val == null ? def : val;
	}

	/**
	 * Set data in session in setter function
	 *
	 * setGlobal("debnr", debnrKunde);
	 * - optional
	 * this.debnrKunde = setGlobal("debnr", debnrKunde);
	 *
	 * @param name
	 * @param val
	 * @return
	 */
	public String setGlobal(String name, String val) {
		try {
			getSession().setAttribute(name, val);
		} catch(Exception e) {}
		return val;
	}
}
