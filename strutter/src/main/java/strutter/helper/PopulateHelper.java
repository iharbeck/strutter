package strutter.helper;

import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.HashPrintJobAttributeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

import strutter.action.FormlessInterface;

public class PopulateHelper
{
	private static Log log = LogFactory.getLog(PopulateHelper.class);

	public static Map populate(HttpServletRequest request, Object bean) throws ServletException
	{ 
		Map parameters = null;

		String contentType = request.getContentType();
		String method = request.getMethod();
		boolean isMultipart = false;

		// Webservice will not implement FormlessInterface
		if(bean instanceof FormlessInterface)
		{ 
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
					return parameters;
				}
				// retrieve form values and put into properties
				parameters = getAllParametersForMultipartRequest(request, multipartHandler);
			}
		}

		if(!isMultipart)
		{
			parameters = request.getParameterMap();
		}

		try
		{
		    // Exclude 
			// ^class\..*,^dojo\..*,^struts\..*,^session\..*,^request\..*,^application\..*,^servlet(Request|Response)\..*,^parameters\..*,^action:.*,^method:.*
			
			//parameters.remove("class");
			
			HashMap tMap = new HashMap();
			
			tMap.putAll(parameters);
			tMap.remove("class");
			
			
			//BeanUtil.populateBean(bean, parameters);
			BeanUtils.populate(bean, tMap);
		}
		catch(Exception e)
		{
			throw new ServletException("BeanUtils.populate", e);
		}
		
		return parameters;
	}

	private static Map getAllParametersForMultipartRequest(
	        HttpServletRequest request,
	        MultipartRequestHandler multipartHandler)
	{
		return multipartHandler.getAllElements();
		
//		Map parameters = new HashMap(multipartHandler.getAllElements().size() + request.getParameterMap().size());
//		
//
//		if(request instanceof MultipartRequestWrapper)
//		{
//			parameters.putAll(multipartHandler.getAllElements());
//		}
//		else
//		{
//			parameters.putAll(request.getParameterMap());
//			//log.debug("Gathering multipart parameters for unwrapped request");
//		}
//
//		return parameters;
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
