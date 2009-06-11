package by.bsu.fami.etl.processing;

import java.util.List;

import by.bsu.fami.etl.processing.document.Document;

public interface IExecuter {
	
	public List<Document> execute(Document document);

}
