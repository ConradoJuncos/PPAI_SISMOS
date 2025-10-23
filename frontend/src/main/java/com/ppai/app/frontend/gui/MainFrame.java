package com.ppai.app.frontend.gui;

import com.ppai.app.frontend.service.ApiService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Ventana principal de la aplicación de escritorio.
 * Aquí puedes diseñar tu interfaz gráfica con JFrame y componentes Swing.
 */
public class MainFrame extends JFrame {

    private final ApiService apiService;
    private final Gson gson;

    // Componentes de la interfaz
    private JTextField txtNombre;
    private JButton btnCrear;
    private JButton btnObtener;
    private JTextArea txtResultados;
    private JScrollPane scrollPane;

    public MainFrame() {
        this.apiService = new ApiService();
        this.gson = new Gson();

        // Configurar la ventana principal
        setTitle("PPAI - Sistema de Sismos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Configurar el layout
        setLayout(new BorderLayout(10, 10));

        // Crear panel superior
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        // Crear panel de resultados
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResultados.setLineWrap(true);
        txtResultados.setWrapStyleWord(true);

        scrollPane = new JScrollPane(txtResultados);
        add(scrollPane, BorderLayout.CENTER);

        // Agregar márgenes
        add(new JPanel(), BorderLayout.SOUTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);

        setVisible(true);
    }

    /**
     * Crea el panel superior con los campos de entrada
     */
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Crear Entidad"));

        // Etiqueta
        JLabel lblNombre = new JLabel("Nombre:");
        panel.add(lblNombre);

        // Campo de texto
        txtNombre = new JTextField(20);
        panel.add(txtNombre);

        // Botón crear
        btnCrear = new JButton("Crear Entidad");
        btnCrear.addActionListener(e -> crearEntidad());
        panel.add(btnCrear);

        // Botón obtener
        btnObtener = new JButton("Obtener Entidades");
        btnObtener.addActionListener(e -> obtenerEntidades());
        panel.add(btnObtener);

        return panel;
    }

    /**
     * Maneja la creación de una nueva entidad
     */
    private void crearEntidad() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("Por favor ingresa un nombre");
            return;
        }

        try {
            // Crear JSON con los datos
            JsonObject json = new JsonObject();
            json.addProperty("nombre", nombre);

            // Enviar solicitud POST
            String respuesta = apiService.crearEntidad(json.toString());

            txtResultados.setText("✓ ENTIDAD CREADA EXITOSAMENTE\n");
            txtResultados.append("─────────────────────────────\n");
            txtResultados.append(respuesta);

            // Limpiar campo
            txtNombre.setText("");

        } catch (Exception e) {
            mostrarError("Error al crear entidad: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene todas las entidades
     */
    private void obtenerEntidades() {
        try {
            String respuesta = apiService.obtenerEntidades();

            txtResultados.setText("✓ ENTIDADES RECUPERADAS\n");
            txtResultados.append("─────────────────────────────\n");
            txtResultados.append(respuesta);

        } catch (Exception e) {
            mostrarError("Error al obtener entidades: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Punto de entrada de la aplicación
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

