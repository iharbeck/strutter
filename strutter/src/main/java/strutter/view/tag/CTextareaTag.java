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

import strutter.htmlparser.nodes.TextNode;
import strutter.htmlparser.tags.TextareaTag;
import strutter.htmlparser.util.NodeList;
import strutter.htmlparser.util.exception.ParserException;
import strutter.view.TagHelper;

public class CTextareaTag extends TextareaTag
{
	private static final long serialVersionUID = 1L;

	Object form;
	ServletRequest request;

	public CTextareaTag(Object form, ServletRequest request)
	{
		this.form = form;
		this.request = request;
	}

	@Override
	public void doSemanticAction() throws ParserException
	{
		TextareaTag texta = new TextareaTag();
		texta.setTagName("/" + this.getTagName());
		this.setEndTag(texta);

		this.setEmptyXmlTag(false);

		String attname = this.getAttribute("name");

		if(attname != null && this.getAttribute("nofill") == null)
		{
			try
			{
				// String val = BeanUtils.getProperty(form,
				// this.getAttribute("name"));
				String val = TagHelper.getFormValue(form, attname);
				if(val != null)
				{
					this.setChildren(new NodeList());
					this.getChildren().add(new TextNode(val));
				}
			}
			catch(Exception e)
			{
			}
		}
	}

	@Override
	public String toHtml()
	{
		String html = super.toHtml();

		try
		{
			if(this.getAttribute("error") != null)
			{
				return TagHelper.handleError(this, request, html);
			}
		}
		catch(Exception e)
		{
		}

		return html;
	}
}
