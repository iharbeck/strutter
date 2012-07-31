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

package strutter.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.MessageResources;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.config.tags.ConfigAuthorityInterface;
import strutter.controller.ConfigWrapper;
import strutter.interceptor.ActionInterceptorInterface;
import strutter.resource.UniversalMessageResources;
import strutter.view.TagHelper;

public class ActionHelper
{
	// storing all actions here
	private static HashMap actions = new HashMap();

	private static class ThreadLocalActionHelper extends InheritableThreadLocal
	{
		public Object initialValue()
		{
			return new ActionHelperData();
		}
	}

	private static volatile ThreadLocalActionHelper helper = new ThreadLocalActionHelper();

	public static void init()
	{
		WebContext ctx = WebContextFactory.get();

		if(ctx == null)
			return;

		clear();
    
		try
		{
			ActionHelper.init(ctx.getServletContext(), ctx.getHttpServletRequest(), ctx.getHttpServletResponse());
		}
		catch(Exception e)
		{
		}
	}
	
	private static void clear()
	{
		helper.set(null);
		helper.remove();
	}
	
	private static final ActionHelperData me()
	{
		ActionHelperData data = (ActionHelperData)helper.get();

		return data;
	}

	private static ConfigAuthorityInterface authority;

	public static void setAuthority(ConfigAuthorityInterface authority)
	{
		ActionHelper.authority = authority;
	}

	public static String getUsername()
	{
		return ActionHelper.authority.getUsername();
	}

	public static boolean hasRole(String role)
	{
		return ActionHelper.authority.isAuthorized() && ActionHelper.authority.hasRole(role);

	}

	public static boolean isAuthorized()
	{
		return ActionHelper.authority.isAuthorized();
	}

	public static final void remove()
	{
		try
		{
			ActionHelper.me().minusThreadcount();
			// System.out.println("down:" + ActionHelper.me().getThreadcount() +
			// " " + ActionHelper.me().getRequest().getRequestURI());
			// System.out.println(helper.get());
			// System.out.println(((ActionHelperData)helper.get()).getSession());
			clear();
		}
		catch(Exception e)
		{
		}
	}

	public final boolean hasObject(String name)
	{
		Object form = Utils.getActionForm(me().getRequest());
		return(TagHelper.getFormObject(form, name) != null);
	}

	public final static Object getObject(String name)
	{
		Object form = Utils.getActionForm(me().getRequest());
		return TagHelper.getFormObject(form, name);
	}

	public final static String getString(String name)
	{
		Object form = Utils.getActionForm(me().getRequest());
		return TagHelper.getFormValue(form, name);
	}

	public final static Object getForm() throws Exception
	{
		Object form = Utils.getActionForm(me().getRequest());

		if(form == null)
			throw new Exception("FormularClass not defined use FormlessInterface");

		return form;
	}

	public final static Object getForm(Class clazz)
	{
		return Utils.getActionForm(me().getRequest(), clazz);
	}

	public final static String getFormAttribute(String name) throws Exception
	{
		return TagHelper.getFormValue(getForm(), name);
	}

	public final static void setFormAttribute(String name, String value) throws Exception
	{
		TagHelper.setFormValue(getForm(), name, value);
	}

	public final static String getParameter(String name)
	{
		return getRequest().getParameter(name);
	}

	public final static String[] getParameterValues(String name)
	{
		return getRequest().getParameterValues(name);
	}

	public final static Object getRequestAttribute(String name)
	{
		return getRequest().getAttribute(name);
	}

	public final static Enumeration getRequestAttributeNames(String name)
	{
		return getRequest().getAttributeNames();
	}

	public final static void setRequestAttribute(String name, Object obj)
	{
		getRequest().setAttribute(name, obj);
	}

	public final static Object getSessionAttribute(String name)
	{
		return getSession().getAttribute(name);
	}

	public final static Enumeration getSessionAttributeNames(String name)
	{
		return getSession().getAttributeNames();
	}

	public final static void setSessionAttribute(String name, Object obj)
	{
		getSession().setAttribute(name, obj);
	}

	public ActionHelper()
	{
	}

	static ServletContext servletcontext;

	public static ServletContext getContext()
	{
		return servletcontext;
	}

	public static String getDocRoot()
	{
		return servletcontext.getRealPath("/");
	}

	public static String getContextname()
	{
		String path = getRequest().getContextPath();
		return path.substring(0, path.indexOf("/", 1) + 1);
	}

	public static void storeFile(FormFile file, String targetfolder, String targetfilename) throws Exception
	{
		FileOutputStream out = new FileOutputStream(targetfolder + "/" + targetfilename);
		out.write(file.getFileData());
		out.close();
	}

	public static void storeFile(FormFile formfile, String folder) throws Exception
	{
		storeFile(formfile, folder, formfile.getFileName());
	}

	public static ActionHelperData init(ServletContext servletcontext, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ActionHelperData me = (ActionHelperData)helper.get();
		me.plusThreadcount();
		// System.out.println("up:" + me.threadcount + " " +
		// request.getRequestURI());

		ActionHelper.servletcontext = servletcontext;

		me.setRequest(request);
		me.setResponse(response);
		me.setSession(request.getSession());

		me.setLocale((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY));

		if(me.getLocale() == null)
			me.setLocale(request.getLocale());

		me.setErrormsgs(getMsg(request, Globals.ERROR_KEY));
		me.setInfomsgs(getMsg(request, Globals.MESSAGE_KEY));

		// me.actionname = Utils.getActionMappingName(request.
		// getServletPath());
		me.setActionname(Utils.getActionMappingName(request)); // , response);

		me.setMapping(getActionMapping(me.getActionname()));
		me.setInitialized(true);

		return me;
	}

	public static boolean isMainThread()
	{
		ActionHelperData me = ActionHelper.me();
		// System.out.println("is:" + me.threadcount);
		return me.getThreadcount() == 1;
	}

	protected static ActionMessages getMsg(HttpServletRequest request, String alias)
	{
		ActionMessages errors = (ActionMessages)request.getAttribute(alias);
		if(errors == null)
		{
			errors = new ActionMessages();
		}
		return errors;
	}

	public static ActionMessages getErrormsgs()
	{
		return me().getErrormsgs();
	}

	public static ActionMessages getInfomsgs()
	{
		return me().getInfomsgs();
	}

	public static Locale getLocale()
	{
		return me().getLocale();
	}

	public static String getActionname()
	{
		return me().getActionname();
	}

	public static HttpServletRequest getRequest()
	{
		return me().getRequest();
	}

	public static HttpServletResponse getResponse()
	{
		return me().getResponse();
	}

	public static HttpSession getSession()
	{

		return me().getSession();
	}

	public static ActionMapping getMapping()
	{
		return me().getMapping();
	}

	public static boolean isInitialized()
	{
		return me().isInitialized();
	}

	public static ActionForward getForward(String forward) throws ServletException
	{
		return findForward(forward);
	}

	public static ActionForward buildRedirect(String actionmethod) throws ServletException
	{
		return buildRedirect(null, actionmethod);
	}

	public static ActionForward buildRedirect(String action, String actionmethod) throws ServletException
	{
		ActionMapping mapping;

		if(action == null)
			mapping = ActionHelper.getActionMapping();
		else
			mapping = ActionHelper.getActionMapping(action);

		String path = mapping.getPath() + ".do?" + mapping.getParameter() + "=" + actionmethod;
		return new ActionForward(path, true);
	}

	public static ActionForward findMethodRedirect(String forward) throws ServletException
	{
		return findForward("#" + forward);
	}

	public static ActionForward findAjaxForward(String forward) throws ServletException
	{
		ActionHelper.setHeading(false);
		return findForward(forward);
	}

	public static ActionForward findForward(String forward) throws ServletException
	{
		ActionForward f = getMapping().findForward(forward);
		if(f == null)
			throw new ServletException("missing forward: " + forward);
		return f;
	}

	public static ActionMapping getActionMapping()
	{
		return getActionMapping(me().getActionname());
	}

	public static ActionMapping getActionMapping(String actionname)
	{

		ModuleConfig mConfig = (ModuleConfig)servletcontext.getAttribute(Globals.MODULE_KEY);

		if(mConfig != null)
			return (ActionMapping)mConfig.findActionConfig(actionname);

		return null;
	}

	public static ActionForward startInterceptors() throws IOException, ServletException
	{
		// ActionMapping is extended to provide interceptor interface
		if(me().getMapping() instanceof ActionMappingExtended)
		{
			for(int i = 0; i < ((ActionMappingExtended)me().getMapping()).getInterceptors().size(); i++)
			{
				ActionInterceptorInterface interceptor = (ActionInterceptorInterface)((ActionMappingExtended)me().getMapping()).getInterceptors().get(i);
				ActionForward forward = interceptor.beforeMethod();

				if(forward != null)
				{
					return forward;
				}
			}
		}
		return null;
	}

	public static ActionForward endInterceptors() throws ServletException
	{
		try
		{
			addErrors();
			addMessages();

			if(me().getMapping() instanceof ActionMappingExtended)
			{
				for(int i = 0; i < ((ActionMappingExtended)me().getMapping()).getInterceptors().size(); i++)
				{
					ActionInterceptorInterface interceptor = (ActionInterceptorInterface)((ActionMappingExtended)me().getMapping()).getInterceptors().get(i);
					ActionForward forward = interceptor.afterMethod();

					if(forward != null)
					{
						return forward;
					}
				}
			}

		}
		finally
		{
		}
		return null;
	}

	protected static void addErrors()
	{
		HttpServletRequest request = me().getRequest();
		ActionMessages errors = me().getErrormsgs();

		if(errors == null)
			return;

		// get any existing errors from the request, or make a new one
		ActionMessages requestErrors = (ActionMessages)request.getAttribute(Globals.ERROR_KEY);
		if(requestErrors == null)
		{
			requestErrors = new ActionMessages();
		}

		// add incoming errors
		try
		{
			if(requestErrors != errors)
				requestErrors.add(errors);
		}
		catch(Exception e)
		{
			System.out.println("CONCURRBUG");
		}

		// if still empty, just wipe it out from the request
		if(requestErrors.isEmpty())
		{
			request.removeAttribute(Globals.ERROR_KEY);
			return;
		}

		// Save the errors
		request.setAttribute(Globals.ERROR_KEY, requestErrors);
	}

	public static String getResource(String key)
	{
		MessageResources resources = (MessageResources)me().getRequest().getAttribute(Globals.MESSAGES_KEY);

		return resources.getMessage(getLocale(), key);
	}

	public static void reloadResources()
	{
		MessageResources resources = (UniversalMessageResources)me().getRequest().getAttribute(Globals.MESSAGES_KEY);

		if(resources instanceof UniversalMessageResources)
			((UniversalMessageResources)resources).reload();
	}

	protected static void addMessages()
	{
		HttpServletRequest request = getRequest();
		ActionMessages messages = getInfomsgs();

		if(messages == null)
			return;

		// get any existing messages from the request, or make a new one
		ActionMessages requestMessages = (ActionMessages)request.getAttribute(Globals.MESSAGE_KEY);
		if(requestMessages == null)
		{
			requestMessages = new ActionMessages();
		}

		// add incoming messages
		try
		{
			if(requestMessages != messages)
				requestMessages.add(messages);
		}
		catch(Exception e)
		{
			System.out.println("CONCURRBUG");
		}

		// if still empty, just wipe it out from the request
		if(requestMessages.isEmpty())
		{
			request.removeAttribute(Globals.MESSAGE_KEY);
			return;
		}

		// Save the messages
		request.setAttribute(Globals.MESSAGE_KEY, requestMessages);
	}

	public static void addError(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getErrormsgs().add(alias, msg);
	}

	public static void addError(String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getErrormsgs().add("", msg);
	}

	public static void addError(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getErrormsgs().add(alias, msg);
	}

	public static void addError(String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getErrormsgs().add("", msg);
	}

	public static void addMessage(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getInfomsgs().add(alias, msg);
	}

	public static void addMessage(String text)
	{
		ActionMessage msg = new ActionMessage(text);
		getInfomsgs().add("", msg);
	}

	public static void addMessage(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getInfomsgs().add(alias, msg);
	}

	public static void addMessage(String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		getInfomsgs().add("", msg);
	}

	public static boolean hasErrors()
	{
		return !getErrormsgs().isEmpty();
	}

	public static boolean hasMessages()
	{
		return !getInfomsgs().isEmpty();
	}

	public static boolean isUserInRole(String role)
	{
		return getRequest().isUserInRole(role);
	}

	public static void validate(boolean condition, String property, String alias)
	{
		if(condition)
			addError(property, alias);
	}

	public static void validate(boolean condition, String alias)
	{
		if(condition)
			addError(alias);
	}

	public static void setLocale(String language)
	{
		Utils.setLocale(getSession(), language);
		me().setLocale(new Locale(language));
	}

	/**
	 * Get session data in getter function, make it sessionenabled
	 * 
	 * return getGlobal("debnr", "0");
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	public static String getGlobal(String name, String def)
	{
		String val = (String)getSession().getAttribute(name);
		return val == null ? def : val;
	}

	/**
	 * Set data in session in setter function, make it sessionenabled
	 * 
	 * setGlobal("debnr", debnrKunde); - optional this.debnrKunde =
	 * setGlobal("debnr", debnrKunde);
	 * 
	 * @param name
	 * @param val
	 * @return
	 */
	public static String setGlobal(String name, String val)
	{
		try
		{
			getSession().setAttribute(name, val);
		}
		catch(Exception e)
		{
		}
		return val;
	}

	public static ConfigWrapper getAction(String actionname)
	{
		return (ConfigWrapper)ActionHelper.actions.get(actionname);
	}

	public static void addAction(String actionname, ConfigWrapper action)
	{
		ActionHelper.actions.put(actionname, action);
	}

	public static boolean isWSAction()
	{
		if(ActionHelper.getMapping() instanceof ActionMappingExtended)
			return ((ActionMappingExtended)ActionHelper.getMapping()).isWsaction();
		return false;
	}

	public static boolean isRemoteAction()
	{
		if(ActionHelper.getMapping() instanceof ActionMappingExtended)
			return ((ActionMappingExtended)ActionHelper.getMapping()).isRemoteaction();
		return false;
	}

	public static boolean isHeading()
	{
		if(ActionHelper.getMapping() instanceof ActionMappingExtended)
			return ((ActionMappingExtended)ActionHelper.getMapping()).isHeading();
		return true;
	}

	public static void setHeading(boolean heading)
	{
		if(ActionHelper.getMapping() instanceof ActionMappingExtended)
			((ActionMappingExtended)ActionHelper.getMapping()).setHeading(heading);
	}
}
