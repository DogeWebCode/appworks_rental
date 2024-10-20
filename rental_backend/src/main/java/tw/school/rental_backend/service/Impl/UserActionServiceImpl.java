package tw.school.rental_backend.service.Impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.model.user.UserAction;
import tw.school.rental_backend.repository.dynamo.UserActionRepository;
import tw.school.rental_backend.service.UserActionService;

import java.time.LocalDateTime;

@Service
@Log4j2
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;

    public UserActionServiceImpl(UserActionRepository userActionRepository) {
        this.userActionRepository = userActionRepository;
    }

    @Override
    public void recordUserAction(User user, Property property, String actionType) {
        UserAction userAction = new UserAction();
        userAction.setUserId(user.getId().toString());
        userAction.setPropertyId(property.getId().toString());
        userAction.setActionType(actionType);
        userAction.setActionDateTime(LocalDateTime.now());

        userActionRepository.save(userAction);

        log.info("User action recorded in DynamoDB: {}", userAction);
    }

    @Override
    public void removeFavoriteAction(Long userId, Long propertyId) {
        log.warn("Remove action is not supported in DynamoDB by default.");
    }
}
