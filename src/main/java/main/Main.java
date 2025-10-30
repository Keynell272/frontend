package main;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import proxy.ServiceProxy;
import view.VentanaLogin;

/**
 * Clase principal del Sistema de Gestión de Recetas Médicas
 * Proyecto #2 - Programación Orientada a Objetos
 */
public class Main {
    
    // Configuración del servidor
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    
    public static void main(String[] args) {
        // Establecer Look and Feel del sistema operativo
        configurarLookAndFeel();
        
        // Mostrar banner en consola
        mostrarBanner();
        
        // Conectar con el servidor
        if (!conectarConServidor()) {
            // Si no se puede conectar, mostrar error y salir
            SwingUtilities.invokeLater(() -> {
                int opcion = JOptionPane.showConfirmDialog(null,
                    "No se pudo conectar con el servidor en " + SERVER_HOST + ":" + SERVER_PORT + "\n\n" +
                    "Posibles causas:\n" +
                    "• El servidor backend no está iniciado\n" +
                    "• Configuración incorrecta del host/puerto\n" +
                    "• Problemas de red o firewall\n\n" +
                    "¿Desea continuar de todos modos?\n" +
                    "(La aplicación puede no funcionar correctamente)",
                    "Error de Conexión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    abrirVentanaLogin();
                } else {
                    System.exit(0);
                }
            });
        } else {
            // Conexión exitosa, abrir ventana de login
            SwingUtilities.invokeLater(() -> abrirVentanaLogin());
        }
    }
    
    /**
     * Intenta conectar con el servidor
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    private static boolean conectarConServidor() {
        try {
            System.out.println("⏳ Conectando con el servidor " + SERVER_HOST + ":" + SERVER_PORT + "...");
            
            ServiceProxy proxy = ServiceProxy.getInstance();
            boolean conectado = proxy.conectar(SERVER_HOST, SERVER_PORT);
            
            if (conectado) {
                System.out.println("✅ Conexión establecida correctamente");
                return true;
            } else {
                System.err.println("❌ No se pudo establecer conexión con el servidor");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Abre la ventana de login
     */
    private static void abrirVentanaLogin() {
        try {
            VentanaLogin ventanaLogin = new VentanaLogin();
            ventanaLogin.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error al iniciar la aplicación:\n" + e.getMessage(),
                "Error Fatal",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * Configura el Look and Feel de la aplicación
     * Intenta usar el L&F nativo del sistema operativo
     */
    private static void configurarLookAndFeel() {
        try {
            // Usar el Look and Feel del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Alternativas si quieres otro estilo:
            // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // Nimbus
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Metal
            
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("⚠ No se pudo establecer el Look and Feel: " + e.getMessage());
            // La aplicación continuará con el Look and Feel por defecto
        }
    }
    
    /**
     * Muestra un banner de bienvenida en la consola
     */
    private static void mostrarBanner() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║    SISTEMA DE GESTIÓN DE RECETAS MÉDICAS - PROYECTO #2     ║");
        System.out.println("║                                                            ║");
        System.out.println("║    Universidad: [Tu Universidad]                           ║");
        System.out.println("║    Curso: Programación Orientada a Objetos                ║");
        System.out.println("║    Versión: 2.0                                            ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("⏰ Fecha de inicio: " + new java.util.Date());
        System.out.println("🌐 Servidor: " + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println();
    }
}
