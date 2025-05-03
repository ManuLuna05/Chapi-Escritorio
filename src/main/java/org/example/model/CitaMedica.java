package org.example.model;

import java.time.LocalDateTime;

public class CitaMedica {
    private int id;
    private int usuarioId;
    private int usuarioCuidadorId;
    private LocalDateTime fechaCita;
    private String lugar;
    private String especialista;

    // Getters y Setters
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

    public int getUsuarioCuidadorId() {
        return usuarioCuidadorId;
    }

    public void setUsuarioCuidadorId(int usuarioCuidadorId) {
        this.usuarioCuidadorId = usuarioCuidadorId;
    }

    public LocalDateTime getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDateTime fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getEspecialista() {
        return especialista;
    }

    public void setEspecialista(String especialista) {
        this.especialista = especialista;
    }
}
