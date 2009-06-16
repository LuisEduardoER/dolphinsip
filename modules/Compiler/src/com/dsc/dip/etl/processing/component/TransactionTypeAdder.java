package com.dsc.dip.etl.processing.component;

import java.util.List;

import com.dsc.dip.etl.processing.document.Document;


public class TransactionTypeAdder extends Component {

	public List<Document> execute(Document document) {
		// TODO Auto-generated method stub
		if (document.getFields().get(0).getName().equals("field1")) {
			document.setTransactiontype("Delete");
		}
		return null;
	}

}
