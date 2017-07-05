# Overview
The following document contains information for building
*rdap-ingressd* both from source and as a Docker container.

# Requirements
The following requirements need to be met in order to build the project.

- [Git](https://git-scm.com/)

From Source:

- Java 8 or higher. Supports both Oracle JDK and OpenJDK
- [Maven](https://maven.apache.org/) 3.5 or higher

Docker Container:

- [Docker CE](https://www.docker.com/community-edition) 17 or higher

# Obtaining The Source Code

The first step in building *rdap-ingressd* is to obtain the source code with
Git.

```
git clone https://github.com/APNIC-net/rdap-ingressd
```

# Building & Running From Source

## Building
*rdap-ingressd* is built using maven. To create a new build of the project
please run the following Maven command.

```
mvn package
```

The project's JARs have now been created and can be executed.

## Running
The project can be executed in one of two ways. The first is through Maven using
spring-boot:run, and the second is by executing the JAR directly.

Executing with Maven:

```
mvn spring-boot:run
```

Executing with Java:

```
java -jar target/rdap-ingressd-<version>.jar
```
Where <version> is the version of the project that has been checked
out with Git.

*rdap-ingressd* is now listening and available on port 8080.

# Building & Running With Docker

## Building
Use the following command to build a Docker image of *rdap-ingressd*

```
docker build . -t apnic/rdap-ingressd
```

## Running
The created Docker image can now be executed:

```
docker run -p 8080:8080 apnic/rdap-ingressd
```

*rdap-ingressd* is now listening and available on port 8080.

See the [deploy](deploy.md) documentation for more detailed instructions on
deploying the Docker image.

# Validating
To validate that *rdap-ingressd* is working, an RDAP query can be issued against
the service:

```
curl -X GET http://localhost:8080/ip/1.2.3.4
```
