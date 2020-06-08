FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} service-translate.jar
ENTRYPOINT ["java","-Dssl=true","-jar","/service-translate.jar"]
EXPOSE 8443

