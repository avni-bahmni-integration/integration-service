package org.avni_integration_service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public TestController(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/test/passwordHash")
    public String getPasswordHash(@RequestParam("password") String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
