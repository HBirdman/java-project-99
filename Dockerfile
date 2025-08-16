FROM gradle:8.3.0-jdk20

WORKDIR /app

COPY / .

RUN gradle installDist

EXPOSE 8080

CMD ./build/install/app/bin/app --spring.profiles.active=production