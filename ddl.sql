/*
PostgreSQL에서는 psql 쉘에서 다양한 유용한 명령어를 사용할 수 있습니다. 이 중에서 몇 가지 유용한 명령어를 소개합니다:

\l: 현재 PostgreSQL 서버에 있는 모든 데이터베이스의 목록을 조회합니다.
\c [database_name]: 다른 데이터베이스에 연결합니다.
\dt: 현재 데이터베이스에 있는 모든 테이블의 목록을 조회합니다.
\d [table_name]: 지정한 테이블의 구조를 조회합니다.
\du: PostgreSQL 서버에 등록된 모든 사용자의 목록을 조회합니다.
\x: 출력 결과를 확장된 형식으로 표시합니다.
\timing: SQL문 실행 시간을 표시합니다.
\! [command]: 쉘 명령어를 실행합니다.
\q: psql 쉘을 종료합니다.
\?: 쉘명령어 도움말
*/

/* postgresql */

CREATE SCHEMA IF NOT EXISTS fcm_owner;  -- change

CREATE USER fcm_owner WITH PASSWORD 'password'; -- change

CREATE DATABASE fcm_db
  WITH OWNER = fcm_owner
       ENCODING = 'UTF8'
       LC_COLLATE = 'C'
       LC_CTYPE = 'C'
       TEMPLATE = template0;


CREATE USER fcm_app WITH PASSWORD 'password'; -- change
GRANT CONNECT ON DATABASE fcm_db TO fcm_owner;
GRANT CONNECT ON DATABASE fcm_db TO fcm_app;
GRANT USAGE ON SCHEMA fcm_owner TO fcm_app;

CREATE TABLE IF NOT EXISTS fcm_owner.fcm_msg (
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


CREATE OR REPLACE PROCEDURE fcm_owner.create_fcm_msg_partitions(start_date DATE, end_date DATE) LANGUAGE plpgsql AS $$
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

CALL fcm_owner.create_fcm_msg_partitions('2023-03-01'::DATE, '2033-12-31'::DATE);
CREATE INDEX ON fcm_owner.fcm_msg(msg_seq, app_name, push_yn);
CREATE INDEX ON fcm_owner.fcm_msg(created_at desc, app_name, push_yn);



CREATE TABLE IF NOT EXISTS fcm_owner.fcm_log (
  log_key VARCHAR(255) NOT NULL,
  log_seq bigserial,
  app_name VARCHAR(50) NOT NULL,
  device_type VARCHAR(50) NULL,
  fcm_token VARCHAR(255) NOT NULL,
  success_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fcm_log_pk  PRIMARY KEY (log_key, created_at)
) PARTITION BY RANGE (created_at);


CREATE OR REPLACE PROCEDURE fcm_owner.create_fcm_log_partitions(start_date DATE, end_date DATE) LANGUAGE plpgsql AS $$
DECLARE
    partition_name TEXT;
    partition_date DATE := DATE_TRUNC('MONTH', start_date)::DATE;
BEGIN
    WHILE partition_date < end_date LOOP
        partition_name := 'fcm_log_partition_' || TO_CHAR(partition_date, 'YYYYMM');
        EXECUTE 'CREATE TABLE IF NOT EXISTS ' || partition_name ||
            ' PARTITION OF fcm_log FOR VALUES FROM (''' || TO_CHAR(partition_date, 'YYYY-MM-01 00:00:00') ||
            ''') TO (''' || TO_CHAR(partition_date + INTERVAL '1 MONTH', 'YYYY-MM-01 00:00:00') || ''')';
        partition_date := DATE_TRUNC('MONTH', partition_date + INTERVAL '1 MONTH')::DATE;
    END LOOP;
END;
$$;

CALL fcm_owner.create_fcm_log_partitions('2023-03-01'::DATE, '2033-12-31'::DATE);
CREATE INDEX ON fcm_owner.fcm_log(fcm_token);
CREATE INDEX ON fcm_owner.fcm_log(created_at desc, app_name, success_yn);

CREATE TABLE IF NOT EXISTS fcm_owner.fcm_set (
  app_name VARCHAR(50) NOT NULL,
  key_path VARCHAR(4000) NOT NULL,
  connection_timeout INTEGER DEFAULT 3 NOT NULL,
  read_timeout INTEGER DEFAULT 5 NOT NULL,
  db_log_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  db_push_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
  db_minus_time VARCHAR(10) DEFAULT '5m' NOT NULL,
  update_id VARCHAR(255) NOT NULL,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  created_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fcm_set_pk  PRIMARY KEY (app_name)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA fcm_owner TO fcm_app;