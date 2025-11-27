package com.gamer.api.service;

import com.gamer.api.dto.GetTagResponse;
import com.gamer.api.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

@Service
public class TagService {

    private final JdbcTemplate jdbcTemplate;

    public TagService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // sp_AddNewTags -> returns int (0 ok, 1 exists)
    public int addTag(String nombre) {
        String call = "{? = call sp_AddNewTags(?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, nombre);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // sp_GetAllTags -> returns rows (tag_id, nombre)
    public List<GetTagResponse> getAllTags() {
        String sql = "{call sp_GetAllTags}";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GetTagResponse t = new GetTagResponse();
            t.setTag_id(rs.getInt("tag_id"));
            t.setNombre(rs.getString("nombre"));
            return t;
        });
    }

    // sp_DeleteTag (no return code in your C# - just execute)
    public boolean deleteTag(int tag_id) {
        String sql = "EXEC sp_DeleteTag @tag_id = ?";
        jdbcTemplate.update(sql, tag_id);
        return true;
    }

    // sp_InsertTagXGame -> returns int (0 inserted, 1 exists, 2 reactivated)
    public int insertTagXGame(Integer juego_id, Integer tag_id) {
        String call = "{? = call sp_InsertTagXGame(?, ?)}";
        return jdbcTemplate.execute(
            (Connection conn) -> {
                CallableStatement cs = conn.prepareCall(call);
                cs.registerOutParameter(1, Types.INTEGER);
                if (juego_id == null) cs.setObject(2, null);
                else cs.setInt(2, juego_id);
                if (tag_id == null) cs.setObject(3, null);
                else cs.setInt(3, tag_id);
                return cs;
            },
            (CallableStatement cs) -> {
                cs.execute();
                return cs.getInt(1);
            }
        );
    }

    // sp_GetTagXGame -> returns rows
    public List<GetTagResponse> getTagsXGame(int juego_id) {
        String sql = "EXEC sp_GetTagXGame @juego_id = ?";
        return jdbcTemplate.query(sql, new Object[]{juego_id}, (rs, rowNum) -> {
            GetTagResponse t = new GetTagResponse();
            t.setTag_id(rs.getInt("tag_id"));
            t.setNombre(rs.getString("nombre"));
            return t;
        });
    }

    // sp_DeleteTagXGame -> just execute SP
    public boolean deleteTagXGame(int juego_id, int tag_id) {
        String sql = "EXEC sp_DeleteTagXGame @juego_id = ?, @tag_id = ?";
        jdbcTemplate.update(sql, juego_id, tag_id);
        return true;
    }
}
