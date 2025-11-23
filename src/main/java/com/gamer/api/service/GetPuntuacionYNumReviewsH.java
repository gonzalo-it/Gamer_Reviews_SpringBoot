package com.gamer.api.service;

import com.gamer.api.dto.GetPuntNumRevR;
import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.sql.Types;
import java.util.Map;

@Service
public class GetPuntuacionYNumReviewsH {

    private final SimpleJdbcCall jdbcCall;

    @Autowired
    public GetPuntuacionYNumReviewsH(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_GetCalificacionYCantReviews")
                .declareParameters(
                        new SqlParameter("juego_id", Types.INTEGER),
                        new SqlOutParameter("TotalPuntos", Types.INTEGER),
                        new SqlOutParameter("CantidadReviews", Types.INTEGER),
                        new SqlOutParameter("Calificacion", Types.DECIMAL)
                );
    }

    public BaseResponse getAll(int juego_id) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("juego_id", juego_id);

            Map<String, Object> result = jdbcCall.execute(params);

            System.out.println("SP result: " + result); // DEBUG

            Integer totalPuntos = (Integer) result.getOrDefault("TotalPuntos", 0);
            Integer cantidadReviews = (Integer) result.getOrDefault("CantidadReviews", 0);

            double calificacion = 0.0;
            Object califObj = result.get("Calificacion");
            if (califObj != null)
                calificacion = ((Number) califObj).doubleValue();

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
