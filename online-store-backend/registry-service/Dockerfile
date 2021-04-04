FROM adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre11u-nightly
RUN apk --no-cache add curl
RUN addgroup -S spring && adduser -S registry-service -G spring
WORKDIR /registry-service
ARG DEPENDENCY=registry-service/target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib ./app/lib
COPY ${DEPENDENCY}/BOOT-INF/classes ./app
COPY ${DEPENDENCY}/META-INF ./app/META-INF
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.sirnoob.registryservice.RegistryServiceApplication"]
