# ReplyFlow MVP

Prototipo móvil de una aplicación para crear y probar respuestas automáticas. Está construido exclusivamente con **HTML, CSS y JavaScript**, sin frameworks ni proceso de compilación.

## Ejecutar

Puedes abrir `index.html` directamente o servir el directorio con cualquier servidor estático:

```bash
python3 -m http.server 8080
```

Después abre `http://localhost:8080`.

## Funcionalidades

- Lista de reglas con estado activo o pausado.
- Interruptor maestro del servicio.
- Creación y edición de reglas.
- Coincidencia exacta, por contenido, similitud, patrón o cualquier mensaje.
- Una o varias respuestas: primera, todas o aleatoria.
- Audiencias y retrasos configurables.
- Eliminación mediante gesto lateral o desde el editor.
- Simulador de conversación con motor de coincidencias funcional.
- Persistencia local mediante `localStorage`.
- Exportación de reglas en JSON.
- Interfaz móvil responsive inspirada en Material Design.

## Alcance

Este MVP valida la experiencia de usuario. El acceso real a notificaciones y el envío de respuestas requieren una implementación Android nativa posterior. ReplyFlow es un producto independiente y no está afiliado a WhatsApp ni a Meta.
