package org.jstryker.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * JPA tool to help with {@link EntityManager}.
 */
public class JPAHelper {

	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;
	private static String persistenceUnitName;

	/**
	 * Creates or returns current {@link EntityManagerFactory}.
	 * @param persistenceUnitName Persistence unit name form persistence.xml.
	 * @return Current {@link EntityManagerFactory}.
	 */
	public static EntityManagerFactory entityManagerFactory(String persistenceUnitName) {
		if (entityManagerFactory == null) {
			JPAHelper.persistenceUnitName = persistenceUnitName;
			entityManagerFactory = Persistence.createEntityManagerFactory(JPAHelper.persistenceUnitName);
		}
		return entityManagerFactory;
	}

	/**
	 * Creates or returns current {@link EntityManager} with {@link EntityTransaction} started.
	 * @return Current {@link EntityManager}.
	 */
	public static EntityManager currentEntityManager() {
		if (entityManager == null) {
			entityManager = entityManagerFactory(persistenceUnitName).createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
		}
		return entityManager;
	}

	/**
	 * Rollback current {@link EntityTransaction} and closes {@link EntityManager}.
	 */
	public static void close() {
		entityManager.getTransaction().rollback();
		entityManager.close();
		entityManager = null;
	}
}
