FROM adoptopenjdk/openjdk11-openj9:x86_64-alpine-jre11u-nightly
WORKDIR config-service
ARG DEPENDENCY=./config-service/target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
RUN apk --no-cache add curl
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.sirnoob.configservice.ConfigServiceApplication"]
