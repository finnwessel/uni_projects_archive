# README #

### Description ###

This services handles requests, offers, templates and categories.



- Manage categories

- Create templates linked to a category

- Create requests validated through given templates
- Create offers



For all types certain crud operations and filters are available. For example get all requests by creator or get categories by type (see swagger-ui for more information, `http:/localhost:<port>/swagger-ui` in development environment).  



### Configuration

#### General Configuration ####

```
application.properties

springdoc.api-docs.path=/rest-api-docs
springdoc.swagger-ui.path=/swagger-ui

jwt.publicKeyPath=publicKey.key

spring.data.mongodb.auto-index-creation=true

spring.jackson.mapper.ACCEPT_CASE_INSENSITIVE_ENUMS = true

server.port=8082

spring.profiles.active=prod
```

#### Production Configuration ####

```
application-prod.properties

spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.authentication-database=admin
spring.data.mongodb.username=root
spring.data.mongodb.password=password
spring.data.mongodb.database=data
```

#### Test configuration ####

```
application-test.properties

spring.data.mongodb.port=0
spring.data.mongodb.auto-index-creation=true
spring.mongodb.embedded.version=3.4.5
```



### How to run tests ###

Run the following command: `./mvnw clean test`



### Dependencies ###

#### Spring Boot Starter Data MongoDB
`org.springframework.boot:spring-boot-starter-data-mongodb:2.6.6`
#### License
`Apache 2.0`

#### Spring Boot Starter Security
`org.springframework.boot:spring-boot-starter-security:2.6.6`
#### License
`Apache 2.0`

#### Spring Boot Starter Validation
`org.springframework.boot:spring-boot-starter-validation:2.6.6`
#### License
`Apache 2.0`

#### Spring Boot Starter Web
`org.springframework.boot:spring-boot-starter-web:2.6.6`
#### License
`Apache 2.0`

#### Spring Boot Devtools
`org.springframework.boot:spring-boot-devtools:2.6.6`
#### License
`Apache 2.0`

#### Project Lombok
`org.projectlombok:lombok:1.18.22`
#### License
`MIT`

#### Spring Boot Starter Test
`org.springframework.boot:spring-boot-starter-test:2.6.6`
#### License
`Apache 2.0`

#### Flapdoodle Embedded MongoDB
`de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.0.0`
#### License
`Apache 2.0`

#### Spring REST Docs MockMvc
`org.springframework.restdocs:spring-restdocs-mockmvc:2.0.6.RELEASE`
#### License
`Apache 2.0`

#### Spring Security Test
`org.springframework.security:spring-security-test:5.6.2`
#### License
`Apache 2.0`

#### JJWT
`io.jsonwebtoken:jjwt:0.9.1`
#### License
`Apache 2.0`

#### MapStruct Core
`org.mapstruct:mapstruct:1.4.2.Final`
#### License
`Apache 2.0`

#### Springdoc OpenAPI UI
`org.springdoc:springdoc-openapi-ui:1.5.10`
#### License
`Apache 2.0`

#### Jackson Datatype JSR310
`com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2`
#### License
`Apache 2.0`

#### Mockito Core
`org.mockito:mockito-core:4.4.0`
#### License
`MIT`