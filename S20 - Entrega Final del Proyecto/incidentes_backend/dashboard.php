<?php

header("Content-Type: application/json; charset=UTF-8");

/* ─────────────────────────  CONEXIÓN ───────────────────────── */
$host = "localhost";
$user = "root";
$pass = "";          // Ajusta tus credenciales
$db   = "incidentes_db";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Conexión fallida: " . $conn->connect_error]);
    exit;
}
$conn->set_charset("utf8");

/* ─────────────────── RESUMEN (total / activos / resueltos / etc.) ─────────────────── */
$resumen = [
    "total"     => 0,
    "activos"   => 0,
    "resueltos" => 0,
    "solucionados" => 0,
    "aplazados" => 0,
    "revisados" => 0,
    "eliminados" => 0
];

$sqlResumen = "
    SELECT
        COUNT(*)                                            AS total,
        SUM(CASE WHEN estatus = 'Activo'      THEN 1 ELSE 0 END) AS activos,
        SUM(CASE WHEN estatus = 'Resuelto'    THEN 1 ELSE 0 END) AS resueltos,
        SUM(CASE WHEN estatus = 'Solucionado' THEN 1 ELSE 0 END) AS solucionados,
        SUM(CASE WHEN estatus = 'Aplazado'    THEN 1 ELSE 0 END) AS aplazados,
        SUM(CASE WHEN estatus = 'Revisado'    THEN 1 ELSE 0 END) AS revisados,
        SUM(CASE WHEN estatus = 'Eliminado'   THEN 1 ELSE 0 END) AS eliminados
    FROM incidentes
";
if ($rs = $conn->query($sqlResumen)) {
    $resumen = $rs->fetch_assoc();
    $rs->free();
}

/* ─────────────── LISTA DE INCIDENTES SEGÚN ESTATUS ─────────────── */
$incidentes = [];
$sqlIncidentes = "
    SELECT
        i.id,
        i.fecha         AS fecha_reportado,
        u.nombre        AS usuario_nombre,
        i.estatus
    FROM incidentes i
    INNER JOIN usuarios u ON i.usuario_id = u.id
    WHERE i.estatus IN ('Activo', 'Resuelto', 'Solucionado', 'Aplazado', 'Revisado', 'Eliminado')
    ORDER BY i.fecha DESC
";
if ($rs = $conn->query($sqlIncidentes)) {
    while ($row = $rs->fetch_assoc()) {
        $incidentes[] = $row;
    }
    $rs->free();
}

/* ────────────────────────── RESPUESTA JSON ───────────────────────── */
echo json_encode([
    "success"  => true,
    "resumen"  => $resumen,
    "incidentes" => $incidentes
]);

$conn->close();
?>
