# Spring Boot + RabbitMQ + Kubernetes Demo

Demo microservices với Spring Boot, RabbitMQ, và Kubernetes deployment.

## 🏗️ Architecture

```
Order Service (8081) → RabbitMQ → Notification Service (8082)
```

## 🛠️ Tech Stack

- **Spring Boot 3.5.0**
- **Java 17**
- **RabbitMQ 3.13**
- **Kubernetes**
- **Docker**
- **Spring AMQP**
- **Swagger/OpenAPI 3.0**

## 📦 Services

### Order Service (Port 8081)
- REST API để tạo orders
- Publish events to RabbitMQ
- Swagger UI: `http://localhost:8081/swagger-ui.html`

### Notification Service (Port 8082)
- Consume events from RabbitMQ
- Send notifications (email/SMS simulation)
- Swagger UI: `http://localhost:8082/swagger-ui.html`

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker Desktop with Kubernetes enabled
- kubectl

### Local Development

#### 1. Start RabbitMQ
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=admin123 \
  rabbitmq:3.13-management-alpine
```

#### 2. Run Order Service
```bash
cd order-service
mvn spring-boot:run
```

#### 3. Run Notification Service
```bash
cd notification-service
mvn spring-boot:run
```

### Kubernetes Deployment

```bash
# Deploy all services
kubectl apply -f k8s/

# Access services
kubectl port-forward svc/order-service 8081:80
kubectl port-forward svc/rabbitmq 15672:15672
```

## 🧪 Testing

### Create Order via API
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "amount": 99.99
  }'
```

### Access RabbitMQ Management UI
```
http://localhost:15672
Username: admin
Password: admin123
```

## 📚 Documentation

- [Swagger UI - Order Service](http://localhost:8081/swagger-ui.html)
- [Swagger UI - Notification Service](http://localhost:8082/swagger-ui.html)
- [RabbitMQ Management](http://localhost:15672)

## 📁 Project Structure

```
.
├── order-service/           # Order microservice
│   ├── src/
│   ├── k8s/                # Kubernetes manifests
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/    # Notification microservice
│   ├── src/
│   ├── k8s/                # Kubernetes manifests
│   ├── Dockerfile
│   └── pom.xml
└── k8s/                    # Shared K8s resources
    ├── rabbitmq/           # RabbitMQ deployment
    └── common/             # ConfigMaps, Secrets
```

## 🔄 Message Flow

1. User creates order via POST `/api/orders`
2. Order Service saves order and publishes `OrderEvent` to RabbitMQ
3. RabbitMQ routes message to `notification.queue`
4. Notification Service consumes message
5. Notification Service sends email/SMS notification

## 📝 License

MIT
