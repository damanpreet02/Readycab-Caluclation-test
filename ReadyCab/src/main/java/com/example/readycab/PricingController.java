package com.example.readycab;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/api/pricing")
    public FareResult getPricing(
            @RequestBody PricingRequest req,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {

        if (authHeader == null || authHeader.isBlank()) {
            throw new RuntimeException("Missing Authorization token");
        }

        return pricingService.getPricing(
                req.getDistance(),
                req.isApplySurcharge(),
                req.isApplyAc(),
                req.getVehicle_id(),
                authHeader
        );
    }


}
