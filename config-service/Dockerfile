FROM maven:3.6-jdk-11-slim AS builder

ENV ONLINE_STORE=/opt/online-store

WORKDIR $ONLINE_STORE
COPY ./pom.xml .

WORKDIR ${ONLINE_STORE}/config-service
COPY ./config-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./config-service/src ./src
RUN mvn -e -B package -DskipTests


FROM azul/zulu-openjdk-alpine:11-jre
EXPOSE 8091
COPY --from=builder ./opt/online-store/config-service/target/config-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-jar", "/config-service-0.0.1-SNAPSHOT.jar"]
