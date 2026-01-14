# Microservices Demo Helm Chart

This Helm chart deploys a complete microservices demo with:
- Order Service (Spring Boot)
- Notification Service (Spring Boot)
- PostgreSQL (Database)
- RabbitMQ (Message Broker)

## Prerequisites

- Kubernetes 1.19+
- Helm 3.0+
- Docker images built and available in your registry

## Installation

### 1. Build Docker Images

```bash
# Build Order Service
cd order-service
docker build -t order-service:latest .

# Build Notification Service
cd notification-service
docker build -t notification-service:latest .
```

### 2. Install Chart

```bash
# Install with default values
helm install microservices-demo ./helm/microservices-demo

# Install with custom values
helm install microservices-demo ./helm/microservices-demo -f my-values.yaml

# Install in specific namespace
helm install microservices-demo ./helm/microservices-demo -n my-namespace --create-namespace
```

## Configuration

Key configuration options in `values.yaml`:

- **Replica Count**: Number of pods for each service
- **Resources**: CPU and memory limits/requests
- **Image Tags**: Docker image versions
- **Secrets**: Database and RabbitMQ credentials
- **Persistence**: Storage size for PostgreSQL and RabbitMQ

## Upgrading

```bash
helm upgrade microservices-demo ./helm/microservices-demo
```

## Uninstalling

```bash
helm uninstall microservices-demo
```

## Accessing Services

### Port Forwarding

```bash
# Order Service
kubectl port-forward svc/order-service 8081:8081

# Notification Service
kubectl port-forward svc/notification-service 8082:8082

# RabbitMQ Management UI
kubectl port-forward svc/rabbitmq-service 15672:15672
```

### Access URLs

- Order Service API: http://localhost:8081
- Order Service Swagger: http://localhost:8081/swagger-ui.html
- Notification Service API: http://localhost:8082
- Notification Service Swagger: http://localhost:8082/swagger-ui.html
- RabbitMQ Management: http://localhost:15672 (admin/admin123)

## Values

See `values.yaml` for all configurable parameters.
