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

package strutter.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;

import strutter.Utils;

public class SecurityFilter extends SSO.SSOFilter {

	public boolean needCheck(HttpServletRequest httprequest)  {
		ModuleConfig mConfig = 
		    (ModuleConfig)filterConfig.getServletContext()
				 .getAttribute(Globals.MODULE_KEY); 
		 
		ActionConfig action = null;
		
		
		if(mConfig != null)
		{ 
			try {
				//String look = httprequest.getServletPath().replaceAll("\\.do", "");
				String look = Utils.getActionMappingName(httprequest, null);
				System.out.println(look);
				action = mConfig.findActionConfig(look);
				System.out.println("found:" + look);
			} catch(IOException e) {
				
			}
		}
		
		return (action == null || action.getRoles() == null);
	}
	
}
