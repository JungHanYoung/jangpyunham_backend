FROM openjdk:11

COPY build/libs /app
WORKDIR /app

EXPOSE 8000

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "jangpyunham-0.0.1-SNAPSHOT.jar"]
