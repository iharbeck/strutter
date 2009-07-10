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
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import struts.Utils;

public class CDivTag extends Div 
{
	 Object form;
	 ServletRequest request;
	 
	 public CDivTag(Object form, ServletRequest request) {
		 this.form = form;
		 this.request = request;
	 }
	 
	 public void doSemanticAction () throws ParserException
	 {
		Div div = new Div();
		div.setTagName("/" + this.getTagName());					 
		this.setEndTag(div);
		 
		this.setEmptyXmlTag(false);
		
		String type = this.getAttribute("type");

	   	if(type != null)
	   	{
	   		String val = "";
	   		
	   		try {
			    
		   		if(type.equals("text"))
		   		{
			   		val = BeanUtils.getProperty(form, this.getAttribute("id"));
		   		} 
		   		else if(type.equals("error") || type.equals("errors")) 
		   		{
		   			Iterator msgs = null;
		   			String id = this.getAttribute("id");

		   			ActionMessages am = Utils.getErrors((HttpServletRequest)request);
		   			if(am == null)
		   				return;
		   			
		   			int size;
		   			if(id == null) {
		   				msgs = am.get();
		   				size = am.size();
		   			} else {
		   				msgs = am.get(id);
		   				size = am.size(id);
		   			}	

		   			if(size == 0)
		   				return;
		   			
		  			Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
	   				MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		  			while(msgs.hasNext())
		   			{
		   				ActionMessage msg = (ActionMessage)msgs.next();
		   				if(size > 1)
		   					val += "<li>"+ resources.getMessage(loc, msg.getKey(), msg.getValues()) + "\n";
		   				else
		   					val += resources.getMessage(loc, msg.getKey(), msg.getValues());
		   			}
		   		}
		   		else if(type.equals("message") || type.equals("messages")) 
		   		{
		   			Iterator msgs = null;
		   			String id = this.getAttribute("id");

		   			ActionMessages am = Utils.getMessages((HttpServletRequest)request);
		   			if(am == null)
		   				return;
		   			
		   			int size;
		   			if(id == null) {
		   				msgs = am.get();
		   				size = am.size();
		   			} else {
		   				msgs = am.get(id);
		   				size = am.size(id);
		   			}	

		   			if(size == 0)
		   				return;
		   			
		  			Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
	   				MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		  			while(msgs.hasNext())
		   			{
		   				ActionMessage msg = (ActionMessage)msgs.next();
		   				if(size > 1)
		   					val += "<li>"+ resources.getMessage(loc, msg.getKey(), msg.getValues()) + "\n";
		   				else
		   					val += resources.getMessage(loc, msg.getKey(), msg.getValues());
		   			}
		   		}
		   		else if(type.equals("resource")) 
		   		{
		   			Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
		   			
		   			MessageResources resources = 
		   				(MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		   			val = resources.getMessage(loc, this.getAttribute("id"));
		   		}
		   		
		   		if(val == null || val.equals(""))
		   			return;
		   		
		   		this.setChildren(new NodeList());
	    		this.getChildren().add(new TextNode(val)); 
	   		}
	   		catch(Exception e) {
	   			System.out.println(e);
	   		}
	   	}
	 }
}
