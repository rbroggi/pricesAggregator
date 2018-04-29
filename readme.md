
# Spring-boot - RabbitMq consumer

Prices Aggregator:
  * REST endpoint for price retrival
  * RabbitMq messages consumer

## Getting Started

1. Setup RabbitMq broker - see Prerequisites
2. git clone https://github.com/rbroggi/pricesAggregator
3. mvn spring-boot:run

### Prerequisites

Setup RabbitMq broker (containerized broker):

docker run -p 5672:5672 -d --hostname my-rabbit --name some-rabbit rabbitmq:3

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/)
* [Maven](https://maven.apache.org/) - Dependency Management
* [RabbitMq](https://www.rabbitmq.com/) - Message broker


## Authors

* [Rodrigo Broggi](https://github.com/rbroggi)

## Acknowledgments

* Big thanks to [Josh Long](https://github.com/joshlong)

