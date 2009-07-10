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
import org.apache.struts.config.ActionConfig;
import org.apache.struts.util.MessageResources;
import org.htmlparser.tags.InputTag;
import org.htmlparser.util.ParserException;

import strutter.view.TagHelper;

public class CInputTag extends InputTag
{
	 private static final long serialVersionUID = 1L;
	
 	 Object form;
	 ServletRequest request;
	 String actionname;
	 
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
		String name = getAttribute("name");
		
		if(type == null)
			return;

		type = type.toUpperCase();

    	try 
	   	{
	   		if(name != null && this.getAttribute("nofill") == null)
	   		{
	   			//String value = BeanUtils.getProperty(form, name);
	   			String value = TagHelper.getFormValue(form, name, !name.equals(actionname));
	   			
	   			if(value != null)
	   			{
	    			if("TEXT".equals(type) || 
	    		       "HIDDEN".equals(type) ||
	    		       "SUBMIT".equals(type) ||
	    		       "CANCEL".equals(type) ||
	    		       "PASSWORD".equals(type)) {
	    		    	this.setAttribute("value", value, '"');
	    		    	
	    		    } 
	    			else if("RADIO".equals(type) || 
			    		    "CHECKBOX".equals(type)) {
	    		    	
	    		    	List sellist;
	    		   		 
	    		   		//String[] sel = BeanUtils.getArrayProperty(form, this.getAttribute("name"));
	    		   		String[] sel = TagHelper.getFormValues(form, this.getAttribute("name"));
	    		   		sellist = Arrays.asList(sel);
	    		   		 
	    		    	setSelected(this.getAttribute("value"), sellist);
	    		    }
	   			}
	   			return;
	   		}
   		
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
	   	try {
	   		return TagHelper.handleError(this, request, super.toHtml());
	   	} catch(Exception e) {
	   		return super.toHtml();
	   	}
    }

}
