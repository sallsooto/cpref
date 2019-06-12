FROM java:8
MAINTAINER Ahmad Fall <fallahmad@outlook.com>
VOLUME /tmp
ADD Application.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
