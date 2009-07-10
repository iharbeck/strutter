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

package strutter.optional.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.optional.interceptor.WebInterceptorInterface;

public class InterceptorFilter implements Filter {

	public FilterConfig filterConfig;
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		
		ModuleConfig mConfig = 
			(ModuleConfig)filterConfig.getServletContext()
			                          .getAttribute(Globals.MODULE_KEY); 
		 
		ActionConfig action = null;
		
		if(mConfig != null)
		{ 
			//extrect actionname cut of ".do" results in /actionname
			String look = Utils.getActionMappingName(((HttpServletRequest)request).getServletPath());
			action = mConfig.findActionConfig(look);
		}
		
		ActionMappingExtended mapping = null;
		
		if(action != null && action instanceof ActionMappingExtended)
			mapping = (ActionMappingExtended) action;		
		
		for(int i=0; mapping != null && i < mapping.getInterceptors().size(); i++)
		{
			if(!(mapping.getInterceptors().get(i) instanceof WebInterceptorInterface))
				continue;
		    WebInterceptorInterface interceptor = (WebInterceptorInterface) mapping.getInterceptors().get(i);
		    ActionForward forward = interceptor.beforeView(mapping, (HttpServletRequest)request, (HttpServletResponse)response);
		    
		    if(forward != null) {
		    	Utils.processForwardConfig((HttpServletRequest)request, (HttpServletResponse)response, forward);
		    	return;
		    }
		}
		
		chain.doFilter(request, response);

		for(int i=0; mapping != null && i < mapping.getInterceptors().size(); i++)
		{
			if(!(mapping.getInterceptors().get(i) instanceof WebInterceptorInterface))
				continue;
		    WebInterceptorInterface interceptor =	(WebInterceptorInterface) mapping.getInterceptors().get(i);
		    ActionForward forward = interceptor.afterView(mapping, (HttpServletRequest)request, (HttpServletResponse)response);

		    if(forward != null) {
		    	Utils.processForwardConfig((HttpServletRequest)request, (HttpServletResponse)response, forward);
		    	return;
		    }
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}
}
