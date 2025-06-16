<?php
include('../config/db.php');

// Verificar si el parámetro 'id' está presente en la URL
if (isset($_GET['id'])) {
    // Obtener el ID desde el parámetro de la URL
    $id = $_GET['id'];

    // Consulta SQL para obtener un solo usuario por ID, incluyendo el campo 'rol'
    $stmt = $conn->prepare("SELECT id, nombre, email, rol FROM usuarios WHERE id = ?");
    $stmt->bind_param("i", $id);  // "i" es para enteros (id)
    $stmt->execute();

    // Obtener el resultado
    $result = $stmt->get_result();

    // Verificar si se encontró el usuario
    if ($result->num_rows > 0) {
        $usuario = $result->fetch_assoc();
        echo json_encode($usuario);  // Devolver el usuario como JSON
    } else {
        echo json_encode(["message" => "Usuario no encontrado"]);
    }

    // Cerrar la sentencia
    $stmt->close();
} else {
    // Si no se proporciona el ID, devolver todos los usuarios, incluyendo el campo 'rol'
    $result = $conn->query("SELECT id, nombre, email, rol FROM usuarios");
    $usuarios = array();

    while ($row = $result->fetch_assoc()) {
        $usuarios[] = $row;
    }

    // Devolver todos los usuarios como JSON
    echo json_encode($usuarios);
}
?>
