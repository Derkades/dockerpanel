# Dockerpanel

An easy to use remote-access panel to manage existing docker containers. No setup required, just add a single docker container. Made specifically for running Minecraft servers using docker-compose but can of course be used for other containers as well.

<img src="https://cdn.discordapp.com/attachments/645710098286510090/688788796149334145/screener_1584290423207.png" height="500">

![desktop screenshot](https://cdn.discordapp.com/attachments/645710098286510090/688791911783596120/Screenshot_from_2020-03-15_17-52-41.png)

## Features and design principles

* Start and stop docker containers
* Run commands in docker containers (send to stdin)
* View container logs
* View container status
* No database, basic configuration through environment variables

This is not a platform to create containers. Use docker-compose for defining your containers, and dockerpanel for easy remote access.

Live demo at [dockerpanel-demo.rkslot.nl](https://dockerpanel-demo.rkslot.nl/)

## Installation

Try it:

```sh
docker run -it --rm -p "8080:80" -v "/var/run/docker.sock:/var/run/docker.sock" derkades/dockerpanel
```

For installation, refer to docker-compose example below.

Environment variables:

* `THEME` - Web UI theme, go [here](https://github.com/Derkades/dockerpanel/tree/master/resources/themes) for a list of themes.
* `CONTAINERS` - Space separated list of containers to show in the panel. Without this environment variable set, all containers are shown.
* `TAIL_LINES` - Number of lines to show in the console (default `100`).
* `DISABLE_INPUT` - Set to disable terminal input
* `DISABLE_BUTTONS` - Set to disable start/stop/restart buttons (default `false`).
* `PORT` - Port number for embedded webserver (default `80`).
* `TITLE` - Site title, defaults to "DockerPanel"

## docker-compose example

See [docker-compose.yaml](https://github.com/Derkades/dockerpanel/blob/master/docker-compose.yaml)
