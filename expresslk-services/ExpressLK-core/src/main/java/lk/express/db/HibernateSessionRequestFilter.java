package lk.express.db;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSessionRequestFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(HibernateSessionRequestFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		try {
			// Begin unit of work
			HibernateUtil.beginTransaction();
			chain.doFilter(request, response);
			// End unit of work
			HibernateUtil.commit();
		} catch (Exception ex) {
			logger.error("Exception while filtering", ex);
			HibernateUtil.rollback();
			if (ServletException.class.isInstance(ex)) {
				throw (ServletException) ex;
			} else {
				throw new ServletException(ex);
			}
		} finally {
			HibernateUtil.closeSession();
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
}
