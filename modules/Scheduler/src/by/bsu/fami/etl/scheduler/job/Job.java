package by.bsu.fami.etl.scheduler.job;

public class Job {

	protected String name;

	protected String checker;

	protected String rule;

	public Job() {
	}

	public Job(String name) {
		this.name = name;
	}

	public Job(String name, String checker, String rule) {
		this.name = name;
		this.checker = checker;
		this.rule = rule;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

}
