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

package struts.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.util.NodeList;

import struts.view.tag.CDivTag;
import struts.view.tag.CInputTag;
import struts.view.tag.CSelectTag;
import struts.view.tag.CSpanTag;
import struts.view.tag.CTextareaTag;


public abstract class AbstractPageFilter implements Filter 
{
	public final static String ID = "name";

	protected abstract Object getForm(ServletRequest request);
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		PrintWriter out = response.getWriter();

		ResponseImpersonator tmpresponse = new ResponseImpersonator((HttpServletResponse) response);

		chain.doFilter(request, tmpresponse);

		String doc = tmpresponse.toString();

		Object form = getForm(request); 
		
		try 
		{
			//long start = System.currentTimeMillis(); 
			
			Parser hparser = new Parser ();

			PrototypicalNodeFactory factory = new PrototypicalNodeFactory (true);
			factory.registerTag (new CSelectTag   (form, request));
			factory.registerTag (new CInputTag    (form, request));
			factory.registerTag (new CDivTag      (form, request));
			factory.registerTag (new CSpanTag     (form, request));
			factory.registerTag (new CTextareaTag (form, request));
			factory.registerTag (new OptionTag());
			
			hparser.setNodeFactory (factory);
			hparser.setInputHTML(doc);
			
            //for (NodeIterator e = hparser.elements (); e.hasMoreNodes (); )
            //	out.println(e.nextNode().toHtml());

			NodeList nl = hparser.parse(null);
			out.println(nl.toHtml());
			
            out.flush();
            // System.out.println(System.currentTimeMillis()-start);
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

	public void init(FilterConfig f) {
	}

	public void destroy() {
	}
}

class ResponseImpersonator extends HttpServletResponseWrapper 
{
	private StringWriter writer = null;
	
	ResponseImpersonator(HttpServletResponse response) {
		super(response);
	}
	
	public PrintWriter getWriter() throws java.io.IOException {
		if (writer != null) 
			throw new IllegalStateException("repeated getWriter() call");
		if (stream != null) 
			throw new IllegalStateException("getOutputStream() was called first");

		writer = new StringWriter(2048);
		return new PrintWriter(writer);
	}

	private ByteArrayOutputStream stream;

	public ServletOutputStream getOutputStream() throws java.io.IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() was called first");
		}
		if (stream != null) {
			throw new IllegalStateException("repeated getOutputStream() call");
		}
		stream = new ByteArrayOutputStream(16 * 1024);

		return new ServletOutputStream() {
			public void write(int i) throws java.io.IOException {
				stream.write(i);
			}
		};
	}

	public String toString() {
		if (writer != null)
			return writer.toString();
		if (stream != null)
			return stream.toString();
		return null;
	}
}
