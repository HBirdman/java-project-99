### Hexlet tests and linter status:
[![Actions Status](https://github.com/HBirdman/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/HBirdman/java-project-99/actions)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=HBirdman_java-project-99&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=HBirdman_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=HBirdman_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=HBirdman_java-project-99)

### Description:
This is a learning project called Task Manager. This web application allows you to track the progress of shared tasks. Tasks can be assigned to different users and marked by different labels and statuses.

### How to use:
You can access the project via this external link: https://java-project-99-l2ru.onrender.com

Or you can launch it locally:
* Clone repository
* Type `./gradlew run --args='--spring.profiles.active=development'` in commandline
* Go to http://localhost:8080 in your browser

Firstly you need to log in as a test user, using `hexlet@example.com` as the username and `qwerty` as the password.
You can perform CRUD operations on `user`, `task`, `taskStatus` and `label` entities.