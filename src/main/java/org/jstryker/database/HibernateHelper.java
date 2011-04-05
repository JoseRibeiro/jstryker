package org.jstryker.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate tool to help with {@link Session}.
 */
public class HibernateHelper {
	
	private static Session session;
	private static SessionFactory sessionFactory;

	/**
	 * Creates or returns current {@link SessionFactory}.
	 * @return Current {@link SessionFactory}.
	 */
	public static SessionFactory sessionFactory() {
		if (sessionFactory == null) {
			Configuration configuration = new Configuration().configure();
			sessionFactory = configuration.buildSessionFactory();
		}
		
		return sessionFactory;
	}

	/**
	 * Opens or returns current {@link Session} with {@link org.hibernate.Transaction} started.
	 * @return Current {@link Session}.
	 */
	public static Session currentSession() {
		if (session == null) {
			session = sessionFactory().openSession();
			session.beginTransaction();
		}
		return session;
	}

	/**
	 * Rollback current {@link org.hibernate.Transaction} and closes {@link Session}.
	 */
	public static void close() {
		session.getTransaction().rollback();
		session.close();
		session = null;
	}
}
