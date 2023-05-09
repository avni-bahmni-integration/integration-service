package org.avni_integration_service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public TestController(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/int/test/passwordHash")
    public String getPasswordHash(@RequestBody PasswordRequest passwordRequest) {
        return bCryptPasswordEncoder.encode(passwordRequest.getPassword());
    }

    static class PasswordRequest {
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
