package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.User;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
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
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IntegrationSystemRepository integrationSystemRepository;

    @Autowired
    public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, IntegrationSystemRepository integrationSystemRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.integrationSystemRepository = integrationSystemRepository;
    }

    @RequestMapping(value = "/int/user", method = {RequestMethod.POST})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public UserContract save(@RequestBody UserContract userRequest) {
        User user = new User();
        return save(userRequest, user);
    }

    private UserContract save(UserContract userRequest, User user) {
        user.setEmail(userRequest.getEmail());
        user.setWorkingIntegrationSystem(integrationSystemRepository.findEntity(userRequest.getWorkingIntegrationSystemId()));
        userRepository.save(user);
        return new UserContract(user);
    }

    @RequestMapping(value = "/int/user/{id}", method = {RequestMethod.PUT})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public UserContract update(@PathVariable("id") Integer id, @RequestBody UserContract userRequest) {
        User user = userRepository.findEntity(id);
        return save(userRequest, user);
    }

    @RequestMapping(value = "/int/currentUser", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public UserContract loggedInUser(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        return new UserContract(user);
    }

    @RequestMapping(value = "/int/user", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public List<UserContract> getUsers() {
        return userRepository.findAllBy().stream().map(UserContract::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/int/user/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public UserContract getUser(@PathVariable("id") Integer id) {
        return new UserContract(userRepository.findById(id).get());
    }
}
