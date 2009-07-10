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

package struts.view;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import struts.Utils;

public final class PageFilter extends AbstractPageFilter
{
	protected Object getForm(ServletRequest request) {
		// Identify the mapping for this request
		Object form = Utils.getActionForm((HttpServletRequest)request);
		
		return form;
	}
	
	public void init(FilterConfig f) {
	}

	public void destroy() {
	}
}

