<?php
require 'vendor/autoload.php';  // Asegúrate de incluir PHPMailer

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

function sendEmail($to, $subject, $body) {
    $mail = new PHPMailer(true);

    try {
        $mail->isSMTP();
        $mail->Host = 'smtp.example.com';  // Configura tu servidor SMTP
        $mail->SMTPAuth = true;
        $mail->Username = 'your_email@example.com';  // Tu correo
        $mail->Password = 'your_password';  // Tu contraseña
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port = 587;

        $mail->setFrom('your_email@example.com', 'Sistema de Incidentes');
        $mail->addAddress($to);

        $mail->isHTML(true);
        $mail->Subject = $subject;
        $mail->Body = $body;

        $mail->send();
    } catch (Exception $e) {
        echo "El correo no pudo ser enviado. Error: {$mail->ErrorInfo}";
    }
}
?>
