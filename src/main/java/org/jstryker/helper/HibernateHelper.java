package org.jstryker.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateHelper {
	
	private static Session session;
	private static SessionFactory sessionFactory;

	public static SessionFactory sessionFactory() {
		if (sessionFactory == null) {
			Configuration configuration = new Configuration().configure();
			sessionFactory = configuration.buildSessionFactory();
		}
		
		return sessionFactory;
	}

	public static Session currentSession() {
		if (session == null) {
			session = sessionFactory().openSession();
			session.beginTransaction();
		}
		return session;
	}

	public static void close() {
		session.getTransaction().rollback();
		session.close();
		session = null;
	}
}
