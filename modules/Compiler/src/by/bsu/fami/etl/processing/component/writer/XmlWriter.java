package by.bsu.fami.etl.processing.component.writer;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.datasource.DataSourceException;
import by.bsu.fami.etl.processing.datasource.LocalFileDataSource;
import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class XmlWriter extends DataWriter {

	private final static Logger LOGGER = Logger.getLogger(XmlWriter.class);

	protected org.w3c.dom.Document xmlDocument;

	protected Element rootElement;

	public synchronized boolean init() throws ComponentInitException {
		boolean init = super.init();
		if (init) {
			if (dataSource instanceof LocalFileDataSource) {
				try {
					xmlDocument = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().newDocument();
					if (StringUtils.isEmpty(rootId)) {
						String message = "Please init rootId element for xml writer "
								+ name;
						LOGGER.error(message);
						throw new ComponentInitException(message);
					}
					rootElement = xmlDocument.createElement(rootId);
					xmlDocument.appendChild(rootElement);
				} catch (ParserConfigurationException e) {
					String message = "Couldn't create new xml document"
							+ " for xml writer " + name;
					LOGGER.error(message, e);
					throw new ComponentInitException(message, e);
				}
			} else {
				String message = "Datasource " + dataSource.getName()
						+ " for xml writer component " + name
						+ " isn't LocalFileDataSource";
				LOGGER.error(message);
				throw new ComponentInitException(message);
			}
		}
		return init;
	}

	public synchronized void writeDocuments() {
		FileOutputStream out = null;
		try {
			out = ((LocalFileDataSource) dataSource).getWriteDataSource();
		} catch (DataSourceException e1) {
			LOGGER.warn("Couldn't access to datasource " + dataSource.getName()
					+ " batch of documents is sciped");
		}
		for (Document document : documents) {
			Element docEl = xmlDocument.createElement("document");
			for (Field field : document.getFields()) {
				Element e = xmlDocument.createElementNS(null, StringEscapeUtils
						.escapeXml(field.getName()));
				Node name = xmlDocument.createTextNode(StringEscapeUtils
						.escapeXml(field.getValue()));
				e.appendChild(name);
				docEl.appendChild(e);
			}
			rootElement.appendChild(docEl);
		}
		OutputFormat of = new OutputFormat("XML", "UTF-8", true);
		of.setIndent(1);
		of.setIndenting(true);
		XMLSerializer serializer = new XMLSerializer(out, of);
		try {
			serializer.asDOMSerializer();
			serializer.serialize(xmlDocument.getDocumentElement());
		} catch (IOException e) {
			LOGGER.warn("Couldn't document tree inccorect serialize, "
					+ "this batch is sciped");
		}
		if (out != null) {
			try {
//				out.flush();
				out.close();
			} catch (IOException e) {
				LOGGER.warn("Couldn't release output file");
			}
		}
	}

}
