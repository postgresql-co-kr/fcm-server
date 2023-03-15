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

CREATE USER fcm_owner WITH PASSWORD 'password'; -- change

CREATE DATABASE fcm_db
  WITH OWNER = fcm_owner
       ENCODING = 'UTF8'
       LC_COLLATE = 'C'
       LC_CTYPE = 'C'
       TEMPLATE = template0;
      
SELECT * FROM pg_collation
where collname like '%utf%';


CREATE USER fcm_app WITH PASSWORD 'password'; -- change
GRANT CONNECT ON DATABASE fcm_db TO fcm_owner;
GRANT CONNECT ON DATABASE fcm_db TO fcm_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO fcm_app;






