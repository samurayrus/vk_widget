FROM java:8
FROM maven:alpine

# image layer
WORKDIR /app
ADD pom.xml /app

# Image layer: with the application
COPY . /app
RUN mvn -v
RUN mvn clean install -DskipTests
ADD ./GroupLogin.properties /app/target/
# TODO: ADD ENV FOR APP VERSION!
ENTRYPOINT ["java","-jar","/app/target/vk_widget-3.8.5-shaded.jar"]