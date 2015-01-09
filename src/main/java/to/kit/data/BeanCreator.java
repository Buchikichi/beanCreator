package to.kit.data;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import to.kit.data.schema.Schema;
import to.kit.data.util.DataBeanCreator;
import to.kit.data.util.SchemaLoader;

/**
 * BeanCreator.
 * @author H.Sasai
 */
public final class BeanCreator {
	/** Logger. */
	private static final Logger LOGGER = Logger.getLogger(BeanCreator.class);

	public void execute() throws SQLException, IOException {
		SchemaLoader loader = new SchemaLoader();
		DataBeanCreator creator = new DataBeanCreator();

		for (Schema schema : loader.loadSchema()) {
			creator.create(schema);
		}
		LOGGER.info("done.");
	}

	public static void main(String[] args) throws Exception {
		BeanCreator app = new BeanCreator();

		app.execute();
	}
}
