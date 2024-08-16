# Spring Security - JWT

## Project Information

* Spring Boot : 3.2.8
* jdk : 21
* Plugins
  * Spring Web
  * Lombok
  * Spring Security
  * Spring Data JPA
  * MySQL Driver
  * jwt : 0.12.3

## DB(MySQL)

```bash
docker run --name mysql -e MYSQL_ROOT_PASSWORD=root -d -p 3306:3306 mysql

docker exec -it mysql bash
mysql -u root -p // 이후 root계정 패스워드 입력

create database security;
```