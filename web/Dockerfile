FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY target/web-*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/application.jar"]