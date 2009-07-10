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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

import struts.Utils;

public class FormlessDispatchAction extends ExtDispatchAction 
{
	private static Log log = LogFactory.getLog(FormlessDispatchAction.class);

	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception 
	{
		//Get existing Action
		//TODO: integrate Spring
		FormlessDispatchAction action = (FormlessDispatchAction)Utils.getActionFormFromSession(request);
		
		if(action == null)	// No Session scope or first call
			action = (FormlessDispatchAction) this.getClass().newInstance();
		
		//Message/Error handler
		action.startExt(request);
		
		action.reset(mapping, request);
			
		//BeanUtils.populate(this, request.getParameterMap());
		
		populate(action, mapping.getPrefix(), mapping.getSuffix(), request);

		// Store Action (Form)
		Utils.setActionForm(request, action);

		// Get the parameter. This could be overridden in subclasses.
        String parameter = action.getParameter(mapping, null, request, response);

        // Get the method's name. This could be overridden in subclasses.
        String name = action.getMethodName(mapping, null, request, response, parameter);

        ActionForward forward = action.dispatchMethod(mapping, null, request, response, name);
        
        // Message/Error handler
        action.endExt();
        
		return forward;
	}

	
	public static void populate(Object bean, String prefix, String suffix, HttpServletRequest request)
            throws ServletException 
    {
        // Build a list of relevant request parameters from this request
        HashMap properties = new HashMap();
        // Iterator of parameter names
        Enumeration names = null;
        // Map for multipart parameters
        Map multipartParameters = null;

        String contentType = request.getContentType();
        String method = request.getMethod();
        boolean isMultipart = false;

        if (bean instanceof ActionForm) {
            ((ActionForm) bean).setMultipartRequestHandler(null);
        }

        MultipartRequestHandler multipartHandler = null;
        if ((contentType != null)
                && (contentType.startsWith("multipart/form-data"))
                && (method.equalsIgnoreCase("POST"))) {

            // Get the ActionServletWrapper from the form bean
            //ActionServletWrapper servlet;
//            if (bean instanceof ActionForm) {
                //servlet = ((ActionForm) bean).getServletWrapper();
//            } else {
//                throw new ServletException(
//                        "bean that's supposed to be "
//                        + "populated from a multipart request is not of type "
//                        + "\"org.apache.struts.action.ActionForm\", but type "
//                        + "\""
//                        + bean.getClass().getName()
//                        + "\"");
//            }

            // Obtain a MultipartRequestHandler
            multipartHandler = getMultipartHandler(request);

            if (multipartHandler != null) {
                isMultipart = true;
                // Set servlet and mapping info

                multipartHandler.setMapping(
                        (ActionMapping) request.getAttribute(Globals.MAPPING_KEY));
                // Initialize multipart request class handler
                
                multipartHandler.handleRequest(request);
                //stop here if the maximum length has been exceeded
                Boolean maxLengthExceeded =
                        (Boolean) request.getAttribute(
                                MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
                if ((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
                    ((ActionForm) bean).setMultipartRequestHandler(multipartHandler);
                    return;
                }
                //retrieve form values and put into properties
                multipartParameters = getAllParametersForMultipartRequest(
                        request, multipartHandler);
                names = Collections.enumeration(multipartParameters.keySet());
            }
        }

        if (!isMultipart) {
            names = request.getParameterNames();
        }

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String stripped = name;
            if (prefix != null) {
                if (!stripped.startsWith(prefix)) {
                    continue;
                }
                stripped = stripped.substring(prefix.length());
            }
            if (suffix != null) {
                if (!stripped.endsWith(suffix)) {
                    continue;
                }
                stripped = stripped.substring(0, stripped.length() - suffix.length());
            }
            Object parameterValue = null;
            if (isMultipart) {
                parameterValue = multipartParameters.get(name);
            } else {
                parameterValue = request.getParameterValues(name);
            }

            // Populate parameters, except "standard" struts attributes
            // such as 'org.apache.struts.action.CANCEL'
            if (!(stripped.startsWith("org.apache.struts."))) {
                properties.put(stripped, parameterValue);
            }
        }

        // Set the corresponding properties of our bean
        try {
            BeanUtils.populate(bean, properties);
        } catch(Exception e) {
            throw new ServletException("BeanUtils.populate", e);
        }

    }
	
	  private static Map getAllParametersForMultipartRequest(
	            HttpServletRequest request,
	            MultipartRequestHandler multipartHandler) {

	        Map parameters = new HashMap();
	        Hashtable elements = multipartHandler.getAllElements();
	        Enumeration e = elements.keys();
	        while (e.hasMoreElements()) {
	            String key = (String) e.nextElement();
	            parameters.put(key, elements.get(key));
	        }

	        if (request instanceof MultipartRequestWrapper) {
	            request = ((MultipartRequestWrapper) request).getRequest();
	            e = request.getParameterNames();
	            while (e.hasMoreElements()) {
	                String key = (String) e.nextElement();
	                parameters.put(key, request.getParameterValues(key));
	            }
	        } else {
	            log.debug("Gathering multipart parameters for unwrapped request");
	        }

	        return parameters;
	    }
	
	
	private static MultipartRequestHandler getMultipartHandler(HttpServletRequest request)
    	throws ServletException 
    {

		MultipartRequestHandler multipartHandler = null;
		String multipartClass = (String) request.getAttribute(Globals.MULTIPART_KEY);
		request.removeAttribute(Globals.MULTIPART_KEY);

		// Try to initialize the mapping specific request handler
		if (multipartClass != null) {
			try {
				multipartHandler = (MultipartRequestHandler) applicationInstance(multipartClass);
			} catch(ClassNotFoundException cnfe) {
				log.error(
                "MultipartRequestHandler class \""
                + multipartClass
                + "\" in mapping class not found, "
                + "defaulting to global multipart class");
			} catch(InstantiationException ie) {
				log.error(
                "InstantiationException when instantiating "
                + "MultipartRequestHandler \""
                + multipartClass
                + "\", "
                + "defaulting to global multipart class, exception: "
                + ie.getMessage());
			} catch(IllegalAccessException iae) {
				log.error(
                "IllegalAccessException when instantiating "
                + "MultipartRequestHandler \""
                + multipartClass
                + "\", "
                + "defaulting to global multipart class, exception: "
                + iae.getMessage());
			}

			if (multipartHandler != null) {
				return multipartHandler;
			}
		}

		ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request);
		
		multipartClass = moduleConfig.getControllerConfig().getMultipartClass();

		// Try to initialize the global request handler
		if (multipartClass != null) {
		    try {
		        multipartHandler = (MultipartRequestHandler) applicationInstance(multipartClass);
		
		    } catch(ClassNotFoundException cnfe) {
		        throw new ServletException(
		                "Cannot find multipart class \""
		                + multipartClass
		                + "\""
		                + ", exception: "
		                + cnfe.getMessage());
		
		    } catch(InstantiationException ie) {
		        throw new ServletException(
		                "InstantiationException when instantiating "
		                + "multipart class \""
		                + multipartClass
		                + "\", exception: "
		                + ie.getMessage());
		
		    } catch(IllegalAccessException iae) {
		        throw new ServletException(
		                "IllegalAccessException when instantiating "
		                + "multipart class \""
		                + multipartClass
		                + "\", exception: "
		                + iae.getMessage());
		    }
		
		    if (multipartHandler != null) {
		        return multipartHandler;
		    }
		}

		return multipartHandler;
	}
	
	  public static Object applicationInstance(String className)
      	throws ClassNotFoundException, IllegalAccessException, InstantiationException 
      {
		  return (applicationClass(className).newInstance());
	  }
	  
	  public static Class applicationClass(String className) throws ClassNotFoundException 
	  {
	        // Look up the class loader to be used
	        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        if (classLoader == null) {
	            classLoader = RequestUtils.class.getClassLoader();
	        }

	        // Attempt to load the specified class
	        return (classLoader.loadClass(className));
	  }
}