<?php
include('../config/db.php');

// Comprobamos si el parámetro 'usuario_id' está presente en la solicitud GET
if (isset($_GET['usuario_id']) && !empty($_GET['usuario_id'])) {
    $usuario_id = (int) $_GET['usuario_id'];  // Convertimos el parámetro a un entero para seguridad

    // Preparamos la consulta para evitar inyección SQL
    $query = $conn->prepare("SELECT id, tipo, descripcion, fecha, estatus, resolucion, usuario_id, imagen FROM incidentes WHERE usuario_id = ?");
    $query->bind_param("i", $usuario_id);
    $query->execute();

    // Obtenemos los resultados
    $result = $query->get_result();
    $datos = array();

    while ($row = $result->fetch_assoc()) {
        // Convertir el BLOB de imagen a base64
        if (!empty($row['imagen'])) {
            $row['imagen'] = base64_encode($row['imagen']);
        } else {
            $row['imagen'] = null; // o "" según prefieras
        }

        $datos[] = $row;
    }

    // Retornamos los resultados en formato JSON
    echo json_encode($datos);

} else {
    // Si no se pasa 'usuario_id', retornamos un error
    echo json_encode(["success" => false, "message" => "Falta el parámetro 'usuario_id'"]);
}
?>
