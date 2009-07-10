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

package sample.action.advanced;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sample.dao.Address;
import strutter.config.ActionConfig;
import strutter.optional.FormlessDispatchAction;

public class ZeroconfAction extends FormlessDispatchAction 
{
	public static ActionConfig struts = new ActionConfig();

	private static Log log = LogFactory.getLog(ZeroconfAction.class);

	String [] anreden = new String[] { "Herr", "Frau", "Dr." };
	
	Address customer = new Address();
	String memo;
	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception 
	{
		log.debug("view");
		
		return mapping.findForward("view");
	}

	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Address getCustomer() {
		return customer;
	}

	public void setCustomer(Address customer) {
		this.customer = customer;
	}

	public String[] getAnreden() {
		return anreden;
	}

	public void setAnreden(String[] anreden) {
		this.anreden = anreden;
	}
}