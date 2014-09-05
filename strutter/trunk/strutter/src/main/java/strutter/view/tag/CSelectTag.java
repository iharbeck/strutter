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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.util.LabelValueBean;

import strutter.Utils;
import strutter.htmlparser.nodes.TextNode;
import strutter.htmlparser.tags.OptionTag;
import strutter.htmlparser.tags.SelectTag;
import strutter.htmlparser.util.NodeList;
import strutter.htmlparser.util.exception.ParserException;
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

	String disabled = "";

	public CSelectTag(Object form, ServletRequest request)
	{
		this.form = form;
		this.request = request;
	}

	public void doSemanticAction() throws ParserException
	{
		String attname = this.getAttribute("name");

		try
		{
			if(attname != null && this.getAttribute("nofill") == null)
			{
				List sellist;

				// aktuelle Auswahl ermitteln
				// String[] sel = BeanUtils.getArrayProperty(form,
				// this.getAttribute("name"));
				String[] sel = TagHelper.getFormValues(form, attname);

				if(sel == null)
					sellist = new ArrayList();
				else
					sellist = Arrays.asList(sel);

				// Stringdaten beginnen mit dem value in option
				// value:optiontext
				boolean hasvalue = this.getAttribute("hasvalue") != null;
				/*
				 * // ID der Liste ermitteln (Option mit id Attribut) NodeList
				 * list = this.children.extractAllNodesThatMatch( new AndFilter
				 * ( new NodeClassFilter (OptionTag.class), new
				 * HasAttributeFilter ("id")));
				 */

				// alle optionen ermitteln
				NodeList list = this.children.extractAllNodesThatMatchClass(OptionTag.class);

				// Alle bestehenden optionen l�schen
				this.getChildren().removeAll();

				for(int i = 0; i < list.size(); i++)
				{
					OptionTag optionTag = (OptionTag)list.elementAt(i);

					String name = optionTag.getAttribute("values");

					if(name == null)
						name = optionTag.getAttribute("id");

					if(name != null)
					{
						// in Session, Request suchen
						Object listvalues = Utils.getObject((HttpServletRequest)request, name);
						if(listvalues == null)
						{
							// in Form suchen
							// System.out.println("options: " +
							// ((OptionTag)list.elementAt(0)).getAttribute("id"));
							listvalues = PropertyUtils.getNestedProperty(form, name);
						}

						if(listvalues instanceof ArrayList)
							generateOptionFrom((ArrayList)listvalues, sellist, hasvalue);
						else if(listvalues instanceof String[])
							generateOptionFrom((String[])listvalues, sellist, hasvalue);
						else if(listvalues instanceof LabelValueBean[])
							generateOptionFrom((LabelValueBean[])listvalues, sellist);
						else if(listvalues instanceof Map)
							generateOptionFrom((Map)listvalues, sellist);
					}
					else
					{
						generateOption(optionTag.getChildrenHTML(), optionTag.getValue(), optionTag.getAttribute("class"), optionTag.getAttribute("style"), sellist);
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(attname);
			// if anything throws an exception, stop processing and return
			// control to the caller.
			e.printStackTrace();
		}
	}

	final void generateOption(String text, String value, String clazz, String style, List selected)
	{
		OptionTag endTag = new OptionTag();
		endTag.setTagName("/OPTION");

		OptionTag option = new OptionTag();

		if(value == null)
			value = text;

		if(selected.contains(value))
		{
			option.setAttribute("SELECTED", null);

			if(getAttribute("disabled") != null)
				disabled += "<input type='hidden' name='" + getAttribute("name") + "' value='" + value + "'>";
		}

		if(value.indexOf('"') >= 0)
		{
			value = value.replaceAll("\"", "\\\"");
		}
		
		// option.setValue(value);
		option.setAttribute("VALUE", value, '"');

		if(clazz != null)
			option.setAttribute("class", clazz, '"');
		if(style != null)
			option.setAttribute("style", style, '"');

		option.setChildren(new NodeList());
		option.getChildren().add(new TextNode(text));

		option.setEndTag(endTag);

		this.getChildren().add(option);
	}

	final void generateOptionFrom(List list, List selected, boolean hasvalue) // List
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i) instanceof LabelValueBean)
			{
				LabelValueBean opt = (LabelValueBean)list.get(i);
				generateOption(opt.getLabel(), opt.getValue(), null, null, selected);
			}
			else
			{
				String val = (String)list.get(i);

				fillOptions(val, selected, hasvalue);
			}
		}
	}

	final void fillOptions(String value, List selected, boolean hasvalue)
	{
		if(hasvalue)
		{
			String[] val = value.split(":");
			if(val.length > 1)
			{
				generateOption(val[1], val[0], null, null, selected);
				return;
			}
		}

		generateOption(value, null, null, null, selected);
	}

	final void generateOptionFrom(String[] list, List selected, boolean hasvalue) // StringArray
	{
		for(int i = 0; i < list.length; i++)
		{
			fillOptions(list[i], selected, hasvalue);
		}
	}

	final void generateOptionFrom(LabelValueBean[] list, List selected) // LVBArray
	{
		for(int i = 0; i < list.length; i++)
		{
			generateOption(list[i].getLabel(), list[i].getValue(), null, null, selected);
		}
	}

	final void generateOptionFrom(Map list, List selected) // HashMap
	{
		ArrayList key = new ArrayList(list.keySet());
		// Collections.sort(key);

		for(Iterator it = key.iterator(); it.hasNext();)
		{
			String k = (String)it.next();
			generateOption((String)list.get(k), k, null, null, selected);
		}
	}

	public String toHtml()
	{
		String tag = super.toHtml();

		try
		{
			if(this.getAttribute("error") != null)
				tag = TagHelper.handleError(this, request, tag);
		}
		catch(Exception e)
		{
		}

		return disabled + tag;
	}
}
