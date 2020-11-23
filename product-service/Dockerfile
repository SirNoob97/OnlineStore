FROM maven:3.6-jdk-11-slim AS builder

ARG PRODUCT_PROFILE
ENV PRODUCT_PROFILE=${PRODUCT_PROFILE}
ENV ONLINE_STORE=/opt/online-store

WORKDIR $ONLINE_STORE
COPY ./pom.xml .

WORKDIR ${ONLINE_STORE}/product-service
COPY ./product-service/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./product-service/src ./src
RUN mvn -e -B -P$PRODUCT_PROFILE package -DskipTests


FROM azul/zulu-openjdk-alpine:11-jre
COPY --from=builder ./opt/online-store/product-service/target/product-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-Dspring.profiles.active=${PRODUCT_PROFILE},docker", "-jar", "/product-service-0.0.1-SNAPSHOT.jar"]
