package by.bsu.fami.etl.processing.component.reader;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import by.bsu.fami.etl.processing.component.BaseJdbcComponentTest;
import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.component.reader.JdbcReader;
import by.bsu.fami.etl.processing.document.Document;

public class JdbcReaderTest extends BaseJdbcComponentTest {

	private final static Logger LOGGER = Logger.getLogger(JdbcReaderTest.class);

	protected JdbcReader reader;

	@Before
	public void before() throws ComponentInitException {
		super.before();
		reader = new JdbcReader();
		reader.setName("jdbcReader");
		reader.setDataSource(ds);
		reader.setMap("SELECT * FROM TEST;");
		reader.init();
		LOGGER.info("Success init jdbc reader");
	}

	@Test
	public void simple() {
		while (reader.hasDocument()) {
			Document doc = new Document();
			reader.readDocument(doc);
			LOGGER.info(doc);
		}
	}

	@After
	public void after() throws ComponentInitException {
		if (reader != null) {
			reader.complete();
		}
	}

}
