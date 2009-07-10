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

package strutter.view;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.util.NodeList;

import strutter.Utils;
import strutter.optional.controller.BasicFilter;
import strutter.optional.controller.ConfigWrapper;
import strutter.optional.controller.DirectInterface;
import strutter.optional.helper.ActionHelper;
import strutter.view.tag.CDivTag;
import strutter.view.tag.CFormTag;
import strutter.view.tag.CInputTag;
import strutter.view.tag.CSelectTag;
import strutter.view.tag.CSpanTag;
import strutter.view.tag.CTextareaTag;


public abstract class AbstractPageFilter extends BasicFilter//implements Filter
{
	public final static String ID = "name";

	protected abstract Object getForm(ServletRequest request);

	private String actionname = null;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpSession session = ((HttpServletRequest)request).getSession(true);

		//extract actionname cut of ".do" results in /actionname
		String action = Utils.getActionMappingName(((HttpServletRequest)request).getServletPath());

		// AJAX and so on
		if(internalProcessing(request, response, action))
			return;
		
		// No Framework - PDF and Excel
		if(directProcessing(request, response, action))
			return;
		
		
		session.setAttribute("thread", Thread.currentThread());

		// init Helper for Reset Function init again in action
		ActionHelper.init(null, (HttpServletRequest)request, (HttpServletResponse)response);

		// do the character encoding staff
		request.setCharacterEncoding(encoding);  // CharsetFilter
		response.setCharacterEncoding(encoding);

		// TODO remember
		request.setAttribute("STRUTTERVIEW", "");

		PrintWriter out = response.getWriter();

		// Response Wrapper
		ResponseImpersonator tmpresponse = new ResponseImpersonator((HttpServletResponse) response);

		chain.doFilter(request, tmpresponse);

		String doc = tmpresponse.toString();

		Object form = getForm(request);

		try
		{
			out.println("<SCRIPT src='strutter.do?script' type='text/javascript'></SCRIPT>");
			//out.println("<SCRIPT>" + script + "</SCRIPT>");

			out.println(htmlProcessing(request, form, doc));
		
			out.println(template);

			// Duplicate Window Check
		    if(session.getAttribute("struttersession") == null)
			{
				session.setAttribute("struttersession", session.getId());
				out.println("<script>setWindowName('" + session.getId() + "');</script>");
		    }

		    // Cookies enabled check
			((HttpServletResponse)response).addCookie(new Cookie("strutter", "1"));

			out.println("<script>strutterloaded();</script>");
			
			out.flush();


		} catch (Exception e) {
		    System.out.println(e);
		} finally {

            // Cleanup Threadlocale
            ActionHelper.remove();

            try { // maybe invalidated somewhere
            	session.setAttribute("thread", null);
            } catch(Exception e) {}
		}
	}

	boolean directProcessing(ServletRequest request, ServletResponse response, String action) 
	{
		if(!actions.containsKey(action))
			return false;
		
		try {
			ConfigWrapper config = (ConfigWrapper)actions.get(action);
			
			DirectInterface bean = (DirectInterface)config.getActionclass().newInstance();

			bean.doexecute((HttpServletRequest)request, (HttpServletResponse)response);
		} 
		catch(Exception e) {
		}

		return true;
	}

	String htmlProcessing(ServletRequest request, Object form, String doc) throws Exception
	{
		Parser hparser = new Parser ();

		PrototypicalNodeFactory factory = new PrototypicalNodeFactory (true);

		if(form != null)
		{
			factory.registerTag (new CSelectTag   (form, request));
			factory.registerTag (new CInputTag    (form, request));
			factory.registerTag (new CDivTag      (form, request));
			factory.registerTag (new CSpanTag     (form, request));
			factory.registerTag (new CTextareaTag (form, request));
			factory.registerTag (new OptionTag());
			factory.registerTag (new CFormTag());
		}

		hparser.setNodeFactory (factory);
		hparser.setInputHTML(doc);

        //for (NodeIterator e = hparser.elements (); e.hasMoreNodes (); )
        //	out.println(e.nextNode().toHtml());

		NodeList nl = hparser.parse(null);

		// add hidden action field as named in configuration 
		if(actionname == null)
		{
			ActionConfig mapping = (ActionConfig)request.getAttribute(Globals.MAPPING_KEY);

			if (mapping != null && mapping.getParameter() != null)
				actionname = mapping.getParameter();
		}

		if(actionname != null)
		{
			NodeList formtags = nl.extractAllNodesThatMatch(new TagNameFilter ("form"), true);

			if(formtags.size() > 0)
			{
				Node formtag = (Node)formtags.elementAt(0);

				NodeList inputtags = nl.extractAllNodesThatMatch(
						  new AndFilter (
						   new TagNameFilter ("input"), new HasAttributeFilter("name", actionname)), true
				);

				if(inputtags.size() == 0)
				{
					InputTag input = new InputTag();

					input.setAttribute("name", actionname, '"');
					input.setAttribute("type", "hidden", '"');

					if(formtag.getChildren() != null)
						formtag.getChildren().add(input);
				}
			}
		}
		
		return nl.toHtml();
	}

	boolean internalProcessing(ServletRequest request, ServletResponse response, String action) throws IOException, ServletException
	{
		if(!action.equals("/strutter"))
			return false;
		
		String internal = ((HttpServletRequest)request).getQueryString();
		HttpSession session = ((HttpServletRequest)request).getSession();

		if(internal != null)
		{
			if("killer".equals(internal))
			{
				try
				{
					Thread thread = ((Thread)session.getAttribute("thread"));

					System.out.println("kill: " + thread);

					if(thread != null)
						thread.interrupt();

					((HttpServletResponse) response).sendRedirect("");
				} catch(Exception e) {
				}
			}
			else if("echo".equals(internal))
			{
				PrintWriter out = response.getWriter();

				out.println(request.getParameter("struttercache"));
				out.flush();
			}
			else if("script".equals(internal))
			{
				PrintWriter out = response.getWriter();

				out.println(script.replaceAll("##sessiontimeout##", Integer.toString(session.getMaxInactiveInterval()*1000-5000)));
				out.flush();
			}
			else if(internal.startsWith("keepalive"))
			{
			}
		}
		
		return true;

	}

	private String encoding = "iso-8859-1";

	public void destroy() { }

	static String script;
	static String template;

	public void init(FilterConfig config) throws ServletException
	{
		// add my own action handler
		super.init(config);
		
		encoding = config.getInitParameter("encoding");

		if (encoding == null)
			encoding = "iso-8859-1"; //"UTF-8";

		System.out.println("***INIT Strutter PAGEFILTER*** [" + encoding + "]");

		script   = getResource("script/process.js");
		template = getResource("script/process.template");
	}

	private String getResource(String name)
	{
		BufferedInputStream streamreader = new BufferedInputStream(
				   getClass().getClassLoader().getResourceAsStream(name)
				);

				StringBuffer stream = new StringBuffer();

				try {
					int data;
					while((data=streamreader.read()) != -1) {
						stream.append((char)data);
					}
				} catch (Exception e) {
				}

		return stream.toString();
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
