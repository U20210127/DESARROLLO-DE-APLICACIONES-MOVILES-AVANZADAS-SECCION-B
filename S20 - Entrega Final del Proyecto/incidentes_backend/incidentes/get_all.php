<?php
include('../config/db.php');

// Función para convertir BLOB a base64 (si $blob no es null)
function blobToBase64($blob) {
    if ($blob !== null) {
        return base64_encode($blob);
    }
    return null;
}

if (isset($_GET['id'])) {
    // Obtener el ID desde el parámetro de la URL
    $id = $_GET['id'];

    // Consulta SQL para obtener un solo incidente por ID, incluyendo imagen
    $stmt = $conn->prepare("SELECT id, tipo, descripcion, fecha, estatus, resolucion, usuario_id, imagen FROM incidentes WHERE id = ?");
    $stmt->bind_param("i", $id);
    $stmt->execute();

    // Obtener el resultado
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $incidente = $result->fetch_assoc();

        // Convertir BLOB imagen a base64
        $incidente['imagen'] = blobToBase64($incidente['imagen']);

        echo json_encode($incidente);
    } else {
        echo json_encode(["message" => "Incidente no encontrado"]);
    }

    $stmt->close();
} else {
    // Obtener todos los incidentes incluyendo imagen
    $result = $conn->query("SELECT id, tipo, descripcion, fecha, estatus, resolucion, usuario_id, imagen FROM incidentes");
    $incidentes = array();

    while ($row = $result->fetch_assoc()) {
        // Convertir BLOB imagen a base64
        $row['imagen'] = blobToBase64($row['imagen']);

        $incidentes[] = $row;
    }

    echo json_encode($incidentes);
}
?>
