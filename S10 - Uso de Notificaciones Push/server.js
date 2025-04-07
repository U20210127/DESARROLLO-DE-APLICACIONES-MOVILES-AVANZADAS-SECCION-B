const express = require('express');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
const serviceAccount = require('./firebase-credentials.json');

const app = express();
const port = 3000;

// Inicializar Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('public'));

app.post('/send-notification', async (req, res) => {
  const { subject, message } = req.body;

  if (!subject || !message) {
    return res.status(400).send('Ambos campos son requeridos.');
  }

  const payload = {
    notification: {
      title: subject,
      body: message,
    },
  };

  try {
    const response = await admin.messaging().send({
      token: 'cluSHKJxTNeO36rfbO-K_o:APA91bG_p0lFyoQhynxwjioxf-BPDCzxOrx8FYaEZWCRSrdq7twlvPux9mpSfZbgCrkmsMet6sRP-G1DAVy3oJtZ-5Q4ev-qtes35vs3RPTDldOCP4tREUM', // Reemplaza con el token real
      notification: {
        title: subject,
        body: message,
      },
    });
    console.log('Notificación enviada con éxito:', response);
    res.send('Notificación enviada con éxito.');
  } catch (error) {
    console.error('Error al enviar la notificación:', error);
    res.status(500).send('Error al enviar la notificación.');
  }
  
});

app.listen(port, () => {
  console.log(`Servidor corriendo en http://localhost:${port}`);
});
