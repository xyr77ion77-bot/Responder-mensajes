# ReplyFlow

Prototipo web responsive de una aplicación para crear y administrar respuestas automáticas, inspirado en el flujo de trabajo de las herramientas de autorespuesta móvil.

## Funcionalidades incluidas

- Dashboard con métricas, actividad reciente y rendimiento semanal.
- Creación y edición de reglas de respuesta.
- Tipos de coincidencia: contiene, exacta, patrón y cualquier mensaje.
- Activación, pausa y eliminación de reglas.
- Audiencias configurables (todos, contactos, desconocidos y grupos).
- Historial de actividad, contactos y configuración.
- Diseño responsive optimizado para escritorio y móvil.

## Ejecutar localmente

```bash
npm install
npm run dev
```

Para generar la versión de producción:

```bash
npm run build
```

## Nota de arquitectura

Esta entrega implementa la experiencia de producto como SPA. Para convertirla en una aplicación Android capaz de responder mensajes reales se necesita una capa nativa (por ejemplo, Kotlin) con `NotificationListenerService`, permiso de acceso a notificaciones, almacenamiento local y envío mediante acciones de respuesta de las notificaciones. La automatización debe respetar los términos de servicio y las políticas de privacidad de las plataformas de mensajería; ReplyFlow no debe presentarse como producto oficial o afiliado a WhatsApp/Meta.
