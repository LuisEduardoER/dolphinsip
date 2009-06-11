package by.bsu.fami.etl.server.dao;

import java.util.List;

import com.google.inject.ImplementedBy;

import by.bsu.fami.etl.server.bean.RuleLogItem;

@ImplementedBy(RuleLogDao.class)
public interface IRuleLogDao {

	public final static String BASE_RULE_TYPE = "BaseRule";

	public final static String CHECK_RULE_TYPE = "CheckRule";

	public int baseRulesSize();

	public List<RuleLogItem> baseRules(int first, int count,
			String sortProperty, boolean sortAsc);

	public int checkRulesSize();

	public List<RuleLogItem> checkRules(int first, int count,
			String sortProperty, boolean sortAsc);

}
