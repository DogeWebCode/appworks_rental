package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.school.rental_backend.data.dto.FavoriteDTO;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.Favorite;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.repository.jpa.user.FavoriteRepository;
import tw.school.rental_backend.repository.jpa.user.UserRepository;
import tw.school.rental_backend.service.FavoriteService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository, PropertyRepository propertyRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    @Override
    public void addFavorite(Long userId, Long propertyId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }

    @Override
    public List<FavoriteDTO> getFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        String imageUrl = "https://d12sfdsmuxoz1g.cloudfront.net/images/";
        return favorites.stream()
                .map(favorite -> new FavoriteDTO(
                        favorite.getId(),
                        favorite.getProperty().getId(),
                        favorite.getProperty().getTitle(),
                        favorite.getProperty().getCity().getCityName(),
                        favorite.getProperty().getDistrict().getDistrictName(),
                        favorite.getProperty().getRoad().getRoadName(),
                        favorite.getProperty().getPrice(),
                        favorite.getProperty().getPropertyType(),
                        imageUrl + favorite.getProperty().getMainImage(),
                        favorite.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
}
