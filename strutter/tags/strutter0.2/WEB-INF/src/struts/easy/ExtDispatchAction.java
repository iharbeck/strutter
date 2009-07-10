package struts.easy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

public class ExtDispatchAction extends DispatchAction {
	
	public ActionForward execute(ActionMapping arg0, ActionForm arg1, HttpServletRequest request, HttpServletResponse arg3) throws Exception 
	{
		startExt(request);
		ActionForward forward = super.execute(arg0, arg1, request, arg3);
		endExt();
		
		return forward;
	}

	void startExt(HttpServletRequest request)
	{
		this.request = request;
		errormsgs = getErrors(request);
		infomsgs  = getMessages(request);
	}

	void endExt()
	{
		addErrors(request, errormsgs);
		addMessages(request, infomsgs);
	}
	
	protected HttpServletRequest request;
	ActionMessages errormsgs;
	ActionMessages infomsgs;
	
	public void addError(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		errormsgs.add(alias, msg);
	}

	public void addError(String alias, String text, Object[] objs)
	{
		ActionMessage msg = new ActionMessage(text, objs);
		errormsgs.add( alias, msg);
	}

	public void addMessage(String alias, String text)
	{
		ActionMessage msg = new ActionMessage(text);
		infomsgs.add(alias, msg);
	}
	
	public boolean hasErrors()
	{
		return !getErrors(request).isEmpty();
	}
	
	public boolean hasMessages()
	{
		return !getMessages(request).isEmpty();
	}
	
}
