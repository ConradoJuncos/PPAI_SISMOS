package com.ppai.app.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Revisión Manual de Eventos Sísmicos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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

        // Tabla de eventos
        String[] columnas = {"Fecha y Hora", "Latitud Epicentro", "Longitud Epicentro", "Latitud Hipocentro", "Longitud Hipocentro"};
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
                        for (int i=0; i<5; i++) {
                            datosPrincipales += modeloTabla.getValueAt(fila, i).toString();
                            if (i!=4){
                                datosPrincipales += ", ";
                            }
                        }
                        tomarSeleccionEventoSismico(datosPrincipales);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        // Panel de datos sísmicos (inicialmente oculto)
        panelDatosSismicos = new JPanel();
        panelDatosSismicos.setLayout(new BoxLayout(panelDatosSismicos, BoxLayout.Y_AXIS));
        panelDatosSismicos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            "Datos Sísmicos Registrados",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
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

        // JTextArea para información sísmica (reemplaza JLabel para mejor formato)
        txtInfoSismica = new JTextArea(10, 50);
        txtInfoSismica.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtInfoSismica.setEditable(false);
        txtInfoSismica.setText("Información Sísmica: -");
        txtInfoSismica.setLineWrap(true);
        txtInfoSismica.setWrapStyleWord(true);
        JScrollPane scrollInfoSismica = new JScrollPane(txtInfoSismica);
        scrollInfoSismica.setBorder(BorderFactory.createTitledBorder("Información Sísmica"));

        panelDatosSismicos.add(lblAlcance);
        panelDatosSismicos.add(lblClasificacion);
        panelDatosSismicos.add(lblOrigen);
        panelDatosSismicos.add(scrollInfoSismica);

        panelCentral.add(panelDatosSismicos, BorderLayout.SOUTH);
        add(panelCentral, BorderLayout.CENTER);

        // Barra inferior (estado)
        lblEstado = new JLabel("Listo.");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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

        this.gestor = new GestorRevisionManual(this, eventos, usuario);
        lblEstado.setText("Eventos cargados correctamente.");
    }

    public void mostrarEventosSismicosYSolicitarSeleccion(List<String> datosPrincipales) {
        modeloTabla.setRowCount(0);

        for (String datos : datosPrincipales) {
            String[] partes = datos.split(",");
            if (partes.length >= 5) {
                modeloTabla.addRow(new Object[]{
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
    public void mostrarDatosSismicosRegistrados(List<Object> metadatos, List<Object> informacionSismica) {
        // Actualizar los labels con los metadatos
        if (metadatos != null && metadatos.size() >= 3) {
            lblAlcance.setText("Alcance: " + metadatos.get(0).toString());
            lblClasificacion.setText("Clasificación: " + metadatos.get(1).toString());
            lblOrigen.setText("Origen de Generación: " + metadatos.get(2).toString());
        }

        // Mostrar información sísmica en el JTextArea
        if (informacionSismica != null && !informacionSismica.isEmpty()) {
            StringBuilder infoText = new StringBuilder();
            int serieNumero = 1;
            for (Object info : informacionSismica) {
                infoText.append("=== Serie Temporal ").append(serieNumero).append(" ===\n");
                infoText.append(info.toString()).append("\n");
                serieNumero++;
            }
            txtInfoSismica.setText(infoText.toString());
            txtInfoSismica.setCaretPosition(0); // Scroll al inicio
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
            javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == javax.swing.JOptionPane.YES_OPTION) {
            lblEstado.setText("Abriendo mapa de eventos sísmicos y estaciones sismológicas...");
            // Aquí se implementaría la lógica para abrir un mapa
            // Por ahora solo mostramos un mensaje
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Funcionalidad de mapa en desarrollo.\n" +
                "Se mostraría:\n" +
                "- Ubicación del evento sísmico (epicentro e hipocentro)\n" +
                "- Estaciones sismológicas involucradas\n" +
                "- Magnitud y alcance del evento",
                "Visualizar Mapa",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            lblEstado.setText("Visualización de mapa cancelada.");
        }
    }
}