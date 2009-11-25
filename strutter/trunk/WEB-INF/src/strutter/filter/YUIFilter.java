package strutter.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import strutter.controller.ResponseWrapper;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Servlet Filter to Compress/Minify JavaScript and CSS (Cascading Style Sheets)
 * For more information visit http://technology.amis.nl/blog/?p=2392
 * 
 * @author Jeroen van Wilgenburg, AMIS Services BV
 * @version 0.1a - 9 September 2007
 *
 */
/*
<filter>    
  <display-name>Yahoo Compressor Filter</display-name>    
  <filter-name>YUIFilter</filter-name>    
  <filter-class>strutter.filter.YUIFilter</filter-class>
  <init-param>
    <param-name>preserve-semi</param-name>
    <param-value>true</param-value>
  </init-param>
</filter>

<filter-mapping>    
  <filter-name>YUIFilter</filter-name>
  <url-pattern>*.js</url-pattern>
</filter-mapping>

<filter-mapping>
  <filter-name>YUIFilter</filter-name>
  <url-pattern>*.css</url-pattern>
</filter-mapping>
*/

public class YUIFilter implements Filter 
{
    private FilterConfig filterConfig;

    private int lineBreakPos = -1;   //Insert a line break after the specified column number
    private boolean verbose = false; //Display possible errors in the code
    private boolean munge = true; //Minify only, do not obfuscate
    private boolean preserveAllSemiColons = true; //Preserve unnecessary semicolons
    private boolean disableOptimizations = false;
    
    private static Map<String, String> cache = new HashMap<String, String>();


    public void init(FilterConfig filterConfig) throws ServletException 
    {
        this.filterConfig = filterConfig;

        String lineBreak = filterConfig.getInitParameter("linebreak");
        if (lineBreak != null) {
            lineBreakPos = Integer.parseInt(lineBreak);
        }

        String verboseString = filterConfig.getInitParameter("verbose");
        if (verboseString != null) {
            verbose = Boolean.parseBoolean(verboseString);
        }

        String noMungeString = filterConfig.getInitParameter("munge");
        if (noMungeString != null) {
            munge = Boolean.parseBoolean(noMungeString);
        }

        String preserveAllSemiColonsString = filterConfig.getInitParameter("preservesemicolons");
        if (preserveAllSemiColonsString != null) {
            preserveAllSemiColons = Boolean.parseBoolean(preserveAllSemiColonsString);
        }

        
        String disableOptimizationsString = filterConfig.getInitParameter("disableoptimizations");
        if (disableOptimizationsString != null) {
            disableOptimizations = Boolean.parseBoolean(disableOptimizationsString); 
        }
        
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException 
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ServletContext context = filterConfig.getServletContext();
        
        String requestPATH = request.getServletPath();

        InputStream inputStream = context.getResourceAsStream(requestPATH);

        ResponseWrapper responsewrapper = new ResponseWrapper(response);
		
        String s = null;
        if(inputStream == null)
        {	
        	filterChain.doFilter(servletRequest, responsewrapper);
        	try {
        		s = responsewrapper.toString("UTF-8");
        	} catch (Exception e) {
        	}
    	} else {
    		s = cache.get(requestPATH);
    	}
        if (s == null) 
        {
            if (requestPATH.endsWith(".js")) {
                s = getCompressedJavaScript(inputStream);
            } else if (requestPATH.endsWith(".css")) {
                s = getCompressedCss(inputStream);
            } 
            cache.put(requestPATH, s);
        }
        
        ServletOutputStream out = response.getOutputStream();
        
        GZIPOutputStream gzipstream = new GZIPOutputStream(out);
		response.addHeader("Content-Encoding", "gzip");
		
		if(s != null) {
			gzipstream.write(s.getBytes());
		
			Calendar cal = Calendar.getInstance();
		    cal.roll(Calendar.YEAR, 10);
		    response.setDateHeader("Expires", cal.getTimeInMillis()); 
		}
		gzipstream.close();
		
        //out.print(s);   
    }

    public static String stream2str(InputStream is)
    {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String ss = sb.toString();
        
        return ss;
    }
    
    public static String compressJavaScriptString(String script)
    {
    	try {
	    	InputStream is = new ByteArrayInputStream(script.getBytes("ISO8859-1"));
	    	
	       	return new YUIFilter().getCompressedJavaScript(is);
    	} catch (Exception e) {
			return script;
		}
    }

    /**
     * Note that the inputStream is closed!
     *
     * @param inputStream
     * @throws IOException
     */
    private String getCompressedJavaScript(InputStream inputStream) throws IOException 
    {
        InputStreamReader isr = new InputStreamReader(inputStream, "ISO8859-1");
        JavaScriptCompressor compressor = new JavaScriptCompressor(isr, new YUIFilterErrorReporter());
        inputStream.close();

        StringWriter out = new StringWriter();
        compressor.compress(out, lineBreakPos, munge, verbose, preserveAllSemiColons, disableOptimizations);
        out.flush();

        StringBuffer buffer = out.getBuffer();
        return buffer.toString();
    }

    /**
     * Note that the inputStream is closed!
     *
     * @param inputStream
     * @throws IOException
     */
    private String getCompressedCss(InputStream inputStream) throws IOException 
    {
        InputStreamReader isr = new InputStreamReader(inputStream, "ISO8859-1");
        CssCompressor compressor = new CssCompressor(isr);
        inputStream.close();

        StringWriter out = new StringWriter();
        compressor.compress(out, lineBreakPos);
        out.flush();

        StringBuffer buffer = out.getBuffer();
        return buffer.toString();
    }

    public void destroy() {
    }
}
