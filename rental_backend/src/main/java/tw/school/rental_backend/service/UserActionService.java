package tw.school.rental_backend.service;

import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.User;

public interface UserActionService {
    void recordUserAction(User user, Property property, String actionType);

//    void batchSaveActions();

    void removeFavoriteAction(Long userId, Long propertyId);
}
