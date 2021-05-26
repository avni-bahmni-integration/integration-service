package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.entity.User;
import org.bahmni_avni_integration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "user", method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public User save(@RequestBody User userRequest) {
        User user = userRepository.findByEmail(userRequest.getEmail());
        if (user == null) {
            user = new User();
            user.setEmail(userRequest.getEmail());
        }
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }
}
