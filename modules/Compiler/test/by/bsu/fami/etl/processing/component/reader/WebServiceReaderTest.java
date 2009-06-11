package by.bsu.fami.etl.processing.component.reader;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.datasource.DataSourceException;
import by.bsu.fami.etl.processing.datasource.WebServiceDataSource;
import by.bsu.fami.etl.processing.document.Document;

public class WebServiceReaderTest {

	protected WebServiceDataSource wsDs;

	protected WebServiceReader wsReader;
	
	@Before
	public void before() throws DataSourceException, ComponentInitException {
		BasicConfigurator.configure();
		wsDs = new WebServiceDataSource();
		wsDs.setName("webServiceDataSource");
		wsDs.addProperty(WebServiceDataSource.PROPERTY_WEBSERVICE_URL,
				"http://localhost:8081/SimpleWebService/"
						+ "services/SimpleWebService");
		wsDs.addProperty(
				WebServiceDataSource.PROPERTY_WEBSERVICE_NAMESPACE_URL,
				"http://webservice.example.etl.fami.bsu.by");
		wsDs.addProperty(WebServiceDataSource.PROPERTY_WEBSERVICE_NAMESPACE,
				"ns");
		wsDs.initConnect();
		wsReader = new WebServiceReader();
		wsReader.setName("webServiceReader");
		wsReader.setMap("statistic");
		wsReader.setDataSource(wsDs);
		wsReader.addProperty(WebServiceReader.PROPERTY_DOCUMENT_ELEMENT,
				"//company");
		wsReader.addProperty(WebServiceReader.PROPERTY_FIELD_ELEMENT,
				"./node()");
		wsReader.addProperty(WebServiceReader.PROPERTY_FIELD_NAME_ELEMENT,
				"local-name(.)");
		wsReader.addProperty(WebServiceReader.PROPERTY_FIELD_VALUE_ELEMENT,
				"./text()");
		wsReader.init();
	}

	@Test
	public void simple() {
		Document doc = new Document(); 
		wsReader.read(doc);
		System.out.println(doc);
	}

	@After
	public void after() throws DataSourceException, ComponentInitException {
		if (wsDs != null) {
			wsDs.releaseConnect();
		}
		if (wsReader != null) {
			wsReader.complete();
		}
	}
}
