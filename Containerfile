FROM docker.io/python:3.14-slim AS base

# dumb workaround to copy files in one go later and not have many layers
FROM base as copy
COPY ["dockerpanel.py", "index.html", "script.js", "styles.css", "/app/"]
COPY api /app/api
COPY icons /app/icons
COPY themes /app/themes

FROM base

RUN PYTHONDONTWRITEBYTECODE=1 pip install --no-cache-dir docker

COPY --from=copy /app /app

WORKDIR /app

ENV PORT=80

EXPOSE 80

CMD [ "python3", "dockerpanel.py" ]
