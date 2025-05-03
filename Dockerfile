FROM amazoncorretto:22
ARG USER=service
ARG GROUP=service

RUN \
    yum update -y -q --security && \
    yum install shadow-utils -y -q && \
    groupadd -r $GROUP -g 1000 && \
    useradd -rm -s /sbin/nologin -g 1000 -u 1000 $USER && \
    chmod 755 /home/$USER && \
    yum remove shadow-utils -y -q && \
    yum autoremove -y -q

USER $USER
COPY --chown=$USER:$GROUP build/libs/*-all.jar service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "service.jar"]
