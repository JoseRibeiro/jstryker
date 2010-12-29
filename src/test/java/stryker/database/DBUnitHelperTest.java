package stryker.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.mockito.InOrder;
import stryker.exception.StrykerException;

/**
 * Tests for {@link DBUnitHelper}.
 */
public class DBUnitHelperTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Connection connection;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Connection createDB = ConnectionHelper.getConnection();
		ScriptRunner scriptRunner = new ScriptRunner(createDB);
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/stryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
			createDB.close();
		}
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		Connection connection = ConnectionHelper.getConnection();
		ScriptRunner scriptRunner = new ScriptRunner(connection);
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/drop-stryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
			connection.close();
		}
	}

	@Before
	public void setUp() throws Exception {
		connection = ConnectionHelper.getConnection();
	}

	@After
	public void tearDown() throws Exception {
		if (!connection.isClosed()) {
			connection.close();
		}
	}

	@Test
	public void shouldResetDataBaseToDataSetContent() throws Exception {
		new DBUnitHelper().cleanInsert("/dbunit-dataset.xml", connection);

		int id = (Integer) new QueryRunner().query(connection, "Select * from stryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 2, id);
	}

	@Test
	public void shouldThrowStrykerExceptionWhenSQLExceptionOccurs() throws Exception {
		String reason = "connection does not exist";
		thrown.expect(StrykerException.class);
		thrown.expectMessage(reason);

		connection.close();
		new DBUnitHelper().cleanInsert("/dbunit-dataset.xml", connection);
	}

	@Test
	public void shouldThrowStrkyerExceptionWhenIOExceptionOccurs() throws Exception {
		String reason = "java.net.MalformedURLException";
		thrown.expect(StrykerException.class);
		thrown.expectMessage(reason);

		new DBUnitHelper().cleanInsert("/unexistent-dataset.xml", connection);
	}

	@Test
	public void shouldResetDataBaseToDataSetContentWithTruncateTable() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		String resourcePath = "/dbunit-dataset.xml";
		dbUnitHelper.truncateAndInsert(resourcePath);
		
		InOrder inOrder = inOrder(dbUnitHelper);
		inOrder.verify(dbUnitHelper).execute(eq(resourcePath), any(Connection.class), eq(DatabaseOperation.TRUNCATE_TABLE));
		inOrder.verify(dbUnitHelper).execute(eq(resourcePath), any(Connection.class), eq(DatabaseOperation.INSERT));
	}

	@Test
	public void shouldResetDataBaseToDataSetContentWithTruncateTableUsingSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		String resourcePath = "/dbunit-dataset.xml";
		dbUnitHelper.truncateAndInsert(resourcePath, connection);

		InOrder inOrder = inOrder(dbUnitHelper);
		inOrder.verify(dbUnitHelper).execute(resourcePath, connection, DatabaseOperation.TRUNCATE_TABLE);
		inOrder.verify(dbUnitHelper).execute(resourcePath, connection, DatabaseOperation.INSERT);
	}

	@Test
	public void shouldGenerateDateSetFromDataSource() throws Exception {
		String path = "target/generatedDataSet.xml";
		DBUnitHelper.generateDataSet(path, connection);
		assertTrue("Should generate dataset from data source.", new File(path).exists());
	}

	@Test
	public void shouldThrowStrykerExceptionWhenDataSetExceptionOccursInDataSetGenerator() throws Exception {
		String path = "target/generatedDataSet.xml";
		String reason = "connection does not exist";
		thrown.expect(StrykerException.class);
		thrown.expectMessage(reason);

		connection.close();
		DBUnitHelper.generateDataSet(path, connection);
	}

	@Test
	public void shouldCreatePathToGenerateDataSetWhenPathDoesNotExist() throws Exception {
		String path = "target/newFolder/generatedDataSet.xml";
		DBUnitHelper.generateDataSet(path, connection);
		assertTrue("Should generate dataset from data source.", new File(path).exists());
	}

	@Test
	public void shouldResetDataSourceToDataSetContent() throws Exception {
		new DBUnitHelper().cleanInsert("/dbunit-dataset.xml");

		int id = (Integer) new QueryRunner().query(connection, "Select * from stryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 2, id);
	}

	@Test
	public void shouldDeleteAllData() throws Exception {
		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.cleanInsert("/dbunit-dataset.xml");
		dbUnitHelper.deleteAll("/dbunit-dataset.xml");

		int id = (Integer)  new QueryRunner().query(connection, "Select count(*) as total from stryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("total");
			}
		});
		assertEquals("Should not have data.", 0, id);
	}

	@Test
	public void shouldTruncateData() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		String resourcePath = "/dbunit-dataset.xml";

		dbUnitHelper.truncate(resourcePath);

		verify(dbUnitHelper).execute(eq(resourcePath), any(Connection.class), eq(DatabaseOperation.TRUNCATE_TABLE));
	}

	@Test
	public void shouldTruncateDataWithSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		String resourcePath = "/dbunit-dataset.xml";

		dbUnitHelper.truncate(resourcePath, connection);

		verify(dbUnitHelper).execute(resourcePath, connection, DatabaseOperation.TRUNCATE_TABLE);
	}

	@Test
	public void shouldDisableMysqlForeignKeyChecks() throws Exception {
		Statement statement = mock(Statement.class);

		Connection connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(statement);

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.disableMysqlForeignKeyChecks(connection);

		InOrder inOrder = inOrder(statement);
		inOrder.verify(statement).execute("SET @@foreign_key_checks = 0");
		inOrder.verify(statement).close();
	}

	@Test
	public void shouldThrowStrykerExceptionWhenSQLExceptionOccursInDisableMysqlForeignKeyChecks() throws Exception {
		thrown.expect(StrykerException.class);
		thrown.expectMessage("connection does not exist");

		connection.close();

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.disableMysqlForeignKeyChecks(connection);
	}

	@Test
	public void shouldEnableMysqlForeignKeyChecks() throws Exception {
		Statement statement = mock(Statement.class);

		Connection connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(statement);

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.enableMysqlForeignKeyChecks(connection);

		InOrder inOrder = inOrder(statement);
		inOrder.verify(statement).execute("SET @@foreign_key_checks = 1");
		inOrder.verify(statement).close();
	}

	@Test
	public void shouldThrowStrykerExceptionWhenSQLExceptionOccursInEnableMysqlForeignKeyChecks() throws Exception {
		thrown.expect(StrykerException.class);
		thrown.expectMessage("connection does not exist");

		connection.close();

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.enableMysqlForeignKeyChecks(connection);
	}
}