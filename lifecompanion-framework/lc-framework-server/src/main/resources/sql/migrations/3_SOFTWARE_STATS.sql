CREATE TABLE software_stat (
	id VARCHAR(255) PRIMARY KEY,
	event VARCHAR(255),
	recorded_at TIMESTAMP,
	version VARCHAR(255),
	system_id VARCHAR(255),
	installation_id VARCHAR(255),
	push_status VARCHAR(255),
	push_error VARCHAR(4096)
);