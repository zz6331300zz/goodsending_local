FROM eclipse-temurin:17-jdk-alpine
COPY ./goodsending/build/libs/*SNAPSHOT.jar project.jar
ENTRYPOINT ["java", "-jar", "project.jar"]