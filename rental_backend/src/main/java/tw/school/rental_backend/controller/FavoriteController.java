package tw.school.rental_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.FavoriteDTO;
import tw.school.rental_backend.data.dto.PropertyResponseDTO;
import tw.school.rental_backend.service.FavoriteService;
import tw.school.rental_backend.util.UserUtil;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserUtil userUtil;

    public FavoriteController(FavoriteService favoriteService, UserUtil userUtil) {
        this.favoriteService = favoriteService;
        this.userUtil = userUtil;
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long propertyId) {
        Long userId = userUtil.getCurrentUserId();
        favoriteService.addFavorite(userId, propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getFavorites() {
        Long userId = userUtil.getCurrentUserId();
        List<FavoriteDTO> favoriteList = favoriteService.getFavoritesByUserId(userId);
        PropertyResponseDTO<List<FavoriteDTO>> response = new PropertyResponseDTO<>(favoriteList);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long propertyId) {
        Long userId = userUtil.getCurrentUserId();
        favoriteService.removeFavorite(userId, propertyId);
        return ResponseEntity.ok().build();
    }
}
