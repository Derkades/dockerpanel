FROM python:3-alpine

RUN pip install docker

COPY . /app

WORKDIR /app

ENV PORT=80

EXPOSE 80

ENTRYPOINT [ "python3", "dockerpanel.py" ]
