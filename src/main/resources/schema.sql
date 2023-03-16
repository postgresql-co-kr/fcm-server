/** h2db, mysql **/
CREATE TABLE IF NOT EXISTS fcm_msg (
  msg_key VARCHAR(255) NOT NULL PRIMARY KEY,
  msg_seq BIGINT NOT NULL AUTO_INCREMENT,
  app_name VARCHAR(50) NOT NULL,
  fcm_token  VARCHAR(255) NOT NULL,
  title  VARCHAR(250) NOT NULL,
  body  VARCHAR(4000) NOT NULL,
  image VARCHAR(2048) NULL,
  send_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  send_time TIMESTAMP NULL,
  success_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX fcm_msg_ix_01 ON fcm_msg(msg_seq, app_name, send_yn);
CREATE INDEX fcm_msg_ix_02 ON fcm_msg(created_time desc);

/* postgresql
CREATE TABLE IF NOT EXISTS fcm_msg (
  msg_key VARCHAR(255) NOT NULL PRIMARY KEY,
  msg_seq SERIAL,
  app_name VARCHAR(50) NOT NULL,
  fcm_token  VARCHAR(255) NOT NULL,
  title  VARCHAR(250) NOT NULL,
  body  VARCHAR(4000) NOT NULL,
  image VARCHAR(2048) NULL,
  send_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  send_time TIMESTAMP NULL,
  success_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX fcm_msg_ix_01 ON fcm_msg(msg_seq, app_name, send_yn);
CREATE INDEX fcm_msg_ix_02 ON fcm_msg(created_time desc, app_name, send_yn);
*/

