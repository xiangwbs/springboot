DDL
CREATE TABLE xwbing(
  id NUMBER(4) DEFAULT 1,
  name VARCHAR2(20) NOT NULL,
  gender CHAR(1) DEFAULT 'M',
  birth DATE,
  salary NUMBER(6,2) DEFAULT 1,
  job VARCHAR2(30) DEFAULT 'M',
  deptno NUMBER(2) DEFAULT 1
)
DESC xwbing;
DROP TABLE xwbing;
truncate table xwbing；
RENAME employ_xwbing TO xwbing;
向表中添加新的字段,只能在当前表的末尾追加,可以同时追加多个列,只需要使用逗号隔开即可,与创建声明列的时候语法一样
ALTER TABLE XWBING ADD(
  HIREDATE DATE DEFAULT SYSDATE
)
ALTER TABLE `table`
ADD COLUMN `col` bigint(20) DEFAULT NULL COMMENT 'xxx',
ADD COLUMN `col` bit(1) DEFAULT NULL COMMENT 'xxx',
ADD COLUMN `col` varchar(32) DEFAULT NULL COMMENT 'xxx';


DML
插入数据
INSERT INTO XWBING (id,name,job,salary) VALUES(1,'JACK','CLERK',5000)
插入日期类型,建议使用TO-DATE函数
INSERT INTO XWBING(id,name,job,birth) VALUES(2,'ROSE','CLERK',TO_DATE('1992-08-02','YYYY-MM-DD'))
修改表中现有字段
可以修改字段的类型,长度,默认值,非空
ALTER TABLE XWBING MODIFY(
  job VARCHAR2(40) DEFAULT 'CLERK'
)
修改表中现有数据
UPDATE XWBING SET SALARY=50000 WHERE name='ROSE'   字符串里区分大小写,用''
删除表中数据
DELETE FROM EMP_XWBING WHERE name='ROSE'


DQL
SELECT语句
SELECT是用来查询数据的语句DQL
查询某张表中的所有字段的记录:
SELECT * FROM EMP_XWBING;
查看指定字段的值:
SELECT ename,sal,job FROM EMP_XWBING;

DQL必须包含的部分是SELECT子句与FROM子句
SELCET用来确定查询的字段,可以使用的有:表的字段,函数,表达式FROM子句来确定查询的表

SELECT中使用表达式
查看每个员工的年薪
SELECT ename,sal*12 FROM emp_xwbing;
