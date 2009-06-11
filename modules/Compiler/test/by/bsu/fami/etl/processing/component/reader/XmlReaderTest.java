package by.bsu.fami.etl.processing.component.reader;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import by.bsu.fami.etl.processing.component.Component;
import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.component.writer.DataWriter;
import by.bsu.fami.etl.processing.component.writer.StubWriter;
import by.bsu.fami.etl.processing.datasource.LocalFileDataSource;
import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class XmlReaderTest {

	protected LocalFileDataSource dataSource;

	protected XmlReader reader;

	@Before
	public void before() throws ComponentInitException {
		BasicConfigurator.configure();
		dataSource = new LocalFileDataSource();
		dataSource.setName("localFileDataSource");
		dataSource.addProperty(LocalFileDataSource.PROPERTY_FILE_NAME,
				"test/input.xml");

		StubWriter writer = new StubWriter();
		writer.setName("stubWriter");
		writer.addProperty(DataWriter.PROPERTY_BATCH_SIZE, "1");
		writer.init();
		List<Component> calleds = new ArrayList<Component>();
		calleds.add(writer);

		reader = new XmlReader();
		reader.setName("xmlReader");
		reader.setMap("//docs");
		reader.setDataSource(dataSource);
		reader.setCalleds(calleds);
		reader.init();
	}

	@Test
	public void simple() {
		reader.addProperty(XmlReader.PROPERTY_RUN_MODE,
				XmlReader.PROPERTY_RUN_MODE_READ);
		reader.runMode = XmlReader.PROPERTY_RUN_MODE_READ;
		reader.execute(null);
	}

	@Test
	public void lookupMode() {
		reader.addProperty(XmlReader.PROPERTY_RUN_MODE,
				XmlReader.PROPERTY_RUN_MODE_LOOKUP);
		reader.runMode = XmlReader.PROPERTY_RUN_MODE_LOOKUP;
		Document doc = new Document();
		Document doc1 = doc.copy();
		doc1.addField(new Field("f1", "String", "v1"));
		doc1.setSeqNumber("1");
		reader.execute(doc1);
		Document doc2 = doc.copy();
		doc2.addField(new Field("f1", "String", "v2"));
		doc2.setSeqNumber("2");
		reader.execute(doc2);
	}

	@After
	public void after() throws ComponentInitException {
		if (reader != null) {
			reader.complete();
		}
	}
}
