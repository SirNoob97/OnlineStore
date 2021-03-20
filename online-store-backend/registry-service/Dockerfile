FROM maven:3.6.3-adoptopenjdk-11-openj9 AS builder

ENV ONLINE_STORE=/opt/online-store

WORKDIR ${ONLINE_STORE}/registry-service
COPY ./registry-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./registry-service/src ./src
RUN mvn -e -B package -DskipTests


FROM adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre11u-nightly
RUN apk --no-cache add curl
COPY --from=builder ./opt/online-store/registry-service/target/registry-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-jar", "/registry-service-0.0.1-SNAPSHOT.jar"]
