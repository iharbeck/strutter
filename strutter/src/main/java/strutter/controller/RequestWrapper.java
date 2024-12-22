package strutter.controller;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import strutter.helper.ActionHelper;

public class RequestWrapper extends HttpServletRequestWrapper
{
	public RequestWrapper(HttpServletRequest request) throws IOException
	{
		super(request);
	}

	@Override
	public boolean isUserInRole(String role)
	{
		return ActionHelper.hasRole(role);
	}

	@Override
	public String getRemoteUser()
	{
		return ActionHelper.getUsername();
	}
}
