FROM maven:3.6-jdk-11-slim AS builder

ARG SHOPPING_PROFILE
ENV SHOPPING_PROFILE=${SHOPPING_PROFILE}
ENV ONLINE_STORE=/opt/online-store

WORKDIR $ONLINE_STORE
COPY ./pom.xml .

WORKDIR ${ONLINE_STORE}/shopping-service
COPY ./shopping-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./shopping-service/src ./src
RUN mvn -e -B -P$SHOPPING_PROFILE package -DskipTests


FROM azul/zulu-openjdk-alpine:11-jre
COPY --from=builder ./opt/online-store/shopping-service/target/shopping-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-Dspring.profiles.active=${SHOPPING_PROFILE}", "-jar", "/shopping-service-0.0.1-SNAPSHOT.jar"]
