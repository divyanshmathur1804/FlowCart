# FlowCart - Microservices E-Commerce Backend (AWS Deployed)

FlowCart is a production-grade backend system built using a microservices architecture with event-driven communication. The system is containerized and deployed on AWS using Kubernetes (EKS).

---

## Architecture Overview

User → API Gateway → Order Service → Kafka → Product Service → PostgreSQL

- Event-driven communication using Apache Kafka  
- Outbox Pattern for reliable message delivery  
- Idempotent consumers to prevent duplicate processing  

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
- Retry and Dead Letter Queue (DLQ) ready architecture  
- Distributed tracing using Zipkin  
- Scalable deployment on AWS EKS  

---

## Deployment

- Deployed on AWS EKS cluster  
- Container images stored in AWS ECR  
- Kubernetes used for orchestration  
- Application exposed via AWS Application Load Balancer  

---

## Live Demo

Base URL:  
http://a367f8901cc434a89b2710736fb6701d-1574466201.ap-south-1.elb.amazonaws.com

This endpoint is served through an AWS Application Load Balancer routing traffic to services running inside the EKS cluster.

---

## API Example

### Create Order

POST /order

Request body:

```json
{
  "productId": 1,
  "quantity": 2
}
