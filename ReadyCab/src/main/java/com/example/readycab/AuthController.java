package com.example.readycab;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REAL_READY_CAB_LOGIN =
        "https://dev.readycab.api.henceforthsolutions.com/admin"; // <-- real API

    private final RestTemplate rest = new RestTemplate();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{"
                + "\"email\":\"" + req.getEmail() + "\","
                + "\"password\":\"" + req.getPassword() + "\""
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                rest.exchange(
                        REAL_READY_CAB_LOGIN,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        // Forward the REAL token to React
        return ResponseEntity.ok(response.getBody());
    }
}

