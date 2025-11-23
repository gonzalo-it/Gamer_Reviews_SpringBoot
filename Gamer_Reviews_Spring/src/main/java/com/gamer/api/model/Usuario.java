package com.gamer.api.model;

public class Usuario {
    private int usuarioId;
    private String usuario;
    private String correo;
    private String perfilURL;
    private String foto;
    private byte rol;
    private byte baja;

    // Getters y setters
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPerfilURL() { return perfilURL; }
    public void setPerfilURL(String perfilURL) { this.perfilURL = perfilURL; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public byte getRol() { return rol; }
    public void setRol(byte rol) { this.rol = rol; }

    public byte getBaja() { return baja; }
    public void setBaja(byte baja) { this.baja = baja; }
}
