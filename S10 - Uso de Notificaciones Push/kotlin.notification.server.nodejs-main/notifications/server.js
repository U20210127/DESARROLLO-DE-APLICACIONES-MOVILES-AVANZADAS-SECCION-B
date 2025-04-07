const express = require('express');
const admin = require('firebase-admin');
const cors = require('cors');
const path = require('path');
const bodyParser = require('body-parser');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

const serviceAccount = require('./firebase-credentials.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.post('/api/send-notification', async (req, res) => {
  try {
    const { titulo, mensaje, token } = req.body;

    // Validación de datos
    if (!titulo || !mensaje) {
      return res.status(400).json({ 
        success: false, 
        message: 'Los campos título y mensaje son obligatorios' 
      });
    }

    let message = {
      notification: {
        title: titulo,
        body: mensaje
      },
      data: {
        tipo: 'notificacion',
        mensaje: mensaje
      }
    };

    let response;


    if (token) {
      message.token = token;
      response = await admin.messaging().send(message);
    } else {

      message.topic = 'all_devices';
      response = await admin.messaging().send(message);
    }

    console.log('Notificación enviada correctamente:', response);
    res.status(200).json({ 
      success: true, 
      message: 'Notificación enviada correctamente',
      response 
    });
  } catch (error) {
    console.error('Error al enviar la notificación:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Error al enviar la notificación', 
      error: error.message 
    });
  }
});


app.post('/api/subscribe', async (req, res) => {
  try {
    const { token } = req.body;

    if (!token) {
      return res.status(400).json({ 
        success: false, 
        message: 'El token es requerido' 
      });
    }

    const response = await admin.messaging().subscribeToTopic(token, 'all_devices');
    console.log('Dispositivo suscrito correctamente:', response);
    
    res.status(200).json({ 
      success: true, 
      message: 'Dispositivo suscrito correctamente',
      response 
    });
  } catch (error) {
    console.error('Error al suscribir el dispositivo:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Error al suscribir el dispositivo', 
      error: error.message 
    });
  }
});

app.listen(PORT, () => {
  console.log(`Servidor iniciado en http://localhost:${PORT}`);
});