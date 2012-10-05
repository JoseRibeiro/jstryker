package org.jstryker.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.dbunit.operation.DatabaseOperation;
import org.jstryker.exception.JStrykerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

/**
 * Tests for {@link DBUnitHelper}.
 */
public class DBUnitHelperTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Connection connection;
	
	@Before
	public void setUp() throws Exception {
		Connection createDB = ConnectionHelper.getConnection();
		ScriptRunner scriptRunner = new ScriptRunner(createDB);
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/jstryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
			createDB.close();
		}
		connection = ConnectionHelper.getConnection();
	}

	@After
	public void tearDown() throws Exception {
		if (!connection.isClosed()) {
			connection.close();
		}
		Connection connection = ConnectionHelper.getConnection();
		ScriptRunner scriptRunner = new ScriptRunner(connection);
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/drop-jstryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
			connection.close();
		}
	}

	@Test
	public void shouldInsertDataSetContent() throws Exception {
		new DBUnitHelper().insert("/dbunit-dataset.xml");

		List<Object> rows = (List<Object>) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<Object> rows = new ArrayList<Object>();
				
				rs.next();
				rows.add(rs.getInt("ID"));

				rs.next();
				rows.add(rs.getInt("ID"));

				return rows;
			}
		});

		assertEquals("Should keep database data.", 1, rows.get(0));
		assertEquals("Should insert dataset content.", 2, rows.get(1));
	}

	@Test
	public void shouldInsertDataSetContentUsingConnection() throws Exception {
		new DBUnitHelper().insert("/dbunit-dataset.xml", connection);

		List<Object> rows = (List<Object>) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<Object> rows = new ArrayList<Object>();

				rs.next();
				rows.add(rs.getInt("ID"));

				rs.next();
				rows.add(rs.getInt("ID"));

				return rows;
			}
		});

		assertEquals("Should keep database data.", 1, rows.get(0));
		assertEquals("Should insert dataset content.", 2, rows.get(1));
	}

	@Test
	public void shouldResetDataBaseToDataSetContent() throws Exception {
		new DBUnitHelper().cleanInsert("/dbunit-dataset.xml", connection);

		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 2, id);
	}

	@Test
	public void shouldThrowJStrykerExceptionWhenSQLExceptionOccurs() throws Exception {
		String reason = "connection does not exist";
		thrown.expect(JStrykerException.class);
		thrown.expectMessage(reason);

		connection.close();
		new DBUnitHelper().cleanInsert("/dbunit-dataset.xml", connection);
	}

	@Test
	public void shouldThrowJStrkyerExceptionWhenIOExceptionOccurs() throws Exception {
		String reason = "java.net.MalformedURLException";
		thrown.expect(JStrykerException.class);
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
	public void shouldThrowJStrykerExceptionWhenDataSetExceptionOccursInDataSetGenerator() throws Exception {
		String path = "target/generatedDataSet.xml";
		String reason = "connection does not exist";
		thrown.expect(JStrykerException.class);
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

		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 2, id);
	}

	@Test
	public void shouldDeleteData() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());

		String resourcePath = "/dbunit-dataset.xml";
		dbUnitHelper.delete(resourcePath);

		verify(dbUnitHelper).delete(eq(resourcePath), any(Connection.class));
	}

	@Test
	public void shouldDeleteDataWithSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.insert("/dbunit-dataset.xml", connection);
		int datasetRowId = (Integer) new QueryRunner().query(connection, "Select * from jstryker where id=2", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});
		assertEquals("Should have dataset row.", 2, datasetRowId);

		dbUnitHelper.delete("/dbunit-dataset.xml", connection);

		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});
		assertEquals("Should have original row created by jstryker.sql.", 1, id);

		boolean hasNext = (Boolean) new QueryRunner().query(connection, "Select * from jstryker where id=2", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				return rs.next();
			}
		});
		assertFalse("Should delete dataset row.", hasNext);
	}

	@Test
	public void shouldDeleteAllData() throws Exception {
		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.cleanInsert("/dbunit-dataset.xml");
		dbUnitHelper.deleteAll("/dbunit-dataset.xml");

		int id = (Integer)  new QueryRunner().query(connection, "Select count(*) as total from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("total");
			}
		});
		assertEquals("Should not have data.", 0, id);
	}

	@Test
	public void shouldDeleteAllDataWithSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());

		String resourcePath = "/dbunit-dataset.xml";
		dbUnitHelper.deleteAll(resourcePath, connection);

		verify(dbUnitHelper).execute(eq(resourcePath), any(Connection.class), eq(DatabaseOperation.DELETE_ALL));
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
	public void shouldThrowJStrykerExceptionWhenSQLExceptionOccursInDisableMysqlForeignKeyChecks() throws Exception {
		thrown.expect(JStrykerException.class);
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
	public void shouldThrowJStrykerExceptionWhenSQLExceptionOccursInEnableMysqlForeignKeyChecks() throws Exception {
		thrown.expect(JStrykerException.class);
		thrown.expectMessage("connection does not exist");

		connection.close();

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.enableMysqlForeignKeyChecks(connection);
	}

	@Test
	public void shouldDisableHsqldbDatabaseReferentialIntegrity() throws Exception {
		Statement statement = mock(Statement.class);

		Connection connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(statement);

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.disableHsqldbDatabaseReferentialIntegrity(connection);

		InOrder inOrder = inOrder(statement);
		inOrder.verify(statement).execute("SET DATABASE REFERENTIAL INTEGRITY FALSE");
		inOrder.verify(statement).close();
	}

	@Test
	public void shouldThrowJStrykerExceptionWhenSQLExceptionOccursInDisableHsqldbDatabaseReferentialIntegrity()
			throws Exception {
		
		thrown.expect(JStrykerException.class);
		thrown.expectMessage("connection does not exist");

		connection.close();

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.disableHsqldbDatabaseReferentialIntegrity(connection);
	}

	@Test
	public void shouldEnableHsqldbDatabaseReferentialIntegrity() throws Exception {
		Statement statement = mock(Statement.class);

		Connection connection = mock(Connection.class);
		when(connection.createStatement()).thenReturn(statement);

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.enableHsqldbDatabaseReferentialIntegrity(connection);

		InOrder inOrder = inOrder(statement);
		inOrder.verify(statement).execute("SET DATABASE REFERENTIAL INTEGRITY TRUE");
		inOrder.verify(statement).close();
	}
	
	@Test
	public void shouldThrowJStrykerExceptionWhenSQLExceptionOccursInEnableHsqldbDatabaseReferentialIntegrity()
			throws Exception {

		thrown.expect(JStrykerException.class);
		thrown.expectMessage("connection does not exist");

		connection.close();

		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		dbUnitHelper.enableHsqldbDatabaseReferentialIntegrity(connection);
	}

	@Test
	public void shouldInsertEntityObjectWithTransientDataSource() throws Exception {
		JstrykerEntityWithTransient jstrykerEntity = new JstrykerEntityWithTransient(3, "Tools to help automated tests in java", "Dummy");
		
		new DBUnitHelper().insert(jstrykerEntity);
		
		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker where id = 3", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 3, id);
	}
	
	@Test
	public void shouldSaveEntityObjectWithTransientDataSourceToDataSetContent() throws Exception {
		JstrykerEntityWithTransient jstrykerEntity = new JstrykerEntityWithTransient(3, "Tools to help automated tests in java", "Dummy");
		
		new DBUnitHelper().cleanInsert(jstrykerEntity);

		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 3, id);
	}

	@Test
	public void shouldInsertEntityObjectWithTransientDataSourceWithConnectionParameter() throws Exception {
		JstrykerEntityWithTransient jstrykerEntity = new JstrykerEntityWithTransient(3, "Tools to help automated tests in java", "Dummy");
		
		new DBUnitHelper().insert(jstrykerEntity, connection);

		connection = ConnectionHelper.getConnection();
		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker where id = 3", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 3, id);
	}
	
	@Test
	public void shouldSaveEntityObjectWithTransientDataSourceToDataSetContentWithConnectionParameter() throws Exception {
		JstrykerEntityWithTransient jstrykerEntity = new JstrykerEntityWithTransient(3, "Tools to help automated tests in java", "Dummy");
		
		new DBUnitHelper().cleanInsert(jstrykerEntity, connection);

		connection = ConnectionHelper.getConnection();
		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 3, id);
	}

	@Test
	public void shouldSaveEntityObjectWithoutColumnDataSourceToDataSetContent() throws Exception {
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		
		new DBUnitHelper().cleanInsert(jstrykerEntity);

		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 3, id);
	}

	@Test
	public void shouldSaveEntityObjectWithoutColumnDataSourceToDataSetContentWithConnectionParameter() throws Exception {
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		
		new DBUnitHelper().cleanInsert(jstrykerEntity, connection);

		connection = ConnectionHelper.getConnection();
		int id = (Integer) new QueryRunner().query(connection, "Select * from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		assertEquals("Should return the dataset ID.", 3, id);
	}

	@Test
	public void shouldResetDataBaseWithEntityObjectToDataSetContentWithTruncateTable() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		
		dbUnitHelper.truncateAndInsert(jstrykerEntity);
		
		InOrder inOrder = inOrder(dbUnitHelper);
		inOrder.verify(dbUnitHelper).executeOperation(anyString(), any(Connection.class), any(ByteArrayInputStream.class), eq(DatabaseOperation.TRUNCATE_TABLE));
		inOrder.verify(dbUnitHelper).executeOperation(anyString(), any(Connection.class), any(ByteArrayInputStream.class), eq(DatabaseOperation.INSERT));
	}

	@Test
	public void shouldResetDataBaseWithEntityObjectToDataSetContentWithTruncateTableUsingSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		
		dbUnitHelper.truncateAndInsert(connection, Arrays.asList(jstrykerEntity));
		
		InOrder inOrder = inOrder(dbUnitHelper);
		inOrder.verify(dbUnitHelper).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.TRUNCATE_TABLE));
		inOrder.verify(dbUnitHelper).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.INSERT));
	}

	@Test
	public void shouldResetDataBaseWithListByEntityObjectToDataSetContentWithTruncateTableUsingSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		List<JstrykerEntityWithoutColumn> list = new ArrayList<JstrykerEntityWithoutColumn>();
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		list.add(jstrykerEntity);
		jstrykerEntity = new JstrykerEntityWithoutColumn(4, "Tools to help automated tests in java 2");
		list.add(jstrykerEntity);
		
		dbUnitHelper.truncateAndInsert(connection, list);
		
		InOrder inOrder = inOrder(dbUnitHelper);
		inOrder.verify(dbUnitHelper, times(2)).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.TRUNCATE_TABLE));
		inOrder.verify(dbUnitHelper, times(1)).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.INSERT));
	}

	@Test
	public void shouldTruncateDataWithEntityObject() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");

		dbUnitHelper.truncate(jstrykerEntity);

		verify(dbUnitHelper).executeOperation(anyString(), any(Connection.class), any(ByteArrayInputStream.class), eq(DatabaseOperation.TRUNCATE_TABLE));
	}

	@Test
	public void shouldTruncateDataWithEntityObjectUsingSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");

		dbUnitHelper.truncate(jstrykerEntity, connection);

		verify(dbUnitHelper).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.TRUNCATE_TABLE));
	}

	@Test
	public void shouldDeleteDataWithEntityObject() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());

		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		
		dbUnitHelper.delete(jstrykerEntity);

		verify(dbUnitHelper).executeOperation(anyString(), any(Connection.class), any(ByteArrayInputStream.class), eq(DatabaseOperation.DELETE));
	}

	@Test
	public void shouldDeleteDataWithEntityObjectUsingSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());

		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(3, "Tools to help automated tests in java");
		
		dbUnitHelper.delete(jstrykerEntity, connection);

		verify(dbUnitHelper).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.DELETE));
	}

	@Test
	public void shouldDeleteAllDataWithEntityObject() throws Exception {
		DBUnitHelper dbUnitHelper = new DBUnitHelper();
		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(4, "Tools to help automated tests in java");

		dbUnitHelper.insert(jstrykerEntity);
		dbUnitHelper.deleteAll(Arrays.asList(jstrykerEntity));

		int id = (Integer)  new QueryRunner().query(connection, "Select count(*) as total from jstryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("total");
			}
		});
		assertEquals("Should not have data.", 0, id);
	}

	@Test
	public void shouldDeleteAllDataWithEntityObjectWithSpecifiedConnection() throws Exception {
		DBUnitHelper dbUnitHelper = spy(new DBUnitHelper());

		JstrykerEntityWithoutColumn jstrykerEntity = new JstrykerEntityWithoutColumn(4, "Tools to help automated tests in java");
		
		dbUnitHelper.deleteAll(connection, Arrays.asList(jstrykerEntity));

		verify(dbUnitHelper).executeOperation(anyString(), eq(connection), any(ByteArrayInputStream.class), eq(DatabaseOperation.DELETE_ALL));
	}

	@Test
	public void shouldThrowJStrykerExceptionWhenParameterIsNotEntityObject() throws Exception {
		Object object = new Object();
		String reason = "Object("+object+") isn't Entity";
		thrown.expect(JStrykerException.class);
		thrown.expectMessage(reason);

		new DBUnitHelper().cleanInsert(object, connection);
	}

}

@Entity
@Table(name = "jstryker")
class JstrykerEntityWithTransient {
	
	public JstrykerEntityWithTransient(Integer pk, String msg, String dummy) {
		this.pk = pk;
		this.msg = msg;
		this.dummy = dummy;
	}
	
	@Id
	@Column(name = "id")
	private Integer pk;
	
	@Column(name = "description")
	private String msg;
	
	@Transient
	private String dummy;

	public Integer getPk() {
		return pk;
	}

	public String getMsg() {
		return msg;
	}

	public String getDummy() {
		return dummy;
	}
}

@Entity
@Table(name = "jstryker")
class JstrykerEntityWithoutColumn {
	
	public JstrykerEntityWithoutColumn(Integer id, String description) {
		this.id = id;
		this.description = description;
	}
	
	@Id
	private Integer id;
	
	private String description;
	
	public Integer getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
}