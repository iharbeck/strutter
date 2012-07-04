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

package sample.action.formless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;

import sample.dao.Address;
import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;

public class FormlessAction extends FormlessDispatchAction implements ConfigInterface
{
	private static Log log = LogFactory.getLog(FormlessAction.class);

	public void config(ActionConfig struts)
	{
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
		struts.addForward("formless_view.jsp");
	}

	FormFile filer;

	Address customer = new Address();
	String memo;

	public ActionForward doView() throws Exception
	{
		log.debug("view simpler");

		return ActionHelper.findForward("view");
	}

	public ActionForward doUpdate()
	        throws Exception
	{
		log.debug("update");

		return ActionHelper.findForward("view");
	}

	public void reset()
	{
	}

	public String[] getAnreden()
	{
		return new String[] { "Herr", "Frau", "Dr." };
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

	public FormFile getFiler()
	{
		return filer;
	}

	public void setFiler(FormFile filer)
	{
		this.filer = filer;
	}
}