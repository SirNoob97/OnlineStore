FROM maven:3.6-jdk-11-slim AS builder

ARG AUTH_PROFILE
ENV AUTH_PROFILE=${AUTH_PROFILE}
ENV ONLINE_STORE=/opt/online-store

WORKDIR $ONLINE_STORE
COPY ./pom.xml .

WORKDIR ${ONLINE_STORE}/auth-service
COPY ./auth-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./auth-service/src ./src
RUN mvn -e -B -P$AUTH_PROFILE package -DskipTests


FROM azul/zulu-openjdk-alpine:11-jre
COPY --from=builder ./opt/online-store/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-Dspring.profiles.active=${AUTH_PROFILE},docker", "-jar", "/auth-service-0.0.1-SNAPSHOT.jar"]
