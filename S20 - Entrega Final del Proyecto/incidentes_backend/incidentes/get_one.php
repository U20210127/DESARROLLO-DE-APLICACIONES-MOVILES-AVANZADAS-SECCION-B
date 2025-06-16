<?php
include('../config/db.php');

$incidente_id = $_GET['id'];

$query = $conn->prepare("SELECT * FROM incidentes WHERE id = ?");
$query->bind_param("i", $incidente_id);
$query->execute();
$result = $query->get_result();
$incidente = $result->fetch_assoc();

if ($incidente) {
    echo json_encode($incidente);
} else {
    echo json_encode(["success" => false, "message" => "Incidente no encontrado"]);
}
?>
