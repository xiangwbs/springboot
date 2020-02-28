CREATE OR REPLACE VIEW V_EMP_10_XWBING AS SELECT empno id,ename,sal*12 salary ,deptno  FROM EMP_XWBING WHERE deptno=10 WITH READ ONLY;
CREATE OR REPLACE VIEW V_EMP_10_XWBING AS SELECT empno id,ename,sal*12 salary ,deptno  FROM EMP_XWBING WHERE deptno=10 WITH CHECK OPTION;
视图:
数据库对象之一
在sql语句中体现的角色与表相同.但视图是虚表,它只是对应一条SELECT语句查询的结果集,使用视图可以重用子查询,简化复杂查询
创建包含10号部门员工信息的视图?
CREATE VIEW V_EMP_10_XWBING AS SELECT empno,ename,sal,deptno  FROM EMP_XWBING WHERE deptno=10;
SELECT * FROM V_EMP_10_XWBING;
SELECT * FROM emp_xwbing;

视图对应的子查询字段可以用别名,若字段含有函数或表达式,必须用别名
CREATE OR REPLACE VIEW V_EMP_10_XWBING AS SELECT empno ID,ename,sal*12 salary ,deptno  FROM EMP_XWBING WHERE deptno=10;

对视图进行DML操作
对视图进行DML操作就是对视图数据来源的基础表进行的
只能对简单视图进行DML操作,复杂视图不允许使用DML操作
对简单视图进行DML操作时,也不不能违背基础表的约束条件
INSERT INTO V_EMP_10_XWBING(EMPNO,ENAME,SALARY,DEPTNO) VALUES(1001,'jack',5000,10);

当通过视图插入一条视图本身不可见的数据时,就是对基表的污染
插入和删除都会造成污染,但是删除不会.
INSERT INTO V_EMP_10_XWBING VALUES(1001,'javk',5000,20);
UPDATE v_emp_10_xwbing SET deptno=20;

对视图添加检查选项
WITH CHECK OPTION 
当视图添加了检查选项后,那么对视图进行插入或修改操作时,视图要求:
插入时:插入的数据视图必须对其可见
修改时:修改后视图必须对数据可见
CREATE OR REPLACE VIEW V_EMP_10_XWBING AS SELECT empno id,ename,sal salary ,deptno  FROM EMP_XWBING WHERE deptno=10 WITH CHECK OPTION;

为视图添加只读选项
WITH READ ONLY
当一个视图添加类只读选项后, 该视图不允许进行DML操作
CREATE OR REPLACE VIEW V_EMP_10_XWBING AS SELECT empno id,ename,sal salary ,deptno  FROM EMP_XWBING WHERE deptno=10 WITH READ ONLY ;

数据字典:user_objects  user_views  user_TABLEs
SELECT OBJECT_NAME FROM user_objects;
SELECT text FROM user_views;
SELECT TABLE_name FROM user_TABLEs;

复杂视图
查询语句含有函数,表达式,分组,去重多表关联查询,复杂视图不能进行DML
创建一个各部门工资情况的视图?
CREATE OR REPLACE view v_emp_deptno_xwbing AS SELECT d.deptno,d.dname,min(e.sal) min_sal,max(e.sal) max_sal,avg(e.sal) avg_sal,sum(e.sal)  sum_sal FROM emp_xwbing e,dept_xwbing d WHERE d.deptno=e.deptno group BY d.deptno,d.dname;
SELECT *FROM v_emp_deptno_xwbing;
SELECT e.ename,e.sal,e.deptno FROM emp_xwbing e,v_emp_deptno_xwbing v WHERE e.deptno=v.deptno AND e.sal>v.avg_sal;


删除视图
DROP VIEW V_name;
删除视图中的数据会将对应的基表数据删除,但是删除视图本身并不会对基表数据产生任何影响
DROP VIEW V_EMP_10_XWBING;

序列
序列也是数据库对象之一
序列是用来生成一系列数字的,序列通常为表的主键提供值使用
CREATE SEQUENCE seq_emp_xwbing start WITH 1 increment BY 1;

序列支持两个伪列
NEXTVAL:使序列生成下一个数字,新创建的序列则返回 START WITH 指定的数字.序列是不能回退的,所以通过NEXTVAL获取了下一个数字后就无法获取之前生成的数字了
CURRVAL:获取序列生成的最后一个数字,无论调用多少次都不会导致序列生成新的数字,但是新创建的序列至少调用一次NEXTVAL后才可以开始使用CURRVAL
SELECT seq_emp_xwbing.nextval FROM dual;
SELECT seq_emp_xwbing.currval FROM dual;

INSERT INTO emp_xwbing(empno,ename,job,sal) VALUES(seq_emp_xwbing.nextval,'jack','clerk',5000);
SELECT *FROM emp_xwbing;
desc emp_xwbing;
CREATE TABLE wwww(id varchar2);
INSERT INTO www(id) VALUES(SYS_GUID());
SELECT * FROM www;
UUID
字符串类型的主键值,32位不重复字符串
ORACLE提供了一个函数可以生成UUID
SELECT SYS_GUID() FROM DUAL;



删除序列
DROP SEQUENCE seq_emp_xwbing;


索引
数据库对象之一
一种提高查询效率的机制
CREATE INDEX idx_name ON name(col);

CREATE INDEX idx_emp ON emp(ename);
复合索引
CREATE INDEX idx_emp_job_sal ON emp(job,sal);
基于函数索引
CREATE INDEX emp_ename_upper_idx ON emp(UPPER(ename));

重建索引
ALTER INDEX IDX_EMP REBUILD;
删除索引
DROP INDEX IDX_EMP;



约束 CONSTRAINT
约束条件:
非空约束(Not Null),简称NN
唯一性约束(Unique),简称UK
主键约束(Primary Key),简称PK
外键约束(Foreign Key),简称FK
检查约束(Check),简称CK


非空约束:not null
建表时添加非空约束
CREATE TABLE employy_xwbing(eid number(6),name varchar2(30) not null, salary number(7,2),hiredate date constraint employy_xwbing_hiredate_nn not null);
修改表添加非空约束
alter TABLE employy_xwbing modify(eid number(6) not null);
修改表取消非空约束
alter TABLE employy_xwbing modify(eid number(6) null);

唯一性约束: UNIQUE
当某个字段使用了唯一性约束后,该字段的值或者字段的组合在表中是不允许有重复值的,但是null除外
CREATE TABLE xxx_xwbing (
eid NUMBER(6),
name VARCHAR2(30),
email VARCHAR2(50),
salary NUMBER(7, 2),
hiredate DATE,
CONSTRAINT empl_xwbing_email_uk UNIQUE(email)
);

INSERT INTO empl_xwbing(eid,name,email) VALUES(21,'21','4');

建表之后添加唯一性约束:
alter TABLE empl_xwbing add constraint empl_xwbing_name_uk unique(name);



主键约束 Primary Key
主键可以用来在表中唯一的确定一行数据.一个表上只允许建立一个主键.


    CREATE TABLE emploo_xwbing (
    eid NUMBER(6) primary key,
    name VARCHAR2(30),
    email VARCHAR2(50),
    salary NUMBER(7, 2),
    hiredate DATE
    );
    
INSERT INTO emploo_xwbing(eid,name) VALUES(2,'jack');

    
外键约束:一般不用

检查约束(Check)
检查(Check)约束条件用来强制在字段上的每个值都要满足Check中定义的条件.当定义了Check约束的列新增或修改数据时,数据必须符合Check约束中定义的条件.

员工薪水必须大于2000?
alter TABLE emploo_xwbing add constraint emploo_xwbing_slary_ck check(salary>2000);
正常插入数据?
INSERT INTO emploo_xwbing(eid,name,salary,email) VALUES(11,'jack',3000,'www');
试图插入小于2000的?
INSERT INTO emploo_xwbing(eid,name,salary,email) VALUES(13,'jene',1111,'www');
SELECT * FROM emploo_xwbing;
























































