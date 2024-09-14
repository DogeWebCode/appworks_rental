package tw.school.rental_backend.service;

import tw.school.rental_backend.model.user.User;

public interface UserService {

    String login(String username, String password);

    User registerUser(User user);

    User findByUsername(String username);
}
