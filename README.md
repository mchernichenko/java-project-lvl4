### Hexlet tests and linter status:
[![Actions Status](https://github.com/mchernichenko/java-project-lvl4/workflows/hexlet-check/badge.svg)](https://github.com/mchernichenko/java-project-lvl4/actions)
![Java CI](https://github.com/mchernichenko/java-project-lvl4/actions/workflows/java-ci.yml/badge.svg)
[![Maintainability](https://api.codeclimate.com/v1/badges/5493257ef1549804949c/maintainability)](https://codeclimate.com/github/mchernichenko/java-project-lvl4/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/5493257ef1549804949c/test_coverage)](https://codeclimate.com/github/mchernichenko/java-project-lvl4/test_coverage)

[Demo on Heroku](https://java-project-lvl4.herokuapp.com/)

* Клонировать проект: git clone git@github.com:mchernichenko/java-project-lvl4.git
* Убедитесь, что у вас в системе установлена утилита make, выполнив в терминале команду *make -v*

  * [Что такое Makefile и как начать его использовать](https://guides.hexlet.io/makefile-as-task-runner/)

* Для запуска проект локально, с использованием H2, из корня проекта выполнить команду 
```sh
make start-dev 
```
* Для запуска проект локально, с использованием Postgres, в файле конфигурации *java-project-lvl4/src/main/resources/application.yaml*
прописать в *datasource.local_pg.url* строку коннекта в БД Postgres и из корня проекта выполнить команду
```sh
make start-pg 
``` 

Запущенное приложение будет доступно по адресу: *https://localhost:5000* 
