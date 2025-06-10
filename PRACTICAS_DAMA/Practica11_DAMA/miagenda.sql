

--
-- Base de datos: `miagenda`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `agenda`
--

CREATE TABLE `agenda` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `agenda`
--

INSERT INTO `agenda` (`id`, `nombre`, `telefono`, `correo`, `fecha_registro`) VALUES
(1, 'Juan Pérez', '555-1234', 'juan.perez@example.com', '2025-05-03 13:24:06'),
(2, 'María Gómez', '555-5678', 'maria.gomez@example.com', '2025-05-03 13:24:06'),
(3, 'Carlos Rodríguez', '555-9012', 'carlos.rodriguez@example.com', '2025-05-03 13:24:06'),
(4, 'Ana Torres', '555-3456', 'ana.torres@example.com', '2025-05-03 13:24:06'),
(5, 'Luis Fernández', '555-7890', 'luis.fernandez@example.com', '2025-05-03 13:24:06'),
(6, 'Lucía Morales', '555-1122', 'lucia.morales@example.com', '2025-05-03 13:24:06'),
(7, 'Miguel Herrera', '555-3344', 'miguel.herrera@example.com', '2025-05-03 13:24:06'),
(8, 'Sofía Ruiz', '555-5566', 'sofia.ruiz@example.com', '2025-05-03 13:24:06');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `agenda`
--
ALTER TABLE `agenda`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `agenda`
--
ALTER TABLE `agenda`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;
