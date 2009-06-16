package com.dsc.dip.etl.processing.component.writer;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.dsc.dip.etl.processing.document.Field;


public class StatementTemplate {

	public static final String SET_FIELD = "#SetField#";

	public static final String KEY_FIELDS = "#KeyFields#";

	public static final String FIELD_VALUES = "#FieldValues#";

	public static final String FIELDS = "#Fields#";

	public static final String TABLE_NAME = "#TableName#";

	public static final String FIELDS_SEPARATOR = ",";

	public static final String TRUNCATE_TEMPLATE = "TRUNCATE TABLE "
			+ TABLE_NAME + "; ";

	protected String template;

	protected String tableName;

	protected String idField;

	protected List<Field> keyFields;

	protected List<Field> fields;

	public StatementTemplate(String template) {
		this.template = template;
	}

	public StatementTemplate(String template, String tableName, String idField,
			List<Field> keyFields, List<Field> fields) {
		this(template);
		this.fields = fields;
		this.idField = idField;
		this.keyFields = keyFields;
		this.tableName = tableName;
	}

	public String getStatement() throws StatementException {
		StringBuilder result = new StringBuilder();
		String fields = prepareFields();
		String fieldsValues = prepareFieldValues();
		String setFields = prepareSetFields();
		String keys = prepareKeys();
		keys = StringUtils.isNotEmpty(keys) ? "WHERE " + keys
				: StringUtils.EMPTY;
		String statement = result.append(template).toString();
		statement = statement.replace(TABLE_NAME, tableName).replace(FIELDS,
				fields).replace(SET_FIELD, setFields).replace(FIELD_VALUES,
				fieldsValues).replace(KEY_FIELDS, keys);
		return statement;
	}

	private String prepareKeys() throws StatementException {
		StringBuilder keys = new StringBuilder();
		for (int i = 0; i < keyFields.size(); i++) {
			if (i > 0) {
				keys.append(" AND ");
			}
			keys.append(keyFields.get(i).getName());
			keys.append(" = ");
			keys.append(preparedFieldValue(fields.get(i)));
		}
		return keys.toString();
	}

	private String prepareSetFields() throws StatementException {
		if (fields == null) {
			throw new StatementException(
					"Array fields values must be initialize");
		}
		StringBuilder f = new StringBuilder();
		boolean check = false;
		for (int i = 0; i < fields.size(); i++) {
			if (!StringUtils.equalsIgnoreCase(idField, fields.get(i).getName())) {
				if (check) {
					f.append(FIELDS_SEPARATOR);
				}
				check = true;
				f.append(fields.get(i).getName());
				f.append(" = ");
				f.append(preparedFieldValue(fields.get(i)));
			}
		}
		return StringUtils.isNotEmpty(f.toString()) ? "SET " + f.toString()
				: StringUtils.EMPTY;
	}

	private String prepareFieldValues() throws StatementException {
		if (fields == null) {
			throw new StatementException(
					"Array fields values must be initialize");
		}
		StringBuilder fsV = new StringBuilder();
		boolean check = false;
		for (int i = 0; i < fields.size(); i++) {
			if (!StringUtils.equalsIgnoreCase(idField, fields.get(i).getName())) {
				if (check) {
					fsV.append(FIELDS_SEPARATOR);
				}
				check = true;
				fsV.append(preparedFieldValue(fields.get(i)));
			}
		}
		return fsV.toString();
	}

	protected String prepareFields() throws StatementException {
		if (fields == null) {
			throw new StatementException("Array fields must be initialize");
		}
		StringBuilder fs = new StringBuilder();
		boolean check = false;
		for (int i = 0; i < fields.size(); i++) {
			if (!StringUtils.equalsIgnoreCase(idField, fields.get(i).getName())) {
				if (check) {
					fs.append(FIELDS_SEPARATOR);
				}
				fs.append(fields.get(i).getName());
				check = true;
			}
		}
		return fs.toString();
	}

	private String preparedFieldValue(Field field) {
		String result;
		if (field.getValue() == null) {
			result = "NULL";
		} else {
			result = field.getValue().toString();
			result = StringEscapeUtils.escapeSql(result);
			if (!"INTEGER".equals(field.getType())) {
				result = "'" + result + "'";
			}
		}
		return result;
	}

	public String getTemplate() {
		return template;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public List<Field> getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(List<Field> keyFields) {
		this.keyFields = keyFields;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

}
