# we use a multistage Dockerfile here so that we can dispose of the mess we make when we explode the .jar
FROM openjdk:11-slim AS builder
WORKDIR layers
ARG JAR_FILE

# copy over the .jar - this name is provided as a parameter to the Maven plugin
ADD target/${JAR_FILE} app.jar

# explode the application into layers for copying to the final image
RUN java -Djarmode=layertools -jar app.jar extract

# now we'll create the image we're going to keep
FROM openjdk:11-slim
WORKDIR application

# With the layers properly exposed, we can now copy them over to the image that we're building.  This allows us to
# minimize what we're sending up to our registry, and also helps minimize what needs to be downloaded when deployed
# containers using this image get updated. This also provides a benefit to performance of the .jar.  This is an
# important part of the process when getting a containerized Spring Boot application ready for production.
COPY --from=builder layers/dependencies/ ./
COPY --from=builder layers/spring-boot-loader/ ./
COPY --from=builder layers/snapshot-dependencies/ ./
COPY --from=builder layers/application/ ./

# we'll benefit from being able to communicate directly with the application on this port
EXPOSE 8080

# TODO set up management and expose on port 8081

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]