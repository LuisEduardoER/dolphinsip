package com.dsc.dip.etl.processing.component;

import java.util.List;

import com.dsc.dip.etl.processing.document.Document;


public class BaseFilter extends Component {

	public List<Document> execute(Document document) {
		// TODO Auto-generated method stub
		if (!document.getTransactiontype().equals("Delete")) {
			callAll(document);
		}
		return null;
	}

}
