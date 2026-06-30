# Flujograma de Estados de Solicitud

## Actores
- **Vendedor** (crea solicitud, revisa observaciones, marca firma)
- **Analista** (asigna, notifica, valida, aprueba)
- **Sistema** (notificaciones automáticas por Telegram)

```mermaid
flowchart TD
    %% Estados
    CREADA["CREADA"]
    ASIGNADA["ASIGNADA"]
    NOTIFICADA["NOTIFICADA"]
    EN_PROCESO["EN PROCESO"]
    VALIDADA["VALIDADA"]
    FIRMA_RECIBIDA["FIRMA RECIBIDA"]
    APROBADA["APROBADA"]
    RECHAZADA["RECHAZADA"]

    %% Transiciones con actores y notificaciones
    V1["Vendedor: crea solicitud"] --> CREADA
    CREADA -->|Sistema: asigna analista| ASIGNADA
    ASIGNADA -->|Analista: clic 'Notificar'| NOTIFICADA
    ASIGNADA -->|Analista: clic 'Validar'| VALIDADA
    ASIGNADA -->|Analista: clic 'Rechazar'| RECHAZADA
    NOTIFICADA -->|Sistema: Telegram → Grupo Aliado| T1["📨 Observación registrada<br/>Por favor revisar"]

    NOTIFICADA -->|Vendedor: clic 'Revisado'| EN_PROCESO
    EN_PROCESO -->|Sistema: Telegram → Grupo Analistas| T2["📨 Vendedor revisó observación<br/>Estado: En proceso"]

    EN_PROCESO -->|Analista: clic 'Validar'| VALIDADA
    EN_PROCESO -->|Analista: clic 'Rechazar'| RECHAZADA
    VALIDADA -->|Sistema: Telegram → Grupo Aliado| T3["📨 Solicitud validada<br/>Instrucciones firma digital"]

    VALIDADA -->|Vendedor: clic 'Firmó'| FIRMA_RECIBIDA
    FIRMA_RECIBIDA -->|Analista: clic 'Notificar'| NOTIFICADA
    FIRMA_RECIBIDA -->|Analista: clic 'Aprobar'| APROBADA
    FIRMA_RECIBIDA -->|Analista: clic 'Rechazar'| RECHAZADA
    APROBADA -->|Sistema: Telegram → Grupo Aliado| T4["📨 SOLICITUD APROBADA<br/>Se puede enrolar"]

    %% Estilos
    style CREADA fill:#e2e8f0
    style ASIGNADA fill:#dbeafe
    style NOTIFICADA fill:#fef3c7
    style EN_PROCESO fill:#e0f2fe
    style VALIDADA fill:#dcfce7
    style FIRMA_RECIBIDA fill:#d1fae5
    style APROBADA fill:#bbf7d0
    style RECHAZADA fill:#fee2e2

    style T1 fill:#f0f9ff
    style T2 fill:#f0f9ff
    style T3 fill:#f0f9ff
    style T4 fill:#f0f9ff
```

## Tabla Resumen

| # | Origen | Acción | Actor | Destino | Notificación Telegram |
|---|--------|--------|-------|---------|---------------------|
| 1 | — | Crear solicitud | Vendedor | `CREADA` | — |
| 2 | `CREADA` | Asignar analista automático | Sistema | `ASIGNADA` | — |
| 3 | `ASIGNADA` | Notificar observación | Analista | `NOTIFICADA` | Grupo **Aliado** |
| 4 | `ASIGNADA` | Validar directamente | Analista | `VALIDADA` | Grupo **Aliado** |
| 5 | `ASIGNADA` | Rechazar solicitud | Analista | `RECHAZADA` | Grupo **Aliado** |
| 6 | `NOTIFICADA` | Marcar como revisada | Vendedor | `EN_PROCESO` | Grupo **Analistas** |
| 7 | `EN_PROCESO` | Validar solicitud | Analista | `VALIDADA` | Grupo **Aliado** |
| 8 | `EN_PROCESO` | Rechazar solicitud | Analista | `RECHAZADA` | Grupo **Aliado** |
| 9 | `VALIDADA` | Marcar firma recibida | Vendedor | `FIRMA_RECIBIDA` | — |
| 10 | `FIRMA_RECIBIDA` | Notificar observación de firma | Analista | `NOTIFICADA` | Grupo **Aliado** |
| 11 | `FIRMA_RECIBIDA` | Aprobar solicitud | Analista | `APROBADA` | Grupo **Aliado** |
| 12 | `FIRMA_RECIBIDA` | Rechazar solicitud | Analista | `RECHAZADA` | Grupo **Aliado** |
