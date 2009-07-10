package strutter.resource;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

public class UniversalMessageResourcesFactory extends PropertyMessageResourcesFactory {

  public MessageResources createResources(String config) 
  {
    try 
    {
      return new UniversalMessageResources(this, config);
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
