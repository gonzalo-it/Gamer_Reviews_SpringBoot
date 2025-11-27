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
import java.util.List;
import java.util.Map;

@Service
public class GetPuntuacionYNumReviewsH {

    private final SimpleJdbcCall jdbcCall;

    @Autowired
    public GetPuntuacionYNumReviewsH(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_ObtenerCalificacionJuego")
                .returningResultSet("result", (rs, rowNum) -> {
                    GetPuntNumRevR dto = new GetPuntNumRevR();
                    dto.setJuego_id(rs.getInt("juegoId"));
                    dto.setTotalPuntos(rs.getInt("totalPuntos"));
                    dto.setCantidadReviews(rs.getInt("cantidadReviews"));
                    dto.setCalificacion(rs.getDouble("calificacion"));
                    return dto;
                });
    }


    public BaseResponse getAll(int juego_id) {
        try {
        	MapSqlParameterSource params = new MapSqlParameterSource()
        	        .addValue("JuegoId", juego_id);

        	Map<String, Object> result = jdbcCall.execute(params);

        	@SuppressWarnings("unchecked")
        	List<GetPuntNumRevR> list = (List<GetPuntNumRevR>) result.get("result");

        	GetPuntNumRevR dto = list.isEmpty() ? new GetPuntNumRevR() : list.get(0);

        	return new DataResponse<>(true, 200, "Datos obtenidos", dto);

        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse(false, 500, "Error interno: " + e.getMessage());
        }
    }
}
