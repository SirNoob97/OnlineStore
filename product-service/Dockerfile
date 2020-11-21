FROM maven:3.6-jdk-11-slim AS builder

ENV PARENT=/parent
ENV PRODUCT_SERVICE=product-service

WORKDIR $PARENT
COPY ./pom.xml .
WORKDIR /$PRODUCT_SERVICE
COPY ./$PRODUCT_SERVICE/pom.xml .

RUN mvn -e -B dependency:resolve
COPY ./$PRODUCT_SERVICE/src ./src
RUN mvn -e -B -P $PROFILE package -DskipTests


FROM azul/zulu-openjdk-alpine:11-jre
EXPOSE 8091
COPY --from=builder ./$PARENT$PRODUCT_SERVICE/target/product-service-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-Dspring.profiles.active=$PROFILE", "-jar", "/product-service-0.0.1-SNAPSHOT.jar"] registry-service
