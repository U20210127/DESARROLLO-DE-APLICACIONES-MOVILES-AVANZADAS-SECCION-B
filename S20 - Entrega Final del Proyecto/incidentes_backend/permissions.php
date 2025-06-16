<?php

// Función para verificar permisos por módulo y acción
function tienePermiso($modulo, $accion) {
    if (!isset($_SESSION['user'])) return false;

    $rol = $_SESSION['user']['rol'];

    // Definición de permisos por rol
    $permisos = [
        'admin' => [
            'usuarios' => ['ver', 'crear', 'editar', 'eliminar'],
            'incidentes' => ['ver', 'update_status']
        ],
        'empleado' => [
            'incidentes' => ['ver_own', 'crear']
        ]
    ];

    // Verificar si el rol tiene permisos para la acción solicitada
    return in_array($accion, $permisos[$rol][$modulo] ?? []);
}
?>
