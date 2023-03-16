/** h2db **/
CREATE TABLE IF NOT EXISTS fcm_msg (
  msg_key VARCHAR(255) NOT NULL,
  msg_seq bigserial,
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

/* postgresql
CREATE TABLE IF NOT EXISTS fcm_msg (
  msg_key VARCHAR(255) NOT NULL,
  msg_seq bigserial,
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
) PARTITION BY RANGE (created_at);


CREATE OR REPLACE PROCEDURE create_fcm_msg_partitions(start_date DATE, end_date DATE) LANGUAGE plpgsql AS $$
DECLARE
    partition_name TEXT;
    partition_date DATE := DATE_TRUNC('MONTH', start_date)::DATE;
BEGIN
    WHILE partition_date < end_date LOOP
        partition_name := 'fcm_msg_partition_' || TO_CHAR(partition_date, 'YYYYMM');
        EXECUTE 'CREATE TABLE IF NOT EXISTS ' || partition_name ||
            ' PARTITION OF fcm_msg FOR VALUES FROM (''' || TO_CHAR(partition_date, 'YYYY-MM-01 00:00:00') ||
            ''') TO (''' || TO_CHAR(partition_date + INTERVAL '1 MONTH', 'YYYY-MM-01 00:00:00') || ''')';
        partition_date := DATE_TRUNC('MONTH', partition_date + INTERVAL '1 MONTH')::DATE;
    END LOOP;
END;
$$;

CALL create_fcm_msg_partitions('2023-03-01'::DATE, '2033-12-31'::DATE);
CREATE INDEX ON fcm_msg(msg_seq, app_name, push_yn);
CREATE INDEX ON fcm_msg(created_at desc, app_name, push_yn);

*/

