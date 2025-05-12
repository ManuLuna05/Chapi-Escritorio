package org.example.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Recordatorios {
    private int recordatorioID;
    private int usuarioID;
    private Integer usuarioCuidadorID;
    private String descripcion;
    private String tipoEvento;
    private int numeroDosis;
    private LocalDate fecha;
    private LocalTime hora;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer citaMedicaID;
    private Integer medicacionID;
    private Integer actividadID;

    //Getters y Setters
    public int getRecordatorioID() {
        return recordatorioID;
    }

    public void setRecordatorioID(int recordatorioID) {
        this.recordatorioID = recordatorioID;
    }

    public int getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(int usuarioID) {
        this.usuarioID = usuarioID;
    }

    public Integer getUsuarioCuidadorID() {
        return usuarioCuidadorID;
    }

    public void setUsuarioCuidadorID(Integer usuarioCuidadorID) {
        this.usuarioCuidadorID = usuarioCuidadorID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public int getNumeroDosis() {
        return numeroDosis;
    }

    public void setNumeroDosis(int numeroDosis) {
        this.numeroDosis = numeroDosis;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getCitaMedicaID() {
        return citaMedicaID;
    }

    public void setCitaMedicaID(Integer citaMedicaID) {
        this.citaMedicaID = citaMedicaID;
    }

    public Integer getMedicacionID() {
        return medicacionID;
    }

    public void setMedicacionID(Integer medicacionID) {
        this.medicacionID = medicacionID;
    }

    public Integer getActividadID() {
        return actividadID;
    }

    public void setActividadID(Integer actividadID) {
        this.actividadID = actividadID;
    }

    @Override
    public String toString() {
        LocalDate hoy = LocalDate.now();
        if (fecha.isEqual(hoy)) {
            return "Recordatorio: " + descripcion + " - Fecha: Hoy - Hora: " + hora;
        } else if (fecha.isEqual(hoy.plusDays(1))) {
            return "Recordatorio: " + descripcion + " - Fecha: Mañana - Hora: " + hora;
        } else {
            return "Recordatorio: " + descripcion + " - Fecha: " + fecha + " - Hora: " + hora;
        }
    }
}
