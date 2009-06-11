package by.bsu.fami.etl.scheduler.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Metadata {

	private static final String PREFIX_METADATA_NAME = "metadata.name";

	private static final String PREFIX_JOB_NAME = "job.name";

	private static final String PREFIX_JOB_CHECKER = "job.checker";

	private static final String PREFIX_JOB_RULE = "job.rule";

	private static final String PREFIX_DEPENDENCE = "dependence";

	protected String name;

	protected Map<String, Job> jobs = new HashMap<String, Job>();

	protected List<String> dependence = new ArrayList<String>();

	public Metadata() {
	}

	public void load(InputStream metadataFile) throws IOException {
		Properties metadata = new Properties();
		metadata.load(metadataFile);
		for (Map.Entry<Object, Object> property : metadata.entrySet()) {
			String key = property.getKey().toString();
			String value = property.getValue().toString();
			if (PREFIX_METADATA_NAME.equals(key)) {
				name = value;
			} else {
				if (PREFIX_JOB_NAME.equals(key)) {
					Job job = jobs.get(value);
					if (job == null) {
						job = new Job(value);
						jobs.put(value, job);
					}
				} else {
					if (key.startsWith(PREFIX_JOB_CHECKER)) {
						String jobName = key.substring(PREFIX_JOB_CHECKER
								.length() + 1);
						Job job = jobs.get(jobName);
						if (job == null) {
							job = new Job(jobName);
							jobs.put(jobName, job);
						}
						job.setChecker(value);
					} else {
						if (key.startsWith(PREFIX_JOB_RULE)) {
							String jobName = key.substring(PREFIX_JOB_RULE
									.length() + 1);
							Job job = jobs.get(jobName);
							if (job == null) {
								job = new Job(jobName);
								jobs.put(jobName, job);
							}
							job.setRule(value);
						} else {
							if (PREFIX_DEPENDENCE.equals(key)) {
								dependence.add(value);
							}
						}
					}
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Job> getJobs() {
		return jobs;
	}

	public void setJobs(Map<String, Job> jobs) {
		this.jobs = jobs;
	}

	public List<String> getDependence() {
		return dependence;
	}

	public void setDependence(List<String> dependence) {
		this.dependence = dependence;
	}

}
