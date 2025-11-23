package com.gamer.api.controller;

import com.gamer.api.dto.AddTagRequest;
import com.gamer.api.dto.DataResponse;
import com.gamer.api.dto.GetTagResponse;
import com.gamer.api.dto.TagXGameRequest;
import com.gamer.api.dto.BaseResponse;
import com.gamer.api.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // POST /api/tag/create-tag
    @PostMapping("/create-tag")
    public ResponseEntity<BaseResponse> createTag(@RequestBody AddTagRequest req) {
        try {
            if (req.getNombre() == null || req.getNombre().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse(false, 400, "Nombre requerido"));
            }

            int code = tagService.addTag(req.getNombre());
            if (code == 0) return ResponseEntity.ok(new BaseResponse(true, 200, "Tag creado correctamente"));
            if (code == 1) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "Tag ya existente"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, "Resultado desconocido"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en create-tag: " + e.getMessage()));
        }
    }

    // GET /api/tag/get-tags
    @GetMapping("/get-tags")
    public ResponseEntity<DataResponse<List<GetTagResponse>>> getTags() {
        try {
            List<GetTagResponse> tags = tagService.getAllTags();
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Lista de tags", tags));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DataResponse<>(false, 500, "Error en get-tags", null));
        }
    }

    // DELETE /api/tag/delete-tag?tag_id=1
    @DeleteMapping("/delete-tag")
    public ResponseEntity<BaseResponse> deleteTag(@RequestParam int tag_id) {
        try {
            tagService.deleteTag(tag_id);
            return ResponseEntity.ok(new BaseResponse(true, 200, "Tag eliminada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en delete-tag: " + e.getMessage()));
        }
    }

    // POST /api/tag/insert-tagXgame
    @PostMapping("/insert-tagXgame")
    public ResponseEntity<BaseResponse> insertTagXGame(@RequestBody TagXGameRequest req) {
        try {
            int res = tagService.insertTagXGame(req.getJuego_id(), req.getTag_id());
            return switch (res) {
                case 0 -> ResponseEntity.ok(new BaseResponse(true, 200, "Tag insertado correctamente"));
                case 1 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, 400, "Tag ya existente"));
                case 2 -> ResponseEntity.ok(new BaseResponse(true, 200, "Tag reactivado"));
                default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, 500, "Resultado desconocido"));
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en insert-tagXgame: " + e.getMessage()));
        }
    }

    // GET /api/tag/get-tagsXgame?juego_id=1
    @GetMapping("/get-tagsXgame")
    public ResponseEntity<DataResponse<List<GetTagResponse>>> getTagsXGame(@RequestParam int juego_id) {
        try {
            List<GetTagResponse> tags = tagService.getTagsXGame(juego_id);
            return ResponseEntity.ok(new DataResponse<>(true, 200, "Lista de tags", tags));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DataResponse<>(false, 500, "Error en get-tagsXgame", null));
        }
    }

    // DELETE /api/tag/delete-tagXgame?juego_id=1&tag_id=2
    @DeleteMapping("/delete-tagXgame")
    public ResponseEntity<BaseResponse> deleteTagXGame(@RequestParam int juego_id, @RequestParam int tag_id) {
        try {
            tagService.deleteTagXGame(juego_id, tag_id);
            return ResponseEntity.ok(new BaseResponse(true, 200, "Tag eliminada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, 500, "Error en delete-tagXgame: " + e.getMessage()));
        }
    }
}
