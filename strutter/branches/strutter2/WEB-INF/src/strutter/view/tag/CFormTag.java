package strutter.view.tag;

import org.htmlparser.tags.FormTag;

/**
* Represents a FORM tag.
* IGNORE HTML ERROR
*/
public class CFormTag extends FormTag
{
 /**
  * Return the set of end tag names that cause this tag to finish.
  * @return The names of following end tags that stop further scanning.
  */
 public String[] getEndTagEnders ()
 {
     return (new String[]{});
 }
}

