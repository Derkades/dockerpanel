FROM maven:3-adoptopenjdk-14 as builder

WORKDIR /data

ARG TEMP_CACHEBUST=3

ADD pom.xml .

# Download dependencies now, copy source code later. This way docker will \
# cache the dependencies if pom.xml doesn't change.
# RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]
RUN mvn dependency:go-offline

COPY src ./src
COPY resources ./resources

RUN mvn --offline package shade:shade

FROM adoptopenjdk:14-jre-hotspot

COPY --from=builder /data/target/dockerpanel.jar /

# Temporarily disabled until https://github.com/Derkades/dockerpanel/issues/25 is fixed
ENV DISABLE_INPUT=true

EXPOSE 80

ENTRYPOINT [ "java", "-jar", "/dockerpanel.jar" ]
