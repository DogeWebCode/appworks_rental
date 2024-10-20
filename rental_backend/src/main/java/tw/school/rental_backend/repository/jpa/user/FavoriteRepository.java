package tw.school.rental_backend.repository.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.user.Favorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);
}
