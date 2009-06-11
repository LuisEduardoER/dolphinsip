package by.bsu.fami.etl.processing.datasource;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WebServiceDataSource extends DataSource {

	private final static Logger LOGGER = Logger
			.getLogger(WebServiceDataSource.class);

	public final static String PROPERTY_WEBSERVICE_URL = "WebServiceUrl";

	public final static String PROPERTY_WEBSERVICE_NAMESPACE_URL = "WebServiceNamespaceUrl";

	public final static String PROPERTY_WEBSERVICE_NAMESPACE = "WebServiceNamespace";

	public final static String PROPERTY_WEBSERVICE_REESULT_Element = "WebServiceResultElement";

	protected ServiceClient sc;

	protected String namespaceUrl;

	protected String namespace;

	protected String result;

	public synchronized boolean initConnect() throws DataSourceException {
		String url;
		if (StringUtils
				.isNotEmpty(url = getPropertyValue(PROPERTY_WEBSERVICE_URL))
				&& StringUtils
						.isNotEmpty(namespaceUrl = getPropertyValue(PROPERTY_WEBSERVICE_NAMESPACE_URL))
				&& StringUtils
						.isNotEmpty(namespace = getPropertyValue(PROPERTY_WEBSERVICE_NAMESPACE))) {
			try {
				sc = new ServiceClient();
				Options opts = new Options();
				opts.setTo(new EndpointReference(url));
				sc.setOptions(opts);
			} catch (AxisFault e) {
				String message = "Couldn't create WebService client "
						+ e.getMessage();
				LOGGER.error(message, e);
				throw new DataSourceException(message, e);
			}
			String reultProperty = getPropertyValue(PROPERTY_WEBSERVICE_REESULT_Element);
			result = StringUtils.isNotEmpty(reultProperty) ? reultProperty
					: "return";
			return true;
		} else {
			String message = "DataSource '" + PROPERTY_WEBSERVICE_URL
					+ "' and '" + PROPERTY_WEBSERVICE_NAMESPACE
					+ "' property must be initialize";
			LOGGER.error(message);
			throw new DataSourceException(message);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized OMElement getServiceResult(String methodName,
			Map<String, String> params) {
		if (sc != null) {
			sc.getOptions().setAction("urn:" + methodName);
			OMElement method = createMethod(methodName, params);
			try {
				OMElement receiveResult = sc.sendReceive(method);
				QName returnElement = new QName(namespace, result);
				Iterator<Object> it = receiveResult
						.getChildrenWithName(returnElement);
				Object obj;
				if ((obj = it.next()) != null) {
					if (obj instanceof OMElement) {
						OMElement result = (OMElement) obj;
						it = result.getChildElements();
						if ((obj = it.next()) != null) {
							if (obj instanceof OMElement) {
								return (OMElement) obj;
							}
						}
						return result;
					}

				}
				return receiveResult;
			} catch (AxisFault e) {
				String message = "Coldn't recieve result from webservice";
				LOGGER.error(message, e);
			}
		}
		return null;
	}

	public OMElement createMethod(String methodName, Map<String, String> params) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(namespaceUrl, namespace);
		OMElement method = fac.createOMElement(methodName, omNs);
		for (Map.Entry<String, String> param : params.entrySet()) {
			OMElement value = fac.createOMElement(param.getKey(), omNs);
			value.setText(param.getValue());
			method.addChild(value);
		}
		return method;
	}

	public synchronized boolean releaseConnect() throws DataSourceException {
		if (sc != null) {
			try {
				sc.cleanup();
			} catch (AxisFault e) {
				LOGGER.warn("Couldn't cleanup webservice datasource ", e);
			}
		}
		return true;
	}

	public ServiceClient getSc() {
		return sc;
	}

	public String getNamespaceUrl() {
		return namespaceUrl;
	}

	public String getNamespace() {
		return namespace;
	}

}
