package struts.view.tag;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.htmlparser.tags.InputTag;
import org.htmlparser.util.ParserException;

import struts.Utils;

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
      	String att = this.getAttribute("error");
    	//this.removeAttribute("error");
    	
    	if(att != null)
	   	{
    		String val = null;
    		try {
	    		ActionMessages am = Utils.getErrors((HttpServletRequest)request);
	   			
	    		System.out.println(getAttribute("name"));
	   			Iterator msgs = am.get(getAttribute("name"));
	   			
	  			Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
	  			MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

  				if(msgs.hasNext())
  				{
  					ActionMessage msg = (ActionMessage)msgs.next();
  					val = resources.getMessage(loc, msg.getKey(), msg.getValues());
  				}	
    		} catch(Exception e) {
    			
    		}
    		
    		if(val != null) {
	    	    att = att.toLowerCase();
			    if(att.equals("behind"))
				   return super.toHtml() + " <span class=\"error\"> " + val + " </span>";
			    else if(att.equals("before"))
				   return "<span class=\"error\"> " + val + " </span> " + super.toHtml();
			    else if(att.equals("class")) 
			       this.setAttribute("class", "error", '"');
    		}
	   	}
    	
    	return super.toHtml();
    }

}
