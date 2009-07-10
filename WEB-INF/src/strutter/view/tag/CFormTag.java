package strutter.view.tag;

import org.htmlparser.tags.FormTag;
import org.htmlparser.util.ParserException;

/**
 * Represents a FORM tag. IGNORE HTML ERROR
 */
public class CFormTag extends FormTag 
{
	private static final long serialVersionUID = 1L;

	public void doSemanticAction() throws ParserException {
		super.doSemanticAction();
		
		if(this.getAttribute("method") == null)
		{
			this.setAttribute("method", "POST", '"');
		}
		
	}
	/**
	 * Return the set of end tag names that cause this tag to finish.
	 * 
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders() {
		return (new String[] {});
	}
}
