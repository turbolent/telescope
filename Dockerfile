FROM openjdk:8-jre-slim

ENV ARGS ""

ADD dist/question-service.jar .

CMD java -jar question-service.jar ${ARGS}

EXPOSE 8080
