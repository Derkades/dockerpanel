FROM openjdk:11-jre

# compile app
# RUN apt update && \
#     apt install -y git openjdk-11-jdk maven && \
#     mkdir /compile && cd /compile && \
#     git clone https://github.com/Derkades/dockerpanel.git &&\
#     cd dockerpanel && \
#     mvn package shade:shade && \
#     apt purge -y git openjdk-11-jdk maven && \
#     mv target/dockerpanel-dev.jar /dockerpanel.jar && \
#     cd / && \
#     rm -rf /compile && \
#     rm -rf /root/.m2 && \
#     apt autoremove -y && \
#     apt clean && \
#     rm -rf /var/lib/apt/lists/*

COPY target/dockerpanel-dev.jar /dockerpanel.jar

# Temporarily disabled until https://github.com/Derkades/dockerpanel/issues/25 is fixed
ENV DISABLE_INPUT=true

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/dockerpanel.jar" ]
