<?php
include('../config/db.php');

// Obtener los datos JSON de la solicitud POST
$data = json_decode(file_get_contents("php://input"), true);

// Verificar si la solicitud JSON se ha recibido correctamente
if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(["success" => false, "message" => "Error al leer los datos JSON"]);
    exit;
}

// Verificar si se han recibido los campos obligatorios
if (!isset($data['nombre']) || !isset($data['email']) || !isset($data['password']) || !isset($data['rol'])) {
    echo json_encode(["success" => false, "message" => "Faltan campos obligatorios"]);
    exit;
}

// Sanitizar y asignar las variables
$nombre = htmlspecialchars(trim($data['nombre']));
$email = filter_var(trim($data['email']), FILTER_SANITIZE_EMAIL);
$password = password_hash(trim($data['password']), PASSWORD_DEFAULT); // Hashear la contraseña
$rol = htmlspecialchars(trim($data['rol']));

// Verificar si el email tiene un formato válido
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["success" => false, "message" => "El formato del email no es válido"]);
    exit;
}

// Preparar la consulta SQL para insertar el nuevo usuario (sin avatar)
$query = $conn->prepare("INSERT INTO usuarios(nombre, email, password, rol) VALUES (?, ?, ?, ?)");
$query->bind_param("ssss", $nombre, $email, $password, $rol);

// Ejecutar la consulta y verificar el resultado
if ($query->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Usuario creado exitosamente"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Error al crear usuario: " . $conn->error
    ]);
}
?>
