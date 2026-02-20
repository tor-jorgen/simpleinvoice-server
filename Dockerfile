# Stage 1: Build server
FROM gradle:9.2.1-jdk21-corretto AS builder
WORKDIR /server
COPY build.gradle.kts gradle.properties settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM amazoncorretto:21
ARG USER=service
ARG GROUP=service

RUN \
    mkdir /migrations && \
    yum update -y -q --security && \
    yum install shadow-utils -y -q && \
    groupadd -r "$GROUP" -g 1000 && \
    useradd -rm -s /sbin/nologin -g 1000 -u 1000 "$USER" && \
    chmod 755 "/home/$USER" && \
    yum remove shadow-utils -y -q && \
    yum autoremove -y -q && \
    yum clean all

USER $USER
COPY --from=builder --chown=$USER:$GROUP /server/build/libs/*-all.jar service.jar
COPY --chown=$USER:$GROUP src/main/resources/db/migration /migrations
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "service.jar"]
