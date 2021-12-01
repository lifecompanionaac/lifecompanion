CREATE TABLE database_migration (
	id VARCHAR(255) PRIMARY KEY,
	script_name VARCHAR(512),
	script_date TIMESTAMP
);

CREATE TABLE app_user (
	id UUID PRIMARY KEY, 
	login VARCHAR(255),
	password VARCHAR(255),
	role VARCHAR(255)
);

CREATE TABLE application(
	id VARCHAR(255) PRIMARY KEY
);

CREATE TABLE application_installer (
	id VARCHAR(255) PRIMARY KEY,
	version VARCHAR(255),
	version_major INT,
	version_minor INT,
	version_patch INT,
	system VARCHAR(255),
	system_modifier VARCHAR(255),
	update_date DATE,
	visibility VARCHAR(255),
	file_size BIGINT,
	file_storage_id VARCHAR(2048),
	file_name_root VARCHAR(255),
	file_name_extension VARCHAR(255),
	file_hash VARCHAR(255),
	application_id VARCHAR(255) REFERENCES application(id)
);

CREATE TABLE application_update (
	id VARCHAR(255) PRIMARY KEY,
	version VARCHAR(255),
	version_major INT,
	version_minor INT,
	version_patch INT,
	update_date DATE,
	visibility VARCHAR(255),
	application_id VARCHAR(255) REFERENCES application(id),
	description VARCHAR(4096)
);

CREATE TABLE application_update_file (
	id VARCHAR(255) PRIMARY KEY,
	target_path VARCHAR(2048),
	file_size BIGINT,
	file_storage_id VARCHAR(2048),
	file_hash VARCHAR(255),
	file_state VARCHAR(255),
	target_type VARCHAR(255),
	system VARCHAR(255),
    system_modifier VARCHAR(255),
    to_unzip BOOLEAN,
	application_update_id VARCHAR(255) REFERENCES application_update(id)
);

CREATE TABLE application_launcher_update (
	id VARCHAR(255) PRIMARY KEY,
	version VARCHAR(255),
	version_major INT,
	version_minor INT,
	version_patch INT,
	system VARCHAR(255),
	system_modifier VARCHAR(255),
	update_date DATE,
	visibility VARCHAR(255),
	file_size BIGINT,
	file_storage_id VARCHAR(2048),
	file_path VARCHAR(512),
	file_hash VARCHAR(255),
	application_id VARCHAR(255) REFERENCES application(id)
);
