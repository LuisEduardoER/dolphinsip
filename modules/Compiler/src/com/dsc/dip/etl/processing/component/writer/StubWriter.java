package com.dsc.dip.etl.processing.component.writer;

import com.dsc.dip.etl.processing.document.Document;

public class StubWriter extends DataWriter {

	public void writeDocuments() {
		for (Document doc : documents) {
			System.out.println(doc.toString());
		}
	}

}
