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

import javax.servlet.ServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.htmlparser.tags.InputTag;
import org.htmlparser.util.ParserException;

public class CInputTag extends InputTag
{
	 Object form;
	 ServletRequest request;
	 
	 public CInputTag(Object form, ServletRequest request) {
		 this.form = form;
		 this.request = request;
	 }
	 
    public void doSemanticAction () throws ParserException
    {
   	try {
   		String type = this.getAttribute("type");
		String name = this.getAttribute("name");
   		if(name != null && type != null)
   		{
   			String value = BeanUtils.getProperty(form, name);
   			if(value != null)
   			{
	    			type = type.toUpperCase();
	    			
	    			if("TEXT".equals(type) || 
	    		       "HIDDEN".equals(type) ||
	    		       "SUBMIT".equals(type) ||
	    		       "CANCEL".equals(type) ||
	    		       "PASSWORD".equals(type)) {
	    		    	this.setAttribute("value", value, '"');
	    		    } else if("RADIO".equals(type) || 
			    		    "CHECKBOX".equals(type)) {
	    		    	
	    		    	List sellist;
	    		   		 
	    		   		String[] sel = BeanUtils.getArrayProperty(form, this.getAttribute("name"));
	    		   		sellist = Arrays.asList(sel);
	    		   		 
	    		    	setSelected(this.getAttribute("value"), sellist);
	    		    }
   			}
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
