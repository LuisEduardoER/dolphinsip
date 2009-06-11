package by.bsu.fami.etl.processing.component.writer;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import by.bsu.fami.etl.processing.component.BaseJdbcComponentTest;
import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class JdbcWriterTest extends BaseJdbcComponentTest {

	private final static Logger LOGGER = Logger.getLogger(JdbcWriterTest.class);

	protected JdbcWriter writer;

	@Before
	public void before() throws ComponentInitException {
		super.before();
		writer = new JdbcWriter();
		writer.setName("jdbcWriter");
		writer.setDataSource(ds);
		writer.setRootId("Test");
		writer.setId("Id");
		writer.setKeys("Id,Key");
		writer.init();
		LOGGER.info("Success init jdbc writer");
	}

	@Test
	public void simpleInsertDocument() throws SQLException {
		Document doc = new Document();
		doc.addField(new Field("Id", "Int", "0"));
		doc.addField(new Field("Key", "Int", "4"));
		doc.addField(new Field("Data", "String", "test4"));
		writer.execute(doc);
		writer.writeDocuments();

		ResultSet rs = ds.getConnection().createStatement().executeQuery(
				"SELECT data FROM Test WHERE Key = 4");
		rs.next();
		Assert.assertEquals("test4", rs.getString(1));
	}

	@Test
	public void simpleUpdateDocument() throws SQLException {
		Document doc = new Document();
		doc.setTransactiontype(Document.UPDATE_TRANSACTION_TYPE);
		doc.addField(new Field("Id", "Int", "1"));
		doc.addField(new Field("Key", "Int", "1"));
		doc.addField(new Field("Data", "String", "super test 1"));
		writer.execute(doc);
		writer.writeDocuments();

		ResultSet rs = ds.getConnection().createStatement().executeQuery(
				"SELECT data FROM Test WHERE Key = 1");
		rs.next();
		Assert.assertEquals("super test 1", rs.getString(1));
	}

	@Test
	public void simpleDeleteDocument() throws SQLException {
		Document doc = new Document();
		doc.setTransactiontype(Document.DELETE_TRANSACTION_TYPE);
		doc.addField(new Field("Id", "Int", "2"));
		doc.addField(new Field("Key", "Int", "2"));
		writer.execute(doc);
		writer.writeDocuments();

		ResultSet rs = ds.getConnection().createStatement().executeQuery(
				"SELECT COUNT(*) FROM Test WHERE Key = 2");
		rs.next();
		Assert.assertEquals(0, rs.getInt(1));
	}

	@After
	public void after() throws ComponentInitException {
		if (writer != null) {
			writer.complete();
		}
	}

}
