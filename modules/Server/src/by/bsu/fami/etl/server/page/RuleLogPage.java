package by.bsu.fami.etl.server.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import by.bsu.fami.etl.server.dao.IRuleLogDao;

public class RuleLogPage extends WebPage {

	@Inject
	protected IRuleLogDao service;

	public RuleLogPage() {
		List<AbstractTab> tabs = new ArrayList<AbstractTab>();
		tabs.add(new AbstractTab(new Model("Base rule")) {

			private static final long serialVersionUID = 1L;

			public Panel getPanel(String panelId) {
				return new RulePanel(panelId, service,
						IRuleLogDao.BASE_RULE_TYPE);
			}
		});
		tabs.add(new AbstractTab(new Model("Check rule")) {

			private static final long serialVersionUID = 1L;

			public Panel getPanel(String panelId) {
				return new RulePanel(panelId, service,
						IRuleLogDao.CHECK_RULE_TYPE);
			}
		});
		add(new AjaxTabbedPanel("tabs", tabs));
	}
}
