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

package sample.action.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.LabelValueBean;

import sample.dao.Address;
import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;

public class SampleAction extends FormlessDispatchAction implements ConfigInterface
{
	private static Log log = LogFactory.getLog(SampleAction.class);

	public void config(ActionConfig struts)
	{
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);

		struts.addForward("sample_view.jsp");
		struts.addForward("sample_preview.jsp");
		struts.setUnspecified("view");
	}

	public ActionForward doView() throws Exception
	{
		log.debug("view");

		return ActionHelper.findForward("view");
	}

	public ActionForward doPreview() throws Exception
	{
		log.debug("preview");

		return ActionHelper.findForward("preview");
	}

	public ActionForward doSimulatemessage() throws Exception
	{
		log.debug("simulatemessage");

		ActionHelper.addMessage("confirm", "text.msg");
		ActionHelper.addMessage("info", "text.info");

		if(ActionHelper.hasMessages())
			return ActionHelper.findForward("view");

		return ActionHelper.findForward("view");
	}

	public ActionForward doSimulateerrors() throws Exception
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

	public ActionForward doSearch() throws Exception
	{
		log.debug("search");

		//SampleActionForm__ form = (SampleActionForm__)ActionHelper.getForm();

		if(getFile() != null)
		{
			String filename = getFile().getFileName();
			log.debug("FILENAME: " + filename);
			setFilename(filename);

			// Utils.writeFile( "d:/uploads", form.getFile());
		}
		return ActionHelper.findForward("view");
	}
	
	
	
	
	
	
	private static final long serialVersionUID = 1L;

	Address customer = new Address();

	// String [] anreden = new String[] { "Herr", "Frau", "Dr." };

	// Alternative
	LabelValueBean[] anreden = new LabelValueBean[] {
	        new LabelValueBean("Herr", "11"),
	        new LabelValueBean("Frau", "22"),
	        new LabelValueBean("Dr.", "33")
	};

	String rposition;

	String[] tog = new String[10];

	String c1iso;
	String c2iso;
	String c3iso;
	String c4iso;

	String memo;

	FormFile file = null;
	String filename = "";

	public void reset()
	{
		//super.reset(arg0, arg1);
		c1iso = "";
		c2iso = "";
		c3iso = "";
		c4iso = "";
		tog = new String[10];
	}

	public String getMemo()
	{
		return memo;
	}

	public void setMemo(String memo)
	{
		this.memo = memo;
	}

	public String getRposition()
	{
		return rposition;
	}

	public void setRposition(String rgolgo)
	{
		this.rposition = rgolgo;
	}

	public String getC1iso()
	{
		return c1iso;
	}

	public void setC1iso(String c1iso)
	{
		this.c1iso = c1iso;
	}

	public String getC2iso()
	{
		return c2iso;
	}

	public void setC2iso(String c2iso)
	{
		this.c2iso = c2iso;
	}

	public String getC3iso()
	{
		return c3iso;
	}

	public void setC3iso(String c3iso)
	{
		this.c3iso = c3iso;
	}

	public String getC4iso()
	{
		return c4iso;
	}

	public void setC4iso(String c4iso)
	{
		this.c4iso = c4iso;
	}

	public LabelValueBean[] getAnreden()
	{
		return anreden;
	}

	public void setAnreden(LabelValueBean[] anreden)
	{
		this.anreden = anreden;
	}

	public Address getCustomer()
	{
		return customer;
	}

	public void setCustomer(Address customer)
	{
		this.customer = customer;
	}

	public FormFile getFile()
	{
		return file;
	}

	public void setFile(FormFile file)
	{
		this.file = file;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String[] getTog()
	{
		return tog;
	}

	public void setTog(String[] tog)
	{
		this.tog = tog;
	}
}