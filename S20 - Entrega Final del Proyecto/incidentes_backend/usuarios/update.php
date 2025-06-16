<?php
include('../config/db.php');

// Leer el cuerpo JSON y convertir a objeto
$rawJson = file_get_contents("php://input");

// Decodificar JSON a objeto PHP
$data = json_decode($rawJson);
if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(["success" => false, "message" => "JSON inválido: " . json_last_error_msg()]);
    exit;
}

// Validar campos obligatorios
$usuario_id = $data->id ?? null;
$nombre = $data->nombre ?? null;
$email = $data->email ?? null;
$rol = $data->rol ?? null;
$password = !empty($data->password) ? password_hash($data->password, PASSWORD_DEFAULT) : null;
$avatar_base64 = $data->avatar ?? null;

if (!$usuario_id || !$nombre || !$email || !$rol) {
    echo json_encode(["success" => false, "message" => "Faltan campos obligatorios (id, nombre, email o rol)."]);
    exit;
}

// Decodificar avatar base64 a binario
$avatar = null;
if ($avatar_base64) {
    $avatar = base64_decode($avatar_base64, true);
    if ($avatar === false) {
        echo json_encode(["success" => false, "message" => "El avatar no es un Base64 válido."]);
        exit;
    }
}

// Preparar consulta dinámica según datos recibidos
if ($password && $avatar !== null) {
    $sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ?, rol = ?, avatar = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        echo json_encode(["success" => false, "message" => "Error preparando la consulta: " . $conn->error]);
        exit;
    }
    $null = NULL;
    $stmt->bind_param("ssssbi", $nombre, $email, $password, $rol, $null, $usuario_id);
    $stmt->send_long_data(4, $avatar); // índice 4 = parámetro avatar
} elseif ($password) {
    $sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ?, rol = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        echo json_encode(["success" => false, "message" => "Error preparando la consulta: " . $conn->error]);
        exit;
    }
    $stmt->bind_param("ssssi", $nombre, $email, $password, $rol, $usuario_id);
} elseif ($avatar !== null) {
    $sql = "UPDATE usuarios SET nombre = ?, email = ?, rol = ?, avatar = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        echo json_encode(["success" => false, "message" => "Error preparando la consulta: " . $conn->error]);
        exit;
    }
    $null = NULL;
    $stmt->bind_param("sssbi", $nombre, $email, $rol, $null, $usuario_id);
    $stmt->send_long_data(3, $avatar); // índice 3 = parámetro avatar
} else {
    $sql = "UPDATE usuarios SET nombre = ?, email = ?, rol = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        echo json_encode(["success" => false, "message" => "Error preparando la consulta: " . $conn->error]);
        exit;
    }
    $stmt->bind_param("sssi", $nombre, $email, $rol, $usuario_id);
}

// Ejecutar consulta
if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Usuario actualizado exitosamente"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al actualizar usuario: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
