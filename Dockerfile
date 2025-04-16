FROM maven:3.8-openjdk-17 AS build
WORKDIR /crypto-wallet-management
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /crypto-wallet-management
COPY --from=build /crypto-wallet-management/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]