# 数据库初始化 #
例子中使用的相关表的初始化语句如下：
```
CREATE TABLE MY_TABLE (
ID VARCHAR(10) PRIMARY KEY,
NAME VARCHAR(50),
AGE INT,
BIRTH DATE,
MEMO VARCHAR(100)
);
INSERT INTO MY_TABLE VALUES ('TEST01', '测试01', 22, '1990-5-1', '第一条记录');


CREATE TABLE T_SEX (
SEXID INT PRIMARY KEY,
SEXNAME VARCHAR(5)
);
INSERT INTO T_SEX VALUES (1, '男');
INSERT INTO T_SEX VALUES (2, '女');

CREATE TABLE T_DEPT (
DEPTID VARCHAR(10) PRIMARY KEY,
DEPTNAME VARCHAR(50)
);
INSERT INTO T_DEPT VALUES ('001', '信息学院');
INSERT INTO T_DEPT VALUES ('002', '管理学院');
INSERT INTO T_DEPT VALUES ('003', '交通学院');

CREATE TABLE T_STUDENT (
ID INT PRIMARY KEY,
NAME VARCHAR(50),
SPELLNAME VARCHAR(50),
SEX INT,
DEPT VARCHAR(10),
EMAIL VARCHAR(100),
AGE INT,
GRADE INT,
COMEDATE DATE,
MEMO VARCHAR(200)
);
```
如果你没有可用的数据库，可以下载h2，官网地址为http://www.h2database.com/，这是个纯java写的迷你型数据库。<br>
注：需要jdk1.5以上运行。<br>
<br>
<h2>配置JDBC</h2>
micromagic_config.properties文件中的数据库连接可按如下配置，如果不是使用默认的库，或使用的是其他数据库，请自行修改相关配置。<br>
<pre><code>dataSource.autoCommit=false<br>
dataSource.description=h2<br>
dataSource.driverClass=org.h2.Driver<br>
dataSource.maxCount=1<br>
dataSource.url=jdbc:h2:~/test<br>
dataSource.user=sa<br>
dataSource.password=sa<br>
</code></pre>
h2的jdbc的jar包就是他的运行包，bin目录下的h2-<code>[版本号]</code>.jar文件。