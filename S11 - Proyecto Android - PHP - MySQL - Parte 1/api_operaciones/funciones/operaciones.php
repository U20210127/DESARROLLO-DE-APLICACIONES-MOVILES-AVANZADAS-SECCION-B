<?php
// Incluir archivos necesarios
require_once '../config/config.php';
require_once '../utils/error.php';

/**
 * Realiza una suma entre dos números
 */
function sumar($num1, $num2) {
    // Validar que sean números
    if (!is_numeric($num1) || !is_numeric($num2)) {
        mostrarError("Los valores deben ser numéricos", 400);
    }
    
    // Realizar la operación
    $resultado = $num1 + $num2;
    
    // Devolver resultado
    return array(
        'operacion' => 'suma',
        'num1' => $num1,
        'num2' => $num2,
        'resultado' => $resultado
    );
}

/**
 * Realiza una resta entre dos números
 */
function restar($num1, $num2) {
    // Validar que sean números
    if (!is_numeric($num1) || !is_numeric($num2)) {
        mostrarError("Los valores deben ser numéricos", 400);
    }
    
    // Realizar la operación
    $resultado = $num1 - $num2;
    
    // Devolver resultado
    return array(
        'operacion' => 'resta',
        'num1' => $num1,
        'num2' => $num2,
        'resultado' => $resultado
    );
}

/**
 * Realiza una multiplicación entre dos números
 */
function multiplicar($num1, $num2) {
    // Validar que sean números
    if (!is_numeric($num1) || !is_numeric($num2)) {
        mostrarError("Los valores deben ser numéricos", 400);
    }
    
    // Realizar la operación
    $resultado = $num1 * $num2;
    
    // Devolver resultado
    return array(
        'operacion' => 'multiplicacion',
        'num1' => $num1,
        'num2' => $num2,
        'resultado' => $resultado
    );
}

/**
 * Realiza una división entre dos números
 */
function dividir($num1, $num2) {
    // Validar que sean números
    if (!is_numeric($num1) || !is_numeric($num2)) {
        mostrarError("Los valores deben ser numéricos", 400);
    }
    
    // Validar división por cero
    if ($num2 == 0) {
        mostrarError("No se puede dividir por cero", 400);
    }
    
    // Realizar la operación
    $resultado = $num1 / $num2;
    
    // Devolver resultado
    return array(
        'operacion' => 'division',
        'num1' => $num1,
        'num2' => $num2,
        'resultado' => $resultado
    );
}
?>

