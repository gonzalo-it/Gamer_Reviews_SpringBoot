package com.gamer.api.dto;

public class ToggleLikeRequest {
    private int comentarioId;
    private int usuarioId;

    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}
