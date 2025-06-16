<?php
include('../config/db.php');
require_once __DIR__ . '/../vendor/autoload.php';

use Dompdf\Dompdf;

$dompdf = new Dompdf();

// Cambié la condición para que traiga solo los resueltos
$query = $conn->query("SELECT * FROM incidentes WHERE estatus = 'Resuelto'");

$html = '<h1 style="text-align:center;">Reporte de Incidentes Resueltos</h1>';
$html .= '<hr>';
$html .= '<div style="font-family: Arial, sans-serif; font-size: 12px;">';

// Iniciar tabla con estilos básicos
$html .= '<table border="1" cellspacing="0" cellpadding="5" style="width: 100%; border-collapse: collapse;">';
$html .= '<thead style="background-color: #f2f2f2;">';
$html .= '<tr>';
$html .= '<th>ID</th>';
$html .= '<th>Tipo</th>';
$html .= '<th>Fecha</th>';
$html .= '<th>Descripción</th>';
$html .= '</tr>';
$html .= '</thead>';
$html .= '<tbody>';

while ($row = $query->fetch_assoc()) {
    $html .= '<tr>';
    $html .= '<td>' . htmlspecialchars($row['id']) . '</td>';
    $html .= '<td>' . htmlspecialchars($row['tipo']) . '</td>';
    $html .= '<td>' . htmlspecialchars($row['fecha']) . '</td>';
    $html .= '<td>' . nl2br(htmlspecialchars($row['descripcion'])) . '</td>';
    $html .= '</tr>';
}

$html .= '</tbody>';
$html .= '</table>';
$html .= '</div>';

$dompdf->loadHtml($html);
$dompdf->setPaper('A4', 'portrait');
$dompdf->render();
$dompdf->stream('reporte_incidentes_resueltos.pdf', ['Attachment' => true]);
?>
