package stryker.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import stryker.exception.StrykerException;
import stryker.test.ConnectionHelper;

/**
 * Tests for {@link DBUnitHelper}.
 */
public class DBUnitHelperTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Connection createDB = ConnectionHelper.getConnection("dbunit");
		ScriptRunner scriptRunner = new ScriptRunner(createDB);
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/stryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
			createDB.close();
		}
	}

	@Before
	public void setUp() throws Exception {
		connection = ConnectionHelper.getConnection("dbunit");
	}

	@After
	public void tearDown() throws Exception {
		if (!connection.isClosed()) {
			connection.close();
		}
	}

	@Test
	public void cannotBeInstantiate() throws Exception {
		thrown.expect(IllegalAccessException.class);
		DBUnitHelper.class.newInstance();
	}

	@Test
	public void shouldResetDataBaseToDataSetContent() throws Exception {
		DBUnitHelper.init("/dbunit-dataset.xml", connection);

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
		String reason = "Connection is closed";
		thrown.expect(StrykerException.class);
		thrown.expectMessage(reason);

		connection.close();
		DBUnitHelper.init("/dbunit-dataset.xml", connection);
	}

	@Test
	public void shouldThrowStrkyerExceptionWhenIOExceptionOccurs() throws Exception {
		String reason = "java.net.MalformedURLException";
		thrown.expect(StrykerException.class);
		thrown.expectMessage(reason);

		DBUnitHelper.init("/unexistent-dataset.xml", connection);
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
		String reason = "Connection is closed";
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
}