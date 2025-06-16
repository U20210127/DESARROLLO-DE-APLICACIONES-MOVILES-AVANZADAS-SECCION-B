<?php
header("Content-Type: application/json");
include('../config/db.php');

// Validar entrada
if (!isset($_POST['nombre_usuario'])) {
    echo json_encode(["error" => "Nombre de usuario no enviado"]);
    exit;
}

$nombre_usuario = $_POST['nombre_usuario']; // Recibir nombre

// Consulta para obtener el id y nombre del usuario
$sql = "SELECT id, nombre FROM usuarios WHERE nombre = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $nombre_usuario);  // "s" para string
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["id" => $row['id'], "nombre" => $row['nombre']]);
} else {
    echo json_encode(["error" => "Usuario no encontrado"]);
}
?>
