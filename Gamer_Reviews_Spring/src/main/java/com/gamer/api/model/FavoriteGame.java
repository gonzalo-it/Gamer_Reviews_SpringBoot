package com.gamer.api.model;

public class FavoriteGame {
    private int usuarioId;
    private int juegoId;
    private boolean botonCheck;
	public int getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}
	public int getJuegoId() {
		return juegoId;
	}
	public void setJuegoId(int juegoId) {
		this.juegoId = juegoId;
	}
	public boolean isBotonCheck() {
		return botonCheck;
	}
	public void setBotonCheck(boolean botonCheck) {
		this.botonCheck = botonCheck;
	}

   
    
}