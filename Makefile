.PHONY: build run test clean

build:
	mvn clean package

run:
	docker-compose up --build

test:
	mvn test

clean:
	mvn clean
	docker-compose down -v