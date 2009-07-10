package strutter.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.ComposableRequestProcessor;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ControllerConfig;
import org.apache.struts.config.ModuleConfig;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.util.NodeList;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.config.ActionPlugin;
import strutter.helper.ActionHelper;
import strutter.helper.PopulateHelper;
import strutter.helper.WSActionHelper;
import strutter.interceptor.WebInterceptorInterface;
import strutter.view.tag.CButtonTag;
import strutter.view.tag.CDivTag;
import strutter.view.tag.CFormTag;
import strutter.view.tag.CInputTag;
import strutter.view.tag.CSelectTag;
import strutter.view.tag.CSpanTag;
import strutter.view.tag.CTextareaTag;

public class RequestProcessorProxy extends RequestProcessor 
{
	static ActionPlugin plugin;
	static String proxyname;

	RequestProcessor proxy;

	static String template;
	static String script = null;
	static RMatcher localisation;
	
	
	public void init(ActionServlet servlet, ModuleConfig moduleConfig)
			throws ServletException
	{
		super.init(servlet, moduleConfig);

		try {
			proxy = (RequestProcessor)Class.forName(proxyname).newInstance();
		} catch(Exception e) {
			servlet.log("STRUTTER: unable to proxy class [" + proxyname + "]", e);
		}

		
		if(!(proxy instanceof ComposableRequestProcessor))
			log.warn("ActionHelper require subclass of ComposableRequestProcessor");

		CatalogFactory factory = CatalogFactory.getInstance();

		ControllerConfig controllerConfig = moduleConfig.getControllerConfig();

		Catalog catalog = factory.getCatalog(controllerConfig.getCatalog());

		Command command = catalog.getCommand("process-view");  // process-action

        ChainBase chain = new ChainBase();

        chain.addCommand(new BeforeRenderCommand());
        chain.addCommand(command);

        catalog.addCommand("process-view", chain);


		proxy.init(servlet, moduleConfig);

		template = getResource("script/process.template");

		localisation = new RMatcher();
	}

	public void destroy() {
		super.destroy();
		proxy.destroy();
	}

	public void process(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		try 
		{
			// do the character encoding staff
			request.setCharacterEncoding(plugin.getEncoding());  // CharsetFilter
			response.setCharacterEncoding(plugin.getEncoding());
			
			// disable caching
			if(plugin.getNocache().equals("1")) {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 1);
			}
			
			ActionHelper.init(getServletContext(), request, response);
	
			if(ActionHelper.isWSAction())
			{
				try {
					Object direct = Class.forName( ActionHelper.getMapping().getType() ).newInstance(); 
					PopulateHelper.populate(direct, request);
				
					String method = request.getParameter(ActionHelper.getMapping().getParameter());
					WSActionHelper.dispatchMethod(direct, method);
				} catch(Exception e) {
					log.error("WSDispatcher", e);
				}
				
				return;
			}
	
			// AJAX and so on
			if(internalProcessing(request, response))
				return;
	
	
			if(interceptorBefore())
				return;


			
			ActionHelper.getSession().setAttribute("thread", Thread.currentThread());


			ActionForward helperforward = ActionHelper.startInterceptors();

			if(helperforward != null) {
				Utils.processForwardConfig(request, response, helperforward);
				return;
			}



			// Wrapper
			RequestWrapper requestwrapper = new RequestWrapper((HttpServletRequest) request);
			ResponseWrapper responsewrapper = new ResponseWrapper((HttpServletResponse) response);


			proxy.process(requestwrapper, responsewrapper);

			String doc;
			
			try 
			{
				doc = responsewrapper.toString(plugin.getEncoding());
			} catch (Exception e) {
				doc = "Encoding Exception 1";
			}

			
			if(doc == null)
				return;


			Object form = ActionHelper.getForm();
			
			
			StringWriter out = new StringWriter(10000);
			
			if(ActionHelper.isMainThread() && ActionHelper.isHeading())
			{
				if(plugin.getDoctype().equals("1"))
					out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
				//out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
				
				if(plugin.getScript().equals("1") || plugin.getCookiecheck().equals("1") || plugin.getSessioncheck().equals("1") || plugin.getTemplate().equals("1") )
					out.write("<SCRIPT src='strutter.do?script' type='text/javascript'></SCRIPT>\n");
			}
			
			if(ActionHelper.isRemoteAction())
			{
				out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");				
				
				out.write("<script type='text/javascript' src='dwr/engine.js'> </script>\n");
				out.write("<script type='text/javascript' src='dwr/util.js'> </script>\n");
				out.write("<script type='text/javascript' src='dwr/interface/" + ActionHelper.getActionMapping().getAttribute() + ".js'> </script>\n");
			}
			
			
			if(plugin.getViewer().equals("1"))
				doc = htmlProcessing(request, response, form, doc);
			
			
						
			String decorator = (String)request.getAttribute("decorator_name");
			
			if(decorator != null) {
				request.setAttribute("decorator_body", doc);
				
				RequestWrapper decorequestwrapper = new RequestWrapper((HttpServletRequest) request);
				ResponseWrapper decoresponsewrapper = new ResponseWrapper((HttpServletResponse) response);
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("/include/decorator/" + decorator);
				dispatcher.include((ServletRequest)decorequestwrapper, (ServletResponse)decoresponsewrapper);
				
				try {
					doc = decoresponsewrapper.toString(plugin.getEncoding());
				} catch (Exception e) {
					doc = "Encoding Exception 2";
				}
			}
				
			// localisierung ${nachname}
			doc = localisation.matchall(doc);
			
			out.write(doc);
				
			
			if(ActionHelper.isMainThread() && ActionHelper.isHeading())
			{	
				if(plugin.getTemplate().equals("1"))
					out.write(template);
	
				if(plugin.getSessioncheck().equals("1"))
				{
					// Duplicate Window Check
				    if(ActionHelper.getSession().getAttribute("struttersession") == null)
					{
				    	ActionHelper.getSession().setAttribute("struttersession", ActionHelper.getSession().getId());
						out.write("<script>setWindowName('" + ActionHelper.getSession().getId() + "');</script>\n");
				    }
				}
				if(plugin.getCookiecheck().equals("1")) {
				    // Cookies enabled check
					((HttpServletResponse)response).addCookie(new Cookie("strutter", "1"));
				}
				
				if(plugin.getCookiecheck().equals("1") || plugin.getSessioncheck().equals("1"))
					out.write("<script>strutterloaded();</script>\n");
				
				if(plugin.getKeepalive().equals("1"))
					out.write("<script>addkeepalive();</script>\n");
			}

			

			try 
			{
				String encoding = request.getHeader("Accept-Encoding");
				
				ServletOutputStream writer = response.getOutputStream();
	
				if((encoding != null && encoding.indexOf("gzip") != -1) && ActionHelper.isMainThread() && plugin.getCompression().equals("1")) {
					GZIPOutputStream gzipstream = new GZIPOutputStream(writer);
					response.addHeader("Content-Encoding", "gzip");
					    
					gzipstream.write(out.toString().getBytes(plugin.getEncoding()));
					gzipstream.close();
				}
				else {
					writer.write(out.toString().getBytes(plugin.getEncoding()));
					writer.flush();
				}
				
				response.flushBuffer();
			} 
			catch(Exception e) 
			{
				PrintWriter writer2 = response.getWriter();
				writer2.write(doc);
				//writer2.flush();
			}
			interceptorAfter();

		}
		finally
		{
            // Cleanup Threadlocale
            ActionHelper.remove();
            ActionHelper.setHeading(true);

            try {
            	ActionHelper.getSession().setAttribute("thread", null);
            } catch(Exception e) {}
		}
	}

	
	
	
	public static void setProxyname(String proxyname) {
		RequestProcessorProxy.proxyname = proxyname;
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

	private BufferedInputStream getResourceAsStream(String name)
	{
		BufferedInputStream streamreader = new BufferedInputStream(
				   getClass().getClassLoader().getResourceAsStream(name)
				);

		return streamreader;
	}


	String actionfieldname;

	String htmlProcessing(ServletRequest request, ServletResponse response, Object form, String doc) throws ServletException
	{
		try 
		{
			Parser hparser = new Parser ();
	
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory (true);
	
			if(form == null)
				form = new Object();
		
			factory.registerTag (new CSelectTag   (form, request));
			factory.registerTag (new CInputTag    (form, request));
			factory.registerTag (new CButtonTag   (form, request));
			factory.registerTag (new CDivTag      (form, request));
			factory.registerTag (new CSpanTag     (form, request));
			factory.registerTag (new CTextareaTag (form, request));
			factory.registerTag (new OptionTag());
			factory.registerTag (new CFormTag());
			//factory.registerTag (new MetaTag());

			hparser.setNodeFactory (factory);
			hparser.setInputHTML(doc);
	
	        //for (NodeIterator e = hparser.elements (); e.hasMoreNodes (); )
	        //	out.println(e.nextNode().toHtml());
	
			NodeList nl = hparser.parse(null);
	
			// META INFORMATION FOR DECORATOR
			{
				NodeList metatags = nl.extractAllNodesThatMatch(new TagNameFilter ("meta"), true);
				
				for(int i = 0; i < metatags.size(); i++)
				{
					TagNode metatag = (TagNode)metatags.elementAt(i);
				
					String name = metatag.getAttribute("NAME");
					if(name != null && name.startsWith("decorator_"))
						request.setAttribute(name, metatag.getAttribute("CONTENT"));
				}
			}
			
			// add hidden action field as named in configuration
			if(actionfieldname == null)
			{
				ActionConfig mapping = ActionHelper.getMapping();
	
				if (mapping != null && mapping.getParameter() != null)
					actionfieldname = mapping.getParameter();
			}
	
			if(actionfieldname != null)
			{
				NodeList formtags = nl.extractAllNodesThatMatch(new TagNameFilter ("form"), true);
	
				if(formtags.size() > 0)
				{
					Node formtag = (Node)formtags.elementAt(0);
	
					NodeList inputtags = nl.extractAllNodesThatMatch(
							  new AndFilter (
							   new TagNameFilter ("input"), new HasAttributeFilter("name", actionfieldname)), true
					);
	
					if(inputtags.size() == 0)
					{
						InputTag input = new InputTag();
	
						input.setAttribute("name", actionfieldname, '"');
						input.setAttribute("type", "hidden", '"');
	
						if(formtag.getChildren() != null)
							formtag.getChildren().add(input);
					}

					// sendertoken
					InputTag inputToken = new InputTag();
					
					inputToken.setAttribute("name", "_TOKEN", '"');
					inputToken.setAttribute("type", "hidden", '"');
					inputToken.setAttribute("value", ""+System.currentTimeMillis(), '"');
					
					if(formtag.getChildren() != null)
						formtag.getChildren().add(inputToken);
				}
				
				
			}
		
			return nl.toHtml();  // --as close to original as possible
		} catch (Exception e) {
			return doc;
		}
	}

	boolean internalProcessing(ServletRequest request, ServletResponse response) throws IOException, ServletException
	{
		if(!ActionHelper.getActionname().equals("/strutter"))
			return false;

		String internal = ((HttpServletRequest)request).getQueryString();
		HttpSession session = ((HttpServletRequest)request).getSession();

		if(internal != null)
		{
			if(internal.startsWith("killer"))
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
			else if(internal.startsWith("echo"))
			{
				PrintWriter out = response.getWriter();

				out.println(request.getParameter("struttercache"));
				out.flush();
			}
			else if(internal.startsWith("script"))
			{
				PrintWriter out = response.getWriter();

				if(script == null)
				{
					script = getResource("script/process.js");
					script = script.replaceAll("##sessiontimeout##", Integer.toString((session.getMaxInactiveInterval()*1000)-(10*1000)));
					script = script.replaceAll("##actionname##", actionfieldname);
				}

				out.println(script);
				out.flush();
			}
			else if(internal.startsWith("img"))
			{
				ServletOutputStream out = response.getOutputStream();

				BufferedInputStream in = getResourceAsStream(request.getParameter("name"));

				int data;
				while((data = in.read()) != -1)
					out.write(data);

				response.setContentType("image/gif");
				out.flush();
				in.close();

			}
			else if(internal.startsWith("keepalive"))
			{
				System.out.println("Strutter: keep alive");
			}
		}

		return true;

	}



	public boolean interceptorBefore() throws ServletException, IOException
	{
		if(! (ActionHelper.getMapping() instanceof ActionMappingExtended) )
			return false;

		ActionMappingExtended mapping = (ActionMappingExtended)ActionHelper.getMapping();

		for(int i=0; mapping != null && i < mapping.getInterceptors().size(); i++)
		{
			if(!(mapping.getInterceptors().get(i) instanceof WebInterceptorInterface))
				continue;
		    WebInterceptorInterface interceptor = (WebInterceptorInterface) mapping.getInterceptors().get(i);
		    ActionForward forward = interceptor.beforeView();

		    if(forward != null) {
		    	Utils.processForwardConfig(ActionHelper.getRequest(), ActionHelper.getResponse(), forward);
		    	return true;
		    }
		}

		return false;
	}

	public boolean interceptorAfter() throws ServletException, IOException
	{
		if(! (ActionHelper.getMapping() instanceof ActionMappingExtended) )
			return false;

		ActionMappingExtended mapping = (ActionMappingExtended)ActionHelper.getMapping();

		for(int i=0; mapping != null && i < mapping.getInterceptors().size(); i++)
		{
			if(!(mapping.getInterceptors().get(i) instanceof WebInterceptorInterface))
				continue;
		    WebInterceptorInterface interceptor =	(WebInterceptorInterface) mapping.getInterceptors().get(i);
		    ActionForward forward = interceptor.afterView();

		    if(forward != null) {
		    	Utils.processForwardConfig(ActionHelper.getRequest(), ActionHelper.getResponse(), forward);
		    }
		}

		return false;
	}

	public static void setPlugin(ActionPlugin plugin) {
		RequestProcessorProxy.plugin = plugin;
	}

}

class BeforeRenderCommand implements Command {

	public boolean execute(Context context) throws Exception
	{
		ServletActionContext actioncontext = (ServletActionContext) context;

		ActionForward helperforward = ActionHelper.endInterceptors();

		if(helperforward != null)
			actioncontext.setForwardConfig(helperforward);

		return false;
	}
}

class RequestWrapper extends HttpServletRequestWrapper 
{
	public RequestWrapper(HttpServletRequest request) throws IOException { 
        super(request); 
    }

	public boolean isUserInRole(String role) 
	{
		return ActionHelper.hasRole(role);
	}

	public String getRemoteUser() {
		return ActionHelper.getUsername();
	} 
}

class ResponseWrapper extends HttpServletResponseWrapper
{
	private StringWriter writer = null;

	ResponseWrapper(HttpServletResponse response) {
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

	public String toString(String encoding) throws Exception {
		if (writer != null)
			return writer.toString();
		if (stream != null)
			return stream.toString(encoding);
		return null;
	}

	public void close() {
		try {
			if (writer != null)
				writer.close();
			if (stream != null)
				stream.close();
		} catch (Exception e) {
		}
	}
}


class RMatcher 
{
	final static String varpattern = "#R\\{(.*?)\\}";
	
	Pattern pattern;

	public RMatcher() {
		pattern = Pattern.compile(varpattern, Pattern.MULTILINE);
	}
	
	public final String matchall(String val) 
	{
		if(val == null)
			return null;
		
		Matcher matcher = pattern.matcher(val);
		
		StringBuilder target = new StringBuilder(4000);
		
		int pos = 0;
		while(matcher.find())
		{
			target.append(val.substring(pos, matcher.start()));
			target.append(ActionHelper.getResource(matcher.group(1)));
			
			pos = matcher.end();
		}
		
		target.append(val.substring(pos));
		
		
		return target.toString();
	}
}
