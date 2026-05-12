FASE 1 — Modelo y persistencia
Crear:
TransactionDocument
enums
repository
FASE 2 — Endpoint HTTP
POST /transfers
FASE 3 — JWT propagation

Igual que hiciste en accountService.

FASE 4 — Crear transacción PENDING

Guardar en Mongo.

FASE 5 — Publicar evento
transfer.requested
FASE 6 — Consumer en accountService

Consumir:

reserve funds
FASE 7 — respuesta async
transfer.funds.reserved
FASE 8 — completar saga

Aquí empieza la magia real.

IMPORTANTÍSIMO

NO intentes:

rollback
compensaciones
timeout
idempotencia avanzada

TODO a la vez.

Primero:

"happy path"

Haz:

crear transferencia
reservar fondos
confirmar
completar

Y LUEGO:

retries
rollback
timeouts
DLT
recovery