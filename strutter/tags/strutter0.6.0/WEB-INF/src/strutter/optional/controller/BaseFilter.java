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

package strutter.optional.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import strutter.Utils;
import strutter.config.ActionConfig;
import strutter.config.ActionPlugin;
import strutter.optional.controller.tags.DirectInterface;
import strutter.optional.helper.ActionHelper;

public class BaseFilter implements Filter {

	public FilterConfig filterConfig;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		//extract actionname cut of ".do" results in /actionname
		String look = Utils.getActionMappingName(((HttpServletRequest)request).getServletPath());

		System.out.println(">>" + look);

		boolean isMultipart = ServletFileUpload.isMultipartContent((HttpServletRequest)request);

		try {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List /* FileItem */ items = upload.parseRequest((HttpServletRequest)request);

		} catch (FileUploadException e) {

		}

		System.out.println(request.getParameterMap());

		ConfigWrapper config = ActionHelper.getAction(look);

		if(config == null)
			return;

		try
		{
			DirectInterface bean = (DirectInterface)config.getActionclass().newInstance();

			BeanUtils.populate(bean, request.getParameterMap());

			bean.execute((HttpServletRequest)request, (HttpServletResponse)response);

		} catch (Exception e) {
			System.out.println();
		}
		//chain.doFilter(request, response);

		//RequestDispatcher rd = request.getRequestDispatcher("/home.jsp");
		RequestDispatcher rd = request.getRequestDispatcher("/blank_view.jsp");
		rd.forward(request, response);
	}



	public void init(FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig = filterConfig;

		filterConfig.getServletContext();

		String packagepath = filterConfig.getInitParameter("");
		packagepath  = "";


		// configs

		// actions

		String path = filterConfig.getServletContext().getRealPath("/WEB-INF/classes" + packagepath);

		// Klassen finden
		ArrayList list  = ActionPlugin.getClasses(path, packagepath);

		System.out.println("## looking for direct actions ##");
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			try {
				Class clazz = Class.forName((String) iterator.next(), false, this.getClass().getClassLoader());

				if(!isInterfaceImplementer(clazz, DirectInterface.class))
					continue;

				// store Actionclass and its configuration
				ConfigWrapper wrapper = new ConfigWrapper();


				wrapper.setConfig(new ActionConfig());
				wrapper.setActionclass(clazz);

				wrapper.getConfig().setPath(getPathFromClassName(clazz.getName(), "Action"));

				System.out.println(wrapper.getConfig().getPath() + "\n" + clazz);

				ActionHelper.addAction(wrapper.getConfig().getPath(), wrapper);

			} catch(Exception e) {
				continue;
			}
		}
		System.out.println("## looking for direct actions DONE ##");


//		request.setCharacterEncoding(encoding);  // CharsetFilter
//		response.setCharacterEncoding(encoding);
	}

	String pathlower = "1";
	String pathformat = "{PATH}";


	public void destroy() {
	}


	String getPathFromClassName(String fqn, String alias)
	{
		String className = getClassName(fqn);

		className = cutAlias(className, alias);

		// Default toLowerCase
		if("1".equals(this.getPathlower()))
			className = className.toLowerCase();

		// Eventuell formatieren des PATH
 		className = getPathformat().replaceAll("\\{PATH\\}", className);

		String path = normalizePath(className);

		return path;
	}

	/**
	 * FQN Klassennamen ermitteln
	 */
	private String getClassName(String fqn) {
		String className = fqn;
		if(className.indexOf('.') > 0){
			className = className.substring(className.lastIndexOf('.') + 1);
		}
		return className;
	}

	final String cutAlias(String classname, String patternstr)
	{
		return classname.substring(0, classname.indexOf(patternstr));
	}

	private String normalizePath(String path) {
		String ret = path;

		if(ret == null)
			return "";
		if(!ret.startsWith("/"))
			ret = "/" + ret;
		if(ret.endsWith("/"))
			ret = ret.substring(0, ret.length() - 1);
		if(ret.equals("/"))
			ret = "";

		return ret;
	}


	public String getPathlower() {
		return pathlower;
	}

	public void setPathlower(String pathlower) {
		this.pathlower = pathlower;
	}

	public String getPathformat() {
		return pathformat;
	}

	public void setPathformat(String pathformat) {
		this.pathformat = pathformat;
	}

	/**
	 * Subclass checker
	 */
	boolean isSubclass(Class target, Class superclass) {
		for(Class s = target; s != Object.class; s = s.getSuperclass()){
			if(s == superclass) return true;
		}
		return false;
	}

	boolean isInterfaceImplementer(Class target, Class superclass) {
		Class[] interfaces = target.getInterfaces();
		for(int i=0; i < interfaces.length; i++) {
			if(interfaces[i] == superclass) return true;
		}
		return false;
	}
}


