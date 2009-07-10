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

import strutter.config.ActionConfig;
import strutter.optional.ExtDispatchAction;

public class SampleAction extends ExtDispatchAction 
{
	private static Log log = LogFactory.getLog(SampleAction.class);

	public static ActionConfig struts = new ActionConfig();
	
	static {
		struts.addForward("/sample_view.jsp");
		struts.addForward("/sample_preview.jsp");
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("view");
		
		return mapping.findForward("view");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("preview");
		
		return mapping.findForward("preview");
	}

	
	public ActionForward simulatemessage(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("simulatemessage");
		
		helper.addMessage("confirm", "text.msg");
		helper.addMessage("info", "text.info");

		if(helper.hasMessages())
			return mapping.findForward("view");

		return mapping.findForward("view");
	}

	public ActionForward simulateerrors(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("simulateerrors");
		
		helper.addError("text.msg");
		
		helper.addError("text.param", new Object[] { "one", "two" });
		
		helper.addError("customer.firstname", "error.missing");
		helper.addError("customer.lastname",  "error.missing");
		
		if(helper.hasErrors())
			return mapping.findForward("view");

		return mapping.findForward("view");
	}

	
	public ActionForward search(ActionMapping mapping, ActionForm actionform,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		log.debug("search");

		SampleActionForm form = (SampleActionForm)actionform;
		
		if(form.getFile() != null)
		{
			String filename = form.getFile().getFileName();
			log.debug("FILENAME: " + filename);
			form.setFilename(filename);

			//Utils.writeFile( "d:/uploads", form.getFile());
		}
		return mapping.findForward("view");
	}
}