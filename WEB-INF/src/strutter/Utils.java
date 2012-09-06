package strutter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

public class Utils
{
	public static void processForwardConfig(HttpServletRequest request,
	        HttpServletResponse response, ForwardConfig forward)
	        throws IOException, ServletException
	{

		if(forward == null)
		{
			return;
		}

		String forwardPath = forward.getPath();
		String uri = null;

		// paths not starting with / should be passed through without any
		// processing
		// (ie. they're absolute)
		if(forwardPath.startsWith("/"))
		{
			uri = RequestUtils.forwardURL(request, forward, null); // get
			                                                       // module
			                                                       // relative
			                                                       // uri
		}
		else
		{
			uri = forwardPath;
		}

		if(forward.getRedirect())
		{
			// only prepend context path for relative uri
			if(uri.startsWith("/"))
			{
				uri = request.getContextPath() + uri;
			}
			response.sendRedirect(response.encodeRedirectURL(uri));

		}
		else
		{
			doForward(uri, request, response);
		}

	}

	protected static void doForward(String uri, HttpServletRequest request,
	        HttpServletResponse response) throws IOException, ServletException
	{
		// Unwrap the multipart request, if there is one.
		if(request instanceof MultipartRequestWrapper)
		{
			request = (HttpServletRequest)((MultipartRequestWrapper)request).getRequest();
		}

		RequestDispatcher rd = request.getRequestDispatcher(uri);
		if(rd == null)
		{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			        uri);
			return;
		}
		rd.forward(request, response);
	}

	public static void writeFile(String path, FormFile file) throws Exception
	{
		String filename = file.getFileName();

		File f = new File(path + filename);
		FileOutputStream wr = new FileOutputStream(f);
		wr.write(file.getFileData());
	}


	/*********************** Struts Request Resources ****************/

	/**
	 * Returns the Struts errors for this request or <code>null</code> if none
	 * exist. Since VelocityTools 1.2, this will also check the session (if
	 * there is one) for errors if there are no errors in the request.
	 * 
	 * @param request
	 *            the servlet request
	 * @since VelocityTools 1.1
	 */
	public static ActionMessages getErrors(HttpServletRequest request)
	{
		ActionMessages errors = (ActionMessages)request.getAttribute(Globals.ERROR_KEY);
		if(errors == null || errors.isEmpty())
		{
			// then check the session
			HttpSession session = request.getSession(false);
			if(session != null)
			{
				errors = (ActionMessages)session.getAttribute(Globals.ERROR_KEY);
			}
		}
		return errors;
	}

	/**
	 * Returns the Struts messages for this request or <code>null</code> if none
	 * exist. Since VelocityTools 1.2, this will also check the session (if
	 * there is one) for messages if there are no messages in the request.
	 * 
	 * @param request
	 *            the servlet request
	 * @since VelocityTools 1.1
	 */
	public static ActionMessages getMessages(HttpServletRequest request)
	{
		ActionMessages messages = (ActionMessages)request.getAttribute(Globals.MESSAGE_KEY);
		if(messages == null || messages.isEmpty())
		{
			// then check the session
			HttpSession session = request.getSession(false);
			if(session != null)
			{
				messages = (ActionMessages)session.getAttribute(Globals.MESSAGE_KEY);
			}
		}
		return messages;
	}

	public static Object getObject(HttpServletRequest request, String name)
	{
		Object obj = request.getAttribute(name);
		if(obj == null)
		{
			// then check the session
			HttpSession session = request.getSession(false);
			if(session != null)
			{
				obj = session.getAttribute(name);
			}
		}
		return obj;
	}

	public static Object getActionForm(HttpServletRequest request, Class clazz)
	{
		String attribute = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);

		HttpSession session = request.getSession(false);
		if(session != null)
		{
			return session.getAttribute(attribute);
		}

		return null;
	}

	public static Object getActionFormFromSession(HttpServletRequest request)
	{
		/* Is there a mapping associated with this request? */
		ActionConfig mapping = getActionConfig(request);
		if(mapping == null)
		{
			return null;
		}

		/* Is there a form bean associated with this mapping? */
		String attribute = mapping.getAttribute();
		if(attribute == null)
		{
			return null;
		}

		/* Look up the existing form bean */
		if("request".equals(mapping.getScope()))
		{
			return null;
		}

		HttpSession session = request.getSession(false);
		if(session != null)
		{
			return session.getAttribute(attribute);
		}
		return null;
	}

	public static void setActionForm(HttpServletRequest request, Action action)
	{
		/* Is there a mapping associated with this request? */
		ActionConfig mapping = getActionConfig(request);
		if(mapping == null)
			return;

		/* Look up the existing form bean */
		if("request".equals(mapping.getScope()))
		{
			request.setAttribute(mapping.getAttribute(), action);
			return;
		}

		HttpSession session = request.getSession(false);
		if(session != null)
		{
			session.setAttribute(mapping.getAttribute(), action);
			return;
		}
	}

	/**
	 * Returns the ActionForm name associated with this request of
	 * <code>null</code> if none exists.
	 * 
	 * @param request
	 *            the servlet request
	 * @param session
	 *            the HTTP session
	 */
	public static String getActionFormName(HttpServletRequest request,
	        HttpSession session)
	{
		/* Is there a mapping associated with this request? */
		ActionConfig mapping = getActionConfig(request);
		if(mapping == null)
		{
			return null;
		}

		return mapping.getAttribute();
	}

	public static ActionConfig getActionConfig(HttpServletRequest request)
	{
		return (ActionConfig)request.getAttribute(Globals.MAPPING_KEY);
	}

	/*************************** Utilities *************************/

	/**
	 * Return the form action converted into an action mapping path. The value
	 * of the <code>action</code> property is manipulated as follows in
	 * computing the name of the requested mapping:
	 * <ul>
	 * <li>Any filename extension is removed (on the theory that extension
	 * mapping is being used to select the controller servlet).</li>
	 * <li>If the resulting value does not start with a slash, then a slash is
	 * prepended.</li>
	 * </ul>
	 */
	public static String getActionMappingName_(String action)
	{

		String value = action;
		int question = action.indexOf("?");
		if(question >= 0)
		{
			value = value.substring(0, question);
		}

		int slash = value.lastIndexOf("/");
		int period = value.lastIndexOf(".");
		if((period >= 0) && (period > slash))
		{
			value = value.substring(slash, period);
		}

		return value.startsWith("/") ? value : ("/" + value);
	}

	public static String getActionMappingName(HttpServletRequest request) throws IOException
	{
		String path = request.getPathInfo();

		if((path != null) && (path.length() > 0))
		{
			return path;
		}

		path = request.getServletPath();

		int slash = path.lastIndexOf("/");
		int period = path.lastIndexOf(".");

		if((period >= 0) && (period > slash))
		{
			path = path.substring(0, period);
		}

		return path ;
	}

	/**
	 * Returns the action forward name converted into a server-relative URI
	 * reference.
	 * 
	 * @param app
	 *            the servlet context
	 * @param request
	 *            the servlet request
	 * @param forward
	 *            the name of a forward as per struts-config.xml
	 */
	public static String getForwardURL(HttpServletRequest request,
	        ServletContext app,
	        String forward)
	{
		ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, app);
		// TODO? beware of null module config if ActionServlet isn't init'ed?
		ForwardConfig fc = moduleConfig.findForwardConfig(forward);
		if(fc == null)
		{
			return null;
		}

		StringBuffer url = new StringBuffer();
		if(fc.getPath().startsWith("/"))
		{
			url.append(request.getContextPath());
			url.append(RequestUtils.forwardURL(request, fc, moduleConfig));
		}
		else
		{
			url.append(fc.getPath());
		}
		return url.toString();
	}
}
