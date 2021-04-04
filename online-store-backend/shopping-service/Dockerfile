FROM adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre11u-nightly
RUN addgroup -S spring && adduser -S shopping-service -G spring
WORKDIR /shopping-service
ARG DEPENDENCY=shopping-service/target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib ./app/lib
COPY ${DEPENDENCY}/BOOT-INF/classes ./app
COPY ${DEPENDENCY}/META-INF ./app/META-INF
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "-Dspring.profiles.active=postgresql", "com.sirnoob.shoppingservice.ShoppingServiceApplication"]
