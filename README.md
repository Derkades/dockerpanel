# Dockerpanel

An easy to use remote-access panel to manage existing docker containers. No setup required, just add a single docker container.

<img src="https://cdn.discordapp.com/attachments/645710098286510090/688788796149334145/screener_1584290423207.png" height="500">

![desktop screenshot](https://cdn.discordapp.com/attachments/645710098286510090/688791911783596120/Screenshot_from_2020-03-15_17-52-41.png)

## Features and design principles

* Start and stop docker containers
* Run commands in docker containers (send to stdin)
* View container logs
* View container status
* No database, basic configuration through environment variables

This is not a platform to create containers. Use docker-compose for defining your containers, and dockerpanel for easy remote access.

Live demo at [https://dockerpanel.derkades.xyz/](https://dockerpanel.derkades.xyz/)

## Installation

```sh
docker run -d -p "8080:8080" -v "/var/run/docker.sock:/var/run/docker.sock" derkades/dockerpanel
```

Environment variables:

* `THEME` - Web UI theme, go [here](https://github.com/Derkades/dockerpanel/tree/master/resources/themes) for a list of themes.
* `CONTAINER_WHITELIST` - Space separated list of containers to show in the panel. Without this environment variable set, all containers are shown.
* `TAIL_LINES` - Number of lines to show in the console (default `100`).
* `DISABLE_INPUT` - Set to `true` to disable terminal input (usually default `false`, but for now in the docker container set to `true` because of [#25](https://github.com/Derkades/dockerpanel/issues/25)).
* `DISABLE_BUTTONS` - Set to `true` to disable start/stop/restart buttons (default `false`).
* `PORT` - Port number for embedded webserver (default `80`).

## docker-compose example

See [docker-compose.yaml](https://github.com/Derkades/dockerpanel/blob/master/docker-compose.yaml)
