<?php
// Incluir archivo de configuración de base de datos
include('../config/db.php');

// Cargar el autoload de Composer para Dompdf
require_once __DIR__ . '/../vendor/autoload.php';

use Dompdf\Dompdf;

// Crear instancia de Dompdf
$dompdf = new Dompdf();

// Consultar los incidentes activos usando mysqli
$query = $conn->query("SELECT * FROM incidentes WHERE estatus = 'Activo'");

// Armar el HTML para el PDF
$html = '<h1 style="text-align:center;">Reporte de Incidentes Activos</h1>';
$html .= '<hr>';
$html .= '<div style="font-family: Arial, sans-serif; font-size: 12px;">';

// Iniciar tabla
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

// Mostrar los datos
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

// Cargar el HTML en Dompdf
$dompdf->loadHtml($html);

// Configurar tamaño y orientación del papel
$dompdf->setPaper('A4', 'portrait');

// Renderizar PDF
$dompdf->render();

// Enviar PDF para descarga al navegador
$dompdf->stream('reporte_incidentes_activos.pdf', ['Attachment' => true]);
?>
