FROM gradle:8.7

WORKDIR /java-project-99

COPY . .

RUN gradle installDist

CMD build/install/java-project-99/bin/java-project-99