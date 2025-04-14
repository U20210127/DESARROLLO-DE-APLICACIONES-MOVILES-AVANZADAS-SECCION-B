<?php
// Incluir archivos necesarios
require_once '../config/config.php';
require_once '../config/database.php';
require_once '../utils/error.php';
require_once '../models/contacto.php';

// Instanciar conexión de base de datos
$database = new Database();
$db = $database->getConnection();
$contactoModel = new Contacto($db);

// Obtener la acción de la solicitud
$action = isset($_GET['action']) ? $_GET['action'] : '';

// Depuración: Imprimir el valor de $action
error_log("Acción recibida: " . $action);

switch ($action) {
    case 'listarContactos':
        $contactos = $contactoModel->listarContactos();
        echo json_encode([
            'status' => 'success',
            'data' => $contactos,
            'total_rows' => count($contactos)
        ]);
        break;

    case 'listarContactoById':
        $id_contacto = isset($_GET['id']) ? (int)$_GET['id'] : 0;
        if ($id_contacto <= 0) {
            echo json_encode(['status' => 'error', 'message' => 'ID de contacto inválido.']);
            exit;
        }
        $contacto = $contactoModel->listarContactoPorId($id_contacto);
        if ($contacto) {
            echo json_encode(['status' => 'success', 'data' => $contacto]);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Contacto no encontrado.']);
        }
        break;

    case 'listarContactosConFiltro':
        $filtro = isset($_GET['filtro']) ? trim($_GET['filtro']) : '';
        $contactos = $contactoModel->listarContactosConFiltro($filtro);
        echo json_encode([
            'status' => 'success',
            'data' => $contactos,
            'total_rows' => count($contactos)
        ]);
        break;

    case 'agregarContacto':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            throw new Exception("Método no permitido. Use POST.");
        }
        
        $data = json_decode(file_get_contents("php://input"), true);
        $nombre = isset($data['nombre']) ? trim($data['nombre']) : '';
        $telefono = isset($data['telefono']) ? trim($data['telefono']) : '';
        
        // Validar campos obligatorios
        if (empty($nombre) || empty($telefono)) {
            throw new Exception("Nombre y teléfono son obligatorios.");
        }
        
        // Validar formato del teléfono
        if (!preg_match("/^[0-9]{10}$/", $telefono)) {
            throw new Exception("El teléfono debe contener exactamente 10 dígitos.");
        }
        
        // Validar longitud del nombre
        if (strlen($nombre) > 50) {
            throw new Exception("El nombre no puede exceder los 50 caracteres.");
        }
        
        $id_contacto = $contactoModel->insertarContacto($nombre, $telefono);
            echo json_encode([
                'status' => 'success',
                'message' => 'Contacto insertado con éxito.',
                'id_contacto' => $id_contacto
            ]);
        break;
        

    case 'actualizarContacto':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            echo json_encode(['status' => 'error', 'message' => 'Método no permitido. Use POST.']);
            exit;
        }
        $data = json_decode(file_get_contents("php://input"), true);
        $id_contacto = isset($data['id_contacto']) ? (int)$data['id_contacto'] : 0;
        $nombre = isset($data['nombre']) ? trim($data['nombre']) : '';
        $telefono = isset($data['telefono']) ? trim($data['telefono']) : '';
        if ($id_contacto <= 0 || empty($nombre) || empty($telefono)) {
            echo json_encode(['status' => 'error', 'message' => 'ID, nombre y teléfono son obligatorios.']);
            exit;
        }
        $filasAfectadas = $contactoModel->actualizarContacto($id_contacto, $nombre, $telefono);
        if ($filasAfectadas > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Contacto actualizado con éxito.']);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'No se encontró el contacto o no se realizaron cambios.']);
        }
        break;

    case 'eliminarContacto':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            echo json_encode(['status' => 'error', 'message' => 'Método no permitido. Use POST.']);
            exit;
        }
        $data = json_decode(file_get_contents("php://input"), true);
        $id_contacto = isset($data['id_contacto']) ? (int)$data['id_contacto'] : 0;
        if ($id_contacto <= 0) {
            echo json_encode(['status' => 'error', 'message' => 'ID de contacto inválido.']);
            exit;
        }
        $filasAfectadas = $contactoModel->eliminarContacto($id_contacto);
        if ($filasAfectadas > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Contacto eliminado con éxito.']);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'No se encontró el contacto.']);
        }
        break;

    default:
        // Depuración: Imprimir mensaje de acción no válida
        error_log("Acción no válida: " . $action);
        echo json_encode(['status' => 'error', 'message' => 'Acción no válida.']);
        break;
}
?>