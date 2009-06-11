package by.bsu.fami.etl.processing.component.reader;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.datasource.DataSourceException;
import by.bsu.fami.etl.processing.datasource.LocalFileDataSource;
import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class XmlReader extends DataReader {

	private final static Logger LOGGER = Logger.getLogger(XmlReader.class);

	public final static String PROPERTY_FIELD_ELEMENT = "FieldElement";

	public final static String PROPERTY_FIELD_NAME_ELEMENT = "FieldNameElement";

	public final static String PROPERTY_FIELD_VALUE_ELEMENT = "FieldValueElement";

	protected static final String FIELD_ELEMENT_DEFAULT = "./field";

	protected static final String FIELD_NAME_ELEMENT_DEFAULT = "./name/text()";

	protected static final String FIELD_VALUE_ELEMENT_DEFAULT = "./value/text()";

	protected DocumentBuilder documentBuilder;

	protected XPathExpression documentXpath;

	protected XPathExpression fieldXpath;

	protected XPathExpression fieldNameXpath;

	protected XPathExpression fieldValueXpath;

	protected ThreadLocal<FileInputStream> readDataSource;

	protected ThreadLocal<NodeList> nodes;

	protected ThreadLocal<Integer> current;

	public synchronized boolean init() throws ComponentInitException {
		boolean init = super.init();
		if (init) {
			if (dataSource instanceof LocalFileDataSource) {
				try {
					DocumentBuilderFactory documentFactory = DocumentBuilderFactory
							.newInstance();
					documentFactory.setNamespaceAware(true);
					documentBuilder = documentFactory.newDocumentBuilder();
					LOGGER.debug("Success create doc builder for xml reader "
							+ name);
					XPathFactory xpathFactory = XPathFactory.newInstance();
					XPath xpath = xpathFactory.newXPath();
					fieldXpath = createXpath(xpath, PROPERTY_FIELD_ELEMENT,
							FIELD_ELEMENT_DEFAULT);
					fieldNameXpath = createXpath(xpath,
							PROPERTY_FIELD_NAME_ELEMENT,
							FIELD_NAME_ELEMENT_DEFAULT);
					fieldValueXpath = createXpath(xpath,
							PROPERTY_FIELD_VALUE_ELEMENT,
							FIELD_VALUE_ELEMENT_DEFAULT);
					LOGGER.debug("Success evalute field xpaths");
					if (StringUtils.isNotEmpty(map)) {
						documentXpath = xpath.compile(map);
						LOGGER.debug("Success compile xpath " + map);
						lookupInit();
					} else {
						String message = "Map for xml reader component " + name
								+ " must be not empty";
						LOGGER.error(message);
						throw new ComponentInitException(message);
					}
					LOGGER.info("Success init xml reader component " + name);
					return true;
				} catch (ParserConfigurationException e) {
					String message = "Some configuration problem of xml reader "
							+ name;
					LOGGER.error(message, e);
					throw new ComponentInitException(message, e);
				} catch (XPathExpressionException e) {
					String message = "Couldn't evalute field xpath expression "
							+ map + " for xml reader component " + name;
					LOGGER.error(message, e);
					throw new ComponentInitException(message, e);
				}
			} else {
				String message = "Datasource " + dataSource.getName()
						+ " for xml reader component " + name
						+ " isn't LocalFileDataSource";
				LOGGER.error(message);
				throw new ComponentInitException(message);
			}
		}
		return init;
	}

	protected void lookupInit() throws ComponentInitException {
		try {
			readDataSource = new ThreadLocal<FileInputStream>();
			readDataSource.set(((LocalFileDataSource) dataSource)
					.getReadDataSource());
			org.w3c.dom.Document xmlDocument = null;
			synchronized (dataSource) {
				xmlDocument = documentBuilder.parse(readDataSource.get());
			}
			if (xmlDocument != null) {
				LOGGER.debug("Success parse document");
				Object result = documentXpath.evaluate(xmlDocument,
						XPathConstants.NODESET);
				nodes = new ThreadLocal<NodeList>();
				nodes.set((NodeList) result);
				current = new ThreadLocal<Integer>();
				current.set(0);
				LOGGER.debug("Success evalute xpath " + map + ", find "
						+ nodes.get().getLength() + " nodes");
				LOGGER.info("Init lookup component " + name);
			}
		} catch (SAXException e) {
			String message = "Couldn't parse xml file from datasource "
					+ dataSource.getName();
			LOGGER.error(message, e);
			throw new ComponentInitException(message, e);
		} catch (IOException e) {
			String message = "Couldn't accesse to file input data source "
					+ dataSource.getName();
			LOGGER.error(message, e);
			throw new ComponentInitException(message, e);
		} catch (XPathExpressionException e) {
			String message = "Couldn't evalute map xpath expression " + map
					+ " for xml reader component " + name;
			LOGGER.error(message, e);
			throw new ComponentInitException(message, e);
		} catch (DataSourceException e) {
			String message = "Couldn't access to datasource " + name;
			LOGGER.error(message, e);
			throw new ComponentInitException(message, e);
		}
	}

	private XPathExpression createXpath(XPath xpath, String property,
			String defaultVal) throws XPathExpressionException {
		String propVal = getPropertyValue(property);
		propVal = StringUtils.isNotEmpty(propVal) ? propVal : defaultVal;
		return xpath.compile(propVal);
	}

	public boolean hasDocument() {
		if (nodes != null && nodes.get() != null) {
			return current.get() < nodes.get().getLength();
		}
		return false;
	}

	public Document readDocument(Document document) {
		document = document == null ? new Document() : document;
		Node node = null;
		if (nodes != null && nodes.get() != null) {
			node = nodes.get().item(current.get());
			current.set(current.get() + 1);
			initSeqNumber(document);
			if (node != null) {
				synchronized (node) {
					if (node instanceof Element) {
						Element docElement = (Element) node;
						try {
							NodeList fieldsNodes = (NodeList) fieldXpath
									.evaluate(docElement,
											XPathConstants.NODESET);
							for (int i = 0; i < fieldsNodes.getLength(); i++) {
								Field field = createFieldFromXML(fieldsNodes
										.item(i));
								if (field != null) {
									document.addField(field);
								}
							}
						} catch (XPathExpressionException e) {
							String message = "Couldn't evalute field xpath expression "
									+ fieldXpath
									+ " for xml reader component "
									+ name + ", sciped this document";
							LOGGER.warn(message, e);
						}
					} else {
						LOGGER.warn("Node " + node
								+ " isn't Element type, this node skiped");
					}
				}
			}
		}
		return document;
	}

	public Field createFieldFromXML(Node node) {
		if (node instanceof Element) {
			Field field = null;
			Element fieldElement = (Element) node;
			try {
				String name = (String) fieldNameXpath.evaluate(fieldElement,
						XPathConstants.STRING);
				if (StringUtils.isEmpty(name)) {
					LOGGER.warn("Field name must be not empty, "
							+ "sciped field element " + fieldElement);
					return null;
				}
				field = new Field(name, "String");
			} catch (XPathExpressionException e) {
				String message = "Couldn't evalute field name xpath expression "
						+ fieldNameXpath
						+ " for xml reader component "
						+ name
						+ ", sciped this field";
				LOGGER.warn(message, e);
				return null;
			}
			if (field != null) {
				try {
					String value = (String) fieldValueXpath.evaluate(
							fieldElement, XPathConstants.STRING);
					field.setValue(value);
				} catch (XPathExpressionException e) {
					String message = "Couldn't evalute field value xpath expression "
							+ fieldValueXpath
							+ " for xml reader component "
							+ name + ", sciped this field";
					LOGGER.warn(message, e);
					return null;
				}
			}
			return field;
		} else {
			LOGGER.warn("Fiedl node " + node
					+ " isn't Element type, this node skiped");
		}
		return null;
	}

	protected void initSeqNumber(Document document) {
		if (StringUtils.isEmpty(document.getSeqNumber())) {
			document.setSeqNumber(Integer.toString(current.get()));
		} else {
			document.setSeqNumber(document.getSeqNumber() + "."
					+ Integer.toString(current.get()));
		}
	}

	public synchronized boolean complete() throws ComponentInitException {
		boolean complete = super.complete();
		if (readDataSource != null && readDataSource.get() != null) {
			try {
				readDataSource.get().close();
			} catch (IOException e) {
				LOGGER.warn("Failure release datasource", e);
			}
			return complete || true;
		}
		return complete;
	}

	protected void lookupComplete() throws ComponentInitException {
		if (readDataSource != null && readDataSource.get() != null) {
			try {
				readDataSource.get().close();
			} catch (IOException e) {
				LOGGER.warn("Failure release datasource", e);
			}
		}
	}

}
