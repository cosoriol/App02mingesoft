#!/bin/bash
# ============================================================
# Datos de prueba para App02Mingesoft (Epicas 1-7, microservicios)
# ============================================================
# Mismo enfoque que App01Mingesoft/seed-data.sh: siembra via la API real
# (a traves del API Gateway, puerto 8080) en vez de INSERTs SQL crudos,
# para que cada fila pase por las reglas de negocio reales de cada
# servicio (hash de password, calculo de descuentos via booking-service,
# confirmacion automatica via payment-service). Idempotente: usuarios y
# paquetes ya existentes se detectan y no se duplican.
set -e

BASE_URL="http://localhost:8080/api"
# "Inside.2009" no pasa la validacion real de fortaleza (el "." no esta en el
# set de caracteres especiales permitidos @#$%^&+=!*) -- se usa una variante
# que si la cumple, igual que en App01Mingesoft/seed-data.sh.
PASSWORD="Inside2009!"

psql_query() {
  docker exec postgres psql -U postgres -d "$1" -tAc "$2" 2>/dev/null
}

ensure_user() {
  local full_name="$1" email="$2"
  local existing_id
  existing_id=$(psql_query user_db "SELECT id FROM users WHERE email='$email';")
  if [ -n "$existing_id" ]; then
    echo "$existing_id"
    return
  fi
  curl -s -X POST "$BASE_URL/auth/register" -H "Content-Type: application/json" -d "{
    \"fullName\": \"$full_name\",
    \"email\": \"$email\",
    \"password\": \"$PASSWORD\"
  }" >/dev/null
  psql_query user_db "SELECT id FROM users WHERE email='$email';"
}

echo "== Usuarios =="
# admin@travelagency.cl y cliente@travelagency.cl ya existen de pruebas anteriores
# (ver App02Mingesoft, sesion de hoy); no se recrean.
JUAN_ID=$(ensure_user "Juan Pérez" "juan@example.com")
MARIA_ID=$(ensure_user "María García" "maria@example.com")
CARLOS_ID=$(ensure_user "Carlos López" "carlos@example.com")
echo "  juan=$JUAN_ID  maria=$MARIA_ID  carlos=$CARLOS_ID"

# Crea un paquete si no existe uno con ese nombre; devuelve su id.
# package-service no exige rol/admin para crear (ver PackageController: sin
# @RequestParam de autorizacion), a diferencia de App01.
ensure_package() {
  local name="$1" destination="$2" description="$3" start="$4" end="$5" price="$6" \
        slots="$7" services="$8" restrictions="$9" type="${10}" season="${11}"
  local existing_id
  existing_id=$(psql_query package_db "SELECT id FROM travel_packages WHERE name='$name' LIMIT 1;")
  if [ -n "$existing_id" ]; then
    echo "$existing_id"
    return
  fi
  curl -s -X POST "$BASE_URL/packages" -H "Content-Type: application/json" -d "{
    \"name\": \"$name\", \"destination\": \"$destination\", \"description\": \"$description\",
    \"startDate\": \"$start\", \"endDate\": \"$end\", \"price\": $price, \"totalSlots\": $slots,
    \"includedServices\": \"$services\", \"restrictions\": \"$restrictions\",
    \"travelType\": \"$type\", \"season\": \"$season\"
  }" | python3 -c "import json,sys; print(json.load(sys.stdin)['id'])"
}

echo "== Paquetes =="
P1=$(ensure_package "Tour Machu Picchu 5 días" "Perú" "Incluye vuelos y hotel 5 estrellas" \
  "2026-08-15" "2026-08-20" 1500.00 20 "Vuelos,Hotel,Tours,Seguro" "Mayor de 18 años" "Aventura" "Verano")
P2=$(ensure_package "Playa Cancún 7 días" "México" "Resort todo incluido en Cancún" \
  "2026-09-01" "2026-09-08" 1200.00 30 "Resort,Playa,Actividades acuáticas" "Ninguna" "Playa" "Verano")
P3=$(ensure_package "Europa Clásica 10 días" "Europa" "París, Ámsterdam, Berlín, Praga" \
  "2026-07-15" "2026-07-25" 2500.00 15 "Vuelos,Hotels,Tours,Guía" "Pasaporte requerido" "Cultural" "Verano")
P4=$(ensure_package "Cartagena Romántica 4 días" "Colombia" "Conoce la ciudad amurallada" \
  "2026-09-15" "2026-09-19" 800.00 25 "Hotel,Tours,Cena romántica" "Ninguna" "Romántico" "Otoño")
P5=$(ensure_package "Atacama Aventura 3 días" "Chile" "Desierto de Atacama con astrónomos" \
  "2026-08-10" "2026-08-13" 600.00 20 "Hotel,Tours,Comidas" "Altura - consultar médico" "Aventura" "Verano")
P6=$(ensure_package "Crucero Caribe 7 días" "Caribe" "Crucero con paradas en 4 islas" \
  "2026-10-05" "2026-10-12" 1800.00 40 "Crucero,Cabina,Comidas,Actividades" "Ninguna" "Playa" "Otoño")
echo "  paquetes: $P1 $P2 $P3 $P4 $P5 $P6"

create_booking() {
  local user_id="$1" package_id="$2" passengers="$3"
  curl -s -X POST "$BASE_URL/bookings?userId=$user_id" -H "Content-Type: application/json" -d "{
    \"packageId\": $package_id, \"passengerCount\": $passengers
  }" | python3 -c "import json,sys; d=json.load(sys.stdin); print(d['id'], d['totalAmount'])"
}

pay_booking() {
  local booking_id="$1" amount="$2"
  curl -s -X POST "$BASE_URL/payments" -H "Content-Type: application/json" -d "{
    \"bookingId\": $booking_id, \"amount\": $amount,
    \"cardNumber\": \"4111111111111111\", \"expirationDate\": \"12/29\", \"cvv\": \"123\",
    \"cardHolderName\": \"Test User\"
  }" >/dev/null
}

echo "== Reservas y pagos =="
EXISTING=$(psql_query booking_db "SELECT COUNT(*) FROM bookings WHERE user_id IN ('$JUAN_ID','$MARIA_ID','$CARLOS_ID');")
if [ "$EXISTING" -gt 0 ]; then
  echo "  ya hay $EXISTING reservas de juan/maria/carlos, no se vuelven a crear"
else
  read -r B1 B1_AMT <<< "$(create_booking "$JUAN_ID" "$P1" 2)";   pay_booking "$B1" "$B1_AMT"
  read -r B2 B2_AMT <<< "$(create_booking "$JUAN_ID" "$P3" 1)";   pay_booking "$B2" "$B2_AMT"
  read -r B3 B3_AMT <<< "$(create_booking "$MARIA_ID" "$P2" 4)";  pay_booking "$B3" "$B3_AMT"
  read -r B4 B4_AMT <<< "$(create_booking "$MARIA_ID" "$P5" 3)"   # queda PENDING (sin pagar)
  read -r B5 B5_AMT <<< "$(create_booking "$CARLOS_ID" "$P4" 2)"; pay_booking "$B5" "$B5_AMT"
  read -r B6 B6_AMT <<< "$(create_booking "$CARLOS_ID" "$P6" 5)"; pay_booking "$B6" "$B6_AMT"

  # Reserva cancelada: se crea y se cancela de inmediato
  read -r B7 B7_AMT <<< "$(create_booking "$JUAN_ID" "$P4" 1)"
  curl -s -X PATCH "$BASE_URL/confirmations/$B7/cancel?userId=$JUAN_ID&role=CLIENT" >/dev/null

  # Reserva expirada: se fuerza el estado directo en la base (no hay tarea programada
  # de expiracion automatica en este proyecto todavia); se liberan los cupos a mano.
  read -r B8 B8_AMT <<< "$(create_booking "$MARIA_ID" "$P1" 3)"
  psql_query booking_db "UPDATE bookings SET status='EXPIRED', created_at = created_at - INTERVAL '1 hour' WHERE id=$B8;"
  psql_query package_db "UPDATE travel_packages SET booked_slots = booked_slots - 3 WHERE id=$P1;"

  echo "  reservas creadas: $B1 $B2 $B3 $B4(pendiente) $B5 $B6 $B7(cancelada) $B8(expirada)"
fi

echo ""
echo "== Resumen =="
psql_query booking_db "SELECT status, COUNT(*) FROM bookings GROUP BY status;"
echo ""
echo "Credenciales de prueba:"
echo "  ADMIN:  admin@travelagency.cl (password de la sesion en que se creo)"
echo "  CLIENT: juan@example.com / maria@example.com / carlos@example.com, password: $PASSWORD"
