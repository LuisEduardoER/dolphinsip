package by.bsu.fami.etl.processing.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WebServiceDataSourceTest {

	protected WebServiceDataSource wsDs;

	@Before
	public void before() throws DataSourceException {
		BasicConfigurator.configure();
		wsDs = new WebServiceDataSource();
		wsDs.setName("WebServiceDataSource");
		wsDs.addProperty(WebServiceDataSource.PROPERTY_WEBSERVICE_URL,
				"http://localhost:8081/SimpleWebService/"
						+ "services/SimpleWebService");
		wsDs.addProperty(
				WebServiceDataSource.PROPERTY_WEBSERVICE_NAMESPACE_URL,
				"http://webservice.example.etl.fami.bsu.by");
		wsDs.addProperty(WebServiceDataSource.PROPERTY_WEBSERVICE_NAMESPACE,
				"ns");
		wsDs.initConnect();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void simple() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("value", "Test service");
		OMElement serviceResult = wsDs.getServiceResult("echo", params);
		QName returnElement = new QName("ns", "return");
		Iterator<Object> it = serviceResult.getChildrenWithName(returnElement);
		Assert.assertEquals("Test service", ((OMText)it.next()).getText());
	}

	@After
	public void after() throws DataSourceException {
		wsDs.releaseConnect();
	}
}
