# Reporte Agenda

Este proyecto genera reportes de los datos de una base de datos de agenda en tres formatos diferentes: **CSV**, **Excel** y **PDF**.

## Pruebas

### Prueba en el navegador:

1. Asegúrate de que los datos en la base de datos estén correctos.
2. Realiza las siguientes peticiones en tu navegador:

   - Para obtener el reporte en formato **CSV**:
     ```
     http://localhost/reporte-agenda/reporte.php?formato=csv
     ```
   - Para obtener el reporte en formato **Excel**:
     ```
     http://localhost/reporte-agenda/reporte.php?formato=excel
     ```
   - Para obtener el reporte en formato **PDF**:
     ```
     http://localhost/reporte-agenda/reporte.php?formato=pdf
     ```

### Prueba en Postman:

1. Método: `GET`
2. URL:
   - Para obtener el reporte en formato **CSV**:
     ```
     http://localhost/reporte-agenda/reporte.php?formato=csv
     ```
   - Para obtener el reporte en formato **Excel**:
     ```
     http://localhost/reporte-agenda/reporte.php?formato=excel
     ```
   - Para obtener el reporte en formato **PDF**:
     ```
     http://localhost/reporte-agenda/reporte.php?formato=pdf
     ```

3. **No es necesario configurar encabezados adicionales ni cuerpo (body) de la solicitud**.
