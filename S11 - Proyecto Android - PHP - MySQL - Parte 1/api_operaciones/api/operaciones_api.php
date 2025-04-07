<?php
// Incluir archivos necesarios
require_once '../config/config.php';
require_once '../funciones/operaciones.php';
require_once '../utils/error.php';

// Asegurar que recibimos un método POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Obtener JSON de la petición
    $json = file_get_contents('php://input');
    $datos = json_decode($json, true);
    
    // Verificar si los datos son válidos
    if ($json === false || $datos === null) {
        mostrarError("Formato JSON inválido", 400);
    }
    
    // Verificar que tenemos los campos necesarios
    if (!isset($datos['operacion']) || !isset($datos['num1']) || !isset($datos['num2'])) {
        mostrarError("Faltan parámetros requeridos (operacion, num1, num2)", 400);
    }
    
    $operacion = $datos['operacion'];
    $num1 = $datos['num1'];
    $num2 = $datos['num2'];
    
    // Seleccionar operación a realizar
    switch($operacion) {
        case 'suma':
            $resultado = sumar($num1, $num2);
            break;
        case 'resta':
            $resultado = restar($num1, $num2);
            break;
        case 'multiplicacion':
            $resultado = multiplicar($num1, $num2);
            break;
        case 'division':
            $resultado = dividir($num1, $num2);
            break;
        default:
            mostrarError("Operación no reconocida", 400);
    }
    
    // Devolver resultado como JSON
    echo json_encode(array(
        'estado' => 'exito',
        'datos' => $resultado
    ));
    
} else {
    // Método no permitido
    mostrarError("Método HTTP no permitido", 405);
}
?>

