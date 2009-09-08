package stryker.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import stryker.exception.StrykerException;

/**
 * Tool to run database scripts.
 */
public class ScriptRunner {

	public static final String DEFAULT_DELIMITER = ";";
	private Connection connection;
	private String delimiter;

	/**
	 * @param connection
	 * @param delimiter
	 */
	public ScriptRunner(Connection connection, String delimiter) {
		this.connection = connection;
		this.delimiter = delimiter;
	}

	/**
	 * @param connection
	 */
	public ScriptRunner(Connection connection) {
		this(connection, DEFAULT_DELIMITER);
	}

	/**
	 * Runs an SQL script.
	 * @param stream The source of the script.
	 * @throws StrykerException When cannot execute script.
	 * @throws IllegalArgumentException
	 */
	public void runScript(InputStream stream) throws StrykerException, IllegalArgumentException {

		if (stream == null) {
			throw new IllegalArgumentException("Stream cannot be null.");
		}

		try {
			List<String> commands = parse(stream);

			for (String command : commands) {
				Statement statement = null;
				try {
					statement = connection.createStatement();
					statement.execute(command.toString());
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}

		} catch (SQLException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (IOException e) {
			throw new StrykerException(e.getMessage(), e);
		} 
	}

	/**
	 * @param stream  The source of the script.
	 * @return List of commands to be executed.
	 * @throws IOException 
	 */
	private List<String> parse(InputStream stream) throws IOException {
		InputStreamReader reader = new InputStreamReader(stream);
		LineNumberReader lineReader = new LineNumberReader(reader);
		List<String> commands = new ArrayList<String>();

		String line = null;
		StringBuilder command = new StringBuilder();

		while ((line = lineReader.readLine()) != null) {
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith("--")) {
				continue;
			} else if (trimmedLine.endsWith(delimiter)) {
				command.append(line.substring(0, line.lastIndexOf(delimiter)));
				commands.add(command.toString());
				command = new StringBuilder();
			} else {
				command.append(line);
				command.append(" ");
			}
		}

		return commands;
	}
}