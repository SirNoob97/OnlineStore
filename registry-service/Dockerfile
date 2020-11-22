FROM maven:3.6-jdk-11-slim AS builder

ENV ONLINE_STORE=/opt/online-store

WORKDIR $ONLINE_STORE
COPY ./pom.xml .

WORKDIR ${ONLINE_STORE}/registry-service
COPY ./registry-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./registry-service/src ./src
RUN mvn -e -B package -DskipTests


FROM azul/zulu-openjdk-alpine:11-jre
EXPOSE 8081
COPY --from=builder ./opt/online-store/registry-service/target/registry-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/registry-service-0.0.1-SNAPSHOT.jar"]
