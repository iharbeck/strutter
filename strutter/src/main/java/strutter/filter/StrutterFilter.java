package strutter.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import strutter.helper.ActionHelper;

public class StrutterFilter implements Filter
{

	FilterConfig config;

	@Override
	public void destroy()
	{

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException
	{
		ActionHelper.init(config.getServletContext(), (HttpServletRequest)request, (HttpServletResponse)response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{
		this.config = config;
	}
}