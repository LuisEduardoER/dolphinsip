connect 'jdbc:derby:scheduler_db;create=true';
CREATE TABLE RuleLog (
	ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	ruleName VARCHAR(20) NOT NULL,
	event_date DATE,
	jobName VARCHAR(20),
	metadataName VARCHAR(20),
	status VARCHAR(10),
	rule_type VARCHAR(10),
	failure VARCHAR(100)
);