CREATE TABLE application_plugin (
	id VARCHAR(255) PRIMARY KEY,
	author VARCHAR(255),
	name VARCHAR(255),
	description VARCHAR(4096),
	application_id VARCHAR(255) REFERENCES application(id)
);

CREATE TABLE application_plugin_update (
	id VARCHAR(255) PRIMARY KEY,
	version VARCHAR(255),
	version_major INT,
	version_minor INT,
	version_patch INT,
	update_date DATE,
	visibility VARCHAR(255),
	file_size BIGINT,
	file_storage_id VARCHAR(255),
	file_name VARCHAR(255),
	file_hash VARCHAR(255),
	application_plugin_id VARCHAR(255) REFERENCES application_plugin(id)
);