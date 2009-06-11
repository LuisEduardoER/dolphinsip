package by.bsu.fami.etl.server.bean;

import static by.bsu.fami.etl.server.bean.DateUtils.format;
import java.util.Date;

import org.apache.wicket.IClusterable;

public class RuleLogItem implements IClusterable {

	private static final long serialVersionUID = 1L;

	protected String ruleName;

	protected Date date;

	protected String jobName;

	protected String metadataName;

	protected String status;

	protected String type;

	protected String failure;

	public RuleLogItem() {
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getDate() {
		return format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getMetadataName() {
		return metadataName;
	}

	public void setMetadataName(String metadataName) {
		this.metadataName = metadataName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}
}
