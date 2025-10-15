FROM openjdk:21-jdk-alpine
VOLUME /tmp
COPY target/myapp.jar app.jar
ENTRYPOINT ["java","-jar","/.jar"]