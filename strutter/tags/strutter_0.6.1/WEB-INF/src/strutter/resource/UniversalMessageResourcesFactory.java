package strutter.resource;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

/**
 * 
 * 
 * <message-resources
	   null="false"
	   parameter="ApplicationResources;gawky.database.DB#getConnection():select key, value from translation locale=?"
	   factory="strutter.resource.UniversalMessageResourcesFactory"
	/>
 * 
 * @author harb05
 *
 */

public class UniversalMessageResourcesFactory extends PropertyMessageResourcesFactory 
{
	private static final long serialVersionUID = 1L;

	public MessageResources createResources(String config) {
		try {
			return new UniversalMessageResources(this, config);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
