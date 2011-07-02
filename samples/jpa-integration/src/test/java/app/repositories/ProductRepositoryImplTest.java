package app.repositories;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.jstryker.database.DBUnitHelper;
import org.jstryker.database.JPAHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ProductRepositoryImplTest {
	
	private ProductRepository repository;
	
	@BeforeClass
	public static void beforeClass() {
		JStrykerHelper.init();
	}
	
	@Before
	public void setUp() throws Exception {
		new DBUnitHelper().cleanInsert("/dataset/product.xml");
		
		EntityManager em = JPAHelper.currentEntityManager();
		repository = new ProductRepositoryImpl(em);
	}

	@After
	public void tearDown() throws Exception {
		JPAHelper.close(); //rollback and closing session
		new DBUnitHelper().deleteAll("/dataset/product.xml"); //cleaning datasource
	}

	@Test
	public void deveListarTodosOsTiposDaNotificacao() throws Exception {
		assertTrue(repository.findAll().size() == 2);
	}
}

