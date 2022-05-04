FROM python:3-slim AS copy

# dumb workaround to copy files in one go later and not have many layers
COPY ["dockerpanel.py", "index.html", "script.js", "styles.css", "/app/"]
COPY api /app/api
COPY icons /app/icons
COPY themes /app/themes

FROM python:3-slim

RUN pip install docker

COPY --from=copy /app /app

WORKDIR /app

ENV PORT=80

EXPOSE 80

CMD [ "python3", "dockerpanel.py" ]
