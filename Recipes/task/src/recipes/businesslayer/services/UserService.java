package recipes.businesslayer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipes.businesslayer.User;
import recipes.persistence.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String email) {
       return userRepository.findUserByEmail(email);
    }

    public void save(User toSave) {
        userRepository.save(toSave);
    }
}
