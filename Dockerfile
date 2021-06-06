FROM adoptopenjdk/openjdk15:ubi
ARG jarFileName
COPY $jarFileName /opt
ENV JAR_FILE_NAME=$jarFileName
EXPOSE 8080
ENTRYPOINT java -jar /opt/$JAR_FILE_NAME