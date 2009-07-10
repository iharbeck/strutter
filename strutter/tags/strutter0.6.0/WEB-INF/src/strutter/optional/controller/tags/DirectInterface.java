package strutter.optional.controller.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DirectInterface extends BaseInterface {

	public void execute(HttpServletRequest request, HttpServletResponse response);

}
