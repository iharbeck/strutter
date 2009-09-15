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
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import strutter.Utils;
import strutter.view.TagHelper;

//TODO my own LabelValueBean

//TODO JSON Support
/*
{	
	[
	    {"value" : "1", "label" : "first"},
	    {"value" : "2", "label" : "second"},
	    {"value" : "3", "label" : "third"}
	]
}
 */

public class CSelectTag extends SelectTag
{
	 private static final long serialVersionUID = 1L;

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
	   		 
	   		 // Stringdaten beginnen mit dem value in option
	   		 // value:optiontext
	   		 boolean hasvalue = this.getAttribute("hasvalue") != null;
/*
	   		 // ID der Liste ermitteln (Option mit id Attribut)
	   		 NodeList list = this.children.extractAllNodesThatMatch(
	   				 new AndFilter (
	                     new NodeClassFilter (OptionTag.class),
	                     new HasAttributeFilter ("id")));
*/	   		 

	   		 // alle optionen ermitteln
    		 NodeList list = this.children.extractAllNodesThatMatch(new NodeClassFilter (OptionTag.class));

    		 // Alle bestehenden optionen l�schen
	   		 this.getChildren().removeAll();
	   		 
			 for(int i=0; i < list.size();i++)
			 {
				 OptionTag optionTag = (OptionTag)list.elementAt(i);

				 String name = optionTag.getAttribute("id");
				 
				 if(name != null)
				 {
					// in Session, Request suchen
		   			 Object listvalues = Utils.getObject((HttpServletRequest)request, name);
		   			 if(listvalues == null)
		   			 {
		   				 // in Form suchen
		   				 //System.out.println("options: " + ((OptionTag)list.elementAt(0)).getAttribute("id"));
		   				listvalues = PropertyUtils.getNestedProperty(form, name);
		   			 }

				 
			   		 if(listvalues instanceof ArrayList)
			   			 generateOptionFrom((ArrayList)listvalues, sellist, hasvalue);
			   		 else if(listvalues instanceof String[])
			   			 generateOptionFrom((String[])listvalues, sellist, hasvalue);
			   		 else if(listvalues instanceof LabelValueBean[])
			   			 generateOptionFrom((LabelValueBean[])listvalues, sellist);
			   		 else if(listvalues instanceof HashMap)
			   			 generateOptionFrom((HashMap)listvalues, sellist);

				 }
				 else 
				 {
					 generateOption(optionTag.getChildrenHTML(), optionTag.getValue(), sellist);
				 }
			 }
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

    final void generateOptionFrom(List list, List selected, boolean hasvalue) //List
    {
		 for(int i=0; i < list.size(); i++) 
		 {
			 if(list.get(i) instanceof LabelValueBean)
			 {
				 LabelValueBean opt = (LabelValueBean)list.get(i);
				 generateOption(opt.getLabel(), opt.getValue(), selected);
			 } else {
				 String val = (String)list.get(i);

				 fillOptions(val, selected, hasvalue);
			 }
		 }
    }
    
    final void fillOptions(String value, List selected, boolean hasvalue) 
    {
    	if(hasvalue) {
			 String[] val = value.split(":");
			 if(val.length > 1) {
				 generateOption(val[1], val[0], selected);
				 return;
			 }
    	} 
    	
    	generateOption(value, null, selected);
    }
    
    final void generateOptionFrom(String[] list, List selected, boolean hasvalue) //StringArray
    {
		 for(int i=0; i < list.length; i++) 
		 {
			 fillOptions(list[i], selected, hasvalue);
		 }
    }
    
    final void generateOptionFrom(LabelValueBean[] list, List selected) //LVBArray
    {
		 for(int i=0; i < list.length; i++) {
			 generateOption(list[i].getLabel(), list[i].getValue(), selected);
		 }
    }

    final void generateOptionFrom(HashMap list, List selected) //HashMap
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
