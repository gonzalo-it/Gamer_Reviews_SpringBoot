package com.gamer.api.controller;

import com.gamer.api.dto.*;
import com.gamer.api.model.Usuario;
import com.gamer.api.service.UsuarioService;
import com.gamer.api.storage.UserFileStorageService;

import jakarta.servlet.http.HttpServletRequest;

import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;


import com.gamer.api.storage.UserFileStorageService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.gamer.api.model.Review;


@RestController
@RequestMapping("/api/Login")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/login/create-user
    @PostMapping("/create-user")
    public ResponseEntity<BaseResponse> createUser(@RequestBody AddUserRequest req) {
        try {
            int res = usuarioService.createUser(req.getCorreo(), req.getContrasena(), req.getNombre(), req.getFechaInscripcion());
            if (res == 0)
                return ResponseEntity.ok(new BaseResponse(true, 200, "Usuario creado correctamente"));
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "El usuario ya existe"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error: " + ex.getMessage()));
        }
    }

    // POST /api/login/login
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest req) {
        try {
            Optional<String> token = usuarioService.login(req.getCorreo(), req.getContrasena());
            if (token.isPresent()) {
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Login exitoso", token.get()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponse(false, 401, "Usuario o contraseña incorrectos"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en login: " + ex.getMessage()));
        }
    }

    // GET /api/login/get-user
    @GetMapping("/get-user")
    public ResponseEntity<BaseResponse> getUser(@RequestParam("usuario_id") int usuarioId) {
        try {
            Optional<Usuario> user = usuarioService.getUserById(usuarioId);
            if (user.isPresent())
                return ResponseEntity.ok(new DataResponse<>(true, 200, "OK", user.get()));
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(false, 404, "Usuario no encontrado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, ex.getMessage()));
        }
    }
    
    
    //despues probar, con el FileStorageService de package com.gamer.api.service;
    //Metodo: PATCH... para actualizar la foto del usuario URL: http://localhost:8080/api/login/update-user
        @Autowired
        @Qualifier("userFileStorageService")
        private UserFileStorageService userFileStorageService;

        @PatchMapping(value = "/update-user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<BaseResponse> updateUser(
                @RequestParam(value = "usuarioId", required = false) String usuarioIdStr,
                @RequestParam(value = "correo", required = false) String correo,
                @RequestParam(value = "contrasena", required = false) String contrasena,
                @RequestParam(value = "nombre", required = false) String nombre,
                @RequestParam(value = "perfilURL", required = false) MultipartFile imagen,
                @RequestParam(value = "urlVieja", required = false) String urlVieja,
                HttpServletRequest request) {

            if (usuarioIdStr == null || usuarioIdStr.equals("undefined") || usuarioIdStr.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, 400, "usuarioId es requerido"));
            }

            Integer usuarioId;
            try {
                usuarioId = Integer.parseInt(usuarioIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, 400, "usuarioId inválido"));
            }

            if (usuarioId <= 0) {
                return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, 400, "usuarioId debe ser mayor a 0"));
            }

            correo = cleanStringParam(correo);
            contrasena = cleanStringParam(contrasena);
            nombre = cleanStringParam(nombre);
            urlVieja = cleanStringParam(urlVieja);

            try {
                String imagenUrl = null;

                if (imagen != null && !imagen.isEmpty()) {
                    if (urlVieja != null && !urlVieja.isBlank()) {
                        try {
                            userFileStorageService.deleteImage(urlVieja);
                        } catch (Exception e) {
                            System.err.println("Error eliminando imagen vieja: " + e.getMessage());
                        }
                    }
                    imagenUrl = userFileStorageService.saveImage(imagen, request);
                }

                int result = usuarioService.updateUser(usuarioId, correo, contrasena, nombre, imagenUrl);

                if (result == 0) {
                    return ResponseEntity.ok(new BaseResponse(true, 200, "Usuario actualizado correctamente"));
                } else {
                    return ResponseEntity.status(404)
                            .body(new BaseResponse(false, 404, "Usuario no encontrado"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500)
                        .body(new BaseResponse(false, 500, "Error: " + e.getMessage()));
            }
        }

        private String cleanStringParam(String param) {
            if (param == null || param.equals("undefined") || param.equals("null") || param.isBlank()) {
                return null;
            }
            return param.trim();
        }
        
        
        // GET /api/login/getIconXUser
        @GetMapping("/getIconXUser")
        public ResponseEntity<BaseResponse> getIconXUser(
                @RequestParam("usuario_id") int usuarioId,
                HttpServletRequest request) {

            try {
                Optional<String> icon = usuarioService.getIconUser(usuarioId);

                String iconUrl = null;

                if (icon.isPresent() && icon.get() != null && !icon.get().isEmpty()) {
                    String iconPath = icon.get();
                    
                    // Extract just the filename if full URL is stored
                    if (iconPath.startsWith("http")) {
                        iconPath = iconPath.substring(iconPath.lastIndexOf("/") + 1);
                    }
                    
                    iconUrl = request.getScheme() + "://" + request.getServerName()
                            + ":" + request.getServerPort() + "/users/" + iconPath;
                }

                return ResponseEntity.ok(
                    new DataResponse<>(true, 200, "OK", iconUrl)
                );

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en getIconXUser: " + e.getMessage()));
            }
        }


        // GET /api/login/get-all-users
        @GetMapping("/get-all-users")
        public ResponseEntity<BaseResponse> getAllUsers(HttpServletRequest request) {
            try {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                var users = usuarioService.getAllUsers(baseUrl);
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Usuarios obtenidos con éxito", users));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en get-all-users: " + e.getMessage()));
            }
        }

        // GET /api/login/getMisReviews
        @GetMapping("/getMisReviews")
        public ResponseEntity<BaseResponse> getMisReviews(@RequestParam("usuario_id") int usuarioId) {
            try {
                var reviews = usuarioService.getReviewsByUser(usuarioId);
                return ResponseEntity.ok(new DataResponse<>(true, 200, "Reviews obtenidos con éxito", reviews));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BaseResponse(false, 500, "Error en getMisReviews: " + e.getMessage()));
            }
        }

        @PutMapping("/change-user-state")
        public ResponseEntity<BaseResponse> changeUserState(
            @RequestParam int id,
            @RequestParam int baja) {
            BaseResponse result = usuarioService.changeUserState(id, baja);
            return ResponseEntity.status(result.getCode()).body(result);
        }


}
