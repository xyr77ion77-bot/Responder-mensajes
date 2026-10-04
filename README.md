# ReplyFlow para Android

Aplicación Android nativa, offline-first, que evalúa reglas locales y responde mediante las acciones `RemoteInput` publicadas por las notificaciones de aplicaciones compatibles. No usa root, accesibilidad, Firebase, telemetría ni servidores propios.

> ReplyFlow es independiente y no está afiliada a WhatsApp, Telegram, Meta, Google ni ninguna plataforma de mensajería.

## Requisitos

- Android Studio Ladybug o posterior.
- JDK 17.
- Android SDK 35 y Build Tools recientes.
- Dispositivo Android 8.0+ (`minSdk 26`).

## Instalar sin Android Studio

No necesitas Android Studio, JDK ni Gradle para probar ReplyFlow:

1. Abre la ejecución más reciente de **GitHub Actions → Android**.
2. En la sección **Artifacts**, descarga `replyflow-installable`.
3. Descomprime el archivo ZIP.
4. Copia `ReplyFlow-installable.apk` al teléfono.
5. Android solicitará permiso para instalar aplicaciones desde el navegador o gestor de archivos utilizado. Actívalo únicamente para completar esta instalación.
6. Instala el APK y después concede el acceso a notificaciones desde ReplyFlow.

`ReplyFlow-installable.apk` es un APK debug firmado automáticamente por Android Gradle Plugin. Sirve para pruebas directas, pero no para publicar en Google Play. El archivo `ReplyFlow-release-unsigned.apk` está optimizado con R8, pero necesita una firma de distribución antes de instalarse.

## Compilar manualmente — opcional

```bash
# APK instalable de pruebas
./gradlew assembleDebug

# Pruebas del motor
./gradlew testDebugUnitTest

# Release optimizado, sin firma de distribución
./gradlew assembleRelease
```

Los APK se generan en `app/build/outputs/apk/`. El proyecto minimiza dependencias y activa R8/resource shrinking. La compilación release de CI se mantiene por debajo del objetivo de 3 MB. El tamaño puede variar ligeramente al añadir una firma de distribución.

El script `gradlew` incluido es un bootstrap ligero que descarga Gradle 8.9 en `~/.gradle` la primera vez. No modifica el repositorio.

## Activar el acceso a notificaciones

1. Instala y abre ReplyFlow.
2. En Inicio o Ajustes, pulsa **Acceso a notificaciones**.
3. Android abrirá `Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS`.
4. Activa ReplyFlow y confirma el aviso del sistema.
5. Crea una respuesta, selecciona sus canales y activa la regla.

ReplyFlow solo puede responder si la aplicación origen publica una `Notification.Action` con `RemoteInput`. Si no existe la acción **Responder/Reply**, el evento se registra localmente y no se usa accesibilidad como alternativa.

## Arquitectura

```text
app/src/main/java/com/replyflow/app/
├── service/   ReplyNotificationListener, AutoReplyEngine, BootReceiver
├── data/      RuleRepository, AiConfigRepository, SettingsRepository, JSON
├── ai/        AiClient (HttpURLConnection), PromptBuilder
├── ui/        Compose screens, Navigation y ViewModels
├── ui/theme/  Tema Discord-like
└── util/      Matcher, TimeWindow, ChannelMapper, DuplicateCache, EventLogger
```

### Persistencia

Los datos viven en `filesDir`:

- `rules.json`
- `ai_config.json` — nunca contiene la clave.
- `settings.json`
- `events.log`

Cada JSON se escribe primero en `.tmp`, se sincroniza con `fsync` y se renombra. La clave API se almacena con `EncryptedSharedPreferences` y queda excluida de backups.

## Motor de respuestas

- Evalúa de arriba hacia abajo y se detiene en la primera coincidencia.
- Soporta `EXACT`, `CONTAINS`, `PATTERN` (`|` y `*`) y `ANY`.
- Aplica canal, estado, horario —incluido cruce de medianoche—, lista denegada y lista permitida.
- Deduplica por paquete, remitente, texto y minuto durante cinco minutos.
- Limita a una respuesta por paquete cada dos segundos.
- Incrementa `sent` solo después de un envío confirmado.

## IA

La IA se configura dentro del editor de una respuesta. El asistente incluye OpenRouter, Gemini y endpoint personalizado. Las llamadas usan `HttpURLConnection`, timeout de 8 segundos y límite total de 6 segundos desde el motor. Ante cualquier fallo se envía `rule.reply` como respaldo.

La única salida de red de ReplyFlow ocurre cuando una regla con IA está activa y el usuario configuró una API.

## Exportar e importar

En **Ajustes → Datos** se puede exportar `rules.json`, importarlo desde el selector del sistema o restaurar las reglas de demostración de `app/src/main/assets/rules.json`.

## Prototipo web

Los archivos originales usados como referencia visual permanecen en `prototype-web/`; no se incluyen en el APK.

## Limitaciones conocidas

- Algunas apps ocultan la acción de respuesta según el tipo de notificación, privacidad, conversación silenciada o versión de Android.
- Android y los fabricantes pueden restringir procesos en segundo plano. El modo estricto es opcional y muestra una notificación persistente.
- La configuración de IA no debe usar claves con privilegios o saldo ilimitado.
