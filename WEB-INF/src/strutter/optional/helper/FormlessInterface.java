package strutter.optional.helper;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.MultipartRequestHandler;

public interface FormlessInterface 
{
	public void reset(ActionMapping mapping, HttpServletRequest request);
	public MultipartRequestHandler getMultipartRequestHandler();
	public void setMultipartRequestHandler(MultipartRequestHandler multipartRequestHandler);
}
