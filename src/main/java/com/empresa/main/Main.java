package com.empresa.main;

import com.empresa.dao.EmpleadoDAO;
import com.empresa.model.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Main extends JFrame {

    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private int idSeleccionado = -1;
    private JTextField txtNombre, txtDepartamento, txtSalario, txtFecha;
    private JComboBox<String> cmbContrato;
    private JCheckBox chkActivo;
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnEliminar, btnLimpiar, btnTotales;

    public Main() {
        setTitle("Sistema de Gestión de Empleados - Parcial 2");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {

        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 5, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));
        panelFormulario.setPreferredSize(new Dimension(350, 0));

        panelFormulario.add(new JLabel("Nombre Completo:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Departamento:"));
        txtDepartamento = new JTextField();
        panelFormulario.add(txtDepartamento);

        panelFormulario.add(new JLabel("Salario Mensual (Q):"));
        txtSalario = new JTextField();
        panelFormulario.add(txtSalario);

        panelFormulario.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFecha = new JTextField();
        panelFormulario.add(txtFecha);

        panelFormulario.add(new JLabel("Tipo de Contrato:"));

        cmbContrato = new JComboBox<>(new String[]{"Temporal", "Permanente", "Por hora"});
        panelFormulario.add(cmbContrato);

        panelFormulario.add(new JLabel("Estado:"));
        chkActivo = new JCheckBox("Activo en la empresa", true);
        panelFormulario.add(chkActivo);


        btnGuardar = new JButton("Guardar / Actualizar");
        btnLimpiar = new JButton("Limpiar Campos");
        panelFormulario.add(btnGuardar);
        panelFormulario.add(btnLimpiar);

        add(panelFormulario, BorderLayout.WEST);

        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Depto", "Salario", "Fecha", "Contrato", "Activo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaEmpleados = new JTable(modeloTabla);
        panelTabla.add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);

        // Botones inferiores de la tabla
        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);

        btnTotales = new JButton("Ver Totales (Mejora #9)");
        btnTotales.setBackground(new Color(40, 167, 69));
        btnTotales.setForeground(Color.WHITE);

        panelBotonesTabla.add(btnEliminar);
        panelBotonesTabla.add(btnTotales);
        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        add(panelTabla, BorderLayout.CENTER);

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnTotales.addActionListener(e -> calcularTotales()); // MEJORA #9

        tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEmpleados.getSelectedRow() != -1) {
                cargarEmpleadoEnFormulario();
            }
        });
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Empleado> lista = empleadoDAO.listarTodos();
            for (Empleado emp : lista) {
                modeloTabla.addRow(new Object[]{
                        emp.getId(), emp.getNombreCompleto(), emp.getDepartamento(),
                        emp.getSalarioMensual(), emp.getFechaContratacion(),
                        emp.getTipoContrato(), emp.isActivo() ? "Sí" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtDepartamento.setText("");
        txtSalario.setText("");
        txtFecha.setText("");
        cmbContrato.setSelectedIndex(1);
        chkActivo.setSelected(true);
        tablaEmpleados.clearSelection();
    }

    private void cargarEmpleadoEnFormulario() {
        int fila = tablaEmpleados.getSelectedRow();
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtDepartamento.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtSalario.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtFecha.setText(modeloTabla.getValueAt(fila, 4).toString());
        cmbContrato.setSelectedItem(modeloTabla.getValueAt(fila, 5).toString());
        chkActivo.setSelected(modeloTabla.getValueAt(fila, 6).toString().equals("Sí"));
    }

    private void guardarEmpleado() {
        try {

            if (txtNombre.getText().isEmpty() || txtDepartamento.getText().isEmpty() || txtSalario.getText().isEmpty() || txtFecha.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nombre = txtNombre.getText().trim();
            String depto = txtDepartamento.getText().trim();
            BigDecimal salario = new BigDecimal(txtSalario.getText().trim());
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            String contrato = cmbContrato.getSelectedItem().toString(); // MEJORA #4
            boolean activo = chkActivo.isSelected();

            Empleado emp = new Empleado(nombre, depto, salario, fecha, activo, contrato);

            if (idSeleccionado == -1) {
                if (empleadoDAO.insertar(emp)) {
                    JOptionPane.showMessageDialog(this, "Empleado registrado exitosamente.");
                }
            } else {
                emp.setId(idSeleccionado);
                if (empleadoDAO.actualizar(emp)) {
                    JOptionPane.showMessageDialog(this, "Empleado actualizado exitosamente.");
                }
            }
            limpiarFormulario();
            cargarTabla();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El salario debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpleado() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (empleadoDAO.eliminar(idSeleccionado)) {
                    JOptionPane.showMessageDialog(this, "Empleado eliminado.");
                    limpiarFormulario();
                    cargarTabla();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void calcularTotales() {
        try {
            List<Empleado> lista = empleadoDAO.listarTodos();

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay empleados registrados para calcular totales.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            BigDecimal sumaTotal = BigDecimal.ZERO;
            int contador = 0;

            for (Empleado emp : lista) {
                sumaTotal = sumaTotal.add(emp.getSalarioMensual());
                contador++;
            }

            BigDecimal promedio = sumaTotal.divide(new BigDecimal(contador), 2, RoundingMode.HALF_UP);

            String mensaje = String.format("Resumen Financiero:\n\nTotal de Empleados: %d\nSuma Total de Salarios: Q%.2f\nPromedio Salarial: Q%.2f",
                    contador, sumaTotal, promedio);

            JOptionPane.showMessageDialog(this, mensaje, "Totales de Nómina", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al calcular totales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}