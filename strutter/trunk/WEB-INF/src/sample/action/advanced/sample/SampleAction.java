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

package sample.action.advanced.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import strutter.action.PlainDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;

public class SampleAction extends PlainDispatchAction implements ConfigInterface
{
	private static Log log = LogFactory.getLog(SampleAction.class);

	public void config(ActionConfig struts)
	{
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);

		struts.addForward("sample_view.jsp");
		struts.addForward("sample_preview.jsp");
		struts.setUnspecified("view");
	}

	public ActionForward doView()
	        throws Exception
	{
		log.debug("view");

		return ActionHelper.findForward("view");
	}

	public ActionForward doPreview()
	        throws Exception
	{
		log.debug("preview");

		return ActionHelper.findForward("preview");
	}

	public ActionForward doSimulatemessage()
	        throws Exception
	{
		log.debug("simulatemessage");

		ActionHelper.addMessage("confirm", "text.msg");
		ActionHelper.addMessage("info", "text.info");

		if(ActionHelper.hasMessages())
			return ActionHelper.findForward("view");

		return ActionHelper.findForward("view");
	}

	public ActionForward doSimulateerrors()
	        throws Exception
	{
		log.debug("simulateerrors");

		ActionHelper.addError("text.msg");

		ActionHelper.addError("text.param", new Object[] { "one", "two" });

		ActionHelper.addError("customer.firstname", "error.missing");
		ActionHelper.addError("customer.lastname", "error.missing");

		if(ActionHelper.hasErrors())
			return ActionHelper.findForward("view");

		return ActionHelper.findForward("view");
	}

	public ActionForward doSearch()
	        throws Exception
	{
		log.debug("search");

		SampleActionForm form = (SampleActionForm)ActionHelper.getForm();

		if(form.getFile() != null)
		{
			String filename = form.getFile().getFileName();
			log.debug("FILENAME: " + filename);
			form.setFilename(filename);

			// Utils.writeFile( "d:/uploads", form.getFile());
		}
		return ActionHelper.findForward("view");
	}
}