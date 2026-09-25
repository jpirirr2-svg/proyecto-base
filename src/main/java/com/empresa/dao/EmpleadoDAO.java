package com.empresa.dao;

import com.empresa.database.ConexionBD;
import com.empresa.model.Empleado;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT id, nombre_completo, departamento, salario_mensual, fecha_contratacion, activo, tipo_contrato FROM empleados ORDER BY id ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre_completo");
                String depto = rs.getString("departamento");
                BigDecimal salario = rs.getBigDecimal("salario_mensual");
                LocalDate fecha = rs.getDate("fecha_contratacion").toLocalDate();
                boolean activo = rs.getBoolean("activo");
                String tipoContrato = rs.getString("tipo_contrato"); // MEJORA #4

                Empleado emp = new Empleado(id, nombre, depto, salario, fecha, activo, tipoContrato);
                lista.add(emp);
            }
        }
        return lista;
    }

    public Empleado obtenerPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre_completo, departamento, salario_mensual, fecha_contratacion, activo, tipo_contrato FROM empleados WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre_completo");
                    String depto = rs.getString("departamento");
                    BigDecimal salario = rs.getBigDecimal("salario_mensual");
                    LocalDate fecha = rs.getDate("fecha_contratacion").toLocalDate();
                    boolean activo = rs.getBoolean("activo");
                    String tipoContrato = rs.getString("tipo_contrato"); // MEJORA #4

                    return new Empleado(id, nombre, depto, salario, fecha, activo, tipoContrato);
                }
            }
        }
        return null;
    }

    public boolean insertar(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (nombre_completo, departamento, salario_mensual, fecha_contratacion, activo, tipo_contrato) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombreCompleto());
            ps.setString(2, empleado.getDepartamento());
            ps.setBigDecimal(3, empleado.getSalarioMensual());
            ps.setDate(4, Date.valueOf(empleado.getFechaContratacion()));
            ps.setBoolean(5, empleado.isActivo());
            ps.setString(6, empleado.getTipoContrato()); // MEJORA #4

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        empleado.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleados SET nombre_completo = ?, departamento = ?, salario_mensual = ?, fecha_contratacion = ?, activo = ?, tipo_contrato = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empleado.getNombreCompleto());
            ps.setString(2, empleado.getDepartamento());
            ps.setBigDecimal(3, empleado.getSalarioMensual());
            ps.setDate(4, Date.valueOf(empleado.getFechaContratacion()));
            ps.setBoolean(5, empleado.isActivo());
            ps.setString(6, empleado.getTipoContrato()); // MEJORA #4
            ps.setInt(7, empleado.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}