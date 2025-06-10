<?php
require 'db.php';
require 'vendor/autoload.php';  // Inclúyelo una sola vez arriba

// IMPORTAR CLASES (fuera del switch)
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use Dompdf\Dompdf;

$formato = $_GET['formato'] ?? 'csv';

$stmt = $pdo->query("SELECT * FROM agenda");
$datos = $stmt->fetchAll(PDO::FETCH_ASSOC);

if (!$datos) {
    die("No hay datos para exportar.");
}

switch ($formato) {
    case 'csv':
        // CSV
        header('Content-Type: text/csv; charset=UTF-8');
        header('Content-Disposition: attachment; filename="agenda.csv"');
    
        // Abrir el archivo CSV para escribir en él
        $salida = fopen('php://output', 'w');
    
        // Escribir los encabezados de las columnas con estilo visual
        // Asegurarnos que los encabezados están en UTF-8
        fputcsv($salida, array_map('utf8_encode', array_keys($datos[0])), ';'); // Usar punto y coma para separar las columnas
    
        // Escribir los datos
        foreach ($datos as $fila) {
            // Convertir cada campo a UTF-8, asegurándonos de que los datos no se corrompan
            $fila_utf8 = array_map('utf8_encode', $fila);
            
            // Formatear las fechas a un formato que Excel pueda entender correctamente
            foreach ($fila_utf8 as $key => $value) {
                if (strtotime($value)) {
                    // Formatear las fechas al formato: Año-Mes-Día Hora:Minuto:Segundo (ISO 8601)
                    $fila_utf8[$key] = date('Y-m-d H:i:s', strtotime($value));
                }
            }
    
            // Escribir la fila en el CSV
            fputcsv($salida, $fila_utf8, ';'); // Separar con punto y coma
        }
    
        fclose($salida);
        break;
    

    case 'excel':
        // Excel
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();

        // Establecer el encabezado
        $sheet->fromArray(array_keys($datos[0]), NULL, 'A1');

        // Aplicar estilo al encabezado (negrita y color de fondo)
        $header = $sheet->getRowDimension(1);
        $sheet->getStyle('A1:' . chr(64 + count($datos[0])) . '1')->getFont()->setBold(true);
        $sheet->getStyle('A1:' . chr(64 + count($datos[0])) . '1')->getFill()->setFillType(\PhpOffice\PhpSpreadsheet\Style\Fill::FILL_SOLID);
        $sheet->getStyle('A1:' . chr(64 + count($datos[0])) . '1')->getFill()->getStartColor()->setRGB('CCCCCC');

        // Escribir los datos a partir de la segunda fila
        $sheet->fromArray($datos, NULL, 'A2');

        // Aplicar bordes a todas las celdas de datos
        $sheet->getStyle('A1:' . chr(64 + count($datos[0])) . (count($datos) + 1))
            ->getBorders()->getAllBorders()->setBorderStyle(\PhpOffice\PhpSpreadsheet\Style\Border::BORDER_THIN);

        // Ajustar el tamaño de las columnas automáticamente
        foreach (range('A', chr(64 + count($datos[0]))) as $col) {
            $sheet->getColumnDimension($col)->setAutoSize(true);
        }

        // Enviar el archivo al navegador
        header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        header('Content-Disposition: attachment; filename="agenda.xlsx"');
        $writer = new Xlsx($spreadsheet);
        $writer->save('php://output');
        break;

    case 'pdf':
        // PDF
        $html = "<h3 style='text-align: center;'>Reporte de Agenda</h3>";
        $html .= "<table style='border: 1px solid black; border-collapse: collapse; width: 100%;'>";
        $html .= "<thead style='background-color: #f2f2f2;'><tr>";

        // Encabezados
        foreach (array_keys($datos[0]) as $col) {
            $html .= "<th style='border: 1px solid black; padding: 5px; text-align: center;'>$col</th>";
        }
        $html .= "</tr></thead><tbody>";

        // Filas
        foreach ($datos as $fila) {
            $html .= "<tr>";
            foreach ($fila as $valor) {
                $html .= "<td style='border: 1px solid black; padding: 5px; text-align: center;'>$valor</td>";
            }
            $html .= "</tr>";
        }

        $html .= "</tbody></table>";

        // Generar PDF
        $dompdf = new Dompdf();
        $dompdf->loadHtml($html);
        $dompdf->setPaper('A4', 'landscape');
        $dompdf->render();
        $dompdf->stream("agenda.pdf");
        break;

    default:
        echo "Formato no soportado.";
}
?>

