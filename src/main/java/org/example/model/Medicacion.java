package org.example.model;

import org.example.service.ControladorMedicacion;

import java.time.LocalDate;

public class Medicacion {
    private int medicacionID;
    private int usuarioID;
    private int medicamentoID;
    private int dosis;
    private String frecuencia;
    private int duracion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    //Getters y Setters
    public int getMedicacionID() {
        return medicacionID;
    }

    public void setMedicacionID(int medicacionID) {
        this.medicacionID = medicacionID;
    }

    public int getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(int usuarioID) {
        this.usuarioID = usuarioID;
    }

    public int getMedicamentoID() {
        return medicamentoID;
    }

    public void setMedicamentoID(int medicamentoID) {
        this.medicamentoID = medicamentoID;
    }

    public int getDosis() {
        return dosis;
    }

    public void setDosis(int dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        try {
            ControladorMedicacion controlador = new ControladorMedicacion();
            return controlador.obtenerNombreMedicamentoPorId(medicamentoID);
        } catch (Exception e) {
            return "Medicamento no encontrado";
        }
    }
}
