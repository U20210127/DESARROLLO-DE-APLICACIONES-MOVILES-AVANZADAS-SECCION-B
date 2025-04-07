# Sistema de Notificaciones Push para Android

Este proyecto incluye una aplicación Android en Kotlin que recibe notificaciones push y un servidor Node.js que permite enviar estas notificaciones a los dispositivos registrados.

## Estructura del Proyecto

- **App Android**: Aplicación cliente que recibe las notificaciones.
- **Servidor Node.js**: Servidor que envía las notificaciones push usando Firebase Cloud Messaging.

## Requisitos

### Para la App Android
- Android Studio (versión 4.0 o superior)
- Dispositivo Android con Android 6.0 (API nivel 23) o superior
- Firebase Project

### Para el Servidor Node.js
- Node.js (versión 14.x o superior)
- npm (incluido con Node.js)
- Cuenta de Firebase (la misma usada para la app Android)

## Configuración

### 1. Configurar Firebase

1. Ve a la [consola de Firebase](https://console.firebase.google.com/) y crea un nuevo proyecto (o usa uno existente).
2. Agrega una aplicación Android al proyecto:
   - Usa el paquete `net.lrivas.notificaciones_push`
   - Descarga el archivo `google-services.json`
3. Agrega una aplicación Web también (para el servidor).
4. Ve a Configuración del proyecto > Cuentas de servicio > Firebase Admin SDK.
5. Genera una nueva clave privada y descarga el archivo JSON.

### 2. Configuración de la App Android

1. Coloca el archivo `google-services.json` descargado en la carpeta `app/` de tu proyecto Android.
2. Abre el proyecto en Android Studio.
3. Asegúrate de que todas las dependencias en los archivos build.gradle estén correctamente configuradas.
4. Compila y ejecuta la aplicación en tu dispositivo.

### 3. Configuración del Servidor Node.js

1. Coloca el archivo de credenciales de Firebase (cuenta de servicio) en la raíz del proyecto del servidor.
2. Renómbralo a `firebase-credentials.json` o actualiza la ruta en `server.js`.
3. Instala las dependencias:

```bash
npm install
```

4. Inicia el servidor:

```bash
npm start
```

## Uso de la Aplicación

### App Android

1. Abre la aplicación en tu dispositivo.
2. Presiona el botón "OBTENER TOKEN" para generar y ver tu token FCM.
3. Puedes copiar este token usando el botón "COPIAR TOKEN".

### Servidor Web

1. Accede a la interfaz web en `http://localhost:3000`.
2. Completa el formulario con:
   - **Asunto**: Título de la notificación.
   - **Mensaje**: Contenido de la notificación.
   - **Token** (opcional): Token específico del dispositivo. Si lo dejas en blanco, se enviará a todos los dispositivos suscritos.
3. Presiona "Enviar Notificación".

## Cómo Modificar el Proyecto

### Personalizar la App Android

- Puedes modificar el diseño en `app/src/main/res/layout/activity_main.xml`.
- Para cambiar la lógica de notificaciones, edita `MyFirebaseMessagingService.kt`.

### Personalizar el Servidor

- Para cambiar la interfaz web, edita `public/index.html`.
- Para modificar la lógica de envío de notificaciones, edita `server.js`.

## Despliegue en Producción

### App Android

Para generar un APK firmado:

1. En Android Studio, ve a Build > Generate Signed Bundle / APK.
2. Sigue las instrucciones para crear una clave de firma si no tienes una.
3. Selecciona APK y completa el proceso.

### Servidor Node.js

Para desplegar el servidor:

1. Configura variables de entorno para producción.
2. Considera usar un servicio como Heroku, Vercel, o AWS para el despliegue.
3. Configura un dominio personalizado si es necesario.

## Solución de Problemas

### App Android

- Si no recibes notificaciones, asegúrate de que la aplicación tenga permisos de notificación.
- Verifica que el archivo `google-services.json` esté correctamente colocado.
- Revisa los logs de la aplicación para ver si hay errores relacionados con FCM.

### Servidor Node.js

- Verifica que las credenciales de Firebase sean correctas.
- Asegúrate de que el token FCM proporcionado sea válido.
- Revisa los logs del servidor para identificar posibles errores.