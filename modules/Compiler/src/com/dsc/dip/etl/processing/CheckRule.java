package com.dsc.dip.etl.processing;

import org.apache.log4j.Logger;

import com.dsc.dip.etl.processing.component.checker.Checker;


public abstract class CheckRule extends BaseRule {

	private final static Logger LOGGER = Logger.getLogger(CheckRule.class);

	protected Checker checker;

	public boolean check() {
		try {
			execute();
		} catch (RuleException e) {
			LOGGER.error(e);
			return false;
		}
		if (checker != null) {
			return checker.check();
		}
		return false;
	}

	public Checker getChecker() {
		return checker;
	}

	public void setChecker(Checker checker) {
		this.checker = checker;
	}
}
