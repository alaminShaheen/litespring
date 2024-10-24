package personal.service;

import personal.litespring.annotation.Autowired;
import personal.litespring.annotation.Component;
import personal.models.User;
import personal.repository.UserRepository;

import java.util.List;

@Component
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public boolean register(User user) {
        user = user.toBuilder()
                .username(user.getName().toLowerCase())
                .build();

        return userRepository.register(user);
//        if(success) return user;
//        return User.builder().build();
    }

    public User signIn(String username, String password) {
        if (userRepository.passwordMatch(username, password)) return userRepository.getUser(username);
        return null;
    }

    public User getUser(String username) {
        return userRepository.getUser(username);
    }

    public List<User> getAllUsers() {
        return userRepository.getUsers();
    }

    public boolean deleteUser(String username) {
        return userRepository.deleteUser(username);
    }
}
