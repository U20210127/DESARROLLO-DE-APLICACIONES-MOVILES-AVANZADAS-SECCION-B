<?php
class Database {
    // Credenciales de la BD
    private $host = "localhost";
    private $db_name = "miagenda";
    private $username = "root";
    private $password = ""; // Por defecto en XAMPP es vacío
    public $conn;
    
    // Conexión a la base de datos
    public function getConnection() {
        $this->conn = null;
        
        try {
            $this->conn = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name,
                $this->username,
                $this->password
            );
            $this->conn->exec("set names utf8");
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch(PDOException $exception) {
            echo "Error de conexión: " . $exception->getMessage();
        }
        
        return $this->conn;
    }
}
?>
