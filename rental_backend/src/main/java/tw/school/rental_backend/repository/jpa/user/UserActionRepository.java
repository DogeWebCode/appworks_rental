package tw.school.rental_backend.repository.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.model.user.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findByUserId(Long userId);

    void deleteByUserIdAndPropertyIdAndActionType(Long userId, Long propertyId, String actionType);

}
