<?php
function mostrarError($mensaje, $codigo) {
    // Crear estructura de respuesta de error
    $respuesta = array(
        'estado' => 'error',
        'mensaje' => $mensaje,
        'codigo' => $codigo
    );
    
    // Devolver respuesta como JSON
    echo json_encode($respuesta);
    exit; // Terminar ejecución
}
?>