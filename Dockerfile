FROM adoptopenjdk/openjdk8:alpine-slim
COPY build/libs/starter-*-all.jar starter.jar
EXPOSE 8080
CMD java -jar starter.jar