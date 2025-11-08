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
        String sql = "EXEC sp_GetSearchGames @search_term = ?";
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
            if (img != null && !img.isBlank())
                j.setImagenURL(baseUrl + "/uploads/" + img);
            return j;
        });
    }
}
