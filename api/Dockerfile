FROM  openjdk:17-oracle

RUN mkdir -p /logs

ENV	PROFILE default
ENV TZ=Asia/Seoul
EXPOSE 8080

ARG JAVA_OPTS

ARG RELEASE_VERSION
ENV DD_VERSION=${RELEASE_VERSION}

ARG JAR_FILE="build/libs/api-*.jar"
COPY ${JAR_FILE} app.jar

ENTRYPOINT java -XX:MaxGCPauseMillis=100 -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 $JAVA_OPTS -jar app.jar
