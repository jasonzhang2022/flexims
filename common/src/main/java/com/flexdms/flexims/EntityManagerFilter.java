package com.flexdms.flexims;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.flexdms.flexims.util.ThreadContext;

/**
 * A servlet Filter to create an EntityManager for each request and leave the
 * EntityManage in tread Local.
 * 
 * @author jason.zhang
 * 
 */
public class EntityManagerFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		EntityManager em = App.getEM();
		try {
			chain.doFilter(request, response);
		} finally {
			ThreadContext.remove(App.EM_KEY);
			try {
				if (em.isOpen()) {
					em.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
