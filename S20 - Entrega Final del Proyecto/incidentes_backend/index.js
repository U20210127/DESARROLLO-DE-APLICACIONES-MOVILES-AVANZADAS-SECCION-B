const admin = require("firebase-admin");
const serviceAccount = require("./notificacion.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const message = {
  notification: {
    title: "Nuevo Incidente",
    body: "Se ha registrado un nuevo incidente."
  },
  topic: "incidentes"
};

admin.messaging().send(message)
  .then((response) => {
    console.log("Notificación enviada:", response);
  })
  .catch((error) => {
    console.error("Error al enviar notificación:", error);
  });