package to.kit.data.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import to.kit.util.Props;

/**
 * DBConnection.
 * @author H.Sasai
 */
public final class DBConnection {
	private static final DBConnection ME = new DBConnection();

	/** database properties. */
	private Props props = new Props(DBConnection.class.getSimpleName());
	/** Connection map. */
	private Map<String, Connection> connectionMap = new HashMap<>();

	private DBConnection() {
		// nop
	}

	public static DBConnection getInstance() {
		return ME;
	}

	@SuppressWarnings("resource")
	public Connection getConnection(String key) {
		Connection conn = null;

		if (!this.connectionMap.containsKey(key)) {
			String url = this.props.get(key + ".url");
			String user = this.props.get(key + ".user");
			String pass = this.props.get(key + ".pass");
			try {
				conn = DriverManager.getConnection(url, user, pass);
				this.connectionMap.put(key, conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			conn = this.connectionMap.get(key);
		}
		return conn;
	}

	public void close() {
		for (Map.Entry<String, Connection> entry : this.connectionMap.entrySet()) {
			Connection conn = entry.getValue();
			try {
				conn.close();
			} catch (SQLException e) {
				// 無視
			}
		}
	}
}
