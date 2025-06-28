FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD ["/wait-for-it.sh", "db:5432", "--", "./mvnw", "spring-boot:run"]
