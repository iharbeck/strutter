package strutter.view.tag;

import strutter.htmlparser.tags.FormTag;
import strutter.htmlparser.tags.InputTag;
import strutter.htmlparser.util.exception.ParserException;

/**
 * Represents a FORM tag. IGNORE HTML ERROR
 */
public class CFormTag extends FormTag
{
	private static final long serialVersionUID = 1L;

	private String actionfieldname;

	public CFormTag(String actionfieldname)
	{
		this.actionfieldname = actionfieldname;
	}

	@Override
	public void doSemanticAction() throws ParserException
	{
		super.doSemanticAction();

		if(this.getAttribute("method") == null)
		{
			this.setAttribute("method", "POST", '"');
		}

		if(actionfieldname != null)
		{
			/*
			 * NodeList inputtags = formtags.extractAllNodesThatMatch( new
			 * AndFilter ( new TagNameFilter ("input"), new
			 * HasAttributeFilter("name", actionfieldname)), true );
			 */

			// hiddenfield action
			InputTag input = new InputTag();

			input.setAttribute("name", actionfieldname, '"');
			input.setAttribute("type", "hidden", '"');

			// hiddenfield sendertoken
			InputTag inputToken = new InputTag();

			inputToken.setAttribute("name", "_TOKEN", '"');
			inputToken.setAttribute("type", "hidden", '"');
			inputToken.setAttribute("value", "" + System.currentTimeMillis(), '"');

			if(getChildren() != null)
			{
				getChildren().add(input);
				getChildren().add(inputToken);
			}
		}
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 * 
	 * @return The names of following end tags that stop further scanning.
	 */
	@Override
	public String[] getEndTagEnders()
	{
		return(new String[] {});
	}
}
