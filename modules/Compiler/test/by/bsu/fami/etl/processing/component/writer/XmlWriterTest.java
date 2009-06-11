package by.bsu.fami.etl.processing.component.writer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.datasource.LocalFileDataSource;
import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class XmlWriterTest {

	private final static Logger LOGGER = Logger.getLogger(XmlWriterTest.class);

	protected XmlWriter writer;

	protected LocalFileDataSource ds;

	@Before
	public void before() throws ComponentInitException {
		BasicConfigurator.configure();
		ds = new LocalFileDataSource();
		ds.addProperty("FileName", "test/output.xml");
		writer = new XmlWriter();
		writer.setName("xmlWriter");
		writer.setDataSource(ds);
		writer.setRootId("root");
		writer.setId("Id");
		writer.setKeys("Id,Key");
		writer.addProperty(DataWriter.PROPERTY_BATCH_SIZE, "3");
		writer.init();
		LOGGER.info("Success init xml writer");
	}

	@Test
	public void simple() {
		Document doc = new Document();
		doc.addField(new Field("Id", "Int", "1"));
		doc.addField(new Field("Key", "Int", "2"));
		writer.execute(doc);
		doc = new Document();
		doc.addField(new Field("Id", "Int", "2"));
		doc.addField(new Field("Key", "Int", "2"));
		writer.execute(doc);
		doc = new Document();
		doc.addField(new Field("Id", "Int", "3"));
		doc.addField(new Field("Key", "Int", "3"));
		writer.execute(doc);
		doc = new Document();
		doc.addField(new Field("Id", "Int", "4"));
		doc.addField(new Field("Key", "Int", "4"));
		writer.execute(doc);
		doc = new Document();
		doc.addField(new Field("Id", "Int", "5"));
		doc.addField(new Field("Key", "Int", "5"));
		writer.execute(doc);
	}

	@After
	public void after() throws ComponentInitException {
		if (writer != null) {
			writer.complete();
		}
	}

}
