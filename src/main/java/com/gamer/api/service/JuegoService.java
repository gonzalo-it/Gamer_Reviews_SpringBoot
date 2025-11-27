package com.gamer.api.service;

import com.gamer.api.model.Juego;

import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class JuegoService {

    private final JdbcTemplate jdbcTemplate;

    public JuegoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    // Helper: ejecutar SP con RETURN int
    // ============================================================
    private int callStoredProcedureReturn(String callSql, List<Object> params) {
        return jdbcTemplate.execute((Connection con) -> {
            CallableStatement cs = con.prepareCall(callSql);
            cs.registerOutParameter(1, Types.INTEGER);
            int index = 2;
            for (Object p : params) cs.setObject(index++, p);
            return cs;
        }, (CallableStatementCallback<Integer>) cs -> {
            cs.execute();
            return cs.getInt(1);
        });
    }

    // ============================================================
    // NUEVO JUEGO
    // ============================================================
    public int addNewGame(String nombre, String descripcion, LocalDate fechaPublicacion,
                          String desarrollador, String editor, String plataforma, String imagenArchivo) {

        String call = "{? = call sp_AddNewGame(?, ?, ?, ?, ?, ?, ?)}";
        List<Object> params = Arrays.asList(
                nombre,
                descripcion,
                Date.valueOf(fechaPublicacion),
                desarrollador,
                editor,
                plataforma,
                imagenArchivo  // 👈 solo nombre archivo
        );

        return callStoredProcedureReturn(call, params);
    }

    // ============================================================
    // OBTENER 1 JUEGO
    // ============================================================
    public Optional<Juego> getOneGame(int gameId) {
        String sql = "EXEC sp_GetOneGame @game_id = ?";

        List<Juego> lista = jdbcTemplate.query(sql, new Object[]{gameId}, (rs, rowNum) -> {
            Juego j = new Juego();
            j.setId(rs.getInt("Id"));
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));

            Date d = rs.getDate("FechaPublicacion");
            if (d != null) j.setFechaPublicacion(d.toLocalDate());

            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));

            // ❗ Importante: solo guardamos el nombre del archivo
            j.setImagenURL(rs.getString("imagenURL"));

            return j;
        });

        if (lista.isEmpty()) return Optional.empty();
        return Optional.of(lista.get(0));
    }

    // ============================================================
    // OBTENER TODOS LOS JUEGOS
    // ============================================================
    public List<Juego> getAllGames() {
        String sql = "EXEC sp_GetGames";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Juego j = new Juego();
            j.setId(rs.getInt("Id"));
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));
            Date d = rs.getDate("FechaPublicacion");
            if (d != null) j.setFechaPublicacion(d.toLocalDate());
            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));

            // Solo nombre archivo
            j.setImagenURL(rs.getString("imagenURL"));
            return j;
        });
    }

    // ============================================================
    // LAZY LOADING (paginado)
    // ============================================================
    public List<Map<String, Object>> getGamesLazy(int pageNumber, int limit) {
        String sql = "EXEC sp_GetGamesLazy @PageNumber = ?, @PageSize = ?";
        return jdbcTemplate.queryForList(sql, pageNumber, limit);
    }

    // ============================================================
    // EDITAR JUEGO
    // ============================================================
    public int editGameById(int juegoId, String nombre, String descripcion, LocalDate fecha,
                            String desarrollador, String editor, String plataforma, String imagenArchivo) {

        String call = "{? = call sp_EditeGameById(?, ?, ?, ?, ?, ?, ?, ?)}";

        List<Object> params = Arrays.asList(
                juegoId,
                nombre,
                descripcion,
                Date.valueOf(fecha),
                desarrollador,
                editor,
                plataforma,
                imagenArchivo   // 👈 null si no hay nueva imagen
        );

        return callStoredProcedureReturn(call, params);
    }

    // ============================================================
    // DAR DE BAJA
    // ============================================================
    public Map<String,Object> darDeBajaJuego(int juegoId) {
        String sql = "EXEC SP_Update_Juego_Baja @JuegoId = ?";
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, juegoId);
        if (rows.isEmpty()) return Map.of("success", 0, "message", "No data");
        return rows.get(0);
    }

    // ============================================================
    // JUEGOS DEL USUARIO
    // ============================================================
    public List<Juego> getMyGames(int usuarioId, String baseUrl) {
        String sql = "EXEC sp_GetMyGames @usuario_id = ?";

        return jdbcTemplate.query(sql, new Object[]{usuarioId}, (rs, rowNum) -> {
            Juego j = new Juego();
            j.setId(rs.getInt("Id"));
            j.setNombre(rs.getString("nombre"));
            j.setDescripcion(rs.getString("descripcion"));

            Date d = rs.getDate("fecha_publicacion");
            if (d != null) j.setFechaPublicacion(d.toLocalDate());

            j.setDesarrollador(rs.getString("desarrollador"));
            j.setEditor(rs.getString("editor"));
            j.setPlataforma(rs.getString("plataforma"));

            // ⬅ reconstruimos URL para que el FRONT pueda ver la imagen
            String imagen = rs.getString("imagenURL");
            if (imagen != null && !imagen.isBlank()) {
                j.setImagenURL(baseUrl + "/uploads/games/" + imagen);
            }

            return j;
        });
    }

    // ============================================================
    // RANKING
    // ============================================================
    public List<Map<String,Object>> getTopRanking() {
        String sql = "EXEC sp_GetTop10JuegosPorCalificacion";
        return jdbcTemplate.queryForList(sql);
    }
}
