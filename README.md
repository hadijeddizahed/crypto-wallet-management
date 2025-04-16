# crypto-wallet-management

## Overview
_This sample project helps people keep track of their “crypto money” (also known as token), like
BTC or ETH, in a special wallet. This wallet stores information about different types of cryptocurrencies, how much
of each person owns, and how much it costs right now._ 

## Technologies Used

1. Java: Version 17
2. Spring Boot: Framework for building the application
3. Spring Batch: For batch processing
4. PostgreSQL: Database for persistent storage
5. Flyway: Database migration tool
6. Maven: Dependency management and build tool
7. JUnit: Unit and integration testing framework

## Prerequisites
Before you begin, ensure you have the following installed:

Java 17
Maven
Docker (for containerized deployment)
PostgreSQL (if running locally without Docker)

## Getting Started
Clone the Repository
git clone https://github.com/hadijeddizahed/crypto-wallet-management.git
cd crypto-wallet-management

## Build the Project
To compile and package the application:
mvn clean install

## Run the Application Locally

Ensure PostgreSQL is running and configured (or use Docker, see below).
Update the application.properties file in src/main/resources with your database credentials.
Run the application:

`mvn spring-boot:run`

## Database Migrations
Flyway automatically applies database migrations on application startup. Ensure your database is accessible and the credentials are correctly configured.
Run with Docker Compose
To spin up the application and PostgreSQL database using Docker Compose:

`docker-compose up -d`

This command starts the database in detached mode. The Docker Compose configuration includes:
PostgreSQL container

## Testing
The project includes unit and integration tests written with JUnit. To run the tests:

`mvn test`

