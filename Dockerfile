FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app
COPY . /app

RUN apt-get update && apt-get install -y iputils-ping

ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD /wait-for-it.sh selenium-hub:4444 -- mvn clean test -P${ENVIRONMENT} -Dbrowser=${BROWSER} -DsuiteXmlFile=${SUITE_XML} -DremoteUrl=${REMOTE_URL}