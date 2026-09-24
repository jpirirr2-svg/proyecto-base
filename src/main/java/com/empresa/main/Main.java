package com.empresa.main;

import com.empresa.dao.EmpleadoDAO;
import com.empresa.model.Empleado;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = 0;
        do {
            System.out.println("\n--- SISTEMA DE GESTIÓN DE EMPLEADOS ---");
            System.out.println("1. Listar empleados");
            System.out.println("2. Registrar nuevo empleado");
            System.out.println("3. Editar empleado");
            System.out.println("4. Eliminar empleado");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: listarEmpleados(); break;
                    case 2: registrarEmpleado(); break;
                    case 3: editarEmpleado(); break;
                    case 4: eliminarEmpleado(); break;
                    case 5: System.out.println("Saliendo del sistema..."); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        } while (opcion != 5);
    }

    private static void listarEmpleados() throws SQLException {
        List<Empleado> lista = empleadoDAO.listarTodos();
        System.out.println("\n--- Listado de Empleados ---");
        if (lista.isEmpty()) {
            System.out.println("No hay empleados registrados.");
        } else {
            for (Empleado emp : lista) {
                System.out.println(emp.toString());
            }
        }
    }

    private static void registrarEmpleado() throws SQLException {
        System.out.println("\n--- Registrar Empleado ---");

        String nombre = leerTextoPlano("Nombre completo: ");
        String depto = leerTextoPlano("Departamento: ");
        BigDecimal salario = leerSalario();
        LocalDate fecha = leerFecha();

        Empleado emp = new Empleado(nombre, depto, salario, fecha, true); // Activo por defecto al registrar

        if (empleadoDAO.insertar(emp)) {
            System.out.println("Empleado registrado exitosamente.");
        } else {
            System.out.println(" No se pudo registrar el empleado.");
        }
    }

    private static void editarEmpleado() throws SQLException {
        System.out.println("\n--- Editar Empleado ---");
        System.out.print("Ingrese el ID del empleado a editar: ");
        int id = Integer.parseInt(scanner.nextLine());

        Empleado emp = empleadoDAO.obtenerPorId(id);
        if (emp == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.println("Editando a: " + emp.getNombreCompleto());
        emp.setNombreCompleto(leerTextoPlano("Nuevo Nombre [" + emp.getNombreCompleto() + "]: "));
        emp.setDepartamento(leerTextoPlano("Nuevo Departamento [" + emp.getDepartamento() + "]: "));
        emp.setSalarioMensual(leerSalario());
        emp.setFechaContratacion(leerFecha());

        System.out.print("¿Sigue activo en la empresa? (S/N) [" + (emp.isActivo() ? "S" : "N") + "]: ");
        String estado = scanner.nextLine().trim().toUpperCase();
        if (estado.equals("S")) emp.setActivo(true);
        else if (estado.equals("N")) emp.setActivo(false);

        if (empleadoDAO.actualizar(emp)) {
            System.out.println(" Empleado actualizado exitosamente.");
        } else {
            System.out.println(" No se pudo actualizar el empleado.");
        }
    }

    private static void eliminarEmpleado() throws SQLException {
        System.out.println("\n--- Eliminar Empleado ---");
        System.out.print("Ingrese el ID del empleado a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        Empleado emp = empleadoDAO.obtenerPorId(id);
        if (emp == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.print("⚠️ ¿Está seguro que desea ELIMINAR físicamente a " + emp.getNombreCompleto() + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (confirmacion.equals("S")) {
            if (empleadoDAO.eliminar(id)) {
                System.out.println(" Empleado eliminado del sistema.");
            } else {
                System.out.println(" Error al eliminar.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private static String leerTextoPlano(String mensaje) {
        String texto;
        while (true) {
            System.out.print(mensaje);
            texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) return texto;
            System.out.println("Error: El campo no puede quedar vacío.");
        }
    }

    private static BigDecimal leerSalario() {
        while (true) {
            System.out.print("Salario mensual (ej. 8500.00): ");
            try {
                BigDecimal salario = new BigDecimal(scanner.nextLine().trim());
                if (salario.compareTo(BigDecimal.ZERO) > 0) {
                    return salario;
                }
                System.out.println("Error: El salario debe ser mayor a cero.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un monto numérico válido.");
            }
        }
    }

    private static LocalDate leerFecha() {
        while (true) {
            System.out.print("Fecha de contratación (YYYY-MM-DD): ");
            try {
                LocalDate fecha = LocalDate.parse(scanner.nextLine().trim());
                if (!fecha.isAfter(LocalDate.now())) {
                    return fecha;
                }
                System.out.println("Error: La fecha no puede ser futura.");
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha incorrecto. Use YYYY-MM-DD.");
            }
        }
    }
}