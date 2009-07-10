package strutter.view.tag;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.htmlparser.Tag;

import strutter.Utils;

public class TagHelper {

	public final static String handleError (Tag tag, ServletRequest request, String superhtml) throws Exception
	{
		String att = tag.getAttribute("error");
    	tag.removeAttribute("error");

    	if(att != null)
	   	{
    		String val = null;
    		try {
	    		ActionMessages am = Utils.getErrors((HttpServletRequest)request);

	    		//System.out.println(getAttribute("name"));
	   			Iterator msgs = am.get(tag.getAttribute("name"));

	  			Locale loc = (Locale) ((HttpServletRequest)request).getSession().getAttribute(Globals.LOCALE_KEY);
	  			MessageResources resources = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

  				if(msgs.hasNext())
  				{
  					ActionMessage msg = (ActionMessage)msgs.next();
  					val = resources.getMessage(loc, msg.getKey(), msg.getValues());
  				}
    		} catch(Exception e) {

    		}

    		if(val != null) {
	    	    att = att.toLowerCase();
			    if(att.equals("behind"))
				   return superhtml + " <span class=\"error_label\"> " + val + " </span>";
			    else if(att.equals("before"))
				   return "<span class=\"error_label\"> " + val + " </span> " + superhtml;
			    else if(att.equals("class"))
			       tag.setAttribute("class", "error_control", '"');
    		}
	   	}
    	throw new Exception();
	}


	public final static String handleList(Tag tag, ServletRequest request, ActionMessages am)
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

	
	public final static String getFormValue(Object form, String name) {
		return getFormValue(form, name, true);
	}

	public final static String getFormValue(Object form, String name, boolean warn) {
		try {
			return BeanUtils.getProperty(form, name);
		} catch (NoSuchMethodException e) {
			if(warn)
				System.out.println("Strutter: missing attribute [" + name + "]");
			return null;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public final static String[] getFormValues(Object form, String name) {
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
	
	public final static Object getFormObject(Object form, String name) {
		return getFormObject(form, name, true);
	}

	public final static Object getFormObject(Object form, String name, boolean warn) {
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
