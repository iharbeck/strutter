package strutter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

public class Utils
{
	public static void setLocale(HttpSession session, String language)
	{
		if(language != null)
			session.setAttribute(Globals.LOCALE_KEY, new Locale(language));
	}

	public static void processForwardConfig(HttpServletRequest request,
			HttpServletResponse response, ForwardConfig forward)
			throws IOException, ServletException {

		if (forward == null) {
			return;
		}

		String forwardPath = forward.getPath();
		String uri = null;

		// paths not starting with / should be passed through without any
		// processing
		// (ie. they're absolute)
		if (forwardPath.startsWith("/")) {
			uri = RequestUtils.forwardURL(request, forward, null); // get
																	// module
																	// relative
																	// uri
		} else {
			uri = forwardPath;
		}

		if (forward.getRedirect()) {
			// only prepend context path for relative uri
			if (uri.startsWith("/")) {
				uri = request.getContextPath() + uri;
			}
			response.sendRedirect(response.encodeRedirectURL(uri));

		} else {
			doForward(uri, request, response);
		}

	}

	protected static void doForward(String uri, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Unwrap the multipart request, if there is one.
		if (request instanceof MultipartRequestWrapper) {
			request = (HttpServletRequest) ((MultipartRequestWrapper) request).getRequest();
		}

		RequestDispatcher rd = request.getRequestDispatcher(uri);
		if (rd == null) {
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

	    /****************** Struts ServletContext Resources ****************/

	    /**
	     * Returns the message resources for this application or <code>null</code>
	     * if not found.
	     *
	     * @param app the servlet context
	     * @since VelocityTools 1.1
	     */
	    public static MessageResources getMessageResources(HttpServletRequest request,
	                                                       ServletContext app)
	    {
	        /* Identify the current module */
	        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, app);
	        return (MessageResources)app.getAttribute(Globals.MESSAGES_KEY +
	                                                  moduleConfig.getPrefix());
	    }


	    /**
	     * Returns the message resources with the specified bundle name for this application
	     * or <code>null</code> if not found.
	     *
	     * @param app the servlet context
	     * @param bundle The bundle name to look for.  If this is <code>null</code>, the
	     *               default bundle name is used.
	     * @since VelocityTools 1.1
	     */
	    public static MessageResources getMessageResources(HttpServletRequest request,
	                                                       ServletContext app,
	                                                       String bundle)
	    {
	        MessageResources resources = null;

	        /* Identify the current module */
	        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, app);


	        if (bundle == null) {
	            bundle = Globals.MESSAGES_KEY;
	        }

	        // First check request scope
	        resources = (MessageResources) request.getAttribute(bundle + moduleConfig.getPrefix());

	        if (resources == null) {
	            resources = (MessageResources) app.getAttribute(bundle + moduleConfig.getPrefix());
	        }

	        return resources;
	    }


	    /**
	     * Select the module to which the specified request belongs, and
	     * add return the corresponding ModuleConfig.
	     *
	     * @param urlPath The requested URL
	     * @param app The ServletContext for this web application
	     * @return The ModuleConfig for the given URL path
	     * @since VelocityTools 1.1
	     */
	    public static ModuleConfig selectModule(String urlPath,
	                                            ServletContext app)
	    {
	        /* Match against the list of sub-application prefixes */
	        String prefix = ModuleUtils.getInstance().getModuleName(urlPath, app);

	        /* Expose the resources for this sub-application */
	        ModuleConfig config = (ModuleConfig)
	            app.getAttribute(Globals.MODULE_KEY + prefix);

	        return config;
	    }


	    /********************** Struts Session Resources ******************/

	    /**
	     * Returns the <code>java.util.Locale</code> for the user. If a
	     * locale object is not found in the user's session, the system
	     * default locale is returned.
	     *
	     * @param request the servlet request
	     * @param session the HTTP session
	     */
	    public static Locale getLocale(HttpServletRequest request,
	                                   HttpSession session)
	    {
	        Locale locale = null;

	        if (session != null)
	        {
	            locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
	        }
	        if (locale == null)
	        {
	            locale = request.getLocale();
	        }
	        return locale;
	    }


	    /**
	     * Returns the transaction token stored in this session or
	     * <code>null</code> if not used.
	     *
	     * @param session the HTTP session
	     */
	    public static String getToken(HttpSession session)
	    {
	        if (session == null)
	        {
	            return null;
	        }
	        return (String)session.getAttribute(Globals.TRANSACTION_TOKEN_KEY);
	    }


	    /*********************** Struts Request Resources ****************/

	    /**
	     * Returns the Struts errors for this request or <code>null</code>
	     * if none exist. Since VelocityTools 1.2, this will also check
	     * the session (if there is one) for errors if there are no errors
	     * in the request.
	     *
	     * @param request the servlet request
	     * @since VelocityTools 1.1
	     */
	    public static ActionMessages getErrors(HttpServletRequest request)
	    {
	        ActionMessages errors = (ActionMessages)request.getAttribute(Globals.ERROR_KEY);
	        if (errors == null || errors.isEmpty())
	        {
	            // then check the session
	            HttpSession session = request.getSession(false);
	            if (session != null)
	            {
	                errors = (ActionMessages)session.getAttribute(Globals.ERROR_KEY);
	            }
	        }
	        return errors;
	    }

	    /**
	     * Returns the Struts messages for this request or <code>null</code>
	     * if none exist.  Since VelocityTools 1.2, this will also check
	     * the session (if there is one) for messages if there are no messages
	     * in the request.
	     *
	     * @param request the servlet request
	     * @since VelocityTools 1.1
	     */
	    public static ActionMessages getMessages(HttpServletRequest request)
	    {
	        ActionMessages messages = (ActionMessages)request.getAttribute(Globals.MESSAGE_KEY);
	        if (messages == null || messages.isEmpty())
	        {
	            // then check the session
	            HttpSession session = request.getSession(false);
	            if (session != null)
	            {
	                messages = (ActionMessages)session.getAttribute(Globals.MESSAGE_KEY);
	            }
	        }
	        return messages;
	    }

	    public static Object getObject(HttpServletRequest request, String name)
	    {
	        Object obj = request.getAttribute(name);
	        if (obj == null)
	        {
	            // then check the session
	            HttpSession session = request.getSession(false);
	            if (session != null)
	            {
	            	obj = session.getAttribute(name);
	            }
	        }
	        return obj;
	    }

	    /**
	     * Returns the <code>ActionForm</code> bean associated with
	     * this request of <code>null</code> if none exists.
	     *
	     * @param request the servlet request
	     * @param session the HTTP session
	     */
	    public static Object getActionForm(HttpServletRequest request)
	    {
	        /* Is there a mapping associated with this request? */
	        ActionConfig mapping = getActionConfig(request);
	        if (mapping == null)
	        {
	            return null;
	        }

	        /* Is there a form bean associated with this mapping? */
	        String attribute = mapping.getAttribute();
	        if (attribute == null)
	        {
	            return null;
	        }

	        /* Look up the existing form bean */
	        if ("request".equals(mapping.getScope()))
	        {
	            return request.getAttribute(attribute);
	        }

	        HttpSession session = request.getSession(false);
	        if (session != null)
	        {
	            return session.getAttribute(attribute);
	        }
	        return null;
	    }

	    public static Object getActionForm(HttpServletRequest request, Class clazz)
	    {
	        String attribute = clazz.getName().substring(clazz.getName().lastIndexOf('.')+1);

	        HttpSession session = request.getSession(false);
	        if (session != null)
	        {
	            return session.getAttribute(attribute);
	        }

	        return null;
	    }

	    
	    public static Object getActionFormFromSession(HttpServletRequest request)
	    {
	        /* Is there a mapping associated with this request? */
	        ActionConfig mapping = getActionConfig(request);
	        if (mapping == null)
	        {
	            return null;
	        }

	        /* Is there a form bean associated with this mapping? */
	        String attribute = mapping.getAttribute();
	        if (attribute == null)
	        {
	            return null;
	        }

	        /* Look up the existing form bean */
	        if ("request".equals(mapping.getScope()))
	        {
	            return null;
	        }

	        HttpSession session = request.getSession(false);
	        if (session != null)
	        {
	            return session.getAttribute(attribute);
	        }
	        return null;
	    }

	    public static void setActionForm(HttpServletRequest request, Action action)
	    {
	    	/* Is there a mapping associated with this request? */
	        ActionConfig mapping = getActionConfig(request);
	        if (mapping == null)
	            return;

	        /* Look up the existing form bean */
	        if ("request".equals(mapping.getScope()))
	        {
	            request.setAttribute(mapping.getAttribute(), action);
	            return;
	        }

	        HttpSession session = request.getSession(false);
	        if (session != null)
	        {
	            session.setAttribute(mapping.getAttribute(), action);
	            return;
	        }
	    }


	    /**
	     * Returns the ActionForm name associated with
	     * this request of <code>null</code> if none exists.
	     *
	     * @param request the servlet request
	     * @param session the HTTP session
	     */
	    public static String getActionFormName(HttpServletRequest request,
	                                           HttpSession session)
	    {
	        /* Is there a mapping associated with this request? */
	        ActionConfig mapping = getActionConfig(request);
	        if (mapping == null)
	        {
	            return null;
	        }

	        return mapping.getAttribute();
	    }

	    public static ActionConfig getActionConfig(HttpServletRequest request) {
	    	return (ActionConfig)request.getAttribute(Globals.MAPPING_KEY);
	    }



	    /*************************** Utilities *************************/

	    /**
	     * Return the form action converted into an action mapping path.  The
	     * value of the <code>action</code> property is manipulated as follows in
	     * computing the name of the requested mapping:
	     * <ul>
	     * <li>Any filename extension is removed (on the theory that extension
	     *     mapping is being used to select the controller servlet).</li>
	     * <li>If the resulting value does not start with a slash, then a
	     *     slash is prepended.</li>
	     * </ul>
	     */
	    public static String getActionMappingName_(String action) {

	        String value = action;
	        int question = action.indexOf("?");
	        if (question >= 0) {
	            value = value.substring(0, question);
	        }

	        int slash = value.lastIndexOf("/");
	        int period = value.lastIndexOf(".");
	        if ((period >= 0) && (period > slash)) {
	            value = value.substring(slash, period);
	        }

	        return value.startsWith("/") ? value : ("/" + value);
	    }
	    
	    public static final String INCLUDE_SERVLET_PATH =  "javax.servlet.include.servlet_path";
	    public static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
	    
	    public static String getActionMappingName(HttpServletRequest request) throws IOException {
	            String path;

	            // For prefix matching, match on the path info (if any)
	            path = (String) request.getAttribute(INCLUDE_PATH_INFO);

	            if (path == null) {
	                path = request.getPathInfo();
	            }

	            if ((path != null) && (path.length() > 0)) {
	                return (path);
	            }

	            // For extension matching, strip the module prefix and extension
	            path = (String) request.getAttribute(INCLUDE_SERVLET_PATH);

	            if (path == null) {
	                path = request.getServletPath();
	            }

	            int slash = path.lastIndexOf("/");
	            int period = path.lastIndexOf(".");

	            if ((period >= 0) && (period > slash)) {
	                path = path.substring(0, period);
	            }

	            return (path);
	        }

	    /**
	     * Returns the action forward name converted into a server-relative URI
	     * reference.
	     *
	     * @param app the servlet context
	     * @param request the servlet request
	     * @param forward the name of a forward as per struts-config.xml
	     */
	    public static String getForwardURL(HttpServletRequest request,
	                                       ServletContext app,
	                                       String forward)
	    {
	        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, app);
	        //TODO? beware of null module config if ActionServlet isn't init'ed?
	        ForwardConfig fc = moduleConfig.findForwardConfig(forward);
	        if (fc == null)
	        {
	            return null;
	        }

	        StringBuffer url = new StringBuffer();
	        if (fc.getPath().startsWith("/"))
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


	    /**
	     * Returns a formatted error message. The error message is assembled from
	     * the following three pieces: First, value of message resource
	     * "errors.header" is prepended. Then, the list of error messages is
	     * rendered. Finally, the value of message resource "errors.footer"
	     * is appended.
	     *
	     * @param property the category of errors to markup and return
	     * @param request the servlet request
	     * @param session the HTTP session
	     * @param application the servlet context
	     *
	     * @return The formatted error message. If no error messages are queued,
	     * an empty string is returned.
	     */
	    public static String errorMarkup(String property,
	                                     HttpServletRequest request,
	                                     HttpSession session,
	                                     ServletContext application)
	    {
	        return errorMarkup(property, null, request, session, application);
	    }


	    /**
	     * Returns a formatted error message. The error message is assembled from
	     * the following three pieces: First, value of message resource
	     * "errors.header" is prepended. Then, the list of error messages is
	     * rendered. Finally, the value of message resource "errors.footer"
	     * is appended.
	     *
	     * @param property the category of errors to markup and return
	     * @param bundle the message resource bundle to use
	     * @param request the servlet request
	     * @param session the HTTP session
	     * @param application the servlet context
	     * @since VelocityTools 1.1
	     * @return The formatted error message. If no error messages are queued,
	     * an empty string is returned.
	     */
	    public static String errorMarkup(String property,
	                                     String bundle,
	                                     HttpServletRequest request,
	                                     HttpSession session,
	                                     ServletContext application)
	    {
	        ActionMessages errors = getErrors(request);
	        if (errors == null)
	        {
	            return "";
	        }

	        /* fetch the error messages */
	        Iterator reports = null;
	        if (property == null)
	        {
	            reports = errors.get();
	        }
	        else
	        {
	            reports = errors.get(property);
	        }

	        if (!reports.hasNext())
	        {
	            return "";
	        }

	        /* Render the error messages appropriately if errors have been queued */
	        StringBuffer results = new StringBuffer();
	        String header = null;
	        String footer = null;
	        String prefix = null;
	        String suffix = null;
	        Locale locale = getLocale(request, session);

	        MessageResources resources =
	            getMessageResources(request, application, bundle);
	        if (resources != null)
	        {
	            header = resources.getMessage(locale, "errors.header");
	            footer = resources.getMessage(locale, "errors.footer");
	            prefix = resources.getMessage(locale, "errors.prefix");
	            suffix = resources.getMessage(locale, "errors.suffix");
	        }
	        if (header == null)
	        {
	            header = "errors.header";
	        }
	        if (footer == null)
	        {
	            footer = "errors.footer";
	        }
	        /* prefix or suffix are optional, be quiet if they're missing */
	        if (prefix == null)
	        {
	            prefix = "";
	        }
	        if (suffix == null)
	        {
	            suffix = "";
	        }

	        results.append(header);
	        results.append("\r\n");

	        String message;
	        while (reports.hasNext())
	        {
	            message = null;
	            ActionMessage report = (ActionMessage)reports.next();
	            if (resources != null && report.isResource())
	            {
	                message = resources.getMessage(locale,
	                                               report.getKey(),
	                                               report.getValues());
	            }

	            results.append(prefix);

	            if (message != null)
	            {
	                results.append(message);
	            }
	            else
	            {
	                results.append(report.getKey());
	            }

	            results.append(suffix);
	            results.append("\r\n");
	        }

	        results.append(footer);
	        results.append("\r\n");

	        /* return result */
	        return results.toString();
	    }

	}
