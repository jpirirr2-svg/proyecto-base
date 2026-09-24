package com.empresa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Empleado {
    private int id;
    private String nombreCompleto;
    private String departamento;
    private BigDecimal salarioMensual;
    private LocalDate fechaContratacion;
    private boolean activo;

    public Empleado() {
    }

    public Empleado(String nombreCompleto, String departamento, BigDecimal salarioMensual, LocalDate fechaContratacion, boolean activo) {
        this.nombreCompleto = nombreCompleto;
        this.departamento = departamento;
        this.salarioMensual = salarioMensual;
        this.fechaContratacion = fechaContratacion;
        this.activo = activo;
    }

    public Empleado(int id, String nombreCompleto, String departamento, BigDecimal salarioMensual, LocalDate fechaContratacion, boolean activo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.departamento = departamento;
        this.salarioMensual = salarioMensual;
        this.fechaContratacion = fechaContratacion;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public BigDecimal getSalarioMensual() {
        return salarioMensual;
    }

    public void setSalarioMensual(BigDecimal salarioMensual) {
        this.salarioMensual = salarioMensual;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-20s | %-15s | Q%.2f | %s",
                id,
                nombreCompleto,
                departamento,
                salarioMensual,
                activo ? "Activo" : "Inactivo");
    }
}