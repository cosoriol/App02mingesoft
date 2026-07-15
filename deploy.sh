#!/bin/bash
set -e

# ============================================================
# Script de build + push de las 11 imagenes de App02Mingesoft a Docker Hub
# ============================================================
# A diferencia de App01Mingesoft (2 imagenes, monolito), aca cada
# microservicio tiene su propia imagen. docker-compose.yml ya tiene
# "image:" ademas de "build:" en cada servicio, asi que "docker-compose
# build" construye y etiqueta con esos nombres en un solo paso; aca solo
# falta empujarlas.

echo "Iniciando despliegue de TravelAgency App02 (microservicios)..."

echo "Construyendo imagenes Docker..."
docker-compose build \
  config-server eureka-server api-gateway user-service \
  package-service search-service booking-service payment-service \
  confirmation-service report-service frontend

echo "Pusheando imagenes a Docker Hub..."
echo "(requiere 'docker login' previo con una cuenta con permiso sobre cosoriol/*)"
for image in \
  cosoriol/travel-agency-app02-config-server \
  cosoriol/travel-agency-app02-eureka-server \
  cosoriol/travel-agency-app02-api-gateway \
  cosoriol/travel-agency-app02-user-service \
  cosoriol/travel-agency-app02-package-service \
  cosoriol/travel-agency-app02-search-service \
  cosoriol/travel-agency-app02-booking-service \
  cosoriol/travel-agency-app02-payment-service \
  cosoriol/travel-agency-app02-confirmation-service \
  cosoriol/travel-agency-app02-report-service \
  cosoriol/travel-agency-app02-frontend
do
  docker push "$image:latest"
done

echo "Despliegue completado. Imagenes listas en Docker Hub."
echo "Proximos pasos en el servidor:"
echo "  docker-compose up -d"
echo "  docker-compose ps"
