FROM adoptopenjdk:14-jre-hotspot

COPY target/dockerpanel-dev.jar /dockerpanel.jar

# Temporarily disabled until https://github.com/Derkades/dockerpanel/issues/25 is fixed
ENV DISABLE_INPUT=true

EXPOSE 80

ENTRYPOINT [ "java", "-jar", "/dockerpanel.jar" ]
