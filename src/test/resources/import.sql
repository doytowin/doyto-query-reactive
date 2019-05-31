INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (1, 'f0rb', '17778888881', 'f0rb@163.com', '测试1', '123456', '高级', true);
INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (2, 'user2', '17778888882', 'test2@163.com', '测试2', '123456', '普通', true);
INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (3, 'user3', '17778888883', 'test3@163.com', '测试3', '123456', '普通', true);
INSERT INTO user (id, username, mobile, email, nickname, password, userLevel, valid) VALUES (4, 'user4', '17778888884', 'test4@163.com', '测试4', '123456', '普通', true);


create table menu_01 (id integer generated by default as identity (start with 1), createTime timestamp, createUserId bigint, updateTime timestamp, updateUserId bigint, memo varchar(255), menuName varchar(255), parentId integer, valid boolean, primary key (id))
create table menu_02 (id integer generated by default as identity (start with 1), createTime timestamp, createUserId bigint, updateTime timestamp, updateUserId bigint, memo varchar(255), menuName varchar(255), parentId integer, valid boolean, primary key (id))

INSERT INTO menu_01 (id, parentId, menuName, memo, valid) VALUES (1, 0, 'root', 'root menu', true);
INSERT INTO menu_01 (id, parentId, menuName, memo, valid) VALUES (2, 1, 'first', 'first menu', true);
INSERT INTO menu_02 (id, parentId, menuName, memo, valid) VALUES (1, 0, 'root', 'root menu', true);