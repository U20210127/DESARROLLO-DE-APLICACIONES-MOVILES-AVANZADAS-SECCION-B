<?php
require_once __DIR__ . '/../config/db.php';  // Asegúrate de que esta ruta esté correcta
require_once __DIR__ . '/../vendor/autoload.php';  // Si es necesario para TCPDF

// Verificar que la conexión se realizó correctamente
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Obtener datos desde la base de datos con mysqli
$sql = "SELECT tipo, descripcion, fecha, estatus FROM incidentes";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $incidentes = $result->fetch_all(MYSQLI_ASSOC);
} else {
    die("No hay incidentes registrados.");
}

function generatePDF($incidentes) {
    $pdf = new TCPDF();
    $pdf->SetCreator(PDF_CREATOR);
    $pdf->SetAuthor('Sistema');
    $pdf->SetTitle('Reporte de Incidentes');
    $pdf->SetMargins(15, 15, 15);
    $pdf->AddPage();
    $pdf->SetFont('helvetica', '', 12);

    $pdf->Cell(0, 10, "Reporte de Incidentes", 0, 1, 'C');
    $pdf->Ln(5);

    foreach ($incidentes as $incidente) {
        $pdf->Cell(0, 10, "Incidente: " . $incidente['tipo'], 0, 1);
        $pdf->MultiCell(0, 10, "Descripción: " . $incidente['descripcion'], 0, 1);
        $pdf->Cell(0, 10, "Fecha: " . $incidente['fecha'], 0, 1);
        $pdf->Cell(0, 10, "Estatus: " . $incidente['estatus'], 0, 1);
        $pdf->Ln(8);
    }

    $pdf->Output('reporte_incidentes.pdf', 'I');
}

generatePDF($incidentes);

// Cerrar la conexión
$conn->close();
?>
