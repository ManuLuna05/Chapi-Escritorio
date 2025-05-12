package org.example.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ActividadFisica {
    private int id;
    private int usuarioId;
    private Integer usuarioCuidadorId;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int duracion;

    //Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getUsuarioCuidadorId() {
        return usuarioCuidadorId;
    }

    public void setUsuarioCuidadorId(Integer usuarioCuidadorId) {
        this.usuarioCuidadorId = usuarioCuidadorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
