package by.bsu.fami.etl.server.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import by.bsu.fami.etl.server.dao.IRuleLogDao;

public class RulePanel extends Panel {

	private static final long serialVersionUID = 1L;

	public RulePanel(String id, IRuleLogDao service, String ruleType) {
		super(id);

		List<AbstractColumn> columns = new ArrayList<AbstractColumn>();
		columns.add(new PropertyColumn(new Model("Rule name"), "ruleName",
				"ruleName"));
		columns
				.add(new PropertyColumn(new Model("Event date"), "date", "date"));
		columns.add(new PropertyColumn(new Model("Job name"), "jobName",
				"jobName"));
		columns.add(new PropertyColumn(new Model("Metadata name"),
				"metadataName", "metadataName"));
		columns
				.add(new PropertyColumn(new Model("Status"), "status", "status"));
		columns.add(new PropertyColumn(new Model("Failure"), "failure"));

		add(new AjaxFallbackDefaultDataTable("table", columns,
				new SortableContactDataProvider(service, ruleType), 30));
	}

}
