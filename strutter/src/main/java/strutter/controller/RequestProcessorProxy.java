package strutter.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.ComposableRequestProcessor;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ControllerConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;

import strutter.Utils;
import strutter.config.ActionMappingExtended;
import strutter.config.ActionPlugin;
import strutter.helper.ActionHelper;
import strutter.helper.ActionHelperData;
import strutter.helper.PopulateHelper;
import strutter.helper.WSActionHelper;
import strutter.htmlparser.NodeFactory;
import strutter.htmlparser.Parser;
import strutter.htmlparser.tags.OptionTag;
import strutter.htmlparser.util.NodeList;
import strutter.interceptor.WebInterceptorInterface;
import strutter.view.tag.CButtonTag;
import strutter.view.tag.CDivTag;
import strutter.view.tag.CFormTag;
import strutter.view.tag.CInputTag;
import strutter.view.tag.CMetaTag;
import strutter.view.tag.CSelectTag;
import strutter.view.tag.CSpanTag;
import strutter.view.tag.CTextareaTag;

public class RequestProcessorProxy extends RequestProcessor
{
	public final static int BUFFERSIZE = 40000;
	static ActionPlugin plugin;
	static String proxyname;

	RequestProcessor proxy;

	static String template;
	static String script = null;
	static RMatcher localisation;

	static String actionfieldname;

	ClassLoader classloader = null;

	@Override
	public void init(ActionServlet servlet, ModuleConfig moduleConfig)
	        throws ServletException
	{
		super.init(servlet, moduleConfig);

		classloader = getClass().getClassLoader();

		try
		{
			proxy = (RequestProcessor)Class.forName(proxyname).newInstance();
		}
		catch(Exception e)
		{
			servlet.log("STRUTTER: unable to proxy class [" + proxyname + "]", e);
		}

		if(!(proxy instanceof ComposableRequestProcessor))
		{
			log.warn("ActionHelper require subclass of ComposableRequestProcessor");
		}

		CatalogFactory factory = CatalogFactory.getInstance();

		ControllerConfig controllerConfig = moduleConfig.getControllerConfig();

		Catalog catalog = factory.getCatalog(controllerConfig.getCatalog());

		Command command = catalog.getCommand("process-view"); // process-action

		ChainBase chain = new ChainBase();

		chain.addCommand(new BeforeRenderCommand());
		chain.addCommand(command);

		catalog.addCommand("process-view", chain);

		proxy.init(servlet, moduleConfig);

		template = getResource("script/process.template");

		localisation = new RMatcher();

		actionfieldname = plugin.getParameter();
	}

	@Override
	public void destroy()
	{
		super.destroy();
		proxy.destroy();
	}

	// add hidden action field as named in configuration
	// if(actionfieldname == null)
	// {
	// if (mapping != null && mapping.getParameter() != null)
	// actionfieldname = mapping.getParameter();
	// }

	@Override
	public void process(HttpServletRequest _request, HttpServletResponse _response) throws IOException, ServletException
	{
		//DEBUG System.out.println("" + System.currentTimeMillis() + _request.getRequestURL() + "?" + _request.getQueryString());

		// AJAX and so on
		if(_request.getServletPath().equals("/strutter.do")) // data.getActionname()
		{
			internalProcessing(_request, _response);
			return;
		}

		long start = System.currentTimeMillis();

		RequestWrapper requestwrapper = new RequestWrapper(_request);

		ActionMappingExtended mappingext = null;

		ActionHelperData ahd = null;
		HttpSession session = null;

		try
		{
			boolean isMainThread = false;
			boolean isHeading = false;
			boolean isRemoting = false;

			ahd = ActionHelper.init(getServletContext(), requestwrapper, _response);

			session = ahd.getSession();

			ActionConfig mapping = ahd.getMapping();

			if(mapping instanceof ActionMappingExtended)
			{
				mappingext = (ActionMappingExtended)mapping;
			}

			// do the character encoding staff
			requestwrapper.setCharacterEncoding(plugin.getEncoding()); // CharsetFilter
			_response.setCharacterEncoding(plugin.getEncoding());

			// disable caching
			if(plugin.getNocache().equals("1"))
			{
				_response.setHeader("Pragma", "No-cache");
				_response.setHeader("Cache-Control", "no-cache");
				_response.setDateHeader("Expires", 1);
			}

			if(mappingext != null && mappingext.isWsaction())
			{
				try
				{
					Object direct = Class.forName(mapping.getType()).newInstance();
					PopulateHelper.populate(requestwrapper, direct);

					String method = requestwrapper.getParameter(mapping.getParameter());
					WSActionHelper.dispatchMethod(direct, method);
				}
				catch(Exception e)
				{
					log.error("WSDispatcher", e);
				}

				return;
			}

			// if(interceptorBefore())
			// return;

			session.setAttribute("thread", Thread.currentThread());

			// ActionForward helperforward = ActionHelper.startInterceptors();
			//
			// if(helperforward != null) {
			// Utils.processForwardConfig(request, response, helperforward);
			// return;
			// }

			// Wrapper
			ResponseWrapper responsewrapper = new ResponseWrapper(_response);

			proxy.process(requestwrapper, responsewrapper);

			String doc;

			try
			{
				doc = responsewrapper.toString(plugin.getEncoding());
			}
			catch(Exception e)
			{
				doc = "Encoding Exception 1";
			}

			if(doc == null)
			{
				return;
			}

			Object form = null;

			try
			{
				form = Utils.getActionFormFromSession(ahd.getRequest());
			}
			catch(Exception e1)
			{
			}

			isMainThread = (ahd.getThreadcount() == 1);
			isHeading = (mappingext != null && mappingext.isHeading());
			isRemoting = (mappingext != null && mappingext.isRemoteaction());

			StringWriter out = new StringWriter(RequestProcessorProxy.BUFFERSIZE);

			if(isMainThread && isHeading)
			{
				if(plugin.getDoctype().equals("1"))
				{
					out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\"> ");
				}

				// out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
				// out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");

				if(plugin.getScript().equals("1") || plugin.getCookiecheck().equals("1") || plugin.getSessioncheck().equals("1") || plugin.getTemplate().equals("1"))
				{
					out.write("<SCRIPT src='strutter.do?js' type='text/javascript'></SCRIPT>\n");
				}
			}

			if(isRemoting)
			{
				out.write("<script type='text/javascript' src='dwr/engine.js'> </script>\n");
				out.write("<script type='text/javascript' src='dwr/util.js'> </script>\n");

				String classname = ActionPlugin.getClassName(mapping.getType());

				out.write("<script type='text/javascript' src='dwr/interface/" + classname + ".js'> </script>\n");
			}

			//System.out.println(doc);

			if(plugin.getViewer().equals("1"))
			{
				doc = htmlProcessing(requestwrapper, form, doc);
			}

			String decorator = (String)requestwrapper.getAttribute("decorator_name");

			if(decorator != null)
			{
				requestwrapper.setAttribute("decorator_body", doc);

				ResponseWrapper decoresponsewrapper = new ResponseWrapper(_response);

				RequestDispatcher dispatcher = requestwrapper.getRequestDispatcher("include/decorator/" + decorator);
				dispatcher.include(requestwrapper, decoresponsewrapper);

				try
				{
					doc = decoresponsewrapper.toString(plugin.getEncoding());

					//System.out.println(doc);
				}
				catch(Exception e)
				{
					doc = "Encoding Exception 2";
				}
			}

			if(mappingext != null && !"0".equals(mappingext.getProperty("INLINELOCALISATION")))
			{
				// localisierung $R{nachname}
				localisation.matchall(ahd, out, doc);
			}
			else
			{
				out.write(doc);
			}

			out.flush();

			if(isMainThread && isHeading)
			{
				if(plugin.getTemplate().equals("1"))
				{
					out.write(template);
				}

				if(plugin.getSessioncheck().equals("1"))
				{
					// Duplicate Window Check
					if(session.getAttribute("struttersession") == null)
					{
						session.setAttribute("struttersession", session.getId());
						out.write("<script>setWindowName('" + session.getId() + "');</script>\n");
					}
				}
				if(plugin.getCookiecheck().equals("1"))
				{
					// Cookies enabled check
					_response.addCookie(new Cookie("strutter", "1"));
				}

				if(plugin.getCookiecheck().equals("1") || plugin.getSessioncheck().equals("1"))
				{
					out.write("<script>strutterloaded();</script>\n");
				}

				if(plugin.getKeepalive().equals("1"))
				{
					out.write("<script>addkeepalive();</script>\n");
				}
			}

			/** AUTOINCLUDE package JS */

			String type = mapping.getType().replace('.', '/');
			String jspath = type + ".js";

			URL jsscript = classloader.getResource(jspath);

			//System.out.println(jsscript);

			if(jsscript != null)
			{
				out.write("<SCRIPT src='strutter.do?js_" + type + "' type='text/javascript'></SCRIPT>\n");
			}

			try
			{
				String encoding = requestwrapper.getHeader("Accept-Encoding");

				ServletOutputStream writer = _response.getOutputStream();

				if((encoding != null && encoding.indexOf("gzip") != -1) && isMainThread && plugin.getCompression().equals("1"))
				{
					GZIPOutputStream gzipstream = new GZIPOutputStream(writer);
					_response.addHeader("Content-Encoding", "gzip");

					gzipstream.write(out.toString().getBytes(plugin.getEncoding()));
					gzipstream.close();
				}
				else
				{
					writer.write(out.toString().getBytes(plugin.getEncoding()));
					writer.flush();
				}

				_response.flushBuffer();
			}
			catch(IOException e)
			{
			}
			catch(Exception e)
			{
				PrintWriter writer2 = _response.getWriter();
				writer2.write(doc);
				// writer2.flush();
			}

			// interceptorAfter();

		}
		finally
		{
			// Cleanup Threadlocale
			ActionHelper.remove();

			if(mappingext != null)
			{
				mappingext.setHeading(true);
			}

			try
			{
				session.setAttribute("thread", null);
			}
			catch(Exception e)
			{
			}

			//DEBUG System.out.println("done" + System.currentTimeMillis());

			System.out.println((System.currentTimeMillis() - start) + "ms " + _request.getRequestURL());
		}
	}

	public static void setProxyname(String proxyname)
	{
		RequestProcessorProxy.proxyname = proxyname;
	}

	private String getResource(String name, Class clazz)
	{
		BufferedInputStream streamreader = null;

		streamreader = new BufferedInputStream(clazz.getResourceAsStream(name));

		return getResource(name, streamreader);
	}

	private String getResource(String name)
	{
		BufferedInputStream streamreader = null;

		streamreader = new BufferedInputStream(classloader.getResourceAsStream(name));

		return getResource(name, streamreader);
	}

	private String getResource(String name, BufferedInputStream streamreader)
	{
		StringBuffer stream = new StringBuffer(RequestProcessorProxy.BUFFERSIZE);

		try
		{
			int data;

			//System.out.println("> xx >" + getClass().getClassLoader().getResource(name).getFile());

			//streamreader = new BufferedInputStream(new FileInputStream(new File(getClass().getResource(name).getFile())));
			// classloader.getResourceAsStream(name) 

			//System.out.println(streamreader);

			while((data = streamreader.read()) != -1)
			{
				stream.append((char)data);
			}
		}
		catch(Exception e)
		{
		}
		finally
		{
			try
			{
				streamreader.close();
			}
			catch(Exception e)
			{
			}
		}

		return stream.toString();
	}

	private BufferedInputStream getResourceAsStream(String name)
	{
		BufferedInputStream streamreader = new BufferedInputStream(RequestProcessor.class.getResourceAsStream(name));

		return streamreader;
	}

	String htmlProcessing(ServletRequest request, Object form, String doc) throws ServletException
	{
		try
		{
			Parser hparser = new Parser();

			//PrototypicalNodeFactory
			NodeFactory factory = new NodeFactory();

			if(form == null)
			{
				form = new Object();
			}

			factory.registerTag(new CSelectTag(form, request));
			factory.registerTag(new CInputTag(form, request));
			factory.registerTag(new CButtonTag(form, request));
			factory.registerTag(new CDivTag(form, request));
			factory.registerTag(new CSpanTag(form, request));
			factory.registerTag(new CTextareaTag(form, request));
			factory.registerTag(new OptionTag());
			factory.registerTag(new CFormTag(actionfieldname)); // Hiddenfield
			                                                    // Handler
			factory.registerTag(new CMetaTag(request)); // Dekorator
			                                            // Metainformation

			hparser.setNodeFactory(factory);
			hparser.setInputHTML(doc);

			//DEBUG System.out.println("P" + System.currentTimeMillis());
			NodeList nl = hparser.parse();
			//DEBUG System.out.println("E" + System.currentTimeMillis());

			return nl.toHtml(); // --as close to original as possible
		}
		catch(Exception e)
		{
			return doc;
		}
	}

	static String ETAG_VALUE;
	final static String IF_NONE_MATCH_HEADER = "If-None-Match";
	final static String ETAG_HEADER = "ETag";

	void streamzip(HttpServletResponse response, String data) throws IOException, ServletException
	{
		ServletOutputStream out = response.getOutputStream();

		GZIPOutputStream gzipstream = new GZIPOutputStream(out);
		response.addHeader("Content-Encoding", "gzip");

		data = data; //YUIFilter.compressJavaScriptString(data);

		gzipstream.write(data.getBytes());
		gzipstream.close();

		// PrintWriter out = response.getWriter();
		// out.println(script);
		// out.flush();
	}

	private final void internalProcessing(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String internal = request.getQueryString();

		if(internal == null)
		{
			return;
		}

		if(internal.equals("js"))
		{
			if(script == null)
			{
				HttpSession session = request.getSession();

				script = getResource("/script/process.js", RequestProcessor.class);

				script = script.replaceAll("##sessiontimeout##", Integer.toString((session.getMaxInactiveInterval() * 1000) - (10 * 1000)));

				if(actionfieldname != null)
				{
					script = script.replaceAll("##actionname##", actionfieldname);
				}

				ETAG_VALUE = String.valueOf(script.hashCode());
			}

			if(ETAG_VALUE.equals(request.getHeader(IF_NONE_MATCH_HEADER)))
			{
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			else
			{
				response.setHeader(ETAG_HEADER, ETAG_VALUE);
				streamzip(response, script);
			}
		}
		else if(internal.startsWith("js_"))
		{
			String file = internal.substring(3);

			String jspath = file + ".js";
			String etag = String.valueOf(new File(classloader.getResource(jspath).getFile()).lastModified());

			if(etag.equals(request.getHeader(IF_NONE_MATCH_HEADER)))
			{
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			else
			{
				response.setHeader(ETAG_HEADER, etag);
				streamzip(response, getResource(jspath));
			}
		}
		else if(internal.startsWith("killer"))
		{
			HttpSession session = request.getSession();

			try
			{
				Thread thread = ((Thread)session.getAttribute("thread"));

				System.out.println("kill: " + thread);

				if(thread != null)
				{
					thread.interrupt();
				}

				response.sendRedirect("");
			}
			catch(Exception e)
			{
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
			{
				out.write(data);
			}

			response.setContentType("image/gif");
			out.flush();
			in.close();
		}
		else if(internal.startsWith("keepalive"))
		{
			System.out.println("Strutter: keep alive");
		}

		return;
	}

	public boolean interceptorBefore() throws ServletException, IOException
	{
		if(!(ActionHelper.getMapping() instanceof ActionMappingExtended))
		{
			return false;
		}

		ActionMappingExtended mapping = (ActionMappingExtended)ActionHelper.getMapping();

		for(int i = 0; mapping != null && i < mapping.getInterceptors().size(); i++)
		{
			if(!(mapping.getInterceptors().get(i) instanceof WebInterceptorInterface))
			{
				continue;
			}
			WebInterceptorInterface interceptor = (WebInterceptorInterface)mapping.getInterceptors().get(i);
			ActionForward forward = interceptor.beforeView();

			if(forward != null)
			{
				Utils.processForwardConfig(ActionHelper.getRequest(), ActionHelper.getResponse(), forward);
				return true;
			}
		}

		return false;
	}

	public boolean interceptorAfter() throws ServletException, IOException
	{
		if(!(ActionHelper.getMapping() instanceof ActionMappingExtended))
		{
			return false;
		}

		ActionMappingExtended mapping = (ActionMappingExtended)ActionHelper.getMapping();

		for(int i = 0; mapping != null && i < mapping.getInterceptors().size(); i++)
		{
			if(!(mapping.getInterceptors().get(i) instanceof WebInterceptorInterface))
			{
				continue;
			}
			WebInterceptorInterface interceptor = (WebInterceptorInterface)mapping.getInterceptors().get(i);
			ActionForward forward = interceptor.afterView();

			if(forward != null)
			{
				Utils.processForwardConfig(ActionHelper.getRequest(), ActionHelper.getResponse(), forward);
			}
		}

		return false;
	}

	public static void setPlugin(ActionPlugin plugin)
	{
		RequestProcessorProxy.plugin = plugin;
	}
}

class BeforeRenderCommand implements Command
{

	@Override
	public boolean execute(Context context) throws Exception
	{
		ActionForward helperforward = ActionHelper.endInterceptors();

		if(helperforward != null)
		{
			((ServletActionContext)context).setForwardConfig(helperforward);
		}

		return false;
	}
}

class RMatcher
{
	static Pattern pattern;

	static
	{
		pattern = Pattern.compile("\\$R\\{(.*?)\\}", Pattern.MULTILINE);
	}

	public final void matchall(ActionHelperData ahd, StringWriter out, String val)
	{
		if(val == null)
		{
			return;
		}

		MessageResources resources = (MessageResources)ahd.getRequest().getAttribute(Globals.MESSAGES_KEY);

		Locale locale = ahd.getLocale();

		Matcher matcher = pattern.matcher(val);

		// without match return without copy / change
		if(!matcher.find())
		{
			out.write(val);
			return;
		}

		int pos = 0;

		do
		{
			out.write(val.substring(pos, matcher.start()));
			out.write(resources.getMessage(locale, matcher.group(1)));

			pos = matcher.end();
		}
		while(matcher.find());

		out.write(val.substring(pos));
		out.flush();

		return;
	}
}
