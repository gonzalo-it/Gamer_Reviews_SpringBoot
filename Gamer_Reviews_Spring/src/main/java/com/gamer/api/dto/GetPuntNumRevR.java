package com.gamer.api.dto;

public class GetPuntNumRevR {
    private int juego_id;
    private int totalPuntos;
    private int cantidadReviews;
    private double calificacion;

    // Getters y setters
    public int getJuego_id() { return juego_id; }
    public void setJuego_id(int juego_id) { this.juego_id = juego_id; }

    public int getTotalPuntos() { return totalPuntos; }
    public void setTotalPuntos(int totalPuntos) { this.totalPuntos = totalPuntos; }

    public int getCantidadReviews() { return cantidadReviews; }
    public void setCantidadReviews(int cantidadReviews) { this.cantidadReviews = cantidadReviews; }

    public double getCalificacion() { return calificacion; }
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
}