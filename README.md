# TravelAgency - Infraestructura de Microservicios

Infraestructura base de microservicios para TravelAgency, construida con Spring Boot 3.2.5,
Spring Cloud 2023.0.0 (Leyton) y Java 17.

## Componentes

| Componente             | Puerto (local) | Descripcion                                   |
|------------------------|----------------|------------------------------------------------|
| config-server          | 8888           | Configuracion centralizada (Spring Cloud Config) |
| eureka-server          | 8761           | Descubrimiento de servicios (Netflix Eureka)   |
| api-gateway            | 8080           | Puerta de enlace (Spring Cloud Gateway)        |
| package-service        | 0 (aleatorio)  | Gestion de paquetes turisticos                 |
| search-service         | 0 (aleatorio)  | Busqueda de paquetes/ofertas                   |
| booking-service        | 0 (aleatorio)  | Reservas                                       |
| payment-service        | 0 (aleatorio)  | Pagos                                          |
| confirmation-service   | 0 (aleatorio)  | Confirmaciones de reserva                      |
| report-service         | 0 (aleatorio)  | Reportes                                       |
| frontend               | 3000           | React + Vite, servido con Nginx                |

Los 6 microservicios usan `server.port=0` (puerto aleatorio) y se registran en Eureka;
el API Gateway enruta hacia ellos mediante `lb://` (balanceo de carga vía Eureka).

## Requisitos previos

- Java 17
- Maven 3.8+
- Docker y Docker Compose
- Node.js 20+ (solo si se quiere correr el frontend fuera de Docker)
- Un clúster de Kubernetes (Minikube, Kind, Docker Desktop, etc.) para el despliegue en K8s

## Compilar el proyecto

```bash
mvn clean install -DskipTests
```

## Ejecutar con Docker Compose

Desde la raíz del proyecto:

```bash
docker compose up --build
```

Esto construye y levanta, en orden: `postgres` → `config-server` → `eureka-server` →
`api-gateway` → los 6 microservicios → `frontend`.

Servicios accesibles desde el host:

- Eureka Dashboard: http://localhost:8761
- Config Server: http://localhost:8888
- API Gateway: http://localhost:8080/api/...
- Frontend: http://localhost:3000
- PostgreSQL: localhost:5432 (usuario/clave: `postgres`/`postgres`)

Para detener y eliminar los contenedores:

```bash
docker compose down
```

Para eliminar también el volumen de datos de PostgreSQL:

```bash
docker compose down -v
```

## Desplegar en Kubernetes

Los manifiestos están en la carpeta `kubernetes/`. Antes de aplicarlos, construye y
publica (o carga en el clúster) las imágenes de cada módulo, por ejemplo:

```bash
docker build -t travelagency/config-server:latest -f config-server/Dockerfile .
docker build -t travelagency/eureka-server:latest -f eureka-server/Dockerfile .
docker build -t travelagency/api-gateway:latest -f api-gateway/Dockerfile .
docker build -t travelagency/package-service:latest -f package-service/Dockerfile .
docker build -t travelagency/search-service:latest -f search-service/Dockerfile .
docker build -t travelagency/booking-service:latest -f booking-service/Dockerfile .
docker build -t travelagency/payment-service:latest -f payment-service/Dockerfile .
docker build -t travelagency/confirmation-service:latest -f confirmation-service/Dockerfile .
docker build -t travelagency/report-service:latest -f report-service/Dockerfile .
docker build -t travelagency/frontend:latest ./frontend
```

Si usas Minikube, carga las imágenes con `minikube image load <imagen>` o construye
apuntando el daemon Docker al de Minikube (`eval $(minikube docker-env)`) antes del build,
ya que todos los manifiestos usan `imagePullPolicy: IfNotPresent`.

Aplica los manifiestos en orden:

```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/postgres.yaml
kubectl apply -f kubernetes/config-server.yaml
kubectl apply -f kubernetes/eureka-server.yaml
kubectl apply -f kubernetes/api-gateway.yaml
kubectl apply -f kubernetes/package-service.yaml
kubectl apply -f kubernetes/search-service.yaml
kubectl apply -f kubernetes/booking-service.yaml
kubectl apply -f kubernetes/payment-service.yaml
kubectl apply -f kubernetes/confirmation-service.yaml
kubectl apply -f kubernetes/report-service.yaml
kubectl apply -f kubernetes/frontend.yaml
```

O, de forma equivalente, aplicando toda la carpeta:

```bash
kubectl apply -f kubernetes/
```

Verificar el estado:

```bash
kubectl get all -n travelagency
```

Acceso externo (NodePort):

- API Gateway: `http://<IP-del-nodo>:31000`
- Frontend: `http://<IP-del-nodo>:31001`

Con Minikube, obtén la IP del nodo con `minikube ip`, o usa `minikube service api-gateway -n travelagency`
y `minikube service frontend -n travelagency`.

## Estructura del repositorio

```
App02Mingesoft/
├── pom.xml                     # POM padre (Spring Boot 3.2.5 / Spring Cloud 2023.0.0)
├── config-server/              # Servidor de configuracion (8888)
├── eureka-server/              # Servidor de descubrimiento (8761)
├── api-gateway/                # Puerta de enlace (8080)
├── package-service/            # Microservicio de paquetes
├── search-service/             # Microservicio de busqueda
├── booking-service/            # Microservicio de reservas
├── payment-service/            # Microservicio de pagos
├── confirmation-service/       # Microservicio de confirmaciones
├── report-service/             # Microservicio de reportes
├── frontend/                   # Frontend React (Vite)
├── config-files/               # Configuraciones leidas por el Config Server
├── postgres/                   # Script de inicializacion de bases de datos
├── kubernetes/                 # Manifiestos de Kubernetes
└── docker-compose.yml
```
