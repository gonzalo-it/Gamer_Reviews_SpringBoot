package com.gamer.api.controller;

import com.gamer.api.dto.*;
import com.gamer.api.model.Usuario;
import com.gamer.api.service.UsuarioService;
import com.gamer.api.dto.BaseResponse;
import com.gamer.api.dto.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/login")
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
}
