FROM maven:3.6.3-adoptopenjdk-11-openj9 AS builder

ENV ONLINE_STORE=/opt/online-store

WORKDIR ${ONLINE_STORE}/auth-service
COPY ./auth-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./auth-service/src ./src
RUN mvn -e -B -P postgresql package -DskipTests


FROM adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre11u-nightly
COPY --from=builder ./opt/online-store/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-Dspring.profiles.active=postgresql", "-jar", "/auth-service-0.0.1-SNAPSHOT.jar"]
