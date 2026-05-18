#!/bin/bash
#ESTO SE DEBE ELIMINAR POR COMPLETO CUANDO LA BASE DE DATOS LA CREAMOS CON USER DESDE UN INICIO
#SOLO SIRVE PARA INICIALIZAR "EN FRIO", SOLO USAR PARA TESTING.
echo "⏳ Esperando a Mongo..."
echo "ADMIN USER: ${MONGO_ROOT_USER}"
echo "ADMIN PASS: ${MONGO_ROOT_PASS}"
# Esperar a que Mongo responda
until mongosh --host transfer-mongo:27017 -u ${MONGO_ROOT_USER} -p ${MONGO_ROOT_PASS} --authenticationDatabase admin --eval "db.adminCommand('ping').ok" | grep 1; do
  echo "⏳ Mongo aún no responde..."
  sleep 2
done

mongosh --host transfer-mongo:27017 -u ${MONGO_ROOT_USER} -p ${MONGO_ROOT_PASS} --authenticationDatabase admin <<EOF
use ${MONGO_DB}
if (!db.getUser("${APP_USER}")) {
  db.createUser({
    user: "${APP_USER}",
    pwd: "${APP_PASS}",
    roles: [{ role: "readWrite", db: "${MONGO_DB}" }]
  })
  print("Usuario creado")
} else {
  print("Usuario ya existe")
}
EOF
echo "APPUSER LISTO listo"
echo "Setup completo"
