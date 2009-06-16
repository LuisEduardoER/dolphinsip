package com.dsc.dip.etl.processing.component.writer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.processing.component.ComponentInitException;
import com.dsc.dip.etl.processing.component.DataStorageComponent;
import com.dsc.dip.etl.processing.document.Document;


public abstract class DataWriter extends DataStorageComponent {

	private final static Logger LOGGER = Logger.getLogger(DataWriter.class);

	public final static String PROPERTY_BATCH_SIZE = "BatchSize";

	protected String rootId;

	protected String id;

	protected String keys;

	protected int batchSize = 64;

	protected List<Document> documents = new ArrayList<Document>(batchSize);

	public synchronized boolean init() throws ComponentInitException {
		String batchSize = getPropertyValue(PROPERTY_BATCH_SIZE);
		if (StringUtils.isNotEmpty(batchSize)
				&& StringUtils.isNumeric(batchSize)) {
			this.batchSize = Integer.parseInt(batchSize);
		}
		documents = new ArrayList<Document>(this.batchSize);
		LOGGER.info("Init data writer component " + name);
		return super.init();
	}

	public List<Document> execute(Document document) {
		List<Document> docs = new ArrayList<Document>(1);
		synchronized (documents) {
			if (documents.size() >= batchSize - 1) {
				documents.add(document);
				LOGGER.debug("Write next batch");
				writeDocuments();
				docs = new ArrayList<Document>(documents.size());
				for (Document doc : documents) {
					doc.setStatus(Document.DOCUMENT_STATUS_COMPLETE);
					docs.add(doc);
				}
				documents.clear();
			} else {
				documents.add(document);
				docs.add(document);
			}
		}
		return docs;
	}

	public synchronized boolean complete() throws ComponentInitException {
		LOGGER.info("Release  data writer component " + name);
		LOGGER.info("Write last documents batch");
		writeDocuments();
		documents.clear();
		return super.complete();
	}

	public abstract void writeDocuments();

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

}
