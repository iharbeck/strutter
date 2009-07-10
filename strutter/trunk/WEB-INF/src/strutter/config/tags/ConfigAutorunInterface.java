package strutter.config.tags;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;

public interface ConfigAutorunInterface {

	public void init(ActionServlet servlet, ModuleConfig moduleConfig);

}
