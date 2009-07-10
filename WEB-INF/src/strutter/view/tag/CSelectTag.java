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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.util.LabelValueBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import strutter.Utils;

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
	   	   if(this.getAttribute("name") != null && this.getAttribute("nofill") == null)
	   	   {
	   		 List sellist;
	   		 
	   		 // aktuelle Auswahl ermitteln
	   		 //String[] sel = BeanUtils.getArrayProperty(form, this.getAttribute("name"));
	   		 String[] sel = TagHelper.getFormValues(form, this.getAttribute("name"));
	   		 
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
	   		 else if(olist instanceof HashMap)
	   			 generateOptionFromHashMap((HashMap)olist, sellist);

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
		 
		 if(value == null) 
			 value = text;
			 
		 if(selected.contains(value))
			 option.setAttribute("SELECTED", null);
		
		 //option.setValue(value);
		 option.setAttribute("VALUE", value, '"');
		 
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

    final void generateOptionFromHashMap(HashMap list, List selected)
    {
    	 ArrayList key = new ArrayList(list.keySet());
    	 Collections.sort(key);
    	 
    	 for (Iterator it = key.iterator(); it.hasNext();) {
			String k = (String) it.next();
			generateOption(k, (String)list.get(k), selected);
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

