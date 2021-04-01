FROM adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre11u-nightly
RUN apk --no-cache add curl
WORKDIR config-service
ARG DEPENDENCY=config-service/target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib ./app/lib
COPY ${DEPENDENCY}/BOOT-INF/classes ./app
COPY ${DEPENDENCY}/META-INF ./app/META-INF
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.sirnoob.configservice.ConfigServiceApplication"]
