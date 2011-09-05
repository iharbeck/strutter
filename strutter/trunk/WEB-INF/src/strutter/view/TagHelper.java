package strutter.view;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import jodd.bean.BeanUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.htmlparser.Tag;

import strutter.Utils;

public class TagHelper {

	public static String handleError (Tag tag, ServletRequest request, String superhtml) throws Exception
	{
		ActionMessages am = Utils.getErrors((HttpServletRequest)request);

		if(am.size() == 0)
			throw new Exception();
		
		String att = tag.getAttribute("error");

    	if(att == null)
    		throw new Exception();

    	tag.removeAttribute("error");

		//System.out.println(getAttribute("name"));
    	ActionMessage msg = (ActionMessage) am.get(tag.getAttribute("name")).next();

		Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
		MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
		String val = resources.getMessage(loc, msg.getKey(), msg.getValues());

		if(val != null) 
		{
    	    att = att.toLowerCase();
		    if(att.equals("behind"))
			   return superhtml + " <span class=\"error_label\"> " + val + " </span>";
		    else if(att.equals("before"))
			   return "<span class=\"error_label\"> " + val + " </span> " + superhtml;
		    else if(att.equals("class"))
		       tag.setAttribute("class", "error_control", '"');
		}
		
		return superhtml;
	}


	public static String handleList(Tag tag, ServletRequest request, ActionMessages am)
	{
		if(am == null)
			return null;

		String val = "";
		Iterator msgs = null;
		String id = tag.getAttribute("id");

		int size;
		if(id == null) {
			msgs = am.get();
			size = am.size();
		} else {
			msgs = am.get(id);
			size = am.size(id);
		}

		if(size == 0)
			return null;

		Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
		MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		while(msgs.hasNext())
		{
			ActionMessage msg = (ActionMessage)msgs.next();
			if(size > 1)
				val += "<li>"+ resources.getMessage(loc, msg.getKey(), msg.getValues()) + "\n";
			else
				val += resources.getMessage(loc, msg.getKey(), msg.getValues());
		}

		return val;
	}

	
	public static String getFormValue(Object form, String name) {
		return getFormValue(form, name, "");
	}

	public static String getFormValue(Object form, String name, String actionname) 
	{ 
		try 
		{
			String ret = (String)BeanUtil.getProperty(form, name);
			//String ret = BeanUtils.getProperty(form, name);
			if(ret == null)
				return "";
			return ret;
		} 
		/*catch (NoSuchMethodException e) {
			if(!name.equals(actionname))
				System.out.println("Strutter: missing attribute [" + name + "]");
			return null;
		} */
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public static void setFormValue(Object form, String name, String value) {
		try {
			BeanUtils.setProperty(form, name, value);
		} catch (Exception e) {
		}
	}

	public static String[] getFormValues(Object form, String name) {
		try {
			return BeanUtils.getArrayProperty(form, name);
		} catch (NoSuchMethodException e) {
			System.out.println("Strutter: missing attribute array [" + name + "]");
			return null;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public static Object getFormObject(Object form, String name) {
		return getFormObject(form, name, true);
	}

	public static Object getFormObject(Object form, String name, boolean warn) {
		try {
			return PropertyUtils.getProperty(form, name);
		} catch (NoSuchMethodException e) {
			if(warn)
				System.out.println("Strutter: missing attribute [" + name + "]");
			return null;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
