FROM gradle:8.7

WORKDIR .

COPY . .

RUN gradle installDist

CMD build/install/app/bin/app