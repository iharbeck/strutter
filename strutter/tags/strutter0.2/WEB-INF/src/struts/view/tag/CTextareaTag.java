package struts.view.tag;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import struts.Utils;

public class CTextareaTag extends  TextareaTag 
{
	 Object form;
	 ServletRequest request;
	 
	 public CTextareaTag(Object form, ServletRequest request) {
		 this.form = form;
		 this.request = request;
	 }
	 
	 public void doSemanticAction () throws ParserException
	 {
		TextareaTag texta = new TextareaTag();
		texta.setTagName("/" + this.getTagName());					 
		this.setEndTag(texta);
		 
		this.setEmptyXmlTag(false);
		 
	   	if(this.getAttribute("name") != null)
	   	{
	   		try {
		    		String val = BeanUtils.getProperty(form, this.getAttribute("name"));
		    		if(val != null)
		    		{
			    		this.setChildren(new NodeList());
			    		this.getChildren().add(new TextNode(val)); 
		    		}
	   		}
	   		catch(Exception e) {
	   		}
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
