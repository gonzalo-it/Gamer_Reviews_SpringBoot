package com.gamer.api.model;

public class Comentario {
    private int comentarioId;
    private String texto;
    private int puntuacion;
    private int usuarioId;
    private int juegoId;
    private String usuarioNombre;
    private String perfilURL;

    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getJuegoId() { return juegoId; }
    public void setJuegoId(int juegoId) { this.juegoId = juegoId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getPerfilURL() { return perfilURL; }
    public void setPerfilURL(String perfilURL) { this.perfilURL = perfilURL; }
}
