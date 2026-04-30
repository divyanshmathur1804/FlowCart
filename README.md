# FlowCart - Microservices E-Commerce Backend (AWS Deployed)

FlowCart is a production-grade backend system built using a microservices architecture with event-driven communication.

The system is fully containerized and deployed on AWS using Kubernetes (EKS).

---

## Architecture Overview

User → API Gateway → Order Service → Kafka → Product Service → PostgreSQL

- Event-driven communication using Kafka
- Outbox Pattern for reliable message delivery
- Idempotent consumers to avoid duplicate processing

---

## Tech Stack

- Java, Spring Boot
- Apache Kafka
- PostgreSQL
- Redis (Caching)
- Docker
- Kubernetes (AWS EKS)
- AWS ECR (Container Registry)
- Zipkin (Distributed Tracing)

---

## Features

- Order creation with event publishing
- Product stock update via Kafka consumer
- Outbox pattern for guaranteed event delivery
- Retry + Dead Letter Queue (DLQ) ready
- Distributed tracing using Zipkin
- Scalable deployment on AWS EKS

---

## Deployment

- Deployed on AWS EKS cluster
- Container images stored in AWS ECR
- Kubernetes used for orchestration

---

## Screenshots

(Add here)
- EKS cluster running
- Kubernetes pods
- Kafka logs
- API response

---

## API Example

POST /order

```json
{
  "productId": 1,
  "quantity": 2
}
