FROM gradle:8.7

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD build/install/java-project-99/bin/java-project-99