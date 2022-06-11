package lk.express.db;

import lk.express.Context;
import lk.express.bean.Entity;
import lk.express.bean.HasAllowedDivisions;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static String path;
	private static Logger logger;

	private static ServiceRegistry serviceRegistry;
	private static SessionFactory sessionFactory;

	public static void init(String path) {
		HibernateUtil.path = path;
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			synchronized (HibernateUtil.class) {
				if (sessionFactory == null) {
					buildSessionFactory(path);
				}
			}
		}
		return sessionFactory;
	}

	private static void buildSessionFactory(String path) {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration configuration = new Configuration();
			if (path == null) {
				configuration.configure();
			} else {
				configuration.configure(path + "/hibernate.cfg.xml");
			}
			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(configuration.getProperties());
			serviceRegistry = serviceRegistryBuilder.build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			getLogger().error("Initial SessionFactory creation failed", ex);
			throw new DBException(ex);
		}
	}

	public static Session getCurrentSession() {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		return sf.getCurrentSession();
	}

	public static Session getCurrentSessionWithTransaction() {
		Session session = getCurrentSession();
		ensureTransaction(session);
		return session;
	}

	private static void ensureTransaction(Session session) {
		if (!session.getTransaction().isActive()) {
			session.beginTransaction();
		}
	}

	public static void commit() {
		commit(getCurrentSession().getTransaction());
	}

	public static void commit(Transaction tx) {
		if (tx != null && tx.isActive()) {
			tx.commit();
		}
	}

	public static void rollback() {
		rollback(getCurrentSession().getTransaction());
	}

	public static void rollback(Transaction tx) {
		try {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
		} catch (Exception e) {
			getLogger().error("Exception while rolling back the transaction", e);
		}
	}

	public static void beginTransaction() {
		beginTransaction(getCurrentSession());
	}

	public static void beginTransaction(Session session) {
		session.beginTransaction();
	}

	public static void closeSession() {
		closeSession(getCurrentSession());
	}

	public static void closeSession(Session session) {
		session.close();
	}

	public static Criteria createCriteria(Class<? extends Entity> clazz) {
		return createCriteria(getCurrentSession(), clazz);
	}

	public static Criteria createCriteria(Session session, Class<? extends Entity> clazz) {
		Criteria criteria = session.createCriteria(clazz);
		if (isLimitedVisibility(clazz)) {
			Criterion allowed = Context.getSessionData().getAllowedDivisionsCriterion();
			if (allowed != null) {
				criteria.add(allowed);
			}
		}
		return criteria;
	}

	public static boolean isLimitedVisibility(Class<? extends Entity> clazz) {
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> i : interfaces) {
			if (HasAllowedDivisions.class.equals(i)) {
				return true;
			}
		}
		return false;
	}

	private synchronized static Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(HibernateUtil.class);
		}
		return logger;
	}
}