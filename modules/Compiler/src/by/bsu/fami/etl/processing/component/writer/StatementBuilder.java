package by.bsu.fami.etl.processing.component.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import by.bsu.fami.etl.processing.document.Document;
import by.bsu.fami.etl.processing.document.Field;

public class StatementBuilder {

	private final static Logger LOGGER = Logger
			.getLogger(StatementBuilder.class);

	public final static String CONFIG_FILE_NAME = "statement.config.file.name";

	public final static String CONFIG_INSERT_TEMPLATE = "statement.template.insert";

	public final static String CONFIG_UPDATE_TEMPLATE = "statement.template.update";

	public final static String CONFIG_DELETE_TEMPLATE = "statement.template.delete";

	public final static String INSERT_DEFAULT_TEMPLATE = "INSERT INTO "
			+ StatementTemplate.TABLE_NAME + " (" + StatementTemplate.FIELDS
			+ ") VALUES (" + StatementTemplate.FIELD_VALUES + ")";

	public final static String UPDATE_DEFAULT_TEMPLATE = "UPDATE "
			+ StatementTemplate.TABLE_NAME + " " + StatementTemplate.SET_FIELD
			+ " " + StatementTemplate.KEY_FIELDS + "";

	public final static String DELETE_DEFAULT_TEMPLATE = "DELETE FROM "
			+ StatementTemplate.TABLE_NAME + " " + StatementTemplate.KEY_FIELDS
			+ "";

	protected Map<String, String> templates = new HashMap<String, String>();

	private StatementBuilder() {
	}

	public static StatementBuilder newInstance() {
		StatementBuilder sb = new StatementBuilder();
		String configFile = System.getProperty(CONFIG_FILE_NAME);
		initDefaultTemplate(sb);
		if (StringUtils.isNotEmpty(configFile) && new File(configFile).exists()) {
			Properties sp = new Properties();
			try {
				sp.load(new FileInputStream(configFile));
				String property = sp.getProperty(CONFIG_INSERT_TEMPLATE);
				sb.templates.put(Document.INSERT_TRANSACTION_TYPE, StringUtils
						.isNotEmpty(property) ? property
						: INSERT_DEFAULT_TEMPLATE);
				property = sp.getProperty(CONFIG_UPDATE_TEMPLATE);
				sb.templates.put(Document.UPDATE_TRANSACTION_TYPE, StringUtils
						.isNotEmpty(property) ? property
						: UPDATE_DEFAULT_TEMPLATE);
				property = sp.getProperty(CONFIG_DELETE_TEMPLATE);
				sb.templates.put(Document.DELETE_TRANSACTION_TYPE, StringUtils
						.isNotEmpty(property) ? property
						: DELETE_DEFAULT_TEMPLATE);
			} catch (FileNotFoundException e) {
				LOGGER.warn("Couldn't init templates from config file, "
						+ "will be use default templates", e);
			} catch (IOException e) {
				LOGGER.warn("Couldn't init templates from config file, "
						+ "will be use default templates", e);
			}
		}
		return sb;
	}

	protected static void initDefaultTemplate(StatementBuilder sb) {
		sb.templates.put(Document.INSERT_TRANSACTION_TYPE,
				INSERT_DEFAULT_TEMPLATE);
		sb.templates.put(Document.UPDATE_TRANSACTION_TYPE,
				UPDATE_DEFAULT_TEMPLATE);
		sb.templates.put(Document.DELETE_TRANSACTION_TYPE,
				DELETE_DEFAULT_TEMPLATE);
	}

	public String buildStatement(String tableName, String idColumn,
			String keys, Document doc) throws StatementException {
		List<Field> keyFields = initKeyFields(doc, keys);
		String template = templates.get(doc.getTransactiontype());
		if (template == null) {
			String message = "Document has incorrect transaction type "
					+ doc.getTransactiontype();
			LOGGER.error(message);
			throw new StatementException(message);
		}
		StatementTemplate st = new StatementTemplate(template, tableName,
				idColumn, keyFields, doc.getFields());
		return st.getStatement();
	}

	protected List<Field> initKeyFields(Document doc, String keys)
			throws StatementException {
		List<Field> keyFields = new ArrayList<Field>();
		for (String key : Arrays.asList(keys.split(","))) {
			Field keyField = doc.findField(key);
			if (keyField == null) {
				String message = "Couldn't find key field " + key
						+ " in document " + doc;
				LOGGER.error(message);
				throw new StatementException(message);
			}
			keyFields.add(keyField);
		}
		return keyFields;
	}

}
