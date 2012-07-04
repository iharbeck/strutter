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

package sample.action.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;

public class ProcessAction extends FormlessDispatchAction implements ConfigInterface
{
	private static Log log = LogFactory.getLog(ProcessAction.class);

	public void config(ActionConfig struts)
	{
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
		struts.addForward("process_view.jsp");
	}

	private String lastname;
	private String firstname;

	public ActionForward doView() throws Exception
	{
		log.debug("view");

		return ActionHelper.findForward("view");
	}

	public ActionForward doRunner() throws Exception
	{
		log.debug("runner");

		Object obj = new Object();
		synchronized(obj)
		{
			log.debug("start");
			obj.wait(30000);
			log.debug("ende");
		}

		return ActionHelper.findForward("view");
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}

}