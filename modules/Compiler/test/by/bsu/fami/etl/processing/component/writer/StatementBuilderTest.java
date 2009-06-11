package by.bsu.fami.etl.processing.component.writer;

import org.junit.Assert;
import org.junit.Test;

import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class StatementBuilderTest {

	protected StatementBuilder sb = StatementBuilder.newInstance();

	@Test
	public void simpleInsert() throws StatementException {
		Document doc = new Document();
		doc.addField(new Field("Id", "Int", "0"));
		doc.addField(new Field("Key", "Int", "1"));
		doc.addField(new Field("Data", "String", "test1"));
		String buildStatement = sb.buildStatement("Test", "Id", "Id,Key", doc);
		System.out.println(buildStatement);
		Assert.assertEquals("INSERT INTO Test (Key,Data) "
				+ "VALUES ('1','test1');", buildStatement);
	}

	@Test
	public void simpleUpdate() throws StatementException {
		Document doc = new Document();
		doc.setTransactiontype(Document.UPDATE_TRANSACTION_TYPE);
		doc.addField(new Field("Id", "Int", "0"));
		doc.addField(new Field("Key", "Int", "1"));
		doc.addField(new Field("Data", "String", "test1"));
		String buildStatement = sb.buildStatement("Test", "Id", "Id,Key", doc);
		System.out.println(buildStatement);
		Assert.assertEquals("UPDATE Test SET Key = '1',Data = 'test1' "
				+ "WHERE Id = '0' AND Key = '1';", buildStatement);
	}

	@Test
	public void simpleDelete() throws StatementException {
		Document doc = new Document();
		doc.setTransactiontype(Document.DELETE_TRANSACTION_TYPE);
		doc.addField(new Field("Id", "Int", "0"));
		doc.addField(new Field("Key", "Int", "1"));
		String buildStatement = sb.buildStatement("Test", "Id", "Id,Key", doc);
		System.out.println(buildStatement);
		Assert.assertEquals("DELETE FROM Test WHERE Id = '0' AND Key = '1';",
				buildStatement);
	}
}
