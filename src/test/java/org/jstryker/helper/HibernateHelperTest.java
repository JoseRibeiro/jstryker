package org.jstryker.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

/**
 * Test to {@link HibernateHelper}.
 */
public class HibernateHelperTest {
	
	@Test
	public void shouldConfigureSessionFactoryOnlyOnce() throws Exception {
		assertSame(HibernateHelper.sessionFactory(), HibernateHelper.sessionFactory());
	}
	
	@Test
	public void shouldOpenNewSessionWhenThereAreNotSession() throws Exception {
		Session session = HibernateHelper.currentSession();
		assertNotNull("Should open new session.", session);
		assertTrue("Should start a new transaction.", session.getTransaction().isActive());
	}
	
	@Test
	public void shouldRetrieveCurrenteSessionWhenSessionWasOpened() throws Exception {
		assertSame(HibernateHelper.currentSession(), HibernateHelper.currentSession());
	}
	
	@Test
	public void shouldRollbackSessionWhenClose() throws Exception {
		Transaction transaction = mock(Transaction.class);
		
		Session session = mock(Session.class);
		when(session.getTransaction()).thenReturn(transaction);
		
		ReflectionHelper.injectValueInStaticField(HibernateHelper.class, "session", session);
		
		HibernateHelper.close();
		verify(transaction).rollback();
	}
	
	@Test
	public void shouldCloseSessionWhenClose() throws Exception {
		Transaction transaction = mock(Transaction.class);
		
		Session session = mock(Session.class);
		when(session.getTransaction()).thenReturn(transaction);
		
		ReflectionHelper.injectValueInStaticField(HibernateHelper.class, "session", session);
		
		HibernateHelper.close();
		verify(session).close();
	}
	
	@Test
	public void shouldCleanCurrenteSessionWhenClose() throws Exception {
		Transaction transaction = mock(Transaction.class);
		
		Session session = mock(Session.class);
		when(session.getTransaction()).thenReturn(transaction);
		
		ReflectionHelper.injectValueInStaticField(HibernateHelper.class, "session", session);
		
		HibernateHelper.close();
		assertNotSame(session, HibernateHelper.currentSession());
		HibernateHelper.close();
	}
}
