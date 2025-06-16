<?php
include('../config/db.php');

$sql = "SHOW COLUMNS FROM usuarios LIKE 'rol'";
$result = $conn->query($sql);
$row = $result->fetch_assoc();

$enum = $row['Type']; // enum('administrador','empleado')

// Extraer los valores entre comillas simples
preg_match_all("/'([^']+)'/", $enum, $matches);
$roles = $matches[1];

echo json_encode($roles);
?>
