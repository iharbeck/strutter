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

package strutter.view.tag;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.util.MessageResources;
import org.htmlparser.tags.InputTag;
import org.htmlparser.util.ParserException;

import strutter.Utils;
import strutter.view.TagHelper;

public class CInputTag extends InputTag
{
	 private static final long serialVersionUID = 1L;
	
 	 Object form;
	 ServletRequest request;
	 String actionname;
	 
	 String disabled = "";
	 String checkboxfix = "";
	 
	 public CInputTag(Object form, ServletRequest request) {
		 this.form = form;
		 this.request = request;
		 
		 ActionConfig mapping = (ActionConfig)request.getAttribute(Globals.MAPPING_KEY);

		 if (mapping != null && mapping.getParameter() != null)
			this.actionname = mapping.getParameter();
	 }
	 
    public void doSemanticAction () throws ParserException
    {
   		String type = getAttribute("type");
		
		if(type == null)
			return;

		type = type.toUpperCase();

		String attname = getAttribute("name");

    	try 
	   	{
    		boolean processed = false;
    		
	   		if(attname != null && this.getAttribute("nofill") == null)
	   		{
	   			//String value = BeanUtils.getProperty(form, name);
	   			String value = TagHelper.getFormValue(form, attname, actionname);
	   			
	   			if(value != null && value.length() > 0)
	   			{
	    			if("TEXT".equals(type) || 
	    		       "HIDDEN".equals(type) ||
	    		       "SUBMIT".equals(type) ||
	    		       "CANCEL".equals(type) ||
	    		       "PASSWORD".equals(type)) 
	    			{
	    		    	this.setAttribute("value", value, '"');
	    		    	processed = true;
	    		    } 
	    			else if("RADIO".equals(type) || 
			    		    "CHECKBOX".equals(type)) {
	    		    	
	    		   		//String[] sel = BeanUtils.getArrayProperty(form, this.getAttribute("name"));
	    		   		String[] sel = TagHelper.getFormValues(form, this.getAttribute("name"));
	    		   		List<String> sellist = Arrays.asList(sel);
	    		   		 
	    		    	setSelected(this.getAttribute("value"), sellist);

	    		    	if("CHECKBOX".equals(type))
	    		    		checkboxfix = ""; //"<input type='hidden' name='" + name + "' value='0'>";

	    		    	processed = true;
	    		    }
	    			
	    			disabled = getAttribute("disabled");
    		    	
    		    	if(disabled != null && getAttribute("CHECKED") != null)
    		    		disabled = "<input type='hidden' name='" + attname + "' value='" + this.getAttribute("value") + "'>";
    		    	else
    		    	    disabled = "";

	   			} else {
	   				if(this.getAttribute("value").length() == 0)
	   					this.removeAttribute("value");
	   			}
	   			return;
	   		}
   		
	   		if(processed)
	   			return;
	   		
	   		if("BUTTON".equals(type) || "SUBMIT".equals(type) || "CANCEL".equals(type) || "RESET".equals(type)) 
	   		{
				String text = this.getAttribute("value");
				
				if(text == null || !text.startsWith("$"))
					return;
				
				Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
	   			
	   			MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

	   			text = resources.getMessage(loc, text.substring(1));
	   			
	   			setAttribute("value", text);
		    }
    	} catch(Exception e) {
		}
    }
    
    final void setSelected(String value, List selected)
    {
		 if(value != null) {
			 if( selected.contains(value) )
				 this.setAttribute("CHECKED", "", '"');
			 else
				 this.removeAttribute("CHECKED");
        }
    }
    
    public String toHtml() 
	{
    	String tag = super.toHtml();
    	
	   	try {
	   		if(this.getAttribute("error") != null)
	   			tag = TagHelper.handleError(this, request, tag);
	   	} catch(Exception e) {
	   	}
	   	
	   	return checkboxfix + disabled + tag;
    }

}
