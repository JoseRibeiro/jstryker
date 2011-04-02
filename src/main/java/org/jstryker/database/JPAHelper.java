package org.jstryker.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * TODO javadoc
 */
public class JPAHelper {

	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;

	public static EntityManagerFactory entityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("jstryker");
		}
		return entityManagerFactory;
	}

	public static EntityManager currentEntityManager() {
		if (entityManager == null) {
			entityManager = entityManagerFactory().createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
		}
		return entityManager;
	}

	public static void close() {
		entityManager.getTransaction().rollback();
		entityManager.close();
		entityManager = null;
	}
}
