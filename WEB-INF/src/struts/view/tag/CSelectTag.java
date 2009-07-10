package struts.view.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import struts.Utils;

//TODO my own LabelValueBean

public class CSelectTag extends SelectTag
{
	 Object form;
	 ServletRequest request;
	 
	 public CSelectTag(Object form, ServletRequest request) {
		 this.form = form;
		 this.request = request;
	 }
	 
    public void doSemanticAction () throws ParserException
    {
	   	try 
	   	{
	   	   if(this.getAttribute("name") != null)
	   	   {
	   		 List sellist;
	   		 
	   		 // aktuelle Auswahl ermitteln
	   		 String[] sel = BeanUtils.getArrayProperty(form, this.getAttribute("name"));
	   		 
	   		 if(sel == null)
	   			 sellist = new ArrayList();
	   		 else
	   			 sellist = Arrays.asList(sel);
	   		 
	   		 Object olist = null;
	   		 
	   		 // ID der Liste ermitteln (Option mit id Attribut)
	   		 NodeList list = this.children.extractAllNodesThatMatch(
	   				 new AndFilter (
	                     new NodeClassFilter (OptionTag.class),
	                     new HasAttributeFilter ("id")));
	   		 
	   		 // id tag referenziert Option aus Session, Request oder Form
	   		 if(list.size() > 0)
	   		 {
	   			 String name = ((OptionTag)list.elementAt(0)).getAttribute("id");
	   			 // in Session, Request suchen
	   			 olist = Utils.getObject((HttpServletRequest)request, name);
	   			 if(olist == null)
	   			 {
	   				 // in Form suchen
	   				 //System.out.println("options: " + ((OptionTag)list.elementAt(0)).getAttribute("id"));
	   				 olist = PropertyUtils.getNestedProperty(form, name);
	   			 }
	   		 }
	   		 else 
	   		 {
	   			 // statische options aus HTML extrahieren
	    		 olist = new ArrayList();
	    		 NodeList list2 = this.children.extractAllNodesThatMatch(
	    				 new NodeClassFilter (OptionTag.class));
	    		 if(list2.size() > 0)
	    		 {
	    			 for(int i=0; i < list2.size();i++)
	    			 {
	    				 OptionTag optionTag = (OptionTag)list2.elementAt(i);
	    				 //System.out.println("options: " + optionTag.getValue() + " >> " + optionTag.getChildrenHTML());
	    				 ((ArrayList)olist).add(new LabelValueBean(optionTag.getChildrenHTML(), optionTag.getValue()));
	    			 }
	    		 }
	   		 }
	   		 // Alle optionen löschen
	   		 this.getChildren().removeAll();
	   		 
	   		 if(olist instanceof ArrayList)
	   			 generateOptionFromList((ArrayList)olist, sellist);
	   		 else if(olist instanceof String[])
	   			 generateOptionFromStringArray((String[])olist, sellist);
	   		 else if(olist instanceof LabelValueBean[])
	   			 generateOptionFromLVBArray((LabelValueBean[])olist, sellist);

	   	   }
	   	   
	   	   
        } catch(Exception e) {
        	System.out.println(this.getAttribute("name"));
			//if anything throws an exception, stop processing and return control to the caller.
       		e.printStackTrace();
	    }
    }

	final void generateOption(String text, String value, List selected)
    {
   	 	 OptionTag endTag = new OptionTag();
		 endTag.setTagName("/OPTION");
		 
		 OptionTag option = new OptionTag();
		 
		 if(value != null) 
		 {
			 if(selected.contains(value))
				 option.setAttribute("SELECTED", null);
			 value = "\"" + value + "\"";
	     }
		 else if(selected.contains(text)) {
			 value = "\"\"";
			 option.setAttribute("SELECTED", null);
		 }
		 else {
			 value = "\"\"";
		 }
		 option.setValue(value);
		 
		 option.setChildren(new NodeList());
		 option.getChildren().add(new TextNode(text));
	
		 option.setEndTag(endTag);
	
		 this.getChildren().add(option);
    }

    final void generateOptionFromList(List list, List selected)
    {
		 for(int i=0; i < list.size(); i++) 
		 {
			 if(list.get(i) instanceof LabelValueBean)
			 {
				 LabelValueBean opt = (LabelValueBean)list.get(i);
				 generateOption(opt.getLabel(), opt.getValue(), selected);
			 } else {
				 String val = (String)list.get(i);
				 generateOption(val, val, selected);
			 }
		 }
    }
    
    final void generateOptionFromStringArray(String[] list, List selected)
    {
		 for(int i=0; i < list.length; i++) {
			 generateOption(list[i], null, selected);
		 }
    }
    
    final void generateOptionFromLVBArray(LabelValueBean[] list, List selected)
    {
		 for(int i=0; i < list.length; i++) {
			 generateOption(list[i].getLabel(), list[i].getValue(), selected);
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

