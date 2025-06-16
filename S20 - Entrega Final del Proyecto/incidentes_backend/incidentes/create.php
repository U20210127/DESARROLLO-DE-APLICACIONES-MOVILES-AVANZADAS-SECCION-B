<?php
include('../config/db.php');

// Leer los datos JSON de la solicitud POST
$data = json_decode(file_get_contents("php://input"), true);

// Verificar si la solicitud JSON se ha recibido correctamente
if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(["success" => false, "message" => "Error al leer los datos JSON"]);
    exit;
}

// Verificar si se han recibido los campos obligatorios (incluye imagen)
if (
    !isset($data['tipo']) || 
    !isset($data['descripcion']) || 
    !isset($data['usuario_id']) || 
    !isset($data['fecha']) || 
    !isset($data['imagen'])
) {
    echo json_encode(["success" => false, "message" => "Faltan campos obligatorios"]);
    exit;
}

// Sanitizar y asignar variables
$tipo = htmlspecialchars(trim($data['tipo']));
$descripcion = htmlspecialchars(trim($data['descripcion']));
$fecha = trim($data['fecha']);
$usuario_id = (int) $data['usuario_id'];
$imagen_base64 = trim($data['imagen']);

// Validar formato de fecha (YYYY-MM-DD)
if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $fecha)) {
    echo json_encode(["success" => false, "message" => "Formato de fecha inválido"]);
    exit;
}

// Decodificar imagen (base64)
$imagen = base64_decode($imagen_base64);
if ($imagen === false) {
    echo json_encode(["success" => false, "message" => "Imagen en formato Base64 inválido"]);
    exit;
}

// Verificar que el usuario existe
$checkQuery = $conn->prepare("SELECT id FROM usuarios WHERE id = ?");
$checkQuery->bind_param("i", $usuario_id);
$checkQuery->execute();
$result = $checkQuery->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Usuario no encontrado"]);
    exit;
}

// Preparar la consulta para insertar incidente con imagen (BLOB)
$query = $conn->prepare("INSERT INTO incidentes (tipo, descripcion, fecha, usuario_id, imagen) VALUES (?, ?, ?, ?, ?)");
$query->bind_param("sssib", $tipo, $descripcion, $fecha, $usuario_id, $imagen);
$query->send_long_data(4, $imagen); // 5to parámetro (índice 4)

// Ejecutar la consulta y devolver respuesta
if ($query->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Incidente creado exitosamente"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Error al crear incidente: " . $conn->error
    ]);
}
?>

