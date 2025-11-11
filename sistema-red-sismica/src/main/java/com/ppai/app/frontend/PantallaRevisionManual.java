package com.ppai.app.frontend;

import com.ppai.app.contexto.Contexto;
import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.frontend.interfaces.IPantallaRevisionManual;
import com.ppai.app.gestor.GestorRevisionManual;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PantallaRevisionManual extends JFrame implements IPantallaRevisionManual {

    private final Contexto contexto;
    private GestorRevisionManual gestor;
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JButton btnEjecutar;
    private JLabel lblEstado;

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
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Revisión Manual de Eventos Sísmicos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        btnEjecutar = new JButton("Ejecutar Caso de Uso");
        btnEjecutar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnEjecutar.addActionListener(e -> ejecutarCasoDeUso());
        panelSuperior.add(btnEjecutar, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        // Tabla central
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

        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        add(scrollPane, BorderLayout.CENTER);

        // Barra inferior (estado)
        lblEstado = new JLabel("Listo.");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(lblEstado, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ================================
    // LÓGICA
    // ================================
    private void ejecutarCasoDeUso() {
        lblEstado.setText("Ejecutando caso de uso...");
        Usuario usuario = contexto.getUsuarios().get(0); // Ejemplo: primer usuario logueado
        List<EventoSismico> eventos = contexto.getEventosSismicos();

        gestor = new GestorRevisionManual(this, eventos, usuario);
        lblEstado.setText("Eventos cargados correctamente.");
    }

    @Override
    public void mostrarEventosSismicosYSolicitarSeleccion(List<String> datosPrincipales) {
        modeloTabla.setRowCount(0); // limpiar tabla

        for (String datos : datosPrincipales) {
            String[] partes = datos.split(",");
            // aseguramos que tenga al menos 5 columnas visibles
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

        lblEstado.setText("Mostrando eventos sísmicos no revisados.");
    }
}
