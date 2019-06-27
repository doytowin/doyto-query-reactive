drop table user if exists;
create table user (id bigint generated by default as identity (start with 1), email varchar(255), mobile varchar(255), nickname varchar(255), password varchar(255), userLevel varchar(255), username varchar(255), memo varchar(255), valid boolean not null, primary key (id));

INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (1, 'f0rb', '17778888881', 'f0rb@163.com', '测试1', '123456', '高级', true);
INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (2, 'user2', '17778888882', 'test2@qq.com', '测试2', '123456', '普通', true);
INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, memo, valid) VALUES (3, 'user3', '17778888883', 'test3@qq.com', '测试3', '123456', '普通', 'memo', true);
INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (4, 'user4', '17778888884', 'test4@qq.com', '测试4', '123456', '普通', true);

create table user_detail (id bigint, address varchar(255), primary key (id));

drop table menu_01 if exists;
create table menu_01 (id integer generated by default as identity (start with 1), createTime timestamp, createUserId bigint, updateTime timestamp, updateUserId bigint, memo varchar(255), menuName varchar(255), parentId integer, valid boolean, primary key (id));
drop table menu_02 if exists;
create table menu_02 (id integer generated by default as identity (start with 1), createTime timestamp, createUserId bigint, updateTime timestamp, updateUserId bigint, memo varchar(255), menuName varchar(255), parentId integer, valid boolean, primary key (id));

INSERT INTO menu_01 (id, parentId, menuName, memo, valid) VALUES (1, 0, 'root', 'root menu', true);
INSERT INTO menu_01 (id, parentId, menuName, memo, valid) VALUES (2, 1, 'first', 'first menu', true);
INSERT INTO menu_02 (id, parentId, menuName, memo, valid) VALUES (1, 0, 'root', 'root menu', true);

create table ROLE(id bigint generated by default as identity (start with 1), ROLENAME VARCHAR(100) not null, ROLECODE VARCHAR(100) not null, VALID boolean DEFAULT TRUE, createTime timestamp, createUserId bigint, updateTime timestamp, updateUserId bigint);
INSERT INTO ROLE (ROLENAME, ROLECODE) VALUES ('测试', 'TEST');
INSERT INTO ROLE (ROLENAME, ROLECODE) VALUES ('高级', 'VIP');

create table t_user_and_role (id bigint generated by default as identity (start with 1), userId bigint, roleId int, createUserId bigint);
create unique index uniq_t_user_and_role on t_user_and_role (userId, roleId);

INSERT INTO t_user_and_role (USERID, ROLEID) VALUES (1, 1);
INSERT INTO t_user_and_role (USERID, ROLEID) VALUES (1, 2);
INSERT INTO t_user_and_role (USERID, ROLEID) VALUES (3, 1);
INSERT INTO t_user_and_role (USERID, ROLEID) VALUES (4, 1);
INSERT INTO t_user_and_role (USERID, ROLEID) VALUES (4, 2);
