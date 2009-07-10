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
import org.apache.struts.action.ActionForward;

import sample.dao.Address;
import strutter.config.ActionConfig;
import strutter.optional.DirectInterface;
import strutter.optional.FormlessPlainDispatchAction;

public class DirectAction extends FormlessPlainDispatchAction implements DirectInterface
{
	private static Log log = LogFactory.getLog(DirectAction.class);
	
	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/formlesssimple_view.jsp");
	}
	
	public void doexecute(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("dir");
	}

	String [] anreden = new String[] { "Herr", "Frau", "Dr." };
	
	
	Address customer = new Address();
	String memo;
	
	public ActionForward view() throws Exception 
	{
		log.debug("view simpler");

		System.out.println("view: " + helper.getRequest().getParameter("customer.anrede"));

		return helper.getForward("view");
	}

	public void reset() 
	{
		System.out.println("reset: " + helper.getRequest().getParameter("customer.anrede"));
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