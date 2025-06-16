<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Dashboard Incidentes</title>
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
  <style>
    /* Estilos para la sidebar */
    .sidebar {
      height: 100vh;
      position: fixed;
      top: 0;
      left: 0;
      width: 250px;
      background-color: #343a40;
      color: white;
      padding-top: 20px;
      display: block; /* Asegura que la sidebar siempre esté visible en pantallas grandes */
      transition: transform 0.3s ease;
    }
    .sidebar a {
      color: white;
      padding: 10px 15px;
      text-decoration: none;
      display: block;
    }
    .sidebar a:hover {
      background-color: #575d63;
    }
    .sidebar.active {
      transform: translateX(0);
    }
    .sidebar.inactive {
      transform: translateX(-100%);
    }
    .main-content {
      margin-left: 250px;
    }
    /* Ajustes para cuando la pantalla es pequeña */
    @media (max-width: 768px) {
      .sidebar {
        width: 250px;
        transform: translateX(-100%); /* Empuja la sidebar fuera de la pantalla */
      }
      .sidebar.active {
        transform: translateX(0); /* Muestra la sidebar cuando está activa */
      }
      .main-content {
        margin-left: 0;
      }
    }
  </style>
</head>
<body>
  <!-- Navbar -->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation" id="toggle-sidebar">
        <span class="navbar-toggler-icon"></span>
      </button>
      <a class="navbar-brand" href="#">Dashboard</a>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="#">Inicio</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Perfil</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Cerrar sesión</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>

  <!-- Sidebar -->
  <div class="sidebar" id="sidebar">
    <a href="#">Dashboard</a>
    <a href="#">Incidentes</a>
    <a href="#">Reportes</a>
    <a href="#">Ajustes</a>
  </div>

  <!-- Main Content -->
  <div class="main-content container py-4">
    <h1 class="mb-4">Dashboard de Incidentes</h1>

    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card text-bg-primary text-center">
          <div class="card-body">
            <h5 class="card-title">Total de Incidentes</h5>
            <p id="total" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-warning text-center">
          <div class="card-body">
            <h5 class="card-title">Activos</h5>
            <p id="activos" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-success text-center">
          <div class="card-body">
            <h5 class="card-title">Resueltos</h5>
            <p id="resueltos" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-info text-center">
          <div class="card-body">
            <h5 class="card-title">Solucionados</h5>
            <p id="solucionados" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
    </div>

    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card text-bg-secondary text-center">
          <div class="card-body">
            <h5 class="card-title">Aplazados</h5>
            <p id="aplazados" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-dark text-center">
          <div class="card-body">
            <h5 class="card-title">Revisados</h5>
            <p id="revisados" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-bg-danger text-center">
          <div class="card-body">
            <h5 class="card-title">Eliminados</h5>
            <p id="eliminados" class="display-5 mb-0">0</p>
          </div>
        </div>
      </div>
    </div>

    <h2>Lista de Incidentes</h2>
    <div class="table-responsive">
      <table class="table table-striped table-bordered">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Fecha Reportado</th>
            <th>Usuario</th>
            <th>Estatus</th>
          </tr>
        </thead>
        <tbody id="incidentes-body">
          <tr><td colspan="4" class="text-center">Cargando...</td></tr>
        </tbody>
      </table>
    </div>
  </div>

  <script>
    // Función para cargar datos
    async function cargarDatos() {
      try {
        const resp = await fetch('dashboard.php');
        if (!resp.ok) throw new Error('Error en la respuesta HTTP');
        const data = await resp.json();

        if(data.success) {
          document.getElementById('total').textContent = data.resumen.total || 0;
          document.getElementById('activos').textContent = data.resumen.activos || 0;
          document.getElementById('resueltos').textContent = data.resumen.resueltos || 0;
          document.getElementById('solucionados').textContent = data.resumen.solucionados || 0;
          document.getElementById('aplazados').textContent = data.resumen.aplazados || 0;
          document.getElementById('revisados').textContent = data.resumen.revisados || 0;
          document.getElementById('eliminados').textContent = data.resumen.eliminados || 0;

          const tbody = document.getElementById('incidentes-body');
          tbody.innerHTML = '';

          if(data.incidentes.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center">No hay incidentes</td></tr>`;
          } else {
            data.incidentes.forEach(inc => {
              const tr = document.createElement('tr');
              tr.innerHTML = `
                <td>${inc.id}</td>
                <td>${inc.fecha_reportado}</td>
                <td>${inc.usuario_nombre}</td>
                <td>${inc.estatus}</td>
              `;
              tbody.appendChild(tr);
            });
          }
        } else {
          alert('Error al obtener los datos');
        }
      } catch (error) {
        alert('Error al cargar datos: ' + error.message);
      }
    }

    document.addEventListener('DOMContentLoaded', cargarDatos);

    // Función para alternar la sidebar en dispositivos pequeños
    document.getElementById('toggle-sidebar').addEventListener('click', function() {
      document.getElementById('sidebar').classList.toggle('active');
    });
  </script>

  <!-- Bootstrap JS Bundle (Popper + Bootstrap JS) -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
