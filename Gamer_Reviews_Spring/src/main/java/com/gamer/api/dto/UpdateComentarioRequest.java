package com.gamer.api.dto;

public class UpdateComentarioRequest {
    private int comentarioId;
    private String nuevoTexto;
    private int nuevaPuntuacion;

    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public String getNuevoTexto() { return nuevoTexto; }
    public void setNuevoTexto(String nuevoTexto) { this.nuevoTexto = nuevoTexto; }

    public int getNuevaPuntuacion() { return nuevaPuntuacion; }
    public void setNuevaPuntuacion(int nuevaPuntuacion) { this.nuevaPuntuacion = nuevaPuntuacion; }
}
