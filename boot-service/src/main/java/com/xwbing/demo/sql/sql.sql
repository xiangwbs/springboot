查询是否开启慢查询和慢查询日志存放位置
show variables like 'slow_query%';
查询超过多少秒才记录
show variables like 'long_query_time';

mysqld --remove 卸载mysql服务
sc delete mysql 彻底删除mysql服务
mysqld --install [mysql] 安装mysql服务，默认名为mysql
mysqld --install [mysql] --defaults-file="my.ini文件" 当不止一个my.ini文件时指定文件路径
初始化data目录
mysqld --initialize-insecure [--console] 没有密码
mysqld --initialize [--console] 有密码，密码在data/err文件里 console密码打印控制台
net start mysql
net stop mysql


mysql -v
连接mysql
mysql -u用户名 -p密码
mysql [-hIP地址] [-P端口号] -u用户名 -p密码
修改密码
mysqladmin [-hIP地址] [-P端口号] -u用户名 -p旧密码 password 新密码
导出数据库
mysqldump [-hIP地址] [-P端口号] -u用户名 -p密码 数据库名 > sql文件名

status;
show databases;
create database <数据库名>;
drop database <数据库名>;
use <数据库名>;
show tables;
describe tableName; 显示表结构
source F:/file.sql;导入sql文件

exit(回车)