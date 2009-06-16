package com.dsc.dip.etl.processing.component.reader;

import com.dsc.dip.etl.processing.document.Document;
import com.dsc.dip.etl.processing.document.Field;

public class StubReader extends DataReader {

	protected int count = 5;

	public boolean hasDocument() {
		return count >= 0;
	}

	public Document readDocument(Document document) {
		document.addField(new Field("f1", "v1", "String"));
		document.addField(new Field("f2", "v2", "String"));
		document.addField(new Field("f3", "v3", "String"));
		count--;
		return document;
	}

}
