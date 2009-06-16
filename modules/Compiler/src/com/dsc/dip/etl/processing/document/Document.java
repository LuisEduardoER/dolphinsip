package com.dsc.dip.etl.processing.document;

import java.util.ArrayList;
import java.util.List;

public class Document {

	public final static String INSERT_TRANSACTION_TYPE = "INSERT";

	public final static String UPDATE_TRANSACTION_TYPE = "UPDATE";

	public final static String DELETE_TRANSACTION_TYPE = "DELETE";
	
	public final static String DOCUMENT_STATUS_NONE = "none";

	public final static String DOCUMENT_STATUS_PROCESS = "process";

	public final static String DOCUMENT_STATUS_COMPLETE = "complete";

	protected String seqNumber = "";

	protected String transactiontype = INSERT_TRANSACTION_TYPE;
	
	protected String status;

	protected List<Field> fields = new ArrayList<Field>();

	public Document() {
	}

	public boolean addField(Field field) {
		if (field != null) {
			Field old = findField(field.name);
			if (old != null) {
				fields.remove(old);
			}
			return fields.add(field);
		} else {
			return false;
		}
	}

	public boolean setFieldValue(String fieldName, String fieldValue) {
		if (fieldName != null) {
			Field field = findField(fieldName);
			if (field != null) {
				field.setValue(fieldValue);
				return true;
			} else {
				field = new Field(fieldName, fieldValue);
				return fields.add(field);
			}
		} else {
			return false;
		}
	}

	public boolean removeField(Field field) {
		if (field != null) {
			return fields.remove(field);
		} else {
			return false;
		}
	}

	public boolean removeField(String fieldName) {
		if (fieldName != null) {
			Field field = findField(fieldName);
			if (field != null) {
				return fields.remove(field);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Field findField(String fieldName) {
		for (Field field : fields) {
			if (field.name.equals(fieldName)) {
				return field;
			}
		}
		return null;
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

	public String getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(String seqNumber) {
		this.seqNumber = seqNumber;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public synchronized Document copy() {
		Document doc = new Document();
		doc.seqNumber = this.seqNumber;
		doc.transactiontype = this.transactiontype;
		for (Field field : fields) {
			doc.addField(field.copy());
		}
		return doc;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Document(" + seqNumber + ") ["
				+ transactiontype + "] {");
		for (Field field : fields) {
			str.append(field.toString() + " ");
		}
		str.setLength(str.length() - 1);
		if (str.toString().contains("{")) {
			str.append("}");
		}
		return str.toString();
	}

}
