package strutter.view.tag;

import javax.servlet.ServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.ParserException;

public class CButtonTag extends CompositeTag
{
	private static final long serialVersionUID = 1L;

	Object form;
	ServletRequest request;

	String actionname;

	private static final String[] mIds = new String[] { "BUTTON" };

	public String[] getIds()
	{
		return(mIds);
	}

	public String[] getEnders()
	{
		return(mIds);
	}

	public String[] getEndTagEnders()
	{
		return(new String[0]);
	}

	public CButtonTag(Object form, ServletRequest request)
	{
		this.form = form;
		this.request = request;

		ActionConfig mapping = (ActionConfig)request.getAttribute(Globals.MAPPING_KEY);

		if(mapping != null && mapping.getParameter() != null)
			this.actionname = mapping.getParameter();
	}

	public void doSemanticAction() throws ParserException
	{
		// String name = getAttribute("name");

	}
}
