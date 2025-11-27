package com.gamer.api.dto;

import java.time.LocalDate;

public class JuegoRanking {
	    private Long id;
	    private String nombre;
	    private String descripcion;
	    private String imagenURL;
	    private LocalDate fechaPublicacion;
	    private String desarrollador;
	    private String editor;
	    private String plataforma;
	    private Integer totalPuntos;
	    private Integer cantidadReviews;
	    private Double calificacion;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getDescripcion() {
			return descripcion;
		}
		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}
		public String getImagenURL() {
			return imagenURL;
		}
		public void setImagenURL(String imagenURL) {
			this.imagenURL = imagenURL;
		}
		public LocalDate getFechaPublicacion() {
			return fechaPublicacion;
		}
		public void setFechaPublicacion(LocalDate fechaPublicacion) {
			this.fechaPublicacion = fechaPublicacion;
		}
		public String getDesarrollador() {
			return desarrollador;
		}
		public void setDesarrollador(String desarrollador) {
			this.desarrollador = desarrollador;
		}
		public String getEditor() {
			return editor;
		}
		public void setEditor(String editor) {
			this.editor = editor;
		}
		public String getPlataforma() {
			return plataforma;
		}
		public void setPlataforma(String plataforma) {
			this.plataforma = plataforma;
		}
		public Integer getTotalPuntos() {
			return totalPuntos;
		}
		public void setTotalPuntos(Integer totalPuntos) {
			this.totalPuntos = totalPuntos;
		}
		public Integer getCantidadReviews() {
			return cantidadReviews;
		}
		public void setCantidadReviews(Integer cantidadReviews) {
			this.cantidadReviews = cantidadReviews;
		}
		public Double getCalificacion() {
			return calificacion;
		}
		public void setCalificacion(Double calificacion) {
			this.calificacion = calificacion;
		}

	    // Getters y setters

}
