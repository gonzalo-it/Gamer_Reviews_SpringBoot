
package com.gamer.api.service;

import com.gamer.api.dto.*;
import com.gamer.api.model.*;
import com.gamer.api.storage.FileStorageCarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;

@Service
public class CarouselJuegoService {

    private final JdbcTemplate jdbcTemplate;
    private final FileStorageCarouselService carouselStorage;

    @Autowired
    public CarouselJuegoService(JdbcTemplate jdbcTemplate,
                        FileStorageCarouselService carouselStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.carouselStorage = carouselStorage;
    }

    // ================= FAVORITOS =================

    public int addOrRemoveFavorite(int usuarioId, int juegoId, boolean botonCheck) {
        String call = "{call sp_AddOrRemoveFavorite(?, ?, ?)}";
        return jdbcTemplate.execute((Connection conn) -> {
            CallableStatement cs = conn.prepareCall(call);
            cs.setInt(1, usuarioId);
            cs.setInt(2, juegoId);
            cs.setBoolean(3, botonCheck);
            cs.execute();
            return 0; // no devuelve return val
        });
    }

    public Optional<Boolean> isFavorite(int usuarioId, int juegoId) {
        String sql = "EXEC sp_IsFavoriteGame @usuario_id = ?, @juego_id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, usuarioId, juegoId);
        if (list.isEmpty()) return Optional.empty();
        Map<String, Object> row = list.get(0);
        Boolean fav = (Boolean) row.get("IsFavorite");
        return Optional.ofNullable(fav);
    }

    // ================= PRÓXIMOS LANZAMIENTOS =================

    public BaseResponse createProxJuego(String nombre, MultipartFile imagen) {
        try {
            String nombreArchivo = carouselStorage.saveImage(imagen);

            String call = "{call sp_AddNewProxJuego(?, ?)}";
            jdbcTemplate.update(call, nombre, nombreArchivo);

            return new BaseResponse(true, 200, "Próximo juego agregado correctamente");
        } catch (Exception ex) {
            return new BaseResponse(false, 500, "Error al agregar próximo juego: " + ex.getMessage());
        }
    }

    public List<ProxJuego> getAllProxJuegos(HttpServletRequest request) {
        String sql = "SELECT Id, Nombre, FotoUrl FROM proxJuegos";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProxJuego p = new ProxJuego();
            p.setId(rs.getInt("Id"));
            p.setNombre(rs.getString("Nombre"));
            String archivo = rs.getString("FotoUrl");
            p.setFotoUrl(String.format("%s://%s/carrousel/%s",
                    request.getScheme(), request.getServerName(), archivo));
            p.setFotoVieja(archivo);
            return p;
        });
    }

    public BaseResponse deleteProxJuego(int id, String imagenVieja) {
        try {
            String call = "{call SP_Delete_ProxJuego(?)}";
            jdbcTemplate.update(call, id);

            carouselStorage.deleteImage(imagenVieja);

            return new BaseResponse(true, 200, "Próximo juego eliminado correctamente");
        } catch (Exception ex) {
            return new BaseResponse(false, 500, "Error al eliminar: " + ex.getMessage());
        }
    }
}
