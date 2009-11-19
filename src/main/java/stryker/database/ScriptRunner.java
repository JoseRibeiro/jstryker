package stryker.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
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

	/**
	 * Default statement delimiter is ';'.
	 */
	public static final String DEFAULT_DELIMITER = ";";

	private Connection connection;
	private String delimiter;
	private Charset charset;

	/**
	 * Create new {@link ScriptRunner} with {@link Connection}, statement delimiter and {@link Charset}.
	 * @param connection {@link Connection} to run script.
	 * @param delimiter Statement delimiter eg: ';' or '$$'.
	 * @param charset {@link Charset} to read stream.
	 */
	public ScriptRunner(Connection connection, String delimiter, Charset charset) {
		this.connection = connection;
		this.delimiter = delimiter;
		this.charset = charset;
	}

	/**
	 * Create new {@link ScriptRunner} with {@link Connection}, default statement delimiter and default {@link Charset}.
	 * @see {@link ScriptRunner#DEFAULT_DELIMITER}
	 * @param connection {@link Connection} to run script.
	 */
	public ScriptRunner(Connection connection) {
		this(connection, DEFAULT_DELIMITER, Charset.defaultCharset());
	}

	/**
	 * Create new {@link ScriptRunner} with {@link Connection}, default statement delimiter and {@link Charset}.
	 * @param connection {@link Connection} to run script.
	 * @param charset {@link Charset} to read stream.
	 */
	public ScriptRunner(Connection connection, Charset charset) {
		this(connection, DEFAULT_DELIMITER, charset);
	}

	/**
	 * Create new {@link ScriptRunner} with {@link Connection}, default {@link Charset} and statement delimiter.
	 * @param connection {@link Connection} to run script.
	 * @param delimiter Statement delimiter eg: ';' or '$$'.
	 */
	public ScriptRunner(Connection connection, String delimiter) {
		this(connection, delimiter, Charset.defaultCharset());
	}

	/**
	 * Run an SQL script.
	 * @param stream The source of the script.
	 * @throws StrykerException When cannot execute script.
	 * @throws IllegalArgumentException If stream is null.
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
	 * @throws IOException When cannot read stream.
	 */
	private List<String> parse(InputStream stream) throws IOException {
		InputStreamReader reader = new InputStreamReader(stream, charset);
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