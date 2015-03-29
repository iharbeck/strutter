package strutter.view.tag;

import javax.servlet.ServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;

import strutter.htmlparser.tags.CompositeTag;
import strutter.htmlparser.util.exception.ParserException;

public class CButtonTag extends CompositeTag
{
	private static final long serialVersionUID = 1L;

	Object form;
	ServletRequest request;

	String actionname;

	private static final String[] mIds = new String[] { "BUTTON" };

	@Override
	public String[] getIds()
	{
		return(mIds);
	}

	@Override
	public String[] getEnders()
	{
		return(mIds);
	}

	@Override
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
		{
			this.actionname = mapping.getParameter();
		}
	}

	@Override
	public void doSemanticAction() throws ParserException
	{
		// String name = getAttribute("name");

	}
}
