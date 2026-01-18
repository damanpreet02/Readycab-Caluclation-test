package com.example.readycab;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/api/pricing")
    @CrossOrigin("http://localhost:3000")
    public FareResult getPricing(@RequestBody PricingRequest req) {
        return pricingService.getPricing(
            req.getDistance(),
            req.isApplySurcharge(),
            req.isApplyAc(),
            req.getVehicle_id()
        );
    }

}
