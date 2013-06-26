package strutter.view.tag;

import javax.servlet.ServletRequest;

import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.ParserException;

/**
 * Represents a FORM tag. IGNORE HTML ERROR
 */
public class CMetaTag extends MetaTag
{
	private static final long serialVersionUID = 1L;

	ServletRequest request;

	public CMetaTag(ServletRequest request)
	{
		this.request = request;
	}

	public void doSemanticAction() throws ParserException
	{
		super.doSemanticAction();

		// META INFORMATION FOR DECORATOR
		{
			// NodeList metatags = nl.extractAllNodesThatMatch(new TagNameFilter
			// ("meta"), true);

			// for(int i = 0; i < metatags.size(); i++)
			{
				// TagNode metatag = (TagNode)metatags.elementAt(i);

				String name = getAttribute("NAME");
				if(name != null && name.startsWith("decorator_"))
					request.setAttribute(name, getAttribute("CONTENT"));
			}
		}
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 * 
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders()
	{
		return(new String[] {});
	}
}
