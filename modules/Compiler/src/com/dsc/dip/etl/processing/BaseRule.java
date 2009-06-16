package com.dsc.dip.etl.processing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.processing.component.Component;
import com.dsc.dip.etl.processing.component.ComponentInitException;
import com.dsc.dip.etl.processing.component.DataStorageComponent;
import com.dsc.dip.etl.processing.datasource.DataSource;
import com.dsc.dip.etl.processing.document.Document;


public abstract class BaseRule {

	private final static Logger LOGGER = Logger.getLogger(BaseRule.class);

	protected List<Component> components = new ArrayList<Component>();

	protected List<DataSource> dataSources = new ArrayList<DataSource>();

	public abstract void initComponents();

	public void execute() throws RuleException {
		Set<String> coms = new HashSet<String>();
		for (Component component : components) {
			if (component != null) {
				for (Component com : component.getCalleds()) {
					if (com != null) {
						coms.add(com.getName());
					}
				}
			}
			if (component instanceof DataStorageComponent) {
				try {
					((DataStorageComponent) component).init();
				} catch (ComponentInitException e) {
					String message = "Couldn't init component "
							+ component.getName();
					LOGGER.error(message, e);
					throw new RuleException(message, e);
				}
			}
		}
		List<ExecutersThread> executers = new ArrayList<ExecutersThread>(coms
				.size());
		for (Component component : components) {
			if (!coms.contains(component.getName())) {
				ExecutersThread executersThread = new ExecutersThread(component);
				executers.add(executersThread);
				executersThread.start();
			}
		}
		for (ExecutersThread executersThread : executers) {
			try {
				executersThread.join();
			} catch (InterruptedException e) {
				LOGGER.warn(e);
			}
		}
		LOGGER.info("All executers threads are finished.");
		boolean canFinish = true;
		do {
			canFinish = true;
			for (ExecutersThread executersThread : executers) {
				if (executersThread != null) {
					for (Document document : executersThread.getDocuments()) {
						if (document != null) {
							if (!Document.DOCUMENT_STATUS_COMPLETE
									.equals(document.getStatus())) {
								canFinish = false;
								break;
							}
							if (!canFinish) {
								break;
							}
						}
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.warn(e);
			}
		} while (canFinish);
		LOGGER.info("All documents are processed.");
	}

	public Component findComponent(String componentName) {
		for (Component component : components) {
			if (StringUtils.equals(component.getName(), componentName)) {
				return component;
			}
		}
		return null;
	}

	public DataSource findDataSource(String dataSourceName) {
		for (DataSource dataSource : dataSources) {
			if (StringUtils.equals(dataSource.getName(), dataSourceName)) {
				return dataSource;
			}
		}
		return null;
	}

	protected class ExecutersThread extends Thread {

		protected Component component;

		protected List<Document> documents;

		public ExecutersThread(Component component) {
			this.component = component;
		}

		public void run() {
			documents = component.execute(new Document());
		}

		public List<Document> getDocuments() {
			return documents;
		}

	}

}
