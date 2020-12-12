# Online Store

This project consists of the REST api for an online store using microservices architecture.

My goal whit this project is to use all knowledge that i have acquired over the course of 8 months in a self-taught way to create a rest-full amateur application for an online store.

## Microservices

* **Product-Service** microservice in charge of products management. [Commands and Endpoints](https://github.com/SirNoob97/OnlineStore/blob/master/product-service/README.md)
* **Auth-Service** microservice in charge of user authentication and authorization, as well as acting as the application gateway. [Commands and Endpoints](https://github.com/SirNoob97/OnlineStore/blob/master/auth-service/README.md)
* **Shopping-Service** microservice in charge of invoice management, to generate invoices it is necessary for this microservice to make requests to products-services, so it is also a client. [Commands and Endpoints](https://github.com/SirNoob97/OnlineStore/blob/master/shopping-service/README.md)

These will obtain their configuration from this repository through a centralized configuration service that will use the Spring Cloud Config Server dependency.

Eureka dependencies will be used for the discovery of all services.

Hystrix's will be used for monitoring thaks to Actuator and Admin Starter, which will provide the information generated by Actuator in a separate and more readable endpoint.

Cloud Gateway to have a single acces endpoint.

Spring Security and JWT for the users authentication and authorization.

## Prerequisites

* [Java 11](https://openjdk.java.net/).
* [Maven](https://maven.apache.org/).
* [Docker](https://www.docker.com/).

## Gettin Started

To run config-service and registration-service, you just need to use this command:

    mvn clean compile spring-boot: run

The other microservices have their own README file that contains the commands to run the microservice using the spring-boot maven plugin.

To run the application with Docker, use the run.sh script as the example below, you can use -h option to see the availables profiles and options.

    ./run.sh build -a test -P test --shopping-service postgresql
    ./run.sh run -a test -P test --shopping-service postgresql

This will take a couple of minutes because the script ensures the execution order by checking the services status.

If you use this method you should use the respective ips of the microservices instead of "localhost".

* PostgreSQL: 192.168.0.21
* Config-Service: 192.168.0.2
* Resgistry-Service: 192.168.0.3
* Auth-Service: 192.168.0.11
* Product-Service: 192.168.0.12
* Shopping-Service: 192.168.0.13

Ideally, only the auth-service ip should be set, as it also acts as the application gateway.  In this way, with all the services with fixed ip, I can test the application in a faster way for me and more friendly for my pc (without having to use the gateway to test the other services).

### Dependencies

* Spring Boot Starter Web, Web Flux, JPA, R2DBC, Validation, Actuator, Security.
* Spring Cloud Starter Config, Netflix Eureka Client and Server, Sleuth, Config Server, Gateway, Openfeign, Netflix Hystrix and Dashboard.
* JWT.
* H2.
* PostgreSQL
* Lombok.
* Codecentric Admin Starter Client and Server.

### TODO

* ***Add security to each microservice***
* Add Unit and Integration Test for shopping-service.
* Implement Customer-Service, auth-service client microservice and with which customers can add/edit more information to their account.
* Implement Admin-Service.
* Decrease repeating code as much as possible.
* Add application sequence diagram.
