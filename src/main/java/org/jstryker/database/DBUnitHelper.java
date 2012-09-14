package org.jstryker.database;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.activation.DataSource;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.jstryker.exception.JStrykerException;

/**
 * Tool for DBUnit.
 */
public class DBUnitHelper {

	/**
	 * Reset the database to dataset content performing a {@link TransactionOperation#CLEAN_INSERT} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public void cleanInsert(String resourcePath, Connection connection) {
		execute(resourcePath, connection, TransactionOperation.CLEAN_INSERT);
	}

	/**
	 * Reset the database to dataset content performing a {@link TransactionOperation#CLEAN_INSERT} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 */
	public void cleanInsert(String resourcePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			new DBUnitHelper().cleanInsert(resourcePath, connection);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	public void cleanInsert(Object object, Connection connection) {
		insertDatabaseOperation(object, connection, TransactionOperation.CLEAN_INSERT);
	}
	
	public void cleanInsert(Object object) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			insertDatabaseOperation(object, connection, TransactionOperation.CLEAN_INSERT);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	private void insertDatabaseOperation(Object object, Connection connection, DatabaseOperation databaseOperation) {
		try {
			if (!object.getClass().isAnnotationPresent(Table.class)) {
				throw new JStrykerException("Object("+object+") isn't Entity");
			}
			
			Table table = object.getClass().getAnnotation(Table.class);

			StringBuilder builder = new StringBuilder("<?xml version=\"1.0\"?>\n<dataset>\n<");
			builder.append(table.name()).append(" ");
			
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				
				Object value = field.get(object);
				
				if (value == null) {
					continue;
				}
				
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				
				Column annotation = field.getAnnotation(Column.class);
				if (annotation != null && !"".equals(annotation.name())) {
					builder.append(annotation.name());
				} else {
					builder.append(field.getName());
				}
				
				builder.append("=\"");
				builder.append(value);
				builder.append("\" ");
			}
			
			builder.append("/>\n</dataset>");
			
			String string = builder.toString();
			execute(null, connection, new ByteArrayInputStream(string.getBytes()), databaseOperation);
		} catch (IllegalAccessException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}

	/**
	 * Reset the database to dataset content performing a {@link TransactionOperation#TRUNCATE_TABLE} and a
	 * {@link TransactionOperation#INSERT} from DBUnit.
	 * @param object Entity Object.
	 */
	public void truncateAndInsert(Object object) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			insertDatabaseOperation(object, connection, TransactionOperation.TRUNCATE_TABLE);
			insertDatabaseOperation(object, connection, TransactionOperation.INSERT);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Reset the database to dataset content performing a {@link TransactionOperation#TRUNCATE_TABLE} and a
	 * {@link TransactionOperation#INSERT} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 */
	public void truncateAndInsert(String resourcePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			execute(resourcePath, connection, TransactionOperation.TRUNCATE_TABLE);
			execute(resourcePath, connection, TransactionOperation.INSERT);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Reset the database to dataset content performing a {@link TransactionOperation#TRUNCATE_TABLE} and a
	 * {@link TransactionOperation#INSERT} from DBUnit.
	 * @param connection {@link Connection}.
	 * @param objects Entity Object.
	 */
	public void truncateAndInsert(Connection connection, Collection<?> coll) {
		for (Object object : coll) {
			insertDatabaseOperation(object, connection, TransactionOperation.TRUNCATE_TABLE);
			insertDatabaseOperation(object, connection, TransactionOperation.INSERT);
		}
	}
	
	/**
	 * Reset the database to dataset content performing a {@link TransactionOperation#TRUNCATE_TABLE} and a
	 * {@link TransactionOperation#INSERT} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public void truncateAndInsert(String resourcePath, Connection connection) {
		execute(resourcePath, connection, TransactionOperation.TRUNCATE_TABLE);
		execute(resourcePath, connection, TransactionOperation.INSERT);
	}

	/**
	 * Insert dataset content into database performing a {@link TransactionOperation#INSERT} from DBUnit.
	 * @param object Entity Object.
	 */
	public void insert(Object object) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			insertDatabaseOperation(object, connection, DatabaseOperation.INSERT);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Insert dataset content into database performing a {@link TransactionOperation#INSERT} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 */
	public void insert(String resourcePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			execute(resourcePath, connection, DatabaseOperation.INSERT);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Insert dataset content into database performing a {@link TransactionOperation#INSERT} from DBUnit.
	 * @param object Entity Object.
	 * @param connection {@link Connection}.
	 */
	public void insert(Object object, Connection connection) {
		insertDatabaseOperation(object, connection, DatabaseOperation.INSERT);
	}
	
	/**
	 * Insert dataset content into database performing a {@link TransactionOperation#INSERT} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public void insert(String resourcePath, Connection connection) {
		execute(resourcePath, connection, DatabaseOperation.INSERT);
	}

	/**
	 * Delete dataset specified rows from database with a {@link TransactionOperation#DELETE} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 */
	public void delete(String resourcePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			delete(resourcePath, connection);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Delete dataset specified rows from database with a {@link TransactionOperation#DELETE} from DBUnit.
	 * @param object Entity Object.
	 */
	public void delete(Object object) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			insertDatabaseOperation(object, connection, DatabaseOperation.DELETE);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Delete dataset specified rows from database with a {@link TransactionOperation#DELETE} from DBUnit.
	 * @param object Entity Object
	 * @param connection {@link Connection}.
	 */
	public void delete(Object object, Connection connection) {
		insertDatabaseOperation(object, connection, DatabaseOperation.DELETE);
	}

	/**
	 * Delete dataset specified rows from database with a {@link TransactionOperation#DELETE} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public void delete(String resourcePath, Connection connection) {
		execute(resourcePath, connection, DatabaseOperation.DELETE);
	}

	/**
	 * Clean the database with a {@link TransactionOperation#DELETE_ALL} from DBUnit.
	 * @param coll List<Entity Object>.
	 */
	public void deleteAll(Collection<?> coll) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			for (Object object : coll) {
				insertDatabaseOperation(object, connection, DatabaseOperation.DELETE_ALL);
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Clean the database with a {@link TransactionOperation#DELETE_ALL} from DBUnit.
	 * @param connection {@link Connection}.
	 * @param coll List<Entity Object>.
	 */
	public void deleteAll(Connection connection, Collection<?> coll) {
		for (Object object : coll) {
			insertDatabaseOperation(object, connection, DatabaseOperation.DELETE_ALL);
		}
	}
	
	/**
	 * Clean the database with a {@link TransactionOperation#DELETE_ALL} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 */
	public void deleteAll(String resourcePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			deleteAll(resourcePath, connection);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Clean the database with a {@link TransactionOperation#DELETE_ALL} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public void deleteAll(String resourcePath, Connection connection) {
		execute(resourcePath, connection, DatabaseOperation.DELETE_ALL);
	}

	/**
	 * Clean the database with a {@link TransactionOperation#TRUNCATE_TABLE} from DBUnit.
	 * @param object Entity Object.
	 */
	public void truncate(Object object) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			insertDatabaseOperation(object, connection, DatabaseOperation.TRUNCATE_TABLE);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Clean the database with a {@link TransactionOperation#TRUNCATE_TABLE} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 */
	public void truncate(String resourcePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			truncate(resourcePath, connection);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Clean the database with a {@link TransactionOperation#TRUNCATE_TABLE} from DBUnit.
	 * @param object Entity Object
	 * @param connection {@link Connection}.
	 */
	public void truncate(Object object, Connection connection) {
		insertDatabaseOperation(object, connection, DatabaseOperation.TRUNCATE_TABLE);
	}
	
	/**
	 * Clean the database with a {@link TransactionOperation#TRUNCATE_TABLE} from DBUnit.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public void truncate(String resourcePath, Connection connection) {
		execute(resourcePath, connection, DatabaseOperation.TRUNCATE_TABLE);
	}

	/**
	 * Generate a DBUnit dataSet file from {@link DataSource}.
	 * @param path Place where dataset will be created. If path does not exist, it will be created.
	 * @param connection {@link Connection} to {@link DataSource}.
	 */
	// TODO: TEST NOT COVERED EXCEPTIONS.
	public static void generateDataSet(String path, Connection connection) {
		try {
			IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
			IDataSet dataSet = dbUnitConnection.createDataSet();
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FlatXmlDataSet.write(dataSet, new FileOutputStream(file));
		} catch (SQLException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (DataSetException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (IOException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (DatabaseUnitException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}

	void execute(String resourcePath, Connection connection, InputStream inputStream, DatabaseOperation... operations) {
		executeOperation(resourcePath, connection, inputStream, operations);
	}
	
	void execute(String resourcePath, Connection connection, DatabaseOperation... operations) {
		executeOperation(resourcePath, connection, null, operations);
	}
	
	/**
	 * Execute dbunit operations in datasource.
	 * @param resourcePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 * @param operations {@link DatabaseOperation} to be executed.
	 */
	void executeOperation(String resourcePath, Connection connection, InputStream inputStream, DatabaseOperation... operations) {
		try {
			InputStream resourceAsStream = (inputStream != null ? inputStream : DBUnitHelper.class.getResourceAsStream(resourcePath));

			FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
			builder.setCaseSensitiveTableNames(true);
			IDataSet dataSet = builder.build(resourceAsStream);

			ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
			replacementDataSet.addReplacementObject("[null]", null);
			IDatabaseConnection iConnection = new DatabaseConnection(connection);

			for(DatabaseOperation operation : operations) {
				operation.execute(iConnection, replacementDataSet);
			}
			resourceAsStream.close();
		} catch (DatabaseUnitException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (IOException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}

	/**
	 * Disable MySQL foreign key checks on this connection.<br>
	 * For certain cases, you must disable foreign key checks before truncate or delete all table data. Remember that
	 * you must use this connection to perform the delete all or the truncate.
	 * @param connection {@link Connection}.
	 * @throws JStrykerException If any error occurs during disable.
	 * @see #enableMysqlForeignKeyChecks(java.sql.Connection)
	 * @see #cleanInsert(String, java.sql.Connection)
	 * @see #truncate(String, java.sql.Connection)
	 * @see #truncateAndInsert(String, java.sql.Connection)
	 */
	public void disableMysqlForeignKeyChecks(Connection connection) throws JStrykerException {
		setMysqlForeignKeyChecks(connection, 0);
	}

	/**
	 * Enable MySQL foreign key checks on this connection.<br>
	 * @param connection {@link Connection}.
	 * @throws JStrykerException If any error occurs during enable.
	 * @see #disableMysqlForeignKeyChecks(java.sql.Connection)
	 */
	public void enableMysqlForeignKeyChecks(Connection connection) throws JStrykerException {
		setMysqlForeignKeyChecks(connection, 1);
	}

	private void setMysqlForeignKeyChecks(Connection connection, int value) throws JStrykerException {
		try {
			Statement statement = connection.createStatement();
			statement.execute("SET @@foreign_key_checks = " + value);
			statement.close();
		} catch (SQLException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}

	/**
	 * Disable HSQLDB database referential integrity on this connection.<br>
	 * For certain cases, you must disable foreign key checks before truncate or delete all table data. Remember that
	 * you must use this connection to perform the delete all or the truncate.
	 * @param connection {@link Connection}.
	 * @throws JStrykerException If any error occurs during disable.
	 * @see #enableHsqldbDatabaseReferentialIntegrity(java.sql.Connection)
	 * @see #cleanInsert(String, java.sql.Connection)
	 * @see #truncate(String, java.sql.Connection)
	 * @see #truncateAndInsert(String, java.sql.Connection)
	 */
	public void disableHsqldbDatabaseReferentialIntegrity(Connection connection) throws JStrykerException {
		setHsqldbDatabaseReferentialIntegrity(connection, "FALSE");
	}

	/**
	 * Enable HSQLDB database referential integrity on this connection.<br>
	 * @param connection {@link Connection}.
	 * @throws JStrykerException If any error occurs during enable.
	 * @see #disableHsqldbDatabaseReferentialIntegrity(java.sql.Connection)
	 */
	public void enableHsqldbDatabaseReferentialIntegrity(Connection connection) throws JStrykerException {
		setHsqldbDatabaseReferentialIntegrity(connection, "TRUE");
	}

	private void setHsqldbDatabaseReferentialIntegrity(Connection connection, String value) {
		try {
			Statement statement = connection.createStatement();
			statement.execute("SET DATABASE REFERENTIAL INTEGRITY " + value);
			statement.close();
		} catch (SQLException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}
}