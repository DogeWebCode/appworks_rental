package tw.school.rental_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.FavoriteDTO;
import tw.school.rental_backend.data.dto.PropertyResponseDTO;
import tw.school.rental_backend.middleware.JwtTokenProvider;
import tw.school.rental_backend.service.FavoriteService;

import java.util.List;


@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtTokenProvider jwtTokenProvider;

    public FavoriteController(FavoriteService favoriteService, JwtTokenProvider jwtTokenProvider) {
        this.favoriteService = favoriteService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<?> addFavorite(HttpServletRequest request, @PathVariable Long propertyId) {
        String token = jwtTokenProvider.resolveToken(request);  // 從請求中提取 JWT Token
        Long userId = jwtTokenProvider.getUserId(token);  // 從 Token 中提取 userId
        favoriteService.addFavorite(userId, propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getFavorites(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);
        List<FavoriteDTO> favoriteList = favoriteService.getFavoritesByUserId(userId);
        PropertyResponseDTO<List<FavoriteDTO>> response = new PropertyResponseDTO<>(favoriteList);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeFavorite(HttpServletRequest request, @PathVariable Long propertyId) {
        String token = jwtTokenProvider.resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);
        favoriteService.removeFavorite(userId, propertyId);
        return ResponseEntity.ok().build();
    }
}
