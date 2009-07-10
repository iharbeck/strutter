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

package strutter2.view;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;

import strutter.view.AbstractPageFilter;

import com.opensymphony.xwork2.ActionContext;

 
public final class PageFilter extends AbstractPageFilter 
{
	public final static String ID = "name";

	protected Object getForm(ServletRequest request) {
		// First Element on the WW Stack is the Action itself
		return ActionContext.getContext().getValueStack().getRoot().get(0); 
	}

	public void init(FilterConfig fc) {
		
//		ActionPlugin plugin = new ActionPlugin();
//		
//		plugin.init(fc);
		
	}
}

