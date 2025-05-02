FROM amazoncorretto:22
EXPOSE 8080
RUN mkdir /app
COPY build/libs/*-all.jar /app/run.jar
ENTRYPOINT ["java","-jar","/app/run.jar"]
