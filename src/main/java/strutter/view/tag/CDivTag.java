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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import strutter.Utils;
import strutter.helper.ActionHelper;
import strutter.view.TagHelper;

public class CDivTag extends Div
{
	private static final long serialVersionUID = 1L;

	Object form;
	ServletRequest request;
	boolean remove = false;

	public CDivTag(Object form, ServletRequest request)
	{
		this.form = form;
		this.request = request;
	}

	public String toHtml()
	{

		return remove ? "" : super.toHtml();
	}

	public void doSemanticAction() throws ParserException
	{
		String type = this.getAttribute("type");

		if(type == null)
			return;

		Div div = new Div();
		div.setTagName("/" + this.getTagName());
		this.setEndTag(div);

		this.setEmptyXmlTag(false);

		String val = "";

		try
		{
			if(type.startsWith("text"))
				// val = BeanUtils.getProperty(form, this.getAttribute("id"));
				val = TagHelper.getFormValue(form, this.getAttribute("id"));
			else if(type.startsWith("error") || type.startsWith("errors"))
				val = TagHelper.handleList(this, request, Utils.getErrors((HttpServletRequest)request));
			else if(type.startsWith("message") || type.startsWith("messages"))
				val = TagHelper.handleList(this, request, Utils.getMessages((HttpServletRequest)request));
			else if(type.startsWith("resource"))
			{
				// Locale loc = (Locale)
				// ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
				//
				// MessageResources resources =
				// (MessageResources)
				// request.getAttribute(Globals.MESSAGES_KEY);
				//
				// val = resources.getMessage(loc, this.getAttribute("id"));

				val = ActionHelper.getResource(this.getAttribute("id"));
			}

			this.removeAttribute("type");

			if(type.endsWith("_min"))
				this.removeAttribute("id");

			if(val == null || val.equals(""))
			{
				this.setAttribute("class", "nomessages", '"');
				remove = true;
				return;
			}

			/*
			 * String oldclass = this.getAttribute("class"); if(oldclass ==
			 * null) this.setAttribute("class", "modify", '"'); else
			 * this.setAttribute("class", oldclass + " " + "modify", '"');
			 * 
			 * this.setAttribute("locale",
			 * ActionHelper.getLocale().getLanguage(), '"');
			 */
			this.setChildren(new NodeList());
			this.getChildren().add(new TextNode(val));
		}
		catch(Exception e)
		{
			//System.out.println(e);
		}
	}
}
