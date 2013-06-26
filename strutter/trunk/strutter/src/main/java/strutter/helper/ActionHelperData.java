package strutter.helper;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

public class ActionHelperData
{
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private HttpSession session = null;
	private ActionMessages errormsgs = null;
	private ActionMessages infomsgs = null;
	private Locale locale = null;
	private ActionMapping mapping = null;
	private String actionname = null;
	private boolean initialized = false;
	private int threadcount = 0;

	public ActionHelperData()
	{
	}

	public HttpServletRequest getRequest()
	{
		return request;
	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}

	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	public HttpSession getSession()
	{
		return session;
	}

	public void setSession(HttpSession session)
	{
		this.session = session;
	}

	public ActionMessages getErrormsgs()
	{
		if(errormsgs == null)
			errormsgs = new ActionMessages();
		
		return errormsgs;
	}

	public void setErrormsgs(ActionMessages errormsgs)
	{
		this.errormsgs = errormsgs;
	}

	public ActionMessages getInfomsgs()
	{
		if(infomsgs == null)
			infomsgs = new ActionMessages();
		
		return infomsgs;
	}

	public void setInfomsgs(ActionMessages infomsgs)
	{
		this.infomsgs = infomsgs;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	public ActionMapping getMapping()
	{
		return mapping;
	}

	public void setMapping(ActionMapping mapping)
	{
		this.mapping = mapping;
	}

	public String getActionname()
	{
		return actionname;
	}

	public void setActionname(String actionname)
	{
		this.actionname = actionname;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	public void setInitialized(boolean initialized)
	{
		this.initialized = initialized;
	}

	public int getThreadcount()
	{
		return threadcount;
	}

	public void setThreadcount(int threadcount)
	{
		this.threadcount = threadcount;
	}

	public void plusThreadcount()
	{
		this.threadcount++;
	}

	public void minusThreadcount()
	{
		this.threadcount--;
	}
}
