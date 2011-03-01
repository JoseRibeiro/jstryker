package org.jstryker.database;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.jstryker.exception.StrykerException;

/**
 * Teste para {@link ScriptRunner}.
 */
public class ScriptRunnerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@After
	public void after() throws Exception {
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
	
	@Test
	public void shouldRunScript() throws Exception {
		Connection connection = ConnectionHelper.getConnection();

		ScriptRunner scriptRunner = new ScriptRunner(connection);
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/stryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
		}

		Integer id = (Integer) new QueryRunner().query(connection, "Select * from stryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		connection.close();
		assertEquals("Deve pegar o id.", new String("1"), id.toString());
	}

	@Test
	public void cannotRunScriptWithNullStrem() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Stream cannot be null.");
		new ScriptRunner(mock(Connection.class)).runScript(null);
	}

	@Test
	public void shouldThrowStrykerExceptionWhenOccurSqlException() throws Exception {
		Connection connection = mock(Connection.class);
		String message = "Stream cannot be null.";
		thrown.expectMessage(message);
		when(connection.createStatement()).thenThrow(new SQLException(message));

		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/stryker.sql");

		thrown.expect(StrykerException.class);
		new ScriptRunner(connection).runScript(sql);
	}

	@Test
	public void shouldRunScriptWithCustomDelimiter() throws Exception {
		Connection connection = ConnectionHelper.getConnection();

		ScriptRunner scriptRunner = new ScriptRunner(connection, "$$");
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/stryker-other-delimiter.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
		}

		Integer id = (Integer) new QueryRunner().query(connection, "Select * from stryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		connection.close();
		assertEquals("Deve pegar o id.", new String("1"), id.toString());
	}

	@Test
	public void shouldRunScriptWithCustomCharSet() throws Exception {
		Connection connection = ConnectionHelper.getConnection();
		ScriptRunner scriptRunner = new ScriptRunner(connection, Charset.forName("utf-8"));
		InputStream sql = ScriptRunnerTest.class.getResourceAsStream("/stryker.sql");

		try {
			scriptRunner.runScript(sql);
		} finally {
			sql.close();
		}

		Integer id = (Integer) new QueryRunner().query(connection, "Select * from stryker", new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("ID");
			}
		});

		connection.close();
		assertEquals("Deve pegar o id.", new String("1"), id.toString());
	}
}