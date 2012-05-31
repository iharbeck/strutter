package strutter.helper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import jodd.bean.BeanUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

import strutter.action.FormlessInterface;

public class PopulateHelper
{
	private static Log log = LogFactory.getLog(PopulateHelper.class);

	public static void populate(Object bean, HttpServletRequest request) throws ServletException
	{
		populate(bean, null, null, request);
	}

	public static HashMap populate(Object bean, String prefix, String suffix, HttpServletRequest request)
	        throws ServletException
	{
		HashMap properties = new HashMap();

		Enumeration names = null;
		Map multipartParameters = null;

		String contentType = request.getContentType();
		String method = request.getMethod();
		boolean isMultipart = false;

		if(bean instanceof FormlessInterface)
		{ // Webservice will not implement FormlessInterface
			((FormlessInterface)bean).reset();
		}

		MultipartRequestHandler multipartHandler = null;
		if((contentType != null) && (contentType.startsWith("multipart/form-data")) && (method.equalsIgnoreCase("POST")))
		{
			// Obtain a MultipartRequestHandler
			multipartHandler = getMultipartHandler(request);

			if(multipartHandler != null)
			{
				isMultipart = true;
				// Set servlet and mapping info

				multipartHandler.setMapping((ActionMapping)request.getAttribute(Globals.MAPPING_KEY));
				// Initialize multipart request class handler

				multipartHandler.handleRequest(request);
				// stop here if the maximum length has been exceeded
				Boolean maxLengthExceeded =
				        (Boolean)request.getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);

				if((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue()))
				{
					// ((FormlessInterface)
					// bean).setMultipartRequestHandler(multipartHandler);
					return properties;
				}
				// retrieve form values and put into properties
				multipartParameters = getAllParametersForMultipartRequest(request, multipartHandler);
				names = Collections.enumeration(multipartParameters.keySet());
			}
		}

		if(!isMultipart)
		{
			names = request.getParameterNames();
		}

		while(names.hasMoreElements())
		{
			String name = (String)names.nextElement();
			String stripped = name;
			if(prefix != null)
			{
				if(!stripped.startsWith(prefix))
				{
					continue;
				}
				stripped = stripped.substring(prefix.length());
			}
			if(suffix != null)
			{
				if(!stripped.endsWith(suffix))
				{
					continue;
				}
				stripped = stripped.substring(0, stripped.length() - suffix.length());
			}
			Object parameterValue = null;
			if(isMultipart)
			{
				parameterValue = multipartParameters.get(name);
			}
			else
			{
				parameterValue = request.getParameterValues(name);
			}

			// Populate parameters, except "standard" struts attributes
			// such as 'org.apache.struts.action.CANCEL'
			if(!(stripped.startsWith("org.apache.struts.")))
			{
				properties.put(stripped, parameterValue);
			}
		}

		// Set the corresponding properties of our bean
		try
		{
			//BeanUtil.populateBean(bean, properties);
			BeanUtils.populate(bean, properties);
		}
		catch(Exception e)
		{
			throw new ServletException("BeanUtils.populate", e);
		}

		return properties;
	}

	private static Map getAllParametersForMultipartRequest(
	        HttpServletRequest request,
	        MultipartRequestHandler multipartHandler)
	{

		Map parameters = new HashMap();
		Hashtable elements = multipartHandler.getAllElements();
		Enumeration e = elements.keys();
		while(e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			parameters.put(key, elements.get(key));
		}

		if(request instanceof MultipartRequestWrapper)
		{
			request = (HttpServletRequest)((MultipartRequestWrapper)request).getRequest();
			e = request.getParameterNames();
			while(e.hasMoreElements())
			{
				String key = (String)e.nextElement();
				parameters.put(key, request.getParameterValues(key));
			}
		}
		else
		{
			log.debug("Gathering multipart parameters for unwrapped request");
		}

		return parameters;
	}

	private static MultipartRequestHandler getMultipartHandler(HttpServletRequest request)
	        throws ServletException
	{
		MultipartRequestHandler multipartHandler = null;
		String multipartClass = (String)request.getAttribute(Globals.MULTIPART_KEY);
		request.removeAttribute(Globals.MULTIPART_KEY);

		// Try to initialize the mapping specific request handler
		if(multipartClass != null)
		{
			try
			{
				multipartHandler = (MultipartRequestHandler)applicationInstance(multipartClass);
			}
			catch(ClassNotFoundException cnfe)
			{
				log.error(
				        "MultipartRequestHandler class \""
				                + multipartClass
				                + "\" in mapping class not found, "
				                + "defaulting to global multipart class");
			}
			catch(InstantiationException ie)
			{
				log.error(
				        "InstantiationException when instantiating "
				                + "MultipartRequestHandler \""
				                + multipartClass
				                + "\", "
				                + "defaulting to global multipart class, exception: "
				                + ie.getMessage());
			}
			catch(IllegalAccessException iae)
			{
				log.error(
				        "IllegalAccessException when instantiating "
				                + "MultipartRequestHandler \""
				                + multipartClass
				                + "\", "
				                + "defaulting to global multipart class, exception: "
				                + iae.getMessage());
			}

			if(multipartHandler != null)
			{
				return multipartHandler;
			}
		}

		ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request);

		multipartClass = moduleConfig.getControllerConfig().getMultipartClass();

		// Try to initialize the global request handler
		if(multipartClass != null)
		{
			try
			{
				multipartHandler = (MultipartRequestHandler)applicationInstance(multipartClass);

			}
			catch(ClassNotFoundException cnfe)
			{
				throw new ServletException(
				        "Cannot find multipart class \""
				                + multipartClass
				                + "\""
				                + ", exception: "
				                + cnfe.getMessage());

			}
			catch(InstantiationException ie)
			{
				throw new ServletException(
				        "InstantiationException when instantiating "
				                + "multipart class \""
				                + multipartClass
				                + "\", exception: "
				                + ie.getMessage());

			}
			catch(IllegalAccessException iae)
			{
				throw new ServletException(
				        "IllegalAccessException when instantiating "
				                + "multipart class \""
				                + multipartClass
				                + "\", exception: "
				                + iae.getMessage());
			}

			if(multipartHandler != null)
			{
				return multipartHandler;
			}
		}

		return multipartHandler;
	}

	public final static Object applicationInstance(String className)
	        throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		// Look up the class loader to be used
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null)
		{
			classLoader = RequestUtils.class.getClassLoader();
		}

		// Attempt to load the specified class
		return (classLoader.loadClass(className)).newInstance();
	}
}
