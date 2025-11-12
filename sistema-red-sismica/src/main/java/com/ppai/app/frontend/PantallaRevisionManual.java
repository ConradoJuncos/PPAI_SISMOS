package com.ppai.app.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.ppai.app.contexto.Contexto;
import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.entidad.Sismografo;
import com.ppai.app.gestor.GestorRevisionManual;

public class PantallaRevisionManual extends JFrame {

    private final Contexto contexto;
    private GestorRevisionManual gestor;
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JButton btnEjecutar;
    private JButton btnVisualizarMapa;
    private JLabel lblEstado;

    // Paneles para mostrar información adicional
    private JPanel panelDatosSismicos;
    private JLabel lblAlcance;
    private JLabel lblClasificacion;
    private JLabel lblOrigen;
    private JTextArea txtInfoSismica;

    public PantallaRevisionManual(Contexto contexto) {
        this.contexto = contexto;
        inicializarComponentes();
    }

    // ================================
    // CONFIGURACIÓN DE LA VENTANA
    // ================================
    private void inicializarComponentes() {
        setTitle("CU23 - Revisión Manual de Eventos Sísmicos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Revisión Manual de Eventos Sísmicos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        btnEjecutar = new JButton("Registrar Revision Manual");
        btnEjecutar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnEjecutar.addActionListener(e -> RegistrarRevisionManual());
        panelBotones.add(btnEjecutar);

        btnVisualizarMapa = new JButton("Visualizar Mapa");
        btnVisualizarMapa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnVisualizarMapa.setEnabled(false); // Deshabilitado inicialmente
        btnVisualizarMapa.addActionListener(e -> visualizarMapa());
        panelBotones.add(btnVisualizarMapa);

        panelSuperior.add(panelBotones, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central con tabla y datos sísmicos
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla de eventos
        String[] columnas = { "Fecha y Hora", "Latitud Epicentro", "Longitud Epicentro", "Latitud Hipocentro",
                "Longitud Hipocentro" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setFont(new Font("Consolas", Font.PLAIN, 13));
        tablaEventos.setRowHeight(25);
        tablaEventos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaEventos.setGridColor(new Color(220, 220, 220));
        tablaEventos.setShowGrid(true);

        tablaEventos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaEventos.getSelectedRow();
                    String datosPrincipales = "";
                    if (fila != -1) {
                        for (int i = 0; i < 5; i++) {
                            datosPrincipales += modeloTabla.getValueAt(fila, i).toString();
                            if (i != 4) {
                                datosPrincipales += ", ";
                            }
                        }
                        tomarSeleccionEventoSismico(datosPrincipales);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Eventos Sísmicos Auto-Detectados No Revisados"));
        panelCentral.add(scrollPane, BorderLayout.NORTH);

        // Panel de datos sísmicos (inicialmente oculto, ahora DESPLAZABLE)
        panelDatosSismicos = new JPanel();
        panelDatosSismicos.setLayout(new BoxLayout(panelDatosSismicos, BoxLayout.Y_AXIS));
        panelDatosSismicos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "Datos Sísmicos Registrados",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14)));
        panelDatosSismicos.setVisible(false);

        // Labels para metadatos
        lblAlcance = new JLabel("Alcance: -");
        lblAlcance.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAlcance.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblClasificacion = new JLabel("Clasificación: -");
        lblClasificacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblClasificacion.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblOrigen = new JLabel("Origen de Generación: -");
        lblOrigen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblOrigen.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // JTextArea para información sísmica con mejor tamaño
        txtInfoSismica = new JTextArea(15, 80);
        txtInfoSismica.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtInfoSismica.setEditable(false);
        txtInfoSismica.setText("Información Sísmica: -");
        txtInfoSismica.setLineWrap(true);
        txtInfoSismica.setWrapStyleWord(true);
        txtInfoSismica.setBackground(new Color(240, 245, 250)); // Azul muy claro
        txtInfoSismica.setForeground(new Color(25, 45, 85)); // Azul oscuro
        txtInfoSismica.setMargin(new java.awt.Insets(12, 12, 12, 12));
        JScrollPane scrollInfoSismica = new JScrollPane(txtInfoSismica);
        scrollInfoSismica.setBorder(BorderFactory.createTitledBorder("📊 Información Sísmica Clasificada por Estación"));
        scrollInfoSismica.setPreferredSize(new java.awt.Dimension(1300, 350));

        panelDatosSismicos.add(lblAlcance);
        panelDatosSismicos.add(lblClasificacion);
        panelDatosSismicos.add(lblOrigen);
        panelDatosSismicos.add(scrollInfoSismica);

        // Agregar panelDatosSismicos dentro de un scroll
        JScrollPane scrollPanelDatos = new JScrollPane(panelDatosSismicos);
        scrollPanelDatos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPanelDatos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelCentral.add(scrollPanelDatos, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // Barra inferior (estado)
        lblEstado = new JLabel("Listo.");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblEstado, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void tomarSeleccionEventoSismico(String datosPrincipales) {
        gestor.tomarSeleccionEventoSismico(datosPrincipales);
    }

    private void RegistrarRevisionManual() {
        lblEstado.setText("asd");
        Usuario usuario = contexto.getUsuarios().get(0);
        List<EventoSismico> eventos = contexto.getEventosSismicos();
        List<Sismografo> sismografos = contexto.getSismografos();

        this.gestor = new GestorRevisionManual(this, eventos, sismografos, usuario);
        lblEstado.setText("Eventos cargados correctamente.");
    }

    public void mostrarEventosSismicosYSolicitarSeleccion(List<String> datosPrincipales) {
        modeloTabla.setRowCount(0);

        for (String datos : datosPrincipales) {
            String[] partes = datos.split(",");
            if (partes.length >= 5) {
                modeloTabla.addRow(new Object[] {
                        partes[0].trim(),
                        partes[1].trim(),
                        partes[2].trim(),
                        partes[3].trim(),
                        partes[4].trim()
                });
            }
        }

        lblEstado.setText("Mostrando eventos sísmicos no revisados. Haga doble clic para seleccionar.");
    }

    // Mostrar los datos sísmicos registrados del evento seleccionado
    public void mostrarDatosSismicosRegistrados(String alcanceSismo, String clasificacionSismo, String origenGeneracion, List<ArrayList<String>> informacionSismica) {
        lblAlcance.setText("Alcance: " + alcanceSismo);
        lblClasificacion.setText("Clasificación: " + clasificacionSismo);
        lblOrigen.setText("Origen de Generación: " + origenGeneracion);

        if (informacionSismica != null && !informacionSismica.isEmpty()) {
            StringBuilder infoText = new StringBuilder();

            // Agrupar información por estación sismológica
            java.util.Map<String, java.util.List<ArrayList<String>>> datosPorEstacion = agruparPorEstacion(informacionSismica);

            // Mostrar información clasificada por estación
            int estacionNumero = 1;
            for (String estacion : datosPorEstacion.keySet()) {
                java.util.List<ArrayList<String>> seriesDeLaEstacion = datosPorEstacion.get(estacion);

                // Encabezado de estación con formato mejorado
                infoText.append("\n");
                infoText.append("╔════════════════════════════════════════════════════════════════╗\n");
                infoText.append("║  ESTACIÓN SISMOLÓGICA #").append(estacionNumero).append("\n");
                infoText.append("║  ").append(estacion).append("\n");
                infoText.append("╚════════════════════════════════════════════════════════════════╝\n\n");

                int serieNumero = 1;
                for (ArrayList<String> info : seriesDeLaEstacion) {
                    // Información de la serie temporal
                    infoText.append("  ├─ SERIE TEMPORAL #").append(serieNumero).append("\n");
                    infoText.append("  │  ID: ").append(info.get(0)).append("\n");
                    infoText.append("  │  Fecha/Hora Inicio: ").append(info.get(1)).append("\n");
                    infoText.append("  │  Frecuencia de Muestreo: ").append(info.get(2)).append(" Hz\n");

                    // Procesar muestras sísmicas (índices 3+)
                    if (info.size() > 3) {
                        infoText.append("  │\n");
                        infoText.append("  │  MUESTRAS SÍSMICAS:\n");

                        int muestraNumero = 1;
                        for (int i = 3; i < info.size(); i++) {
                            String datosMustra = info.get(i);
                            String[] valores = datosMustra.split("\\|");

                            if (valores.length >= 4) {
                                String fechaHora = valores[0];
                                String velocidad = valores[1];
                                String frecuencia = valores[2];
                                String longitud = valores[3];

                                boolean esUltima = (i == info.size() - 1);
                                String prefijo = esUltima ? "  │  └─" : "  │  ├─";

                                infoText.append(prefijo).append(" Muestra #").append(muestraNumero).append("\n");
                                infoText.append("  │     ├─ Fecha/Hora: ").append(fechaHora).append("\n");
                                infoText.append("  │     ├─ Velocidad de Onda: ").append(velocidad).append(" km/seg\n");
                                infoText.append("  │     ├─ Frecuencia de Onda: ").append(frecuencia).append(" Hz\n");
                                infoText.append("  │     └─ Longitud de Onda: ").append(longitud).append(" km/ciclo\n");

                                muestraNumero++;
                            }
                        }
                    }

                    infoText.append("  │\n");
                    serieNumero++;
                }

                estacionNumero++;
            }

            infoText.append("\n╔════════════════════════════════════════════════════════════════╗\n");
            infoText.append("║  INFORMACIÓN COMPLETAMENTE CARGADA Y CLASIFICADA              ║\n");
            infoText.append("╚════════════════════════════════════════════════════════════════╝\n");

            txtInfoSismica.setText(infoText.toString());
            txtInfoSismica.setCaretPosition(0);
        } else {
            txtInfoSismica.setText("Información Sísmica: No hay datos disponibles");
        }

        // Hacer visible el panel de datos sísmicos
        panelDatosSismicos.setVisible(true);

        // Actualizar estado
        lblEstado.setText("Datos sísmicos registrados mostrados correctamente. Evento bloqueado en revisión.");

        // Revalidar y repintar para actualizar la interfaz
        revalidate();
        repaint();
    }

    /**
     * Agrupa la información sísmica por estación sismológica.
     * Utiliza el código y nombre de estación que vienen al final de cada ArrayList<String>.
     */
    private java.util.Map<String, java.util.List<ArrayList<String>>> agruparPorEstacion(List<ArrayList<String>> informacionSismica) {
        java.util.Map<String, java.util.List<ArrayList<String>>> datosPorEstacion = new java.util.LinkedHashMap<>();

        for (ArrayList<String> datosSSerie : informacionSismica) {
            // El código y nombre de estación están al final después de clasificar
            String nombreEstacion = "Desconocida";
            String codigoEstacion = "N/A";
            int indiceFinDatos = datosSSerie.size();

            // Verificar si los últimos dos elementos son código y nombre de estación
            // El código debe ser numérico (o similar), y el nombre debe ser texto descriptivo
            if (datosSSerie.size() >= 5) {
                String posibleCodigo = datosSSerie.get(datosSSerie.size() - 2);
                String posibleNombre = datosSSerie.get(datosSSerie.size() - 1);

                // Verificar que el código sea un número (código de estación)
                // y el nombre no contenga pipe (|) que indicaría que son datos de muestra
                if (!posibleNombre.contains("|") && esCodigoEstacion(posibleCodigo)) {
                    codigoEstacion = posibleCodigo;
                    nombreEstacion = posibleNombre;
                    indiceFinDatos = datosSSerie.size() - 2;
                }
            }

            String clave = nombreEstacion + " (Código: " + codigoEstacion + ")";

            // Crear una copia sin los datos de estación para almacenar
            ArrayList<String> datosLimpios = new ArrayList<>();
            for (int i = 0; i < indiceFinDatos; i++) {
                datosLimpios.add(datosSSerie.get(i));
            }

            datosPorEstacion.computeIfAbsent(clave, k -> new java.util.ArrayList<>()).add(datosLimpios);
        }

        return datosPorEstacion;
    }

    /**
     * Valida si una cadena es un código de estación válido.
     * Los códigos son típicamente números o números cortos.
     */
    private boolean esCodigoEstacion(String codigo) {
        try {
            Long.parseLong(codigo);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Habilitar la opción de visualizar mapa
    public void habilitarVisualizacionMapa() {
        btnVisualizarMapa.setEnabled(true);
        btnVisualizarMapa.setBackground(new Color(76, 175, 80)); // Color verde
        btnVisualizarMapa.setForeground(Color.WHITE);
        lblEstado.setText("Mapa de eventos habilitado. Puede visualizar el evento y las estaciones involucradas.");
        System.out.println("Botón de visualización de mapa habilitado.");
    }

    // Acción al presionar el botón de visualizar mapa
    private void visualizarMapa() {
        // Mostrar popup de confirmación
        int respuesta = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Desea visualizar el mapa del evento sísmico y las estaciones sismológicas involucradas?",
                "Confirmar Visualización de Mapa",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);

        if (respuesta == javax.swing.JOptionPane.YES_OPTION) {
            lblEstado.setText("Abriendo mapa de eventos sísmicos y estaciones sismológicas...");
            System.out.println("no se supone que apretes este :'v 67");
        } else {
            lblEstado.setText("Visualización de mapa cancelada.");
            gestor.tomarNoVisualizacion();
        }
    }

	public void solicitarModificaciónDatosSismicos() {
		int respuesta = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Desea modificar los datos sísmicos?",
                "Confirmar Modificación de Datos Sísmicos",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (respuesta == javax.swing.JOptionPane.YES_OPTION) {
            lblEstado.setText("Modificación de datos sísmicos iniciada.");
        } else {
            lblEstado.setText("Modificación de datos sísmicos cancelada.");
            gestor.tomarRechazoModificacion();
        }
	}

    public void solicitarOpcAccionEvento() {
        Object[] options = {
        "Aceptar",  // Option 0
        "TODO",   // Option 1
        "Rechazar" // Option 2
        };
        int respuesta = javax.swing.JOptionPane.showOptionDialog(
            this, // Parent component
            "¿Desea modificar los datos sísmicos?", // Message
            "Confirmar Modificación de Datos Sísmicos", // Title
            javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, // Option type (or DEFAULT_OPTION)
            javax.swing.JOptionPane.QUESTION_MESSAGE, // Message type (for the icon)
            null, // Icon (null to use default based on message type)
            options, // The custom array of buttons
            options[0] // The button to be initially focused
        );

        if (respuesta == 0) {
            lblEstado.setText("Modificación de datos sísmicos aceptada.");
        } else if (respuesta == 1) {
            lblEstado.setText("Modificación de datos sísmicos TODO seleccionada.");
        } else if (respuesta == 2) {
            gestor.rechazarEventoSismicoSeleccionado();
            lblEstado.setText("Evento sísmico rechazado.");
        }
    }
}