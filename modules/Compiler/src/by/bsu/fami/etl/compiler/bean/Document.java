package by.bsu.fami.etl.compiler.bean;

import java.util.ArrayList;
import java.util.List;

public class Document {
	
	protected String transactiontype;
	
	protected List<Field> fields = new ArrayList<Field>();
	
	public Document(){
	}

	public String getTransactiontype() {
		return transactiontype;
	}

	public void setTransactiontype(String transactiontype) {
		this.transactiontype = transactiontype;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
}
