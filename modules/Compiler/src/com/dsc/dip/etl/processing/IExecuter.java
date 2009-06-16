package com.dsc.dip.etl.processing;

import java.util.List;

import com.dsc.dip.etl.processing.document.Document;


public interface IExecuter {
	
	public List<Document> execute(Document document);

}
