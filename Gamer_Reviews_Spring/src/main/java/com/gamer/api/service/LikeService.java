package com.gamer.api.service;

import com.gamer.api.model.LikeResult;
import com.gamer.api.model.LikeStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

@Service
public class LikeService {

    private final JdbcTemplate jdbcTemplate;

    public LikeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🔹 SP: sp_ToggleComentarioLike
    public LikeResult toggleLike(int comentarioId, int usuarioId) {
        String sql = "EXEC sp_ToggleComentarioLike @comentario_id = ?, @usuario_id = ?";
        List<LikeResult> list = jdbcTemplate.query(sql, new Object[]{comentarioId, usuarioId}, (ResultSet rs, int rowNum) -> {
            LikeResult lr = new LikeResult();
            lr.setLikeCount(rs.getInt("LikeCount"));
            lr.setLiked(rs.getInt("Liked") == 1);
            return lr;
        });

        return list.isEmpty() ? null : list.get(0);
    }

    // 🔹 SP: sp_GetComentarioLike
    public LikeStatus getLike(int comentarioId, int usuarioId) {
        String sql = "EXEC sp_GetComentarioLike @comentario_id = ?, @usuario_id = ?";
        List<LikeStatus> list = jdbcTemplate.query(sql, new Object[]{comentarioId, usuarioId}, (ResultSet rs, int rowNum) -> {
            LikeStatus ls = new LikeStatus();
            ls.setLikeCount(rs.getInt("LikeCount"));
            ls.setLiked(rs.getInt("Liked") == 1);
            return ls;
        });

        return list.isEmpty() ? null : list.get(0);
    }
}
