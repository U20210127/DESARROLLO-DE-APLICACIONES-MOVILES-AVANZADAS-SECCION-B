<?php
class Contacto {
    private $conn;

    public function __construct($db) {
        $this->conn = $db;
    }

    public function listarContactos() {
        try {
            $stmt = $this->conn->prepare("SELECT id_contacto, nombre, telefono FROM contactos");
            $stmt->execute();
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            throw new Exception("Error al listar contactos: " . $e->getMessage());
        }
    }

    public function listarContactoPorId($id_contacto) {
        try {
            $stmt = $this->conn->prepare("SELECT id_contacto, nombre, telefono FROM contactos WHERE id_contacto = ?");
            $stmt->execute([$id_contacto]);
            return $stmt->fetch(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            throw new Exception("Error al listar contacto: " . $e->getMessage());
        }
    }

    public function listarContactosConFiltro($filtro) {
        try {
            $consulta = "SELECT id_contacto, nombre, telefono FROM contactos WHERE (nombre LIKE ? OR telefono LIKE ?) ORDER BY nombre ASC";
            $stmt = $this->conn->prepare($consulta);
            $filtroParam = "%$filtro%";
            $stmt->execute([$filtroParam, $filtroParam]);
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            throw new Exception("Error al listar contactos con filtro: " . $e->getMessage());
        }
    }

    public function insertarContacto($nombre, $telefono) {
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
    
        try {
            $stmt = $this->conn->prepare("INSERT INTO contactos (nombre, telefono) VALUES (?, ?)");
            $stmt->execute([$nombre, $telefono]);
            return $this->conn->lastInsertId();
        } catch (PDOException $e) {
            throw new Exception("Error al insertar contacto: " . $e->getMessage());
        }
    }
    

    public function actualizarContacto($id_contacto, $nombre, $telefono) {
        try {
            $stmt = $this->conn->prepare("UPDATE contactos SET nombre = ?, telefono = ? WHERE id_contacto = ?");
            $stmt->execute([$nombre, $telefono, $id_contacto]);
            return $stmt->rowCount();
        } catch (PDOException $e) {
            throw new Exception("Error al actualizar contacto: " . $e->getMessage());
        }
    }

    public function eliminarContacto($id_contacto) {
        try {
            $stmt = $this->conn->prepare("DELETE FROM contactos WHERE id_contacto = ?");
            $stmt->execute([$id_contacto]);
            return $stmt->rowCount();
        } catch (PDOException $e) {
            throw new Exception("Error al eliminar contacto: " . $e->getMessage());
        }
    }
}
?>