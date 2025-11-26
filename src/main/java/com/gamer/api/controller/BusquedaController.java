package com.gamer.api.controller;

import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;
import com.gamer.api.model.Juego;
import com.gamer.api.service.BusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/busqueda")
public class BusquedaController {

    private final BusquedaService busquedaService;

    @Autowired
    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/get-search-games")
    public ResponseEntity<?> getSearchGames(
            @RequestParam("search_term") String searchTerm,
            HttpServletRequest request) {
        try {
            System.out.println("Search term received: " + searchTerm); // Debug
            
            String baseUrl = request.getScheme() + "://" + request.getServerName() +
                    (request.getServerPort() != 80 && request.getServerPort() != 443
                            ? ":" + request.getServerPort() : "");
            
            System.out.println("Base URL: " + baseUrl); // Debug
            
            List<Juego> juegos = busquedaService.buscarJuegos(searchTerm, baseUrl);
            
            System.out.println("Results found: " + juegos.size()); // Debug
            
            if (juegos.isEmpty())
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Sin resultados", List.of()));
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Lista de juegos", juegos));
        } catch (Exception e) {
            e.printStackTrace(); // IMPORTANT: Check your console!
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en búsqueda: " + e.getMessage()));
        }
    }
}
