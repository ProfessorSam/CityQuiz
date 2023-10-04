# Builder stage
FROM gradle:latest as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

# Runner stage
FROM gcr.io/distroless/java17-debian12
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/app.jar
EXPOSE 80
WORKDIR /app
ENTRYPOINT ["java","-jar","app.jar"]