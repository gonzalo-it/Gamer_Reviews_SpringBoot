package com.gamer.api.service;

import com.gamer.api.model.Juego;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class BusquedaService {

    private final JdbcTemplate jdbcTemplate;

    public BusquedaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Llama a sp_GetSearchGames
    public List<Juego> buscarJuegos(String searchTerm, String baseUrl) {
    	String sql = "{call sp_GetSearchGames(?)}";
        return jdbcTemplate.query(sql, new Object[]{searchTerm}, (rs, rowNum) -> {
            Juego j = new Juego();
            j.setId(rs.getInt("Id"));
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));
           // j.setFechaPublicacion(rs.getDate("FechaPublicacion"));
            Date sqlDate = rs.getDate("FechaPublicacion");
            if (sqlDate != null) {
                j.setFechaPublicacion(sqlDate.toLocalDate());
            }

            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));
            String img = rs.getString("imagenURL");
            if (img != null && !img.isBlank()) {
                // Si ya es una URL absoluta o ya contiene /uploads, respetarla o completarla correctamente
                if (img.startsWith("http")) {
                    j.setImagenURL(img);
                } else if (img.startsWith("/uploads")) {
                    j.setImagenURL(baseUrl + img);
                } else if (img.contains("/uploads/")) {
                    // por si el SP devuelve "games/xxx" o "uploads/games/xxx"
                    if (img.startsWith("uploads")) j.setImagenURL(baseUrl + "/" + img);
                    else j.setImagenURL(baseUrl + "/uploads/games/" + img);
                } else {
                    // Asumimos solo nombre de archivo → usar la carpeta games
                    j.setImagenURL(baseUrl + "/uploads/games/" + img);
                }
            }
             return j;
         });
     }
 }