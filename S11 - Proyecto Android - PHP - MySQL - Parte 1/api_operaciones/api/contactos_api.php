<?php
// Incluir archivos necesarios
require_once '../config/config.php';
require_once '../config/database.php';
require_once '../utils/error.php';

// Instanciar conexión de base de datos
$database = new Database();
$db = $database->getConnection();

// Revisar el método HTTP usado
$method = $_SERVER['REQUEST_METHOD'];

switch($method) {
    case 'GET':
        // Obtener contactos
        if(isset($_GET['id'])) {
            // Obtener un contacto específico
            $id = $_GET['id'];
            $query = "SELECT * FROM contactos WHERE id = ?";
            $stmt = $db->prepare($query);
            $stmt->execute([$id]);
            $contacto = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if($contacto) {
                echo json_encode(array(
                    'estado' => 'exito',
                    'contacto' => $contacto
                ));
            } else {
                mostrarError("Contacto no encontrado", 404);
            }
        } else {
            // Obtener todos los contactos
            $query = "SELECT * FROM contactos";
            $stmt = $db->prepare($query);
            $stmt->execute();
            $contactos = $stmt->fetchAll(PDO::FETCH_ASSOC);
            
            echo json_encode(array(
                'estado' => 'exito',
                'contactos' => $contactos
            ));
        }
        break;
        
    case 'POST':
        // Crear nuevo contacto
        $data = json_decode(file_get_contents("php://input"), true);
        
        if(!isset($data['nombre']) || !isset($data['telefono']) || !isset($data['email'])) {
            mostrarError("Datos incompletos", 400);
        }
        
        $query = "INSERT INTO contactos (nombre, telefono, email) VALUES (?, ?, ?)";
        $stmt = $db->prepare($query);
        
        if($stmt->execute([$data['nombre'], $data['telefono'], $data['email']])) {
            $id = $db->lastInsertId();
            echo json_encode(array(
                'estado' => 'exito',
                'mensaje' => 'Contacto creado correctamente',
                'id' => $id
            ));
        } else {
            mostrarError("Error al crear contacto", 500);
        }
        break;
        
    case 'PUT':
        // Actualizar contacto existente
        $data = json_decode(file_get_contents("php://input"), true);
        
        if(!isset($data['id'])) {
            mostrarError("ID de contacto requerido", 400);
        }
        
        $id = $data['id'];
        $sets = array();
        $params = array();
        
        // Construir consulta según campos proporcionados
        if(isset($data['nombre'])) {
            $sets[] = "nombre = ?";
            $params[] = $data['nombre'];
        }
        
        if(isset($data['telefono'])) {
            $sets[] = "telefono = ?";
            $params[] = $data['telefono'];
        }
        
        if(isset($data['email'])) {
            $sets[] = "email = ?";
            $params[] = $data['email'];
        }
        
        if(empty($sets)) {
            mostrarError("No hay datos para actualizar", 400);
        }
        
        // Agregar ID al final de params
        $params[] = $id;
        
        $query = "UPDATE contactos SET " . implode(", ", $sets) . " WHERE id = ?";
        $stmt = $db->prepare($query);
        
        if($stmt->execute($params)) {
            echo json_encode(array(
                'estado' => 'exito',
                'mensaje' => 'Contacto actualizado correctamente'
            ));
        } else {
            mostrarError("Error al actualizar contacto", 500);
        }
        break;
        
    case 'DELETE':
        // Eliminar contacto
        if(!isset($_GET['id'])) {
            mostrarError("ID de contacto requerido", 400);
        }
        
        $id = $_GET['id'];
        $query = "DELETE FROM contactos WHERE id = ?";
        $stmt = $db->prepare($query);
        
        if($stmt->execute([$id])) {
            echo json_encode(array(
                'estado' => 'exito',
                'mensaje' => 'Contacto eliminado correctamente'
            ));
        } else {
            mostrarError("Error al eliminar contacto", 500);
        }
        break;
        
    default:
        mostrarError("Método no permitido", 405);
}
?>
