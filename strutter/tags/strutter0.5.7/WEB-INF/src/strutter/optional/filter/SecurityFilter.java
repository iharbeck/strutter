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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;

import strutter.Utils;

public class SecurityFilter extends SSO.SSOFilter {

	public boolean needCheck(HttpServletRequest httprequest) {
		ModuleConfig mConfig = 
		    (ModuleConfig)filterConfig.getServletContext()
				 .getAttribute(Globals.MODULE_KEY); 
		 
		ActionConfig action = null;
		
		if(mConfig != null)
		{ 
			//String look = httprequest.getServletPath().replaceAll("\\.do", "");
			String look = Utils.getActionMappingName(httprequest.getServletPath());
			action = mConfig.findActionConfig(look);
		}
		
		return (action == null || action.getRoles() == null);
	}
	
}
