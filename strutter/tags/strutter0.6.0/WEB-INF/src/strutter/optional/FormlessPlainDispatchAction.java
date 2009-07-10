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

package strutter.optional;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class FormlessPlainDispatchAction extends FormlessDispatchAction
{
	protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String name) throws Exception
	{
		return dispatchMethod(name);
	}

	protected ActionForward dispatchMethod(String name) throws Exception
    {
      if(name == null)
          return unspecified();

      Method method = (Method)methods.get(name);

      if(method == null) {
          method = clazz.getMethod(name, null);
          methods.put(name, method);
      }
      return (ActionForward)method.invoke(this, null);
    }

	protected ActionForward unspecified() throws Exception
	{
		return view();
	}

	public ActionForward view() throws Exception
	{
		return helper.findForward("view");
	}
}