<?php
include('../config/db.php');
$data = json_decode(file_get_contents("php://input"));

$id = $data->id;
$estatus = $data->estatus;
$resolucion = $data->resolucion;
$imagen_base64 = isset($data->imagen) ? $data->imagen : null;

if ($imagen_base64) {
    // Eliminar el encabezado si existe (ej: data:image/png;base64,...)
    if (strpos($imagen_base64, 'base64,') !== false) {
        $imagen_base64 = explode('base64,', $imagen_base64)[1];
    }

    $imagen_binario = base64_decode($imagen_base64);

    $query = $conn->prepare("UPDATE incidentes SET estatus = ?, resolucion = ?, imagen = ? WHERE id = ?");
    $query->bind_param("sssi", $estatus, $resolucion, $imagen_binario, $id);
} else {
    // Si no se envía imagen, actualizar sin tocar el campo imagen
    $query = $conn->prepare("UPDATE incidentes SET estatus = ?, resolucion = ? WHERE id = ?");
    $query->bind_param("ssi", $estatus, $resolucion, $id);
}

if ($query->execute()) {
    echo json_encode(["success" => true]);
} else {
    echo json_encode(["success" => false, "message" => "Error al actualizar estado"]);
}
?>

