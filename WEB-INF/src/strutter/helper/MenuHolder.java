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
 * lable:
 * $menulable,link,target,permission
 * $menulable,link,target,permission
 * $menulable,link,target,permission
 * 
 * @author harb05
 */

public class MenuHolder 
{
	public static void main(String[] args) throws Exception {
		MenuHolder.addMenuHolder("sample");
		
		System.out.println(MenuHolder.getMenu("menu1", "sample"));
		System.out.println("done");
		System.out.println(MenuHolder.getMenu("menu2", "sample"));
	}
	
	static HashMap hsHolder = new HashMap();
	
	HashMap	templates = new HashMap();
	
	public static void addMenuHolder(String name) {
		try {
			MenuHolder holder = new MenuHolder(name);
			hsHolder.put(name, holder);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public MenuHolder(String name) throws Exception
	{
		String filepath = this.getClass().getClassLoader().getResource(name + ".menu").getPath();
		
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

	public static String getMenu(String id, String name) throws Exception {
		MenuHolder holder = (MenuHolder)hsHolder.get(name);
		
		if(holder == null)
			throw new Exception("No class mapping for " + name + " [" + id + "]");
			
		return holder.getContent(id);
	}
	
	public String getContent(String id) throws Exception
	{
		String content = ((StringBuffer)templates.get(id)).toString();

		if(content == null)
			throw new Exception("No menu template available [" + id + "]");
		
		if(ActionHelper.isInitialized())
			content = doLocalize(content);
		
		return content;
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
