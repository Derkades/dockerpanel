import docker
from os import environ as env

# Wrapper class around docker API to prevent accessing non-whitelisted containers

def list_containers(only_running=False):
    client = docker.from_env()
    containers = []
    for container in client.containers.list(all=(not only_running)):
        if is_whitelisted(container.name):
            containers.append(container)
    return containers


def get_container(name_or_id):
    client = docker.from_env()
    try:
        container = client.containers.get(name_or_id)
        return container if is_whitelisted(container.name) else None
    except docker.errors.NotFound:
        return None


def is_whitelisted(name):
    if 'CONTAINERS' in env:
        containers = env['CONTAINERS'].split(' ')
        return name in containers
    else:
        return True
