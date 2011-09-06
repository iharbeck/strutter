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

import org.apache.commons.beanutils.BeanUtils;
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
	 
	 String extend = null;
	 
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

		String attname = getAttribute("name");

    	try 
	   	{
    		boolean processed = false;
    		
	   		if(attname != null && this.getAttribute("nofill") == null)
	   		{
	   			//String value = BeanUtils.getProperty(form, name);
	   			String value = TagHelper.getFormValue(form, attname, actionname);
	   			
	   			if(value.length() > 0)
	   			{
	    			if("text".equals(type) || 
	    		       "hidden".equals(type) ||
	    		       "submit".equals(type) ||
	    		       "cancel".equals(type) ||
	    		       "password".equals(type)) 
	    			{
	    		    	this.setAttribute("value", value, '"');
	    		    	processed = true;
	    		    } 
	    			else if("radio".equals(type) || 
			    		    "checkbox".equals(type)) {
	    		    	
	    		   		//String[] sel = BeanUtils.getArrayProperty(form, this.getAttribute("name"));
	    		   		String[] sel = TagHelper.getFormValues(form, this.getAttribute("name"));
	    		   		List<String> sellist = Arrays.asList(sel);
	    		   		 
	    		    	setSelected(this.getAttribute("value"), sellist);

	    		    	//if("checkbox".equals(type)) {
	    		    	//	extend += ""; //"<input type='hidden' name='" + name + "' value='0'>";
	    				//}
	    		    	
	    		    	processed = true;
	    		    }

	    			if(getAttribute("disabled") == null || getAttribute("CHECKED") == null)
	    			{
	    			}
	    			else
	    			{
	    				extend = "<input type='hidden' name='" + attname + "' value='" + this.getAttribute("value") + "'>";
	    			}

	   			} else {
	   				if(this.getAttribute("value").length() == 0)
	   					this.removeAttribute("value");
	   			}
	   			return;
	   		}
   		
	   		if(processed)
	   			return;
	   		
	   		if("button".equals(type) || "submit".equals(type) || "cancel".equals(type) || "reset".equals(type)) 
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
    
    void setSelected(String value, List selected)
    {
		 if(value == null) 
			 return;
		 
		 if( selected.contains(value) )
			 this.setAttribute("checked", "", '"');
		 else
			 this.removeAttribute("checked");
    }
    
    public String toHtml() 
	{
    	String tag = super.toHtml();
    	
	   	try {
	   		if(this.getAttribute("error") != null)
	   			tag = TagHelper.handleError(this, request, tag);
	   	} catch(Exception e) {
	   	}
	   	
	   	
	   	//int length = checkboxfix.length() + disabled.length() + tag.length();

	   	if(extend == null)
	   		return tag;
	   	
	   	return extend + tag;
    }
}

