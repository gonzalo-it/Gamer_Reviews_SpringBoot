package com.gamer.api.dto;

public class AddComentarioRequest {
    private String comentario;
    private int puntuacion;
    private int usuarioId;
    private int juegoId;

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getJuegoId() { return juegoId; }
    public void setJuegoId(int juegoId) { this.juegoId = juegoId; }
}
