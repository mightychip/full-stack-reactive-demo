# WebFlux Back End
The back end is built using Spring Boot and WebFlux on a Netty server.  This is my first foray into WebFlux, so we'll learn the ropes together. This microservice provides some basic REST endpoints, and a simple WebSocket implementation for a guest book application with live updates.

This serves as a solid example of some basic principles for building a reactive microservice.  The focus of a course using this teaching material should be on the basics of Unit/Integration testing, RouterFunction configuration, and wiring in functional REST endpoints. 

## Prerequisites
Docker must be installed and a `registry` container must be running on port `5000` in order for the Docker image to successfully pushed.

## How to Build
This application is built using the `install` goal, which will automatically run the Unit and Integration Tests (unless specifically disabled).  Provided the `build-and-push-docker-image` Maven profile is active (it is active by default), a Docker image will be built and pushed to the registry at the address specified by the `docker.image.prefix`. The resultant repository will be named `webflux-front-end`.  Both the registry address and repository name can be changed in the configuration for the `docker-maven-plugin

**NOTE:** The `build-and-push-docker-image` profile requires Docker to be installed on the machine running the `install` goal.  Further, a `registry` container must be running and available on port 5000.

## How to Run
There are currently two possible run-configurations for this application:
- **Locally/Standalone Mode** - The easiest way to run this application is to start it as a Service in IntelliJ IDEA or via the `spring-boot:run` Maven goal.  No additional profiles need to be provided.  However, it should be noted that this requires MongoDB to be running locally on port 27017 and must be using its default username and password.

- **Containerized Mode** - After being built, this application can be easily run in a Docker container.  If it is started using the `docker-compose.yml` file located in the `docker-compose/dev` folder at the root of this project, all required infrastructure including a simple copy of the front-end.

## Unit/Integration Tests
Several simple Unit and Integration Tests are provided to demonstrate testing of various WebFlux application components.  In case you're here looking specifically for one of those, they've been indexed below:
- [CORS Configuration Test](src/test/java/ca/purpleowl/example/reactive/CorsTest.java) - Configuration for this was a bit of a headache to figure out
- [WebSocket Testing](src/test/java/ca/purpleowl/example/reactive/websocket/WebSocketTests.java)
- [WebFlux Unit Testing](src/test/java/ca/purpleowl/example/reactive/service/GuestBookServiceTests.java)
- [RouterFunction Integration Testing](src/test/java/ca/purpleowl/example/reactive/route/GuestBookRouteTests.java)
