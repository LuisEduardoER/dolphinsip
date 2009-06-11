package by.bsu.fami.etl.processing.component.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.datasource.WebServiceDataSource;
import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class WebServiceReader extends DataReader {

	private final static Logger LOGGER = Logger
			.getLogger(WebServiceReader.class);

	public final static String PROPERTY_DOCUMENT_ELEMENT = "DocumentElement";

	public final static String PROPERTY_FIELD_ELEMENT = "FieldElement";

	public final static String PROPERTY_FIELD_NAME_ELEMENT = "FieldNameElement";

	public final static String PROPERTY_FIELD_VALUE_ELEMENT = "FieldValueElement";

	protected static final String FIELD_DOCUMENT_DEFAULT = "//document";

	protected static final String FIELD_ELEMENT_DEFAULT = "./field";

	protected static final String FIELD_NAME_ELEMENT_DEFAULT = "./name/text()";

	protected static final String FIELD_VALUE_ELEMENT_DEFAULT = "./value/text()";

	@SuppressWarnings("unchecked")
	protected List documents;

	protected int current;

	protected AXIOMXPath fieldXpath;

	protected AXIOMXPath fieldNameXpath;

	protected AXIOMXPath fieldValueXpath;

	public synchronized boolean init() throws ComponentInitException {
		boolean init = super.init();
		if (init) {
			if (dataSource instanceof WebServiceDataSource) {
				try {
					LOGGER
							.debug("Success create doc builder for WebService reader "
									+ name);
					AXIOMXPath docXpath = createXpath(
							PROPERTY_DOCUMENT_ELEMENT, FIELD_DOCUMENT_DEFAULT);
					fieldXpath = createXpath(PROPERTY_FIELD_ELEMENT,
							FIELD_ELEMENT_DEFAULT);
					fieldNameXpath = createXpath(PROPERTY_FIELD_NAME_ELEMENT,
							FIELD_NAME_ELEMENT_DEFAULT);
					fieldValueXpath = createXpath(PROPERTY_FIELD_VALUE_ELEMENT,
							FIELD_VALUE_ELEMENT_DEFAULT);
					LOGGER.debug("Success evalute field xpaths");
					if (StringUtils.isNotEmpty(map)) {
						String[] split = map.split("[|]");
						String methodName = split[0];
						Map<String, String> params = new HashMap<String, String>();
						for (int i = 1; i < split.length; i++) {
							if (StringUtils.isNotEmpty(split[i])) {
								String[] param = split[i].split("[=]");
								if (param.length == 2) {
									params.put(param[0], param[1]);
								}
							}
						}
						LOGGER.debug("Success compile xpath " + map);
						OMElement root = ((WebServiceDataSource) dataSource)
								.getServiceResult(methodName, params);
						documents = docXpath.selectNodes(root);
						current = 0;
					} else {
						String message = "Map for WebService reader component "
								+ name + " must be not empty";
						LOGGER.error(message);
						throw new ComponentInitException(message);
					}
					LOGGER.info("Success init WebService reader component "
							+ name);
					return true;
				} catch (JaxenException e) {
					String message = "Couldn't evalute field xpath expressions "
							+ "for WebService reader component " + name;
					LOGGER.error(message, e);
					throw new ComponentInitException(message, e);
				}
			} else {
				String message = "Datasource " + dataSource.getName()
						+ " for WebService reader component " + name
						+ " isn't WebServiceDataSource";
				LOGGER.error(message);
				throw new ComponentInitException(message);
			}
		}
		return init;
	}

	private AXIOMXPath createXpath(String property, String defaultVal)
			throws JaxenException {
		String propVal = getPropertyValue(property);
		propVal = StringUtils.isNotEmpty(propVal) ? propVal : defaultVal;
		return new AXIOMXPath(propVal);
	}

	public boolean hasDocument() {
		return current < documents.size();
	}

	@SuppressWarnings("unchecked")
	public Document readDocument(Document document) {
		OMElement doc = (OMElement) documents.get(current);
		if (document == null) {
			document = new Document();
		}
		initSeqNumber(document);
		try {
			List fields = fieldXpath.selectNodes(doc);
			for (Object obj : fields) {
				OMElement field = (OMElement) obj;
				String name = fieldNameXpath.stringValueOf(field), value = fieldValueXpath
						.stringValueOf(field);
				document.addField(new Field(name, "String", value));
			}
		} catch (JaxenException e) {
			LOGGER.error("Couldn't extract fields from document " + doc);
		}
		current++;
		return document;
	}

	protected void initSeqNumber(Document document) {
		if (StringUtils.isEmpty(document.getSeqNumber())) {
			document.setSeqNumber(Integer.toString(current + 1));
		} else {
			document.setSeqNumber(document.getSeqNumber() + "."
					+ Integer.toString(current + 1));
		}
	}

	public synchronized boolean complete() throws ComponentInitException {
		boolean complete = super.complete();
		return complete;
	}

}
