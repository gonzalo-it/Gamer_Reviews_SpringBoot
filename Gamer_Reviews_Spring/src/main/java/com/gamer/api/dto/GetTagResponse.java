package com.gamer.api.dto;

public class GetTagResponse {
    private Integer tag_id;
    private String nombre;

    public Integer getTag_id() { return tag_id; }
    public void setTag_id(Integer tag_id) { this.tag_id = tag_id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
