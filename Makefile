setup:
	gradle wrapper --gradle-version 7.2

clean:
	./gradlew clean

build:
	./gradlew clean build

start-dev:
	APP_ENV=development ./gradlew run

start-prod:
	APP_ENV=production ./gradlew run

start-pg:
	APP_ENV=local_pg ./gradlew run

install:
	./gradlew install

run-dist:
	APP_ENV=production ./build/install/app/bin/app

generate-migrations:
	./gradlew generateMigrations

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

.PHONY: build
