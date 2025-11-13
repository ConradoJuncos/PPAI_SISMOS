package com.ppai.app.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
    private JButton btnBloquearEvento;
    private JLabel lblEstado;

    // Paneles para mostrar información adicional
    private JPanel panelDatosSismicos;
        private JScrollPane scrollDatosSismicos; // ScrollPane para hacer visible
    private JLabel lblAlcance;
    private JLabel lblClasificacion;
    private JLabel lblOrigen;
    private JPanel panelInfoSismica; // Cambio de JTextArea a JPanel

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
        setSize(1200, 950);
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

        btnBloquearEvento = new JButton("Bloquear Evento");
        btnBloquearEvento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBloquearEvento.setEnabled(false); // Deshabilitado inicialmente
        btnBloquearEvento.addActionListener(e -> visualizarMapa());
        panelBotones.add(btnBloquearEvento);

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
        scrollPane.setPreferredSize(new java.awt.Dimension(1300, 180));
        panelCentral.add(scrollPane, BorderLayout.NORTH);

        // Panel de datos sísmicos (inicialmente oculto) con ÚNICA SCROLLBAR
        panelDatosSismicos = new JPanel();
        panelDatosSismicos.setLayout(new BoxLayout(panelDatosSismicos, BoxLayout.Y_AXIS));
        panelDatosSismicos.setBackground(Color.WHITE);
        panelDatosSismicos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels para metadatos
        lblAlcance = new JLabel("Alcance: -");
        lblAlcance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAlcance.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblClasificacion = new JLabel("Clasificación: -");
        lblClasificacion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblClasificacion.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblOrigen = new JLabel("Origen de Generación: -");
        lblOrigen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblOrigen.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Separador visual
        javax.swing.JSeparator separador = new javax.swing.JSeparator();
        separador.setForeground(new Color(70, 130, 180));

        // Panel para información sísmica con mejor diseño visual
        panelInfoSismica = new JPanel();
        panelInfoSismica.setLayout(new BoxLayout(panelInfoSismica, BoxLayout.Y_AXIS));
        panelInfoSismica.setBackground(new Color(245, 248, 252));
        panelInfoSismica.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "📊 Información Sísmica Clasificada por Estación",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(70, 130, 180)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        panelDatosSismicos.add(lblAlcance);
        panelDatosSismicos.add(lblClasificacion);
        panelDatosSismicos.add(lblOrigen);
        panelDatosSismicos.add(javax.swing.Box.createVerticalStrut(10));
        panelDatosSismicos.add(separador);
        panelDatosSismicos.add(javax.swing.Box.createVerticalStrut(15));
        panelDatosSismicos.add(panelInfoSismica);

        // ÚNICA scrollbar para toda la sección de datos sísmicos
        scrollDatosSismicos = new JScrollPane(panelDatosSismicos);
        scrollDatosSismicos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollDatosSismicos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDatosSismicos.getVerticalScrollBar().setUnitIncrement(20);
        scrollDatosSismicos.setVisible(false); // Inicialmente oculto
        panelCentral.add(scrollDatosSismicos, BorderLayout.CENTER);

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
    // El datosClasificados ya viene procesado en formato: [[idSerie, nombreEstacion, codigoEstacion, fechaInicio, frecuencia, muestras...], ...]
    public void mostrarDatosSismicosRegistrados(String alcanceSismo, String clasificacionSismo, String origenGeneracion, String datosClasificados) {
        lblAlcance.setText("Alcance: " + alcanceSismo);
        lblClasificacion.setText("Clasificación: " + clasificacionSismo);
        lblOrigen.setText("Origen de Generación: " + origenGeneracion);

        // Limpiar panel de información sísmica
        panelInfoSismica.removeAll();

        // Parsear el string de datos clasificados
        String contenidoLimpio = datosClasificados.replaceAll("^\\[\\[|\\]\\]$", "").trim();
        String[] estaciones = contenidoLimpio.split("\\], \\[");

        if (estaciones.length > 0 && !estaciones[0].isEmpty()) {
            for (String estacion : estaciones) {
                // Remover corchetes residuales
                estacion = estacion.replaceAll("^\\[|\\]$", "");

                // Parsear componentes: idSerie, nombreEstacion, codigoEstacion, fechaInicio, frecuencia, muestras...
                String[] componentes = estacion.split(", ");

                if (componentes.length >= 5) {
                    String idSerie = componentes[0].trim();
                    String nombreEstacion = componentes[1].trim();
                    String codigoEstacion = componentes[2].trim();
                    String fechaInicio = componentes[3].trim();
                    String frecuencia = componentes[4].trim();

                    // Compilar todas las muestras (a partir del índice 5)
                    StringBuilder muestrasBuilder = new StringBuilder();
                    for (int i = 5; i < componentes.length; i++) {
                        if (i > 5) muestrasBuilder.append(", ");
                        muestrasBuilder.append(componentes[i].trim());
                    }

                    // Crear panel para esta estación
                    JPanel panelEstacion = crearPanelEstacionConMuestras(
                        nombreEstacion, codigoEstacion, idSerie,
                        fechaInicio, frecuencia, muestrasBuilder.toString()
                    );
                    panelInfoSismica.add(panelEstacion);
                    panelInfoSismica.add(javax.swing.Box.createVerticalStrut(20)); // Espaciado entre estaciones
                }
            }

            // Panel final de confirmación
            JPanel panelFinal = new JPanel();
            panelFinal.setBackground(new Color(76, 175, 80));
            panelFinal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            JLabel lblFinal = new JLabel("✓ Información completamente cargada y clasificada");
            lblFinal.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblFinal.setForeground(Color.WHITE);
            panelFinal.add(lblFinal);
            panelInfoSismica.add(panelFinal);

        } else {
            JLabel lblNoData = new JLabel("No hay datos disponibles");
            lblNoData.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblNoData.setForeground(Color.GRAY);
            panelInfoSismica.add(lblNoData);
        }

        // Hacer visible el scroll de datos sísmicos
        scrollDatosSismicos.setVisible(true);

        // Actualizar estado
        lblEstado.setText("Datos sísmicos registrados mostrados correctamente. Evento bloqueado en revisión.");

        // Revalidar y repintar para actualizar la interfaz
        panelInfoSismica.revalidate();
        panelInfoSismica.repaint();

        // Hacer scroll al inicio
        javax.swing.SwingUtilities.invokeLater(() -> {
            scrollDatosSismicos.getVerticalScrollBar().setValue(0);
        });

        revalidate();
        repaint();
    }

    /**
     * Crea un panel visual completo para una estación sismológica con sus muestras sísmicas.
     */
    private JPanel crearPanelEstacionConMuestras(String nombreEstacion, String codigoEstacion,
                                                  String idSerie, String fechaInicio, String frecuencia,
                                                  String muestrasString) {
        JPanel panelEstacion = new JPanel();
        panelEstacion.setLayout(new BoxLayout(panelEstacion, BoxLayout.Y_AXIS));
        panelEstacion.setBackground(new Color(240, 248, 255));
        panelEstacion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelEstacion.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, panelEstacion.getPreferredSize().height));

        // Panel para encabezado + imagen (lado a lado)
        JPanel panelEncabezadoConImagen = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panelEncabezadoConImagen.setBackground(new Color(70, 130, 180));
        panelEncabezadoConImagen.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 70));

        // Encabezado de la estación
        JLabel lblEstacion = new JLabel("🏢 " + nombreEstacion + " (Código: " + codigoEstacion + ")");
        lblEstacion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstacion.setForeground(Color.WHITE);
        panelEncabezadoConImagen.add(lblEstacion);

        // Cargar y agregar imagen al encabezado
        try {
            // Intentar cargar desde la ruta del archivo del sistema
            java.io.File imagenFile = new java.io.File("src/main/java/com/ppai/app/resources/sismograma.png");

            if (!imagenFile.exists()) {
                // Intentar con ruta relativa al jar compilado
                imagenFile = new java.io.File("com/ppai/app/resources/sismograma.png");
            }

            if (imagenFile.exists()) {
                ImageIcon icono = new ImageIcon(imagenFile.getAbsolutePath());
                java.awt.Image imagenEscalada = icono.getImage().getScaledInstance(120, 50, java.awt.Image.SCALE_SMOOTH);
                ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
                JLabel lblImagen = new JLabel(iconoEscalado);
                panelEncabezadoConImagen.add(lblImagen);
                System.out.println("✓ Imagen cargada desde: " + imagenFile.getAbsolutePath());
            } else {
                System.err.println("✗ Archivo de imagen no encontrado en: " + imagenFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("✗ Error al cargar imagen: " + e.getMessage());
            e.printStackTrace();
        }

        panelEstacion.add(panelEncabezadoConImagen);
        panelEstacion.add(javax.swing.Box.createVerticalStrut(10));

        // Información de la serie temporal
        JLabel lblInfoSerie = new JLabel("Serie Temporal #" + idSerie);
        lblInfoSerie.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInfoSerie.setForeground(new Color(70, 130, 180));
        panelEstacion.add(lblInfoSerie);

        JLabel lblFechaInicio = new JLabel("📅 Fecha/Hora Inicio: " + fechaInicio);
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFechaInicio.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));
        panelEstacion.add(lblFechaInicio);

        JLabel lblFrecuencia = new JLabel("📡 Frecuencia de Muestreo: " + frecuencia + " Hz");
        lblFrecuencia.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFrecuencia.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 5));
        panelEstacion.add(lblFrecuencia);

        // Separador
        javax.swing.JSeparator sep = new javax.swing.JSeparator();
        sep.setForeground(new Color(176, 196, 222));
        panelEstacion.add(sep);
        panelEstacion.add(javax.swing.Box.createVerticalStrut(10));

        // Procesar y mostrar muestras
        if (!muestrasString.isEmpty()) {
            String[] muestras = muestrasString.split(", ");
            int numeroMuestra = 1;

            for (String muestra : muestras) {
                // Parsear muestra: fechaHora|velocidad|frecuencia|longitud
                String[] datosMuestra = muestra.split("\\|");
                if (datosMuestra.length == 4) {
                    String fechaHoraMuestra = datosMuestra[0];
                    String velocidad = datosMuestra[1];
                    String frecuenciaOnda = datosMuestra[2];
                    String longitud = datosMuestra[3];

                    JPanel panelMuestra = new JPanel();
                    panelMuestra.setLayout(new BoxLayout(panelMuestra, BoxLayout.Y_AXIS));
                    panelMuestra.setBackground(Color.WHITE);
                    panelMuestra.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                    ));

                    JLabel lblNumeroMuestra = new JLabel("⚡ Muestra #" + numeroMuestra + " - " + fechaHoraMuestra);
                    lblNumeroMuestra.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lblNumeroMuestra.setForeground(new Color(70, 130, 180));
                    panelMuestra.add(lblNumeroMuestra);

                    JLabel lblVelocidad = new JLabel("  🌊 Velocidad de Onda: " + velocidad + " km/seg");
                    lblVelocidad.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    panelMuestra.add(lblVelocidad);

                    JLabel lblFrecuenciaOnda = new JLabel("  📡 Frecuencia de Onda: " + frecuenciaOnda + " Hz");
                    lblFrecuenciaOnda.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    panelMuestra.add(lblFrecuenciaOnda);

                    JLabel lblLongitudOnda = new JLabel("  📏 Longitud de Onda: " + longitud + " km/ciclo");
                    lblLongitudOnda.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    panelMuestra.add(lblLongitudOnda);

                    panelEstacion.add(panelMuestra);
                    panelEstacion.add(javax.swing.Box.createVerticalStrut(8)); // Espaciado entre muestras

                    numeroMuestra++;
                }
            }
        }

        // Revalidar y repintar para mostrar cambios
        panelEstacion.revalidate();
        panelEstacion.repaint();

        return panelEstacion;
    }

    /**
     * Crea un panel visual simplificado para una estación sismológica.
     */
    private JPanel crearPanelEstacion(String nombreEstacion, String numero, String series) {
        JPanel panelEstacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelEstacion.setBackground(Color.WHITE);
        panelEstacion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblEstacion = new JLabel("Estación #" + numero + " | Nombre: " + nombreEstacion + " | Series: " + series);
        lblEstacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelEstacion.add(lblEstacion);

        return panelEstacion;
    }

    /**
     * Crea un panel visual para una serie temporal - ahora no se usa.
     */
    private JPanel crearPanelSerieTemporal(int idEstacion, String datosSerie) {
        JPanel panelSerie = new JPanel();
        panelSerie.setLayout(new BoxLayout(panelSerie, BoxLayout.Y_AXIS));
        panelSerie.setBackground(new Color(245, 248, 252));
        panelSerie.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(176, 196, 222), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return panelSerie;
    }

    /**
     * Crea un panel visual para una muestra sísmica.
     */
    private JPanel crearPanelMuestra(int numero, String[] valores) {
        JPanel panelMuestra = new JPanel();
        panelMuestra.setLayout(new GridLayout(4, 1, 3, 3));
        panelMuestra.setBackground(Color.WHITE);
        panelMuestra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        String fechaHora = valores[0];
        String velocidad = valores[1];
        String frecuencia = valores[2];
        String longitud = valores[3];

        JLabel lblNumero = new JLabel("    ⚡ Muestra #" + numero + " - " + fechaHora);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNumero.setForeground(new Color(70, 130, 180));

        JLabel lblVel = new JLabel("       🌊 Velocidad de Onda: " + velocidad + " km/seg");
        lblVel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel lblFreq = new JLabel("       📡 Frecuencia de Onda: " + frecuencia + " Hz");
        lblFreq.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel lblLong = new JLabel("       📏 Longitud de Onda: " + longitud + " km/ciclo");
        lblLong.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        panelMuestra.add(lblNumero);
        panelMuestra.add(lblVel);
        panelMuestra.add(lblFreq);
        panelMuestra.add(lblLong);

        return panelMuestra;
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
        btnBloquearEvento.setEnabled(true);
        btnBloquearEvento.setBackground(new Color(76, 175, 80)); // Color verde
        btnBloquearEvento.setForeground(Color.WHITE);
        lblEstado.setText("Mapa de eventos habilitado. Puede visualizar el evento y las estaciones involucradas.");
        System.out.println("Botón de bloqueo de evento habilitado.");
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
        "Confirmar",  // Option 0
        "Derivar A Experto",   // Option 1
        "Rechazar" // Option 2
        };
        int respuesta = javax.swing.JOptionPane.showOptionDialog(
            this, // Parent component
            "Seleccione una acción", // Message
            "Seleccion de Acción", // Title
            javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, // Option type (or DEFAULT_OPTION)
            javax.swing.JOptionPane.QUESTION_MESSAGE, // Message type (for the icon)
            null, // Icon (null to use default based on message type)
            options, // The custom array of buttons
            options[0] // The button to be initially focused
        );

        if (respuesta == 0) {
            gestor.confirmarEventoSismicoSeleccionado();
            lblEstado.setText("Modificación de datos sísmicos aceptada.");
        } else if (respuesta == 1) {
            gestor.derivarAExpertoEventoSismicoSeleccionado();
            lblEstado.setText("Modificación de datos sísmicos TODO seleccionada.");
        } else if (respuesta == 2) {
            gestor.rechazarEventoSismicoSeleccionado();
            lblEstado.setText("Evento sísmico rechazado.");
        }
    }
}