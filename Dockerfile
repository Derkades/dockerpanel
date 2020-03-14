FROM ubuntu

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

# install docker
RUN apt update && \
    apt install -y curl && \
    curl -sSL https://get.docker.com | sh && \
    apt purge -y curl && \
    apt autoremove -y && \
    apt clean && \
    rm -rf /var/lib/apt/lists/*

# install jre and socat
RUN apt update && \
    apt install -y openjdk-11-jre socat && \
    apt clean && \
    rm -rf /var/lib/apt/lists/*

COPY target/dockerpanel-dev.jar /dockerpanel.jar

ENTRYPOINT [ "java", "-jar", "/dockerpanel.jar" ]
