package com.yss.id.server.filter;

import com.yss.id.server.context.ContextFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * 维护FSIPContext上下文
 * 
 * @author LSP
 *
 */
@Order
@WebFilter(filterName = "contextFilter", urlPatterns = "/*")
public class ContextFilter implements Filter {

	@Autowired
	private ContextFactory contextFactory;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		//创建上下文信息
		contextFactory.createContext();

		chain.doFilter(request, response);
	}
	//兼容filter 低版本，低版本没有默认实现
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	//兼容filter 低版本，低版本没有默认实现
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

}
