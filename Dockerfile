FROM gcr.io/distroless/java17-debian12
COPY ./build/libs/*.jar /app/app.jar
EXPOSE 80
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]