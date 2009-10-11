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

package sample.action.advanced.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import sample.dao.Address;
import strutter.optional.FormlessPlainDispatchAction;
import strutter.optional.controller.tags.BaseInterface;

public class ConfigAutoAction extends FormlessPlainDispatchAction implements BaseInterface
{
	private static Log log = LogFactory.getLog(ConfigAutoAction.class);

	Address customer = new Address();
	String  memo;

	public ActionForward view() throws Exception
	{
		log.debug("view simpler");

		return helper.findForward("view");
	}

	public void reset()
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
		return new String[] { "Herr", "Frau", "Dr." };
	}
}