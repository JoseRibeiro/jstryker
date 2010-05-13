package stryker.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.activation.DataSource;

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

import stryker.exception.StrykerException;

/**
 * Tool for DBUnit.
 */
public final class DBUnitHelper {

	/**
	 * Cannot be instantiate.
	 */
	private DBUnitHelper() {
	}

	/**
	 * Reset the database to dataset content.
	 * @param resoucePath Path for dbunit dataset.
	 * @param connection {@link Connection}.
	 */
	public static void init(String resoucePath, Connection connection) {
		execute(resoucePath, connection, TransactionOperation.CLEAN_INSERT);
	}
	
	/**
	 * Reset the database to dataset content.
	 * @param resoucePath Path for dbunit dataset.
	 */
	public static void init(String resoucePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			init(resoucePath, connection);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new StrykerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Clean the database.
	 * @param resoucePath Path for dbunit dataset.
	 */
	public static void clean(String resoucePath) {
		Connection connection = ConnectionHelper.getConnection();
		try {
			execute(resoucePath, connection, TransactionOperation.DELETE_ALL);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new StrykerException(e.getMessage(), e);
			}
		}
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
			throw new StrykerException(e.getMessage(), e);
		} catch (DataSetException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (IOException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (DatabaseUnitException e) {
			throw new StrykerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Execute dbunit operations in datasource.
	 * @param resoucePath Path for dbunit dataset.
	 * @param connection connection {@link Connection}.
	 * @param operations {@link DatabaseOperation} to be executed.
	 */
	private static void execute(String resoucePath, Connection connection, DatabaseOperation... operations) {
		try {
			InputStream resourceAsStream = DBUnitHelper.class.getResourceAsStream(resoucePath);
			
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
			throw new StrykerException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (IOException e) {
			throw new StrykerException(e.getMessage(), e);
		}
	}
}