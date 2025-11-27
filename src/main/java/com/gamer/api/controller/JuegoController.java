package com.gamer.api.controller;
// 5) Controller (endpoints identicos a C#)

/* Observaciones:

En create-juego y edit-game acepto parametros @RequestParam y @RequestPart MultipartFile para emular [FromForm] de C#.

Ajusta los nombres de los parametros si tu front los envía con otros keys.
*/

import com.gamer.api.dto.*;
import com.gamer.api.model.FavoriteGame;
import com.gamer.api.model.IsFavoriteResult;
import com.gamer.api.model.Juego;
import com.gamer.api.service.JuegoService;
import com.gamer.api.storage.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import com.gamer.api.service.CarouselJuegoService;  // 👈 usa tu nuevo servicio
import com.gamer.api.service.GetPuntuacionYNumReviewsH;

@RestController
@RequestMapping("/api/Juego")
public class JuegoController {

    private final JuegoService juegoService;
    private final FileStorageService fileStorage;
    private final CarouselJuegoService carouselStorage;  // 👈 cambia el tipo
    private final GetPuntuacionYNumReviewsH puntuacionHandler;

    public JuegoController(JuegoService juegoService,
                           @Qualifier("imageFileStorageService") FileStorageService fileStorage,
                           CarouselJuegoService carouselStorage,
                           GetPuntuacionYNumReviewsH puntuacionHandler) {
        this.juegoService = juegoService;
        this.fileStorage = fileStorage;
        this.carouselStorage = carouselStorage;
        this.puntuacionHandler = puntuacionHandler;
    }

 // ============================================================
    // AGREGAR JUEGO
    // ============================================================
    @PostMapping(value = "/create-juego", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createJuego(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String fechaCreacion,
            @RequestParam String desarrollador,
            @RequestParam String editor,
            @RequestParam String plataforma,
            @RequestPart(required = false) MultipartFile imagen
    ) {
        try {
            LocalDate fecha = LocalDate.parse(fechaCreacion);

            // guardar imagen → solo nombre archivo
            String fileName = null;
            if (imagen != null && !imagen.isEmpty()) {
                fileName = fileStorage.saveImage(imagen, "games");
            }

            int res = juegoService.addNewGame(
                    nombre, descripcion, fecha,
                    desarrollador, editor, plataforma, fileName
            );

            if (res != 0)
                return ResponseEntity.badRequest()
                        .body(new BaseResponse(false, 400, "El juego ya existe"));

            return ResponseEntity.ok(new BaseResponse(true, 200, "Juego agregado"));
        }
        catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new BaseResponse(false, 500, "Error: " + e.getMessage()));
        }
    }

    // ============================================================
    // EDITAR JUEGO
    // ============================================================
    @PostMapping(value = "/edit-game", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editGame(
            @RequestParam("juegoId") int juegoId,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("fechaPublicacion") String fechaPublicacion,
            @RequestParam("desarrollador") String desarrollador,
            @RequestParam("editor") String editor,
            @RequestParam("plataforma") String plataforma,
            @RequestParam(value = "imagenVieja", required=false) String imagenVieja,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {

        try {
            LocalDate fecha = LocalDate.parse(fechaPublicacion);

            // nombre archivo nuevo (si hay imagen)
            String newFileName = null;

            if (imagen != null && !imagen.isEmpty()) {
                newFileName = fileStorage.saveImage(imagen, "games");

                // borrar foto anterior
                if (imagenVieja != null && !imagenVieja.isBlank()) {
                    fileStorage.deleteImageByUrl(imagenVieja);
                }
            }

            int res = juegoService.editGameById(
                    juegoId, nombre, descripcion, fecha,
                    desarrollador, editor, plataforma,
                    newFileName    // puede ser null
            );

            if (res != 0)
                return ResponseEntity.badRequest()
                        .body(new BaseResponse(false, 400, "Juego no encontrado"));

            return ResponseEntity.ok(new BaseResponse(true, 200, "Juego editado"));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new BaseResponse(false, 500, "Error: " + ex.getMessage()));
        }
    }
  /*  @PostMapping("/create-juego")
    public ResponseEntity<BaseResponse> createJuego(
    		HttpServletRequest request,   // <-- AGREGA ESTO
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam(required = false) String fechaCreacion, // ISO YYYY-MM-DD
            @RequestParam String desarrollador,
            @RequestParam String editor,
            @RequestParam String plataforma,
            @RequestPart(required = false) MultipartFile imagen
    ) {
        try {
            LocalDate fecha = (fechaCreacion == null || fechaCreacion.isBlank()) ? LocalDate.now() : LocalDate.parse(fechaCreacion);

            String imagenUrl = null;
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = fileStorage.saveImage(imagen, "games", request);
            }

            int result = juegoService.addNewGame(nombre, descripcion, fecha, desarrollador, editor, plataforma, imagenUrl);

            if (result == 0) return ResponseEntity.ok(new BaseResponse(true, 200, "Juego agregado correctamente"));
            else if (result == 1) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "El juego ya existe"));
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, "Error desconocido"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, ex.getMessage()));
        }
    }
    
    @PostMapping(value = "/edit-game", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> editGame(
    		HttpServletRequest request,   // <-- AGREGA ESTO
            @RequestParam("juegoId") Integer juegoId,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("fechaPublicacion") String fechaPublicacion,
            @RequestParam("desarrollador") String desarrollador,
            @RequestParam("editor") String editor,
            @RequestParam("plataforma") String plataforma,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "imagenVieja", required = false) String imagenVieja
    ) {
        try {

            LocalDate fecha = LocalDate.parse(fechaPublicacion);

            String imagenUrl = null;

            // si hay nueva imagen
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = fileStorage.saveImage(imagen, "games", request);
                fileStorage.deleteImageByUrl(imagenVieja);
            }
            if (fechaPublicacion == null || fechaPublicacion.isBlank()) {
                return ResponseEntity.badRequest().body(new BaseResponse(false, 400, "Fecha inválida"));
            }
           

            int res = juegoService.editGameById(
                    juegoId, nombre, descripcion, fecha,
                    desarrollador, editor, plataforma, imagenUrl
            );

            if (res == 0)
                return ResponseEntity.ok(new BaseResponse(true, 200, "Juego editado correctamente"));

            return ResponseEntity.status(400).body(new BaseResponse(false, 400, "No encontrado"));

        } catch (Exception ex) {
        	ex.printStackTrace();  // 👈 AGREGA ESTO
            return ResponseEntity.status(500).body(
                    new BaseResponse(false, 500, "Error: " + ex.getMessage())
            );
        }
    }
    */

 // ============================================================
    // GET ONE GAME
    // ============================================================
    @GetMapping("/get-one-game")
    public ResponseEntity<?> getOneGame(
            @RequestParam("game_id") int gameId,
            HttpServletRequest request
    ) {
        Optional<Juego> maybe = juegoService.getOneGame(gameId);

        if (maybe.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(false, 404, "Juego no encontrado"));

        Juego j = maybe.get();

        // construir URL final
        String archivo = j.getImagenURL();
        String baseUrl = request.getScheme() + "://" +
                request.getServerName() + ":" + request.getServerPort();

        if (archivo != null && !archivo.isBlank()) {
            j.setImagenURL(baseUrl + "/uploads/games/" + archivo);
        } else {
            j.setImagenURL(null);
        }

        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", j));
    }

    // ============================================================
    // GET ALL GAMES
    // ============================================================
    @GetMapping("/get-all-games")
    public ResponseEntity<?> getAllGames(HttpServletRequest request) {

        List<Juego> lista = juegoService.getAllGames();

        String baseUrl = request.getScheme() + "://" +
                request.getServerName() + ":" + request.getServerPort();

        for (Juego j : lista) {
            if (j.getImagenURL() != null && !j.getImagenURL().isBlank()) {
                j.setImagenURL(baseUrl + "/uploads/games/" + j.getImagenURL());
            }
        }

        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", lista));
    }

    // ============================================================
    // LAZY LOADING
    // ============================================================
    @GetMapping("/get-all-games-lazy")
    public ResponseEntity<?> getGamesLazy(HttpServletRequest request) {
        int page = parseIntParam(request, new String[]{"page", "pageNumber"}, 1);
        int size = parseIntParam(request, new String[]{"size", "pageSize"}, 20);

        List<Map<String, Object>> lista = juegoService.getGamesLazy(page, size);

        String baseUrl = request.getScheme() + "://" +
                request.getServerName() + ":" + request.getServerPort();

        for (Map<String, Object> row : lista) {
            Object archivo = row.get("imagenURL");
            if (archivo != null && !archivo.toString().isBlank()) {
                row.put("imagenURL", baseUrl + "/uploads/games/" + archivo);
            }
        }

        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", lista));
    }

    private int parseIntParam(HttpServletRequest request, String[] keys, int defaultValue) {
        for (String k : keys) {
            String v = request.getParameter(k);
            if (v != null && !v.isBlank()) {
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return defaultValue;
    }

    // ============================================================
    // DELETE (BAJA)
    // ============================================================
    @DeleteMapping("/delete-juego")
    public ResponseEntity<?> deleteJuego(@RequestParam("id") int id) {

        Map<String,Object> res = juegoService.darDeBajaJuego(id);

        return ResponseEntity.ok(
                new BaseResponse(
                        (Integer) res.get("success") == 1,
                        200,
                        res.get("message").toString()
                )
        );
    }
    
    @GetMapping("/get-my-games/{usuarioId}")
    public ResponseEntity<?> getMyGames(
            @PathVariable int usuarioId,   // ← CORRECTO
            HttpServletRequest request
    ) {
        String baseUrl = request.getScheme() + "://" 
                + request.getServerName() + ":" 
                + request.getServerPort();

        List<Juego> list = juegoService.getMyGames(usuarioId, baseUrl);

        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", list));
    }


    @GetMapping("/get-ranking")
    public ResponseEntity<DataResponse<List<Map<String,Object>>>> getRanking(HttpServletRequest request) {
        List<Map<String,Object>> ranking = juegoService.getTopRanking();

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        // Reconstruir image URLs en cada fila si existe la clave imagenURL
        for (Map<String, Object> row : ranking) {
            Object archivo = row.get("imagenURL");
            if (archivo != null && !archivo.toString().isBlank()) {
                row.put("imagenURL", baseUrl + "/uploads/games/" + archivo.toString());
            }
        }

        return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", ranking));
    }
    
 // ================= FAVORITOS =================
    @PostMapping("/add-favorite-game")
    public ResponseEntity<BaseResponse> addFavoriteGame(@RequestBody FavoriteGame request) {
        try {
            int result = carouselStorage.addOrRemoveFavorite(
                    request.getUsuarioId(),
                    request.getJuegoId(),
                    request.isBotonCheck()
            );

            return switch (result) {
                case 0 -> ResponseEntity.ok(new BaseResponse(true, 200, "Operación de favorito ejecutada correctamente"));
                case 1 -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "Usuario o juego no encontrado"));
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse(false, 400, "Error desconocido"));
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/is-favorite")
    public ResponseEntity<BaseResponse> isFavorite(@RequestParam int usuarioId, @RequestParam int juegoId) {
        try {
            Optional<Boolean> result = carouselStorage.isFavorite(usuarioId, juegoId);
            if (result.isPresent()) {
                IsFavoriteResult dto = new IsFavoriteResult();
                dto.setIsFavorite(result.get());
                return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", dto));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "No encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en isFavorite: " + e.getMessage()));
        }
    }

    // ================= PRÓXIMOS LANZAMIENTOS =================

    @PostMapping(value = "/create-proxjuego", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> createProxJuego(
            @RequestParam("nombre") String nombre,
            @RequestParam("imagen") MultipartFile imagen
    ) {
        try {
            BaseResponse res = carouselStorage.createProxJuego(nombre, imagen);
            return ResponseEntity.status(res.getCode()).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error al crear el próximo juego: " + e.getMessage()));
        }
    }

    @GetMapping("/get-proxjuegos")
    public ResponseEntity<BaseResponse> getProxJuegos(HttpServletRequest request) {
        try {
            var lista = carouselStorage.getAllProxJuegos(request);
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Lista obtenida con éxito", lista));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-proxjuego")
    public ResponseEntity<BaseResponse> deleteProxJuego(
            @RequestParam int id,
            @RequestParam String imagenVieja
    ) {
        try {
            BaseResponse res = carouselStorage.deleteProxJuego(id, imagenVieja);
            return ResponseEntity.status(res.getCode()).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error al eliminar el próximo juego: " + e.getMessage()));
        }
    }

    @GetMapping("/getPuntuacion/NumReviews")
    public ResponseEntity<BaseResponse> getPuntuacionYNumReviews(@RequestParam int juego_id) {
        try {
            var result = puntuacionHandler.getAll(juego_id);

            if (result == null) {
                return ResponseEntity.ok(new DataResponse<>(true, 200, "OK",
                    Map.of("puntuacion", 0, "reviews", 0)
                ));
            }

            return ResponseEntity.status(result.getCode()).body(result);

        } catch (Exception e) {
            return ResponseEntity.ok(
                new DataResponse<>(true, 200, "OK",
                    Map.of("puntuacion", 0, "reviews", 0)
                )
            );
        }
    }
}
