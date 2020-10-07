from os import environ as env
from urllib.parse import parse_qs


def parse(required_args=[]):
    query = parse_qs(env['QUERY_STRING'])
    args = {}
    for arg in required_args:
        if arg not in query:
            print(f'Missing required parameter `{arg}`')
            exit()
        args[arg] = query[arg][0]

    if len(required_args) == 0:
        return None
    elif len(required_args) == 1:
        return args[required_args[0]]
    else:
        return args
