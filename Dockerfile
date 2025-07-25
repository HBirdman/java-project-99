FROM gradle:8.7

WORKDIR /app

COPY /java-project-99 .

RUN gradle installDist

CMD build/install/app/bin/app