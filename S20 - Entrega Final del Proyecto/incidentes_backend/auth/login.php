<?php
header("Content-Type: application/json");
include('../config/db.php');

// Iniciar la sesión
session_start();

// Validar entrada
if (!isset($_POST['email'], $_POST['password'])) {
    http_response_code(400);
    echo json_encode(["message" => "Datos incompletos"]);
    exit;
}

$email = trim($_POST['email']);
$password = trim($_POST['password']);

// Buscar usuario incluyendo avatar
$query = $conn->prepare("SELECT id, nombre, email, password, rol, avatar FROM usuarios WHERE email = ?");
$query->bind_param("s", $email);
$query->execute();
$result = $query->get_result();
$user = $result->fetch_assoc();

if ($user) {
    $storedPassword = $user['password'];

    // Verificar si es hash o texto plano
    $isValid = password_verify($password, $storedPassword) || $password === $storedPassword;

    if ($isValid) {
        unset($user['password']); // No enviar la contraseña

        // Convertir avatar blob a Base64 para enviar en JSON
        if (!is_null($user['avatar'])) {
            $user['avatar'] = base64_encode($user['avatar']);
        } else {
            $user['avatar'] = null;
        }

        // Guardar en sesión (sin avatar para no sobrecargar, opcional)
        $_SESSION['user'] = [
            'id' => $user['id'],
            'nombre' => $user['nombre'],
            'email' => $user['email'],
            'rol' => $user['rol']
        ];

        echo json_encode($user);
        exit;
    }
}

http_response_code(401); // Unauthorized
echo json_encode(["message" => "Credenciales inválidas"]);
?>
