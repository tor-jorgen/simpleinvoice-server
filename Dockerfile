# Stage 1: Build server
FROM gradle:9.4.1-jdk25-corretto AS builder
WORKDIR /server
COPY build.gradle.kts gradle.properties settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM amazoncorretto:25-alpine
ARG USER=service
ARG GROUP=service

RUN \
    mkdir /migrations && \
    apk add --no-cache shadow && \
    groupadd -r "$GROUP" -g 1000 && \
    useradd -rm -s /sbin/nologin -g 1000 -u 1000 "$USER" && \
    chmod 755 "/home/$USER" && \
    apk del shadow

USER $USER
COPY --from=builder --chmod=755 /server/build/libs/*-all.jar service.jar
COPY --chmod=755 src/main/resources/db/migration /migrations
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "service.jar"]
