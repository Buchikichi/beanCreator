package to.kit.data.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import to.kit.data.schema.AttrInfo;
import to.kit.data.schema.EntityInfo;
import to.kit.data.schema.Schema;
import to.kit.util.NameUtils;
import to.kit.util.Props;

/**
 * Schema loader.
 * @author H.Sasai
 */
public final class SchemaLoader {
	/** Logger. */
	private static final Logger LOGGER = Logger.getLogger(SchemaLoader.class);
	/** database properties. */
	private Props props = new Props(SchemaLoader.class.getSimpleName());
	/** database connection. */
	private DBConnection db = DBConnection.getInstance();

	private Set<String> getExcludeSet(String key) {
		Set<String> set = new HashSet<>();
		String[] excludes = this.props.get(key + ".table.excludes").split("[\\s,]");

		for (String table : excludes) {
			set.add(table);
		}
		return set;
	}

	private List<EntityInfo> getTableList(Connection conn, String key)
			throws SQLException {
		List<EntityInfo> result = new ArrayList<>();
		String query = this.props.get(key + ".table");
		Set<String> excludes = getExcludeSet(key);

		try (Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");

					if (excludes.contains(tableName)) {
						continue;
					}
					EntityInfo entity = new EntityInfo();
					String comment = rs.getString("TABLE_COMMENT");
					entity.setName(tableName);
					entity.setComment(StringUtils.defaultString(comment));
					result.add(entity);
				}
			}
		}
		return result;
	}

	private void loadAttr(Connection conn, String key, EntityInfo entity)
			throws SQLException {
		String query = this.props.get(key + ".column");
		Set<String> nameSet = new HashSet<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			String tableName = entity.getName();

			LOGGER.info(tableName);
			stmt.setString(1, tableName);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String name = rs.getString("COLUMN_NAME");
					String camel = NameUtils.toCamel(name);

					if (nameSet.contains(camel)) {
						continue;
					}
					String precision = rs.getString("DATA_PRECISION");
					AttrInfo attr = new AttrInfo();
					attr.setName(name);
					attr.setType(rs.getString("DATA_TYPE"));
					if (StringUtils.isBlank(precision)) {
						attr.setSize(rs.getString("DATA_LENGTH"));
					} else {
						String scale = rs.getString("DATA_SCALE");
						if (StringUtils.isNotBlank(scale)) {
							attr.setSize(precision + "." + scale);
						} else {
							attr.setSize(precision);
						}
					}
					String nullable = StringUtils.defaultString(rs.getString("NULLABLE"));
					attr.setNullable(nullable.startsWith("Y")); // Y or YES
					attr.setPk(StringUtils.isNotBlank(rs.getString("PK")));
					attr.setComment(rs.getString("COL_COMMENT"));
					entity.addAttr(attr);
					nameSet.add(camel);
				}
			}
		}
	}

	/**
	 * Load schema.
	 * @return schema list
	 * @throws SQLException
	 */
	public List<Schema> loadSchema() throws SQLException {
		List<Schema> schemaList = new ArrayList<>();
		String[] keyList = this.props.get("schema").split("[\\s,]");

		for (String key : keyList) {
			Schema schema = new Schema();
			String namespace = this.props.get(key + ".namespace");

			schema.setPath(this.props.get(key + ".dir"));
			schema.setNameSpace(namespace);
			schema.setInterfaces(this.props.get(key + ".implements"));
			try (Connection conn = this.db.getConnection(key)) {
				for (EntityInfo entity : getTableList(conn, key)) {
					schema.addEntity(entity);
					loadAttr(conn, key, entity);
				}
			}
			schemaList.add(schema);
		}
		return schemaList;
	}
}
