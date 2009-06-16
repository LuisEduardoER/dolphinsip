package com.dsc.dip.etl.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


import com.dsc.dip.etl.processing.component.Component;
import com.dsc.dip.etl.processing.document.Document;
import com.dsc.dip.etl.processing.document.Field;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XmlWriter extends Component {

	public List<Document> execute(Document document) {
		Element e = null;
		Node n = null;
		try {
			File file = new File("documents.xml");
			org.w3c.dom.Document xmldoc;
			Element root;
			boolean isNew = false;
			if (file.exists()) {
				xmldoc = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(file);
				root = xmldoc.getDocumentElement();
			} else {
				xmldoc = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().newDocument();
				root = xmldoc.createElement("root");
				isNew = true;
			}
			Element docEl = xmldoc.createElement("Docuemnt");
			for (Field field : document.getFields()) {
				// Child i.
				e = xmldoc.createElementNS(null, field.getName());
				n = xmldoc.createTextNode(field.getValue());
				e.appendChild(n);
				docEl.appendChild(e);
			}
			root.appendChild(docEl);
			if (isNew) {
				xmldoc.appendChild(root);
			}
			FileOutputStream fos = new FileOutputStream(file);
			OutputFormat of = new OutputFormat("XML", "UTF-8", true);
			of.setIndent(1);
			of.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(fos, of);
			serializer.asDOMSerializer();
			serializer.serialize(xmldoc.getDocumentElement());
			fos.close();
		} catch (ParserConfigurationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (SAXException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		callAll(document);
		return null;
	}

}
