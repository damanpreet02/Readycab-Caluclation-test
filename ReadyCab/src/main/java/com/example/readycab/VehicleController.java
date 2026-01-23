package com.example.readycab;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api")

public class VehicleController {


    private static final String VEHICLE_URL =
        "https://dev.readycab.api.henceforthsolutions.com/vehicle";

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/vehicles/active")
    public ResponseEntity<String> getVehicles(
            @RequestHeader("Authorization") String authHeader) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
            restTemplate.exchange(
                VEHICLE_URL,
                HttpMethod.GET,
                entity,
                String.class
            );
        
        System.out.println(response);

        return ResponseEntity.ok(response.getBody());
    }
}
