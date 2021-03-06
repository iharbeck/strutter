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

package sample.action.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sample.dao.Address;
import strutter.action.FormlessInterface;
import strutter.action.PlainDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;
import strutter.interceptor.TokenInterceptor;

public class WebInterceptorAction extends PlainDispatchAction implements ConfigInterface, FormlessInterface
{
	private static Log log = LogFactory.getLog(WebInterceptorAction.class);

	public void config(ActionConfig struts)
	{
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
		struts.addInterceptor(new TokenInterceptor());

		struts.addForward("webinterceptor_invalidtoken.jsp");
		struts.addForward("webinterceptor_view.jsp");
	}

	String[] anreden = new String[] { "Herr", "Frau", "Dr." };

	Address customer = new Address();
	String memo;

	public ActionForward doView(ActionMapping mapping, ActionForm actionform,
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception
	{
		log.debug("view");

		return mapping.findForward("view");
	}

	public ActionForward doUpdate(ActionMapping mapping, ActionForm actionform,
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception
	{
		log.debug("update");

		ActionHelper.addMessage("update done");

		return mapping.findForward("view");
	}

	public void reset(ActionMapping arg0, HttpServletRequest arg1)
	{
	}

	public String getMemo()
	{
		return memo;
	}

	public void setMemo(String memo)
	{
		this.memo = memo;
	}

	public Address getCustomer()
	{
		return customer;
	}

	public void setCustomer(Address customer)
	{
		this.customer = customer;
	}

	public String[] getAnreden()
	{
		return anreden;
	}

	public void setAnreden(String[] anreden)
	{
		this.anreden = anreden;
	}
}