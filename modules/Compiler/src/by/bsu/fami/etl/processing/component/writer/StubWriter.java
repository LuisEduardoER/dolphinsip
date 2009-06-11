package by.bsu.fami.etl.processing.component.writer;

import by.bsu.fami.etl.processing.document.Document;

public class StubWriter extends DataWriter {

	public void writeDocuments() {
		for (Document doc : documents) {
			System.out.println(doc.toString());
		}
	}

}
