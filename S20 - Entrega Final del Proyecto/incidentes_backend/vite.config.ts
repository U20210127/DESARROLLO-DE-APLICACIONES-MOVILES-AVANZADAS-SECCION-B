import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue'; // Si estás usando Vue.js
import react from '@vitejs/plugin-react'; // Si estás usando React

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(), // Comentario si no usas Vue
    react() // Comentario si no usas React
  ],
  server: {
    port: 3000, // Cambia el puerto si es necesario
    open: true, // Abrir el navegador automáticamente
  },
  build: {
    outDir: 'dist', // Directorio de salida
    sourcemap: true, // Generar mapas de fuente
  },
  resolve: {
    alias: {
      '@': '/src', // Alias para la carpeta src
    }
  }
});
