# Quota Service

![Coverage](.github/badges/jacoco.svg)

[![Java CI](https://github.com/guinichetti/quota-service/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/guinichetti/quota-service/actions/workflows/maven.yml)

This Spring Boot application is designed to improve the efficiency of our Web API usage by implementing a quota mechanism. 
The goal is to prevent abuse caused by excessive requests from multiple users. During the day (9:00 - 17:00 UTC), user data is sourced from MySQL, and during the night (17:01 - 8:59 UTC), it switches to ElasticSearch. The application offers two approaches for deployment: a local solution using an H2 database with an Interceptor and a cloud solution using Docker with MySQL and Redis.

## Tech stack

* Java 17
* Springboot 3.2.5
* Docker
* OpenApi (API first generating controller from swagger.yml spec)
* Jacoco (Code coverage report available on Github pipeline)
* Github Actions (Continuous Integration)

## Features

- CRUD Operations for User Model:
  - createUser(User user): Creates a new user.
  - getUser(String userId): Retrieves a user by ID.
  - updateUser(String userId, User updatedUser): Updates an existing user.
  - deleteUser(String userId): Deletes a user.
  
- Quota Management:
  - consumeQuota(String userId): Main function for accessing and counting user quotas. Limits users to a specified number of API requests per timeframe (e.g., 5 requests per user).
  - getUsersQuota(): Retrieves all users and their current quota statuses.

## Prerequisites

- Java Development Kit (JDK) 17 or higher installed
- Apache Maven installed or Maven Wrapper
- IDE (e.g., IntelliJ IDEA, Eclipse) for development
- Docker

## Getting Started

### From Git repo

1. Clone the repository:

    ```bash
    git clone https://github.com/nichetti/quota-service.git
    ```
2. Navigate to the project directory:

    ```bash
    cd quota-service
    ```

### From zip file package

1. Unzip the zip file you received:
   ```bash
   unzip quota-service-master.zip
   ```
2. Navigate to the project directory:

    ```bash
    cd quota-service-master
    ```
   Then you can move to step 3

### With repo cloned or zip extracted you can proceed as below:

3. Build the project using Maven:

    ```bash
    mvn clean package -DskipTests
    ```
   or
   ```bash
    ./mvnw clean package -DskipTests
    ```

4. Run unit tests using Maven:

    ```bash
    mvn clean verify
    ```
   or
   ```bash
    ./mvnw clean verify
    ```


5. Running the application:

## Local Solution (H2 Database and Interceptor)
*
    ```bash
    java -jar target/quota-service-0.0.1-SNAPSHOT.jar
    ```
   or
    ```bash
    mvn spring-boot:run
    ```

## Cloud Solution (Docker with MySQL and Redis)
* Ensure Docker is installed and running on your machine.
  ```bash
  docker-compose up --build
  ```

6. Access the API documentation and endpoints via a web browser.

   ```http
   http://localhost:8080/swagger-ui/index.html
   ```

## Usage

1. Utilize the provided API endpoints to manage user quotas efficiently:
    * Create a new user using the createUser endpoint.
    * Retrieve a user by their ID using the getUser endpoint.
    * Update an existing user's information with the updateUser endpoint.
    * Delete a user by their ID using the deleteUser endpoint.
    * Consume quota for a user by making requests to the consumeQuota endpoint.
    * Retrieve quota status for all users using the getUsersQuota endpoint.
2. Test the API functionality using tools such as cURL, Postman, or your preferred REST client.
3. Ensure the application handles user quotas effectively:
   * Each user is limited to a specified number of API requests per timeframe.
   * Users are blocked from further requests once they exceed their quota