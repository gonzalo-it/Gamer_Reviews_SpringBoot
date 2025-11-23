package com.gamer.api.service;

import com.gamer.api.model.Comentario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

@Service
public class ComentarioService {

    private final JdbcTemplate jdbcTemplate;

    public ComentarioService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🔹 SP: sp_AddComentario
    public int addComentario(String comentario, int puntuacion, int usuarioId, int juegoId) {
        String call = "{? = call sp_AddComentario(?, ?, ?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, java.sql.Types.INTEGER);
                cs.setString(2, comentario);
                cs.setInt(3, puntuacion);
                cs.setInt(4, usuarioId);
                cs.setInt(5, juegoId);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // 🔹 SP: sp_GetComentariosByJuego
    public List<Comentario> getComentariosByJuego(int juegoId, String baseUrl) {
        String sql = "EXEC sp_GetComentariosByJuego @juego_id = ?";
        return jdbcTemplate.query(sql, new Object[]{juegoId}, (ResultSet rs, int rowNum) -> {
            Comentario c = new Comentario();
            c.setComentarioId(rs.getInt("comentario_id"));
            c.setTexto(rs.getString("comentario"));
            c.setPuntuacion(rs.getInt("puntuacion"));
            c.setUsuarioId(rs.getInt("usuario_id"));
            c.setJuegoId(rs.getInt("juego_id"));
            c.setUsuarioNombre(rs.getString("UsuarioNombre"));
            String foto = rs.getString("perfilURL");
            c.setPerfilURL(foto != null ? baseUrl + "/users/" + foto : null);
            return c;
        });
    }

    // 🔹 SP: sp_UpdateComentario
    public int updateComentario(int comentarioId, String nuevoTexto, int nuevaPuntuacion) {
        String call = "{? = call sp_UpdateComentario(?, ?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, java.sql.Types.INTEGER);
                cs.setInt(2, comentarioId);
                cs.setString(3, nuevoTexto);
                cs.setInt(4, nuevaPuntuacion);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // 🔹 SP: sp_DeleteComentario
    public int deleteComentario(int comentarioId) {
        String call = "{? = call sp_DeleteComentario(?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, java.sql.Types.INTEGER);
                cs.setInt(2, comentarioId);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }
}
