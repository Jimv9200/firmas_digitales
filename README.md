Descripción general:
Aaplicación web en Java con Spring Boot orientada a la firma digital de archivos para garantizar la integridad y la autenticidad durante el intercambio de documentos entre usuarios. La aplicación permite a un emisor firmar digitalmente un archivo y enviarlo a un receptor, quien, al recibirlo, verifica la firma y, posteriormente, realiza su propia firma digital como confirmación de recepción. Esto asegura un proceso de validación robusto y trazable de ambas partes involucradas.

🔐 Características principales:
Gestión de usuarios e identidad federada:

Integración con Keycloak para la autenticación y gestión de roles de usuarios.

Soporte para inicio de sesión federado con cuentas de Google, permitiendo autenticación segura y fluida.

Firmas digitales:

Generación de claves públicas/privadas para cada usuario.

Firma de archivos mediante algoritmos criptográficos robustos (como SHA-256 con RSA).

Validación de firmas digitales tanto en el envío como en la recepción.

Interfaz amigable:

Portal web intuitivo para cargar archivos, revisar su estado y visualizar la trazabilidad de la firma.

Notificaciones de confirmación de firma y recepción.

Auditoría y seguridad:

Registro completo del proceso de firma para garantizar trazabilidad.

Seguridad en la transmisión de archivos mediante HTTPS y control de acceso por roles y archivos encriptados.

🧰 Tecnologías utilizadas:
Backend: Java 17, Spring Boot 3 (Web, Security, Data JPA)

Seguridad: Keycloak, OAuth2, JWT

Federación de identidad: Google OAuth2

Persistencia: MySQL

Otras: Gradle, Docker, Git

🎯 Objetivo del sistema:
Proveer una solución segura y legalmente válida para el intercambio de archivos firmados digitalmente, reduciendo la dependencia de procesos en papel, mejorando la eficiencia y asegurando la identidad de las partes involucradas.
