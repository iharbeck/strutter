package strutter.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import strutter.helper.ActionHelper;

public class RequestWrapper extends HttpServletRequestWrapper
{
	public RequestWrapper(HttpServletRequest request) throws IOException
	{
		super(request);
	}

	public boolean isUserInRole(String role)
	{
		return ActionHelper.hasRole(role);
	}

	public String getRemoteUser()
	{
		return ActionHelper.getUsername();
	}
}
