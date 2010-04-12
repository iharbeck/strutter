package strutter.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
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
import javax.servlet.http.HttpServletResponse;
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
import strutter.filter.YUIFilter;
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

	public void process(HttpServletRequest _request, HttpServletResponse response)
			throws IOException, ServletException
	{
		try 
		{
			RequestWrapper request = new RequestWrapper((HttpServletRequest) _request);
			
			
			ActionHelper.init(getServletContext(), request, response);

			// AJAX and so on
			if(internalProcessing(request, response))
				return;
			
			// do the character encoding staff
			request.setCharacterEncoding(plugin.getEncoding());  // CharsetFilter
			response.setCharacterEncoding(plugin.getEncoding());
			
			// disable caching
			if(plugin.getNocache().equals("1")) {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 1);
			}
			
	
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


			Object form = null;
			
			try {
				form = ActionHelper.getForm();
			} catch (Exception e1) {
			}
			
			
			StringWriter out = new StringWriter(10000);
			
			if(ActionHelper.isMainThread() && ActionHelper.isHeading())
			{
				if(plugin.getDoctype().equals("1"))
				   out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\"> ");
				
					//out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
				//out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
				
				if(plugin.getScript().equals("1") || plugin.getCookiecheck().equals("1") || plugin.getSessioncheck().equals("1") || plugin.getTemplate().equals("1") )
				   out.write("<SCRIPT src='strutter.do?script' type='text/javascript'></SCRIPT>\n");
			}
			
			if(ActionHelper.isRemoteAction())
			{
				if(plugin.getDoctype().equals("1"))
					out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\"> ");

				//out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");				
				
				out.write("<script type='text/javascript' src='dwr/engine.js'> </script>\n");
				out.write("<script type='text/javascript' src='dwr/util.js'> </script>\n");
				
				String classname = ActionPlugin.getClassName(ActionHelper.getActionMapping().getType());
				
				out.write("<script type='text/javascript' src='dwr/interface/" + classname + ".js'> </script>\n");
			}
			
			
			if(plugin.getViewer().equals("1"))
				doc = htmlProcessing(request, response, form, doc);
			
						
			String decorator = (String)request.getAttribute("decorator_name");
			
			if(decorator != null) {
				request.setAttribute("decorator_body", doc);
				
				ResponseWrapper decoresponsewrapper = new ResponseWrapper((HttpServletResponse) response);
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("/include/decorator/" + decorator);
				dispatcher.include((ServletRequest)request, (ServletResponse)decoresponsewrapper);
				
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

			
			/** AUTOINCLUDE package JS */
			
			
			String path = ActionHelper.getMapping().getType();
			
			String jspath = path.replace('.', '/');
			
			jspath = jspath + ".js";
			
			
			String jsscript = getResource(jspath);
			
			if(jsscript != null)
			{
				out.write("<script>\n");
				out.write(jsscript);
				out.write("\n</script>");
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

	private String getResourceWithoutcache(String name) 	
	{
		try {
			BufferedInputStream streamreader = new BufferedInputStream(
					   new FileInputStream(
							   getClass().getClassLoader().getResource(name).getFile() )
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
		} catch (Exception e) {
			return "";
		}
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
	
					/*NodeList inputtags = formtags.extractAllNodesThatMatch(
							  new AndFilter (
							   new TagNameFilter ("input"), new HasAttributeFilter("name", actionfieldname)), true
					);*/

					// hiddenfield action 
					InputTag input = new InputTag();

					input.setAttribute("name", actionfieldname, '"');
					input.setAttribute("type", "hidden", '"');
						

					// hiddenfield sendertoken
					InputTag inputToken = new InputTag();
					
					inputToken.setAttribute("name", "_TOKEN", '"');
					inputToken.setAttribute("type", "hidden", '"');
					inputToken.setAttribute("value", ""+System.currentTimeMillis(), '"');
					
					if(formtag.getChildren() != null)
					{
						formtag.getChildren().add(input);
						formtag.getChildren().add(inputToken);
					}
				}
			}
		
			return nl.toHtml();  // --as close to original as possible
		} catch (Exception e) {
			return doc;
		}
	}

	final static String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";
	final static String IF_NONE_MATCH_HEADER = "If-None-Match";
	
	final static String CACHE_CONTROL_HEADER = "Cache-Control";
	final static String CACHE_CONTROL_VALUE = "public, max-age=315360000, post-check=315360000, pre-check=315360000";
	final static String LAST_MODIFIED_HEADER = "Last-Modified";
	final static String LAST_MODIFIED_VALUE = "Sun, 06 Nov 2005 12:00:00 GMT";
	final static String ETAG_HEADER = "ETag";
	final static String ETAG_VALUE = "2740050219";
	final static String EXPIRES_HEADER = "Expires"; 

	
	boolean internalProcessing(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		if(!ActionHelper.getActionname().equals("/strutter"))
			return false;

		String internal = ((HttpServletRequest)request).getQueryString();
		HttpSession session = ((HttpServletRequest)request).getSession();

		if(internal != null)
		{
			if(internal.startsWith("script"))
			{
				//if (null != request.getHeader(IF_MODIFIED_SINCE_HEADER) || null != request.getHeader(IF_NONE_MATCH_HEADER)) 
					//{
					//	response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					//	return true;
				//} 
				   
				//response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE);
				//response.setHeader(LAST_MODIFIED_HEADER, LAST_MODIFIED_VALUE);
				//response.setHeader(ETAG_HEADER, ETAG_VALUE);
				//Calendar cal = Calendar.getInstance();
				//cal.roll(Calendar.YEAR, 10);
				//response.setDateHeader(EXPIRES_HEADER, cal.getTimeInMillis()); 
			       

				if(script == null)
				{
					script = getResource("script/process.js");
					script = YUIFilter.compressJavaScriptString(script);
					script = script.replaceAll("##sessiontimeout##", Integer.toString((session.getMaxInactiveInterval()*1000)-(10*1000)));
				}

				if(actionfieldname != null)
					script = script.replaceAll("##actionname##", actionfieldname);

				ServletOutputStream out = response.getOutputStream();
		        
		        GZIPOutputStream gzipstream = new GZIPOutputStream(out);
				response.addHeader("Content-Encoding", "gzip");
				    
				gzipstream.write(script.getBytes());
				gzipstream.close();
				
				//PrintWriter out = response.getWriter();
				//out.println(script);
				//out.flush();
			}
			else if(internal.startsWith("killer"))
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