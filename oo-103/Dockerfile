FROM ubuntu:latest

RUN apt update && apt install -y openjdk-17-jre-headless

WORKDIR /app

COPY ACME-v1.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "ACME-v1.jar"]