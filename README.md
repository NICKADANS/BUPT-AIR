# BUPT-AIR
<b>bupt-airsystem-backend</b><br>
* 第一次pull到本地后，在src/main/下创建一个resources的文件夹<br>
在其中新建一个文件，文件名为application.properties<br>
该文件的内容如下
```
spring.datasource.url=jdbc:mysql://localhost:3306/airsystem?serverTimezone=UTC
spring.datasource.username=你的mysql用户名
spring.datasource.password=你的mysql密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```