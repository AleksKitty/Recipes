package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import recipes.businesslayer.*;
import recipes.businesslayer.services.UserService;

import javax.validation.Valid;


@RestController
public class RegistrationController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    // add new user to db
    @PostMapping("/api/register")
    public void register(@Valid @RequestBody User user) {

        // email already taken
        if (userService.findUserByEmail(user.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setRole("ROLE_USER");
        user.setPassword(encoder.encode(user.getPassword()));

        userService.save(user);
    }
}
