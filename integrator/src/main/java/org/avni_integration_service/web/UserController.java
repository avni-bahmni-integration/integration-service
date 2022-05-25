package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.User;
import org.avni_integration_service.integration_data.repository.UserRepository;
import org.avni_integration_service.web.contract.UserContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping(value = "user", method = {RequestMethod.POST})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public User save(@RequestBody User userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    @RequestMapping(value = "user/{id}", method = {RequestMethod.PUT})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public User update(@PathVariable("id") Integer id, @RequestBody User userRequest) {
        User user = userRepository.findById(id).get();
        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    @RequestMapping(value = "currentUser", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public User loggedInUser(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email);
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public List<UserContract> getUsers() {
        return userRepository.findAllBy().stream().map(UserContract::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public UserContract getUser(@PathVariable("id") Integer id) {
        return new UserContract(userRepository.findById(id).get());
    }
}
