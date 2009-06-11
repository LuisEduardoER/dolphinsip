package by.bsu.fami.etl.server.page;

import org.apache.wicket.model.LoadableDetachableModel;

import by.bsu.fami.etl.server.bean.RuleLogItem;

public class DetachableRuleModel extends LoadableDetachableModel {

	private static final long serialVersionUID = 1L;

	protected RuleLogItem rule;

	public DetachableRuleModel(RuleLogItem rule) {
		this.rule = rule;
	}

	protected Object load() {
		return rule;
	}

}
