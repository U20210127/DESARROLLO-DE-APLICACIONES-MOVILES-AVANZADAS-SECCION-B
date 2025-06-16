<?php
header('Content-Type: application/json; charset=utf-8');
include('../config/db.php');

// Obtener entrada JSON
$data = json_decode(file_get_contents("php://input"));

// Verificar si se ha recibido el ID del usuario
if (!isset($data->id)) {
    echo json_encode(["success" => false, "message" => "Falta el campo 'id'"]);
    exit;
}

// Asignar variables
$user_id = $data->id;

// Consultar en la base de datos si el usuario tiene un avatar
$query = $conn->prepare("SELECT avatar FROM usuarios WHERE id = ?");
$query->bind_param("i", $user_id);
$query->execute();
$result = $query->get_result();

// Verificar si el usuario existe
if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    $avatar = $user['avatar'];

    // Comprobar si el avatar está vacío o es NULL
    if ($avatar) {
        echo json_encode([
            "success" => true,
            "message" => "El usuario tiene un avatar.",
            "avatar" => $avatar
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "El usuario no tiene un avatar."
        ]);
    }
} else {
    echo json_encode([
        "success" => false,
        "message" => "Usuario no encontrado."
    ]);
}
?>
