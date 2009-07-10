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

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import strutter.Utils;

public class CDivTag extends Div 
{
	 Object form;
	 ServletRequest request;
	 boolean remove = false;
	 
	 public CDivTag(Object form, ServletRequest request) {
		 this.form = form;
		 this.request = request;
	 }
	 
	 public String toHtml() {
		 
		 return remove ? "" : super.toHtml();
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
			   		val = BeanUtils.getProperty(form, this.getAttribute("id"));
		   		else if(type.equals("error") || type.equals("errors")) 
		   			val = TagHelper.handleList(this, request, Utils.getErrors((HttpServletRequest)request));
		   		else if(type.equals("message") || type.equals("messages")) 
		   			val = TagHelper.handleList(this, request, Utils.getMessages((HttpServletRequest)request));
		   		else if(type.equals("resource")) 
		   		{
		   			Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
		   			
		   			MessageResources resources = 
		   				(MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		   			val = resources.getMessage(loc, this.getAttribute("id"));
		   		}
		   		
		   		if(val == null || val.equals("")) {
		   			remove = true;
		   			return;
		   		}
		   		
		   		this.setChildren(new NodeList());
	    		this.getChildren().add(new TextNode(val)); 
	   		}
	   		catch(Exception e) {
	   			System.out.println(e);
	   		}
	   	}
	 }
}
