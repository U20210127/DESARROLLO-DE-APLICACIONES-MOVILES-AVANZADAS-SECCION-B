<?php
// Permitir solicitudes desde cualquier origen
header('Access-Control-Allow-Origin: *');
// Permitir métodos específicos
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
// Permitir ciertos encabezados
header('Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept');
// Tipo de contenido de respuesta
header('Content-Type: application/json');
?>


