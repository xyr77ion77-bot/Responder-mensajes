# ReplyFlow MVP

Prototipo móvil de una aplicación multicanal para crear y administrar respuestas automáticas. Está construido exclusivamente con **HTML, CSS y JavaScript**, sin frameworks, dependencias ni compilación.

## Ejecutar

Abre `index.html` directamente o utiliza cualquier servidor estático:

```bash
python3 -m http.server 8080
```

Después visita `http://localhost:8080`.

## Pantallas

- **Respuestas:** interruptor maestro, filtros y reglas activas, pausadas o programadas.
- **Editor:** mensaje activador, respuesta, coincidencia, canales, listas personalizadas y horario.
- **Informes:** estadísticas, actividad semanal, rendimiento por regla y exportación CSV.
- **Ayuda:** primeros pasos, manual, preguntas frecuentes y solución de problemas.
- **Ajustes:** permisos simulados, comportamiento y exportación de datos.
- **Acceso a notificaciones:** reproducción visual del flujo que deberá implementar la versión Android.

## Funcionalidades

- Crear, editar, activar, pausar y eliminar respuestas.
- Coincidencia exacta, por contenido, patrón o cualquier mensaje.
- Selección de varios canales de mensajería.
- Listas de contactos permitidos o bloqueados.
- Horarios configurables por regla.
- Persistencia local mediante `localStorage`.
- Exportación de reglas JSON e informes CSV.
- Interfaz oscura mobile-first con estética inspirada en Discord: superficies grafito, navegación compacta y acento blurple.
- Presentación de escritorio centrada y sin contenido promocional ajeno a la aplicación.

## Alcance

Este MVP valida la interfaz y los flujos. Un navegador no puede leer ni responder notificaciones de otras aplicaciones. Para convertirlo en un producto real se necesitará una aplicación Android nativa con permisos explícitos y `NotificationListenerService`.

ReplyFlow es un producto independiente y no está afiliado a ninguna plataforma de mensajería.
