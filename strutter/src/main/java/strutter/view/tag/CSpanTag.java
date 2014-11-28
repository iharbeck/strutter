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

import strutter.Utils;
import strutter.helper.ActionHelper;
import strutter.htmlparser.nodes.TextNode;
import strutter.htmlparser.tags.SpanTag;
import strutter.htmlparser.util.NodeList;
import strutter.htmlparser.util.exception.ParserException;
import strutter.view.TagHelper;

public class CSpanTag extends SpanTag
{
	private static final long serialVersionUID = 1L;

	Object form;
	ServletRequest request;
	boolean remove = false;
	boolean plain = false;

	public CSpanTag(Object form, ServletRequest request)
	{
		this.form = form;
		this.request = request;
	}

	@Override
	public String toHtml()
	{
		if(plain)
		{
			return this.getChildren().toHtml();
		}

		return remove ? "" : super.toHtml();
	}

	@Override
	public void doSemanticAction() throws ParserException
	{
		String type = this.getAttribute("type");

		if(type == null)
		{
			return;
		}

		SpanTag span = new SpanTag();
		span.setTagName("/" + this.getTagName());
		this.setEndTag(span);

		this.setEmptyXmlTag(false);

		String val = "";

		try
		{
			if(type.startsWith("text"))
			{
				val = TagHelper.getFormValue(form, this.getAttribute("id"));
			}
			else if(type.equals("error") || type.equals("errors"))
			{
				val = TagHelper.handleList(this, request, Utils.getErrors((HttpServletRequest)request));
			}
			else if(type.equals("message") || type.equals("messages"))
			{
				val = TagHelper.handleList(this, request, Utils.getMessages((HttpServletRequest)request));
			}
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

			if(type.endsWith("_min"))
			{
				this.removeAttribute("id");
			}
			else if(type.endsWith("_plain"))
			{
				plain = true;
			}

			if(val == null || val.equals(""))
			{
				remove = true;
				this.setAttribute("class", "nomessages", '"');
				return;
			}

			this.removeAttribute("type");

			/*
			 * String oldclass = this.getAttribute("class"); if(oldclass ==
			 * null) this.setAttribute("class", "modify"); else
			 * this.setAttribute("class", oldclass + " " + "modify");
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
