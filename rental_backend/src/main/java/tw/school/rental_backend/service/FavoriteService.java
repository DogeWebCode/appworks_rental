package tw.school.rental_backend.service;

import tw.school.rental_backend.data.dto.FavoriteDTO;

import java.util.List;

public interface FavoriteService {
    void addFavorite(Long userId, Long propertyId);

    List<FavoriteDTO> getFavoritesByUserId(Long userId);

    void removeFavorite(Long userId, Long propertyId);

}
