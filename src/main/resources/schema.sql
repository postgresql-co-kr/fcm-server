


/** h2db **/
CREATE TABLE IF NOT EXISTS fcm_msg (
  msg_key VARCHAR(255) NOT NULL,
  msg_seq BIGINT NOT NULL AUTO_INCREMENT,
  app_name VARCHAR(50) NOT NULL,
  device_type VARCHAR(50) NULL,
  fcm_token VARCHAR(255) NOT NULL,
  title  VARCHAR(250) NOT NULL,
  body  VARCHAR(4000) NOT NULL,
  image VARCHAR(2048) NULL,
  push_yn  VARCHAR(1) DEFAULT 'N' NOT NULL,
  push_time  TIMESTAMP NULL,
  success_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fcm_msg_pk  PRIMARY KEY (msg_key, created_at)
);

CREATE INDEX fcm_msg_ix_01 ON fcm_msg(msg_seq, app_name, created_at);
CREATE INDEX fcm_msg_ix_02 ON fcm_msg(created_at desc, app_name, push_yn);

INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('afdads1', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('fassafa1', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('fadssadfa', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('asdfads1', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('afdasfdasads1', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('afdsfeddads1', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('sdfaee', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('adsfas3d', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('fads3d', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('sdfa', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');
INSERT INTO fcm_msg(msg_key, app_name, fcm_token, title, body) values('afda3dsff', 'ecobridgeapp', 'token-asdfad', 'title-sdfaaf', 'body-sdfads');

CREATE TABLE IF NOT EXISTS fcm_log (
  log_key VARCHAR(255) NOT NULL,
  log_seq BIGINT NOT NULL AUTO_INCREMENT,
  app_name VARCHAR(50) NOT NULL,
  device_type VARCHAR(50) NULL,
  fcm_token VARCHAR(255) NOT NULL,
  success_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fcm_log_pk  PRIMARY KEY (log_key, created_at)
);


CREATE INDEX fcm_log_ix_01 ON fcm_log(fcm_token);
CREATE INDEX fcm_log_ix_02 ON fcm_log(created_at desc, app_name, success_yn);

CREATE TABLE IF NOT EXISTS fcm_set (
  app_name VARCHAR(50) NOT NULL,
  key_path VARCHAR(4000) NOT NULL,
  connection_timeout INTEGER DEFAULT 5 NOT NULL,
  read_timeout INTEGER DEFAULT 10 NOT NULL,
  db_log_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  db_push_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  db_minus_time VARCHAR(10) DEFAULT '5m' NOT NULL,
  update_id VARCHAR(255) NOT NULL,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  created_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fcm_set_pk  PRIMARY KEY (app_name)
);

CREATE TABLE IF NOT EXISTS fcm_set (
  app_name VARCHAR(50) NOT NULL,
  key_path VARCHAR(4000) NOT NULL,
  connection_timeout INTEGER DEFAULT 3 NOT NULL,
  read_timeout INTEGER DEFAULT 5 NOT NULL,
  db_log_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  db_push_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  db_minus_time VARCHAR(1) DEFAULT 'N' NOT NULL,
  update_id VARCHAR(255) NOT NULL,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  created_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fcm_set_pk  PRIMARY KEY (app_name)
);


CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE ,
  email VARCHAR(100) NULL,
  password VARCHAR(100) NOT NULL,
  last_login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT users_pk  PRIMARY KEY (id)
);


CREATE TABLE user_roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  role_name VARCHAR(20) NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT user_roles_pk  PRIMARY KEY (id)
);