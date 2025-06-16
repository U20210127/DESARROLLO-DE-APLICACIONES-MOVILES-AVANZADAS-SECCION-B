<?php
include('../config/db.php');
$data = json_decode(file_get_contents("php://input"));
$usuario_id = $data->id;

$query = $conn->prepare("DELETE FROM usuarios WHERE id = ?");
$query->bind_param("i", $usuario_id);

if ($query->execute()) {
    echo json_encode(["success" => true, "message" => "Usuario eliminado exitosamente"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al eliminar usuario"]);
}
?>
