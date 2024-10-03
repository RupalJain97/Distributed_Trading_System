FROM openjdk:11-jdk-slim
VOLUME /tmp
ADD target/distributed_trading_system.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
