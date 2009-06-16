package com.dsc.dip.etl.processing.component.checker;

import java.util.ArrayList;
import java.util.List;

import com.dsc.dip.etl.processing.document.Document;


public class SimpleChecker extends Checker {

	public boolean check = false;

	public boolean check() {
		return check;
	}

	public List<Document> execute(Document document) {
		check = true;
		List<Document> docs = new ArrayList<Document>();
		docs.add(document);
		document.setStatus(Document.DOCUMENT_STATUS_COMPLETE);
		return docs;
	}

}
