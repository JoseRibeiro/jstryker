package org.jstryker.database;

import org.jstryker.reflection.ReflectionHelper;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes for {@link JPAHelper}.
 */
public class JPAHelperTest {

	@Test
	public void shouldConfigureEntityManagerFactoryOnlyOnce() throws Exception {
		String persistenceUnitName = "jstryker";
		assertSame(JPAHelper.entityManagerFactory(persistenceUnitName), JPAHelper.entityManagerFactory(persistenceUnitName));
	}

	@Test
	public void shouldCreateNewEntityManagerWhenThereIsNoEntityManager() throws Exception {
		EntityManager entityManager = JPAHelper.currentEntityManager();
		assertNotNull("Should crate new entity manager.", entityManager);
		assertTrue("Should start a new transaction.", entityManager.getTransaction().isActive());
	}

	@Test
	public void shouldRetrieveCurrentEntityManagerWhenEntityManagerWasOpened() throws Exception {
		assertSame(JPAHelper.currentEntityManager(), JPAHelper.currentEntityManager());
	}

	@Test
	public void shouldRollbackTransactionOnClose() throws Exception {
		EntityTransaction transaction = mock(EntityTransaction.class);
		EntityManager entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(transaction);

		ReflectionHelper.injectValueInStaticField(JPAHelper.class, "entityManager", entityManager);

		JPAHelper.close();

		verify(transaction).rollback();
	}

	@Test
	public void shouldCloseEntityManagerOnClose() throws Exception {
		EntityTransaction transaction = mock(EntityTransaction.class);
		EntityManager entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(transaction);

		ReflectionHelper.injectValueInStaticField(JPAHelper.class, "entityManager", entityManager);

		JPAHelper.close();

		verify(entityManager).close();
	}

	@Test
	public void shouldCleanCurrentEntityManagerOnClose() throws Exception {
		EntityTransaction transaction = mock(EntityTransaction.class);
		EntityManager entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(transaction);

		ReflectionHelper.injectValueInStaticField(JPAHelper.class, "entityManager", entityManager);

		JPAHelper.close();
		assertNotSame(entityManager, JPAHelper.currentEntityManager());
		JPAHelper.close();
	}
}
