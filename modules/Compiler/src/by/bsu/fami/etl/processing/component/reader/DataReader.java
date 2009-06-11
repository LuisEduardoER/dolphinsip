package by.bsu.fami.etl.processing.component.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import by.bsu.fami.etl.processing.component.ComponentInitException;
import by.bsu.fami.etl.processing.component.DataStorageComponent;
import by.bsu.fami.etl.processing.document.Document;

public abstract class DataReader extends DataStorageComponent {

	private final static Logger LOGGER = Logger.getLogger(DataReader.class);

	public final static String PROPERTY_PARALLEL_COUNT = "ParallelCount";

	public final static String PROPERTY_RUN_MODE = "RunMode";

	public final static String PROPERTY_RUN_MODE_READ = "read";

	public final static String PROPERTY_RUN_MODE_LOOKUP = "lookup";

	protected String map;

	protected ThreadPoolExecutor pool;

	protected boolean parallel = false;

	protected String runMode = PROPERTY_RUN_MODE_READ;

	protected ArrayBlockingQueue<Runnable> queue;

	protected List<DocumentProccessing> processDocs;

	public boolean init() throws ComponentInitException {
		LOGGER.info("Init data reader component " + name);
		runMode = StringUtils
				.isNotEmpty(runMode = getPropertyValue(PROPERTY_RUN_MODE)) ? runMode
				: PROPERTY_RUN_MODE_READ;
		if (PROPERTY_RUN_MODE_READ.equals(runMode)) {
			checkParallel();
		}
		return super.init();
	}

	protected void lookupInit() throws ComponentInitException {
	}

	public List<Document> execute(Document document) {
		if (document == null) {
			document = new Document();
		}
		if (PROPERTY_RUN_MODE_LOOKUP.equals(runMode)) {
			return lookup(document);
		} else {
			if (PROPERTY_RUN_MODE_READ.equals(runMode)) {
				return read(document);
			}
		}
		return null;
	}

	public List<Document> read(Document document) {
		LOGGER.info("Start reading process " + name + " ...");
		List<Document> docs = new ArrayList<Document>();
		processDocs = new ArrayList<DocumentProccessing>();
		while (hasDocument()) {
			Document newdocument = readDocument(new Document());
			newdocument.setStatus(Document.DOCUMENT_STATUS_NONE);
			docs.add(newdocument);
			if (parallel) {
				parallelProcessDocument(newdocument);
			} else {
				docs.addAll(processDocument(newdocument));
			}
		}
		if (parallel && pool != null) {
			while (pool.getActiveCount() > 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					LOGGER.warn(e);
				}
			}
			for (DocumentProccessing proccessingDocument : processDocs) {
				List<Document> documents = proccessingDocument.getDocuments();
				if (documents != null) {
					docs.addAll(documents);
				}
			}
			pool.shutdown();
		}
		return docs;
	}

	public List<Document> lookup(Document document) {
		LOGGER.info("Execute lookup " + name + " ...");
		List<Document> docs = new ArrayList<Document>();
		try {
			lookupInit();
			while (hasDocument()) {
				Document newdocument = readDocument(document.copy());
				docs.add(newdocument);
				docs.addAll(processDocument(newdocument));
			}
			lookupComplete();
		} catch (ComponentInitException e) {
			LOGGER.warn("Couldn't execute lookup, document "
					+ document.getSeqNumber() + " is sciped");
		}
		return docs;
	}

	public boolean complete() throws ComponentInitException {
		LOGGER.info("Release data reader component " + name);
		if (pool != null) {
			pool.shutdown();
		}
		return super.complete();
	}

	protected void lookupComplete() throws ComponentInitException {
	}

	protected void checkParallel() {
		String parallelNumberStr = getPropertyValue(PROPERTY_PARALLEL_COUNT);
		if (StringUtils.isNotEmpty(parallelNumberStr)
				&& StringUtils.isNumeric(parallelNumberStr)) {
			int parallelNumber = Integer.parseInt(parallelNumberStr);
			queue = new ArrayBlockingQueue<Runnable>(parallelNumber * 10);
			pool = new ThreadPoolExecutor(parallelNumber, parallelNumber, 10,
					TimeUnit.SECONDS, queue);
			LOGGER
					.info("Config parallel processing documents, thread pool size  = "
							+ parallelNumber);
			parallel = true;
		} else {
			parallel = false;
		}
	}

	public abstract Document readDocument(Document document);

	public abstract boolean hasDocument();

	protected List<Document> processDocument(Document document) {
		LOGGER.info("Start processing new read document: "
				+ document.getSeqNumber());
		document.setStatus(Document.DOCUMENT_STATUS_PROCESS);
		return callAll(document);
	}

	protected void parallelProcessDocument(Document document) {
		if (parallel && pool != null) {
			DocumentProccessing documentProccessing = new DocumentProccessing(
					document);
			processDocs.add(documentProccessing);
			pool.execute(documentProccessing);
			LOGGER
					.info("Proccessing documents count " + queue.size()
							+ " ....");
		} else {
			processDocument(document);
		}
	}

	protected class DocumentProccessing implements Runnable {

		protected Document document;

		protected List<Document> documents;

		public DocumentProccessing(Document document) {
			this.document = document.copy();
		}

		public void run() {
			documents = processDocument(document);
		}

		public List<Document> getDocuments() {
			return documents;
		}

	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

}
