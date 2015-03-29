package strutter.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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