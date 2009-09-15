package strutter.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * 
 * lable:
 * 
 * select * from client where name like ':name'
 * 
 * @author harb05
 *
 */

public class SqlHolder 
{
	static HashMap hsHolder = new HashMap();
	
	HashMap	templates = new HashMap();
	
	public static void addSqlHolder(Class clazz) {
		try {
			SqlHolder holder = new SqlHolder(clazz);
			hsHolder.put(clazz.getName(), holder);
		} catch (Exception e) {
		}
	}
	
	public SqlHolder(Class clazz) throws Exception
	{
		String filename = clazz.getName();
		filename = filename.substring(filename.lastIndexOf('.')+1);
		System.out.println(filename);
		System.out.println(clazz.getCanonicalName());

		String filepath = clazz.getResource(filename + ".sql").getPath();
		
		BufferedReader is = new BufferedReader(new FileReader(filepath));
		
		StringBuffer buf=null;
		
		String line = null;
	    while((line = is.readLine()) != null) {
	    	if(line.trim().equals("") || line.trim().startsWith("#") || line.trim().startsWith("--"))
	    		continue;
	    	
	    	if(line.matches("\\w*:\\s?"))
	    	{
	    		String id = line.replaceAll("[\\s:]", "");
	    		buf = new StringBuffer();
	    		templates.put(id, buf);
	    		System.out.println(">>" +  id);
	    		continue;
	    	}
	    	if(buf != null)
	    		buf.append(line).append("\n");
	    }
    	is.close();
	}

	public static String getSql(String id) throws Exception {
		return getSql(id, null);
	}	
	
	public static String getSql(String id, HashMap map) throws Exception {
		String clazzname; 
		try {
			clazzname = ActionHelper.getMapping().getType();  // WEB
		} catch (Exception e) {
			throw new Exception("Mapping not available [" + id + "] direct call invalid");
		}
		return getSql(id, clazzname, map);
	}
	
	public static String getSql(String id, String clazzname, HashMap map) throws Exception {
		SqlHolder holder = (SqlHolder)hsHolder.get(clazzname);
		
		if(holder == null)
			throw new Exception("No class mapping for " + clazzname + " [" + id + "]");
			
		return holder.getContent(id, map);
	}
	
	public String getContent(String id, HashMap map) throws Exception
	{
		String content = ((StringBuffer)templates.get(id)).toString();

		if(content == null)
			throw new Exception("No SQL template available [" + id + "]");
		
		if(map != null)
			content = doParameter(content, map);

		//if(ActionHelper.isInitialized())
		//	content = doLocalize(content);
		
		return content;
	}
	
	static Pattern patternParameter = Pattern.compile(":[a-zA-Z0-9\\._]+");
	
	public String doParameter(String content, HashMap map) 
	{
		StringBuffer buf = new StringBuffer();
		
		Matcher m = patternParameter.matcher(content);

		int start=0;
		while ( m.find() ) {
			String alias = content.substring(m.start()+1, m.end());
			
		    buf.append(content.substring(start, m.start()));
		    String val = (String)map.get(alias);
		    if(val == null) 
		    	val = "\n--:" + alias + "\n";
		    buf.append(val);

		    start = m.end();
		}
		buf.append(content.substring(start));
		
		return buf.toString();
	}
	
	static Pattern patternLocalize = Pattern.compile("\\$[a-zA-Z0-9\\._]*");

	public String doLocalize(String content) 
	{
		StringBuffer buf = new StringBuffer();
		Matcher m = patternLocalize.matcher(content);

		MessageResources mr = (MessageResources)ActionHelper.getRequestAttribute(Globals.MESSAGES_KEY);
		Locale locale = (Locale)ActionHelper.getSessionAttribute(Globals.LOCALE_KEY);

		int start=0;
		while ( m.find() ) {
			String alias = content.substring(m.start()+1, m.end());

		    buf.append(content.substring(start, m.start()));
		    buf.append(getMessage(alias, mr, locale));

		    start = m.end();
		}
		buf.append(content.substring(start));
		
		return buf.toString();
	}

	private final String getMessage(String key, MessageResources mr, Locale locale) {
		if(mr == null)
			return ("XXX" + key + "XXX").substring(0, Math.min(30, key.length()));
		String msg = mr.getMessage(locale, key).replace('?', 'X');
		return msg.substring(0, Math.min(30, msg.length()));
	}

}