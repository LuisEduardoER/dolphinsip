package com.dsc.dip.etl.processing.component;

import java.util.List;

import com.dsc.dip.etl.processing.document.Document;


public class FieldTransformer extends Component {

	public List<Document> execute(Document document) {
		// TODO Auto-generated method stub
		callAll(document);
		return null;
	}

}
