# README #

### Description ###

This service provides authentication by providing jwt tokens.
In basic configuration user data are stored in the mongo database.
If ldap is enabled, user information are stored only in ldap, but refresh token ids are stored in the mongo database.


### Configuration ###

```
application.properties

jwt.issuer = TOKEN_ISSUER
jwt.accessTokenExpirationInMin = 10
jwt.refreshTokenExpirationInMin = 1440

# Choose between (prod-ldap, ldap) or (prod-basic, basic)
spring.profiles.active=prod-ldap,ldap
```

#### Database configuration ####

```
application-prod-basic.properties

spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.authentication-database=admin
spring.data.mongodb.username=username
spring.data.mongodb.password=password
spring.data.mongodb.database=authService
```

#### Ldap configuration ####

```
application-prod-ldap.properties

spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.authentication-database=admin
spring.data.mongodb.username=username
spring.data.mongodb.password=password
spring.data.mongodb.database=authService

spring.ldap.urls=ldap://localhost:1389
spring.ldap.base=dc=example,dc=org
```

### How to run tests ###
Run the following command:
`./mvnw test`

### Deployment instructions ###

TODO

### Dependencies ###

#### Spring Boot Configuration Processor
`org.springframework.boot:spring-boot-configuration-processor`
##### License
`Apache 2.0`

#### Spring Boot Starter Data LDAP
`org.springframework.boot:spring-boot-starter-data-ldap`
##### License
`Apache 2.0`

#### Spring Boot Starter Data LDAP
`org.springframework.boot:spring-boot-starter-data-mongodb:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter OAuth2 Client
`org.springframework.boot:spring-boot-starter-oauth2-client:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter OAuth2 Client
`org.springframework.boot:spring-boot-starter-oauth2-client:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter Security
`org.springframework.boot:spring-boot-starter-security:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter Validation
`org.springframework.boot:spring-boot-starter-validation:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter Validation
`org.springframework.boot:spring-boot-starter-validation:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter Web
`org.springframework.boot:spring-boot-starter-web:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot DevTools
`org.springframework.boot:spring-boot-devtools:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Starter Test
`org.springframework.boot:spring-boot-starter-test:2.6.6`
##### License
`Apache 2.0`

#### Spring Boot Maven Plugin
`org.springframework.boot:spring-boot-maven-plugin:2.6.6`
##### License
`Apache 2.0`

---

#### Spring REST Docs MockMvc
`org.springframework.restdocs:spring-restdocs-mockmvc:2.0.6.RELEASE`
##### License
`Apache 2.0`

#### Spring Security Test
`org.springframework.security:spring-security-test:5.6.2`
##### License
`Apache 2.0`

#### Project Lombok
`org.projectlombok:lombok:1.18.22`
##### License
`MIT`

#### UnboundID LDAP SDK For Java
`com.unboundid:unboundid-ldapsdk:6.0.4`
##### License
`Apache 2.0, GPL 2.0, LGPL 2.1`

#### UnboundID LDAP SDK For Java
`com.unboundid:unboundid-ldapsdk:6.0.4`
##### License
`Apache 2.0, GPL 2.0, LGPL 2.1`

#### Flapdoodle Embedded MongoDB
`de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.4.5`
##### License
`Apache 2.0`

#### JJWT
`io.jsonwebtoken:jjwt-impl:0.11.2`
##### License
`Apache 2.0`

#### JAXB API
`javax.xml.bind:jaxb-api:2.3.1`
##### License
`CDDL 1.1`

#### MapStruct Core
`org.mapstruct:mapstruct:1.4.2.Final`
##### License
`Apache 2.0`

#### Springdoc OpenAPI UI
`org.springdoc:springdoc-openapi-ui:1.6.6`
##### License
`Apache 2.0`

