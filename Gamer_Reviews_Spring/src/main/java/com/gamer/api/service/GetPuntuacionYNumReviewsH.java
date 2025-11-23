package com.gamer.api.service;

import com.gamer.api.dto.GetPuntNumRevR;
import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

@Service
public class GetPuntuacionYNumReviewsH {

    private final SimpleJdbcCall jdbcCall;

    @Autowired
    public GetPuntuacionYNumReviewsH(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_GetCalificacionYCantReviews");
    }

    public BaseResponse getAll(int juego_id) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("juego_id", juego_id)
                    .addValue("TotalPuntos", 0)
                    .addValue("CantidadReviews", 0)
                    .addValue("Calificacion", 0.0);

            Map<String, Object> result = jdbcCall.execute(params);

            System.out.println("SP result: " + result); // 👈 para debug

            int totalPuntos = (Integer) result.get("TotalPuntos");
            int cantidadReviews = (Integer) result.get("CantidadReviews");
            double calificacion = ((Number) result.get("Calificacion")).doubleValue();

            GetPuntNumRevR dto = new GetPuntNumRevR();
            dto.setJuego_id(juego_id);
            dto.setTotalPuntos(totalPuntos);
            dto.setCantidadReviews(cantidadReviews);
            dto.setCalificacion(calificacion);

            return new DataResponse<>(true, 200, "Datos obtenidos", dto);

        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse(false, 500, "Error interno: " + e.getMessage());
        }
    }
}