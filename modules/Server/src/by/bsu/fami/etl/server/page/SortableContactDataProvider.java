package by.bsu.fami.etl.server.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import by.bsu.fami.etl.server.bean.RuleLogItem;
import by.bsu.fami.etl.server.dao.IRuleLogDao;

public class SortableContactDataProvider extends SortableDataProvider {

	private static final long serialVersionUID = 1L;

	protected final static Log LOGGER = LogFactory
			.getLog(SortableContactDataProvider.class);

	protected IRuleLogDao service;

	protected String ruleType;

	public SortableContactDataProvider(IRuleLogDao service, String ruleType) {
		this.service = service;
		this.ruleType = ruleType;
		setSort("ruleName", true);
	}

	public Iterator<?> iterator(int first, int count) {
		SortParam sp = getSort();
		if (sp == null) {
			sp = new SortParam("ruleName", true);
		}
		if ("date".equals(sp.getProperty())) {
			sp = new SortParam("event_date", sp.isAscending());
		}
		if (service != null) {
			List<RuleLogItem> rules = null;
			if (StringUtils.equals(IRuleLogDao.BASE_RULE_TYPE, ruleType)) {
				rules = service.baseRules(first, count, sp.getProperty(), sp
						.isAscending());
			} else {
				if (StringUtils.equals(IRuleLogDao.CHECK_RULE_TYPE, ruleType)) {
					rules = service.checkRules(first, count, sp.getProperty(),
							sp.isAscending());
				}
			}
			if (rules != null) {
				return rules.iterator();
			}
		}
		return new ArrayList<RuleLogItem>().iterator();
	}

	public int size() {
		if (service != null) {
			if (IRuleLogDao.BASE_RULE_TYPE.equals(ruleType)) {
				return service.baseRulesSize();
			} else {
				if (IRuleLogDao.CHECK_RULE_TYPE.equals(ruleType)) {
					return service.checkRulesSize();
				}
			}
		}
		return 0;
	}

	public IModel model(Object object) {
		if (object instanceof RuleLogItem) {
			return new DetachableRuleModel((RuleLogItem) object);
		}
		return null;
	}

}
