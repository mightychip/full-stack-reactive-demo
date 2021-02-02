FROM openjdk:11-slim AS builder
WORKDIR layers
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:11-slim
WORKDIR application

COPY --from=builder layers/dependencies/ ./
COPY --from=builder layers/spring-boot-loader/ ./
COPY --from=builder layers/snapshot-dependencies/ ./
COPY --from=builder layers/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]