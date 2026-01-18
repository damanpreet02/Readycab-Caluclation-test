package com.example.readycab;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PricingService {

    private static final String VEHICLE_BASE_URL =
        "https://dev.readycab.api.henceforthsolutions.com/vehicle/";

    private static final String GST_URL =
        "https://dev.readycab.api.henceforthsolutions.com/configuration";

    private static final String POST_URL =
        "https://dev.readycab.api.henceforthsolutions.com/booking/admin/calculate/pricing";

    private static final String TOKEN =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
      + "eyJ1c2VyX2lkIjoiNjZlYmJlZGQ5OTQ4YmRmMzBhM2UxZmRj"
      + "Iiwic2NvcGUiOiJhZG1pbiIsImlhdCI6MTc2ODU4Nzc2Mn0."
      + "C6QigyIlELW6YMMAfZ9uminzj2cjp8BEeee4pGpua0A";

    private final RestTemplate rest = new RestTemplate();

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", TOKEN);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    // ===== THIS NOW MATCHES YOUR CONTROLLER =====
    public FareResult getPricing(float distance,
                                   boolean applySurcharge,
                                   boolean applyAc,
                                   String vehicleId) {

        // ---------- API 1 : VEHICLE ----------
        String vehicleUrl = VEHICLE_BASE_URL + vehicleId;

        ResponseEntity<String> vehicleRes =
                rest.exchange(vehicleUrl, HttpMethod.GET,
                        new HttpEntity<>(authHeaders()), String.class);

        String vehicleJson = vehicleRes.getBody();

        float base_fare = JsonUtil.getFloat(vehicleJson, "base_fare");
        float base_km = JsonUtil.getFloat(vehicleJson, "base_km");
        float perKm = JsonUtil.getFloat(vehicleJson, "distance_price");
        float acExtra = JsonUtil.getFloat(vehicleJson, "ac_extra_per_km");
        float surchargeMulti = JsonUtil.getFloat(vehicleJson, "surcharge_price");
        float infraFee = JsonUtil.getFloat(vehicleJson, "infrastructure_fees");
        float insuranceFee = JsonUtil.getFloat(vehicleJson, "insurance_fees");
        float cityFee = JsonUtil.getFloat(vehicleJson, "city_fees");
        float airportPickup = JsonUtil.getFloat(vehicleJson, "airport_pickup_charges");
        
        System.out.println(vehicleJson);

        // ---------- API 2 : GST ----------
        ResponseEntity<String> gstRes =
                rest.exchange(GST_URL, HttpMethod.GET,
                        new HttpEntity<>(authHeaders()), String.class);

        String gstJson = gstRes.getBody();
        String taxBlock = JsonUtil.extractTaxBlock(gstJson);

        float adminGST =
                JsonUtil.getFloat(taxBlock, "commission_tax_percentage");
        float driverGST =
                JsonUtil.getFloat(taxBlock, "payout_tax_percentage");

        // ---------- RUN CALCULATOR ----------
        Calculator calc = new Calculator(
        		base_fare,
        		base_km,
                perKm,
                acExtra,
                surchargeMulti,
                applySurcharge,   // ✅ IMPORTANT
                applyAc,   // ✅ IMPORTANT
                infraFee,
                insuranceFee,
                cityFee,
                adminGST,
                driverGST
        );


        FareResult result = calc.calculate(distance);

        // add fields calculator doesn’t set
        result.airport_pickup_charges = airportPickup;

        // if UI unchecked surcharge, force it to zero
        if (!applySurcharge) {
            result.surcharge = 0;
        }
        if (!applyAc) {
        	result.ac_price = 0;
        }

        // ---------- POST TO API 3 ----------
        String postBody = "{"
                + "\"vehicle_id\":\"689b2ef954f6141162c53b74\","
                + "\"distance_in_km\":" + distance + ","
                + "\"apply_surcharge\":false,"
                
                + "\"base\":" + result.base_fare + ","
                + "\"base_fee\":" + result.base_fee + ","
                + "\"charges_per_km\":" + result.charges_per_km + ","
                + "\"surcharge\":" + result.surcharge + ","
                + "\"platform_fee\":" + result.platform_fee + ","
                + "\"infrastructure_fee\":" + result.infrastructure_fee + ","
                + "\"insurance_fee\":" + result.insurance_fee + ","
                + "\"city_fee\":" + result.city_fee + ","
                + "\"gst_for_app\":" + result.gst_for_app + ","
                + "\"gst_for_driver\":" + result.gst_for_driver + ","
                + "\"basic_app_earning\":" + result.basic_app_earning + ","
                + "\"basic_driver_earning\":" + result.basic_driver_earning + ","
                + "\"basic_trip_amount\":" + result.basic_trip_amount + ","
                + "\"app_payment_infrafee\":" + result.app_payment_infrafee + ","
                + "\"insurance_fee_app_payment\":" + result.insurance_fee_app_payment + ","
                + "\"total_trip_price_to_be_paid_by_customer\":"
                + result.total_trip_price_to_be_paid_by_customer + ","
                + "\"total_trip_price_to_be_paid_by_customer_app_payment\":"
                + result.total_trip_price_to_be_paid_by_customer_app_payment + ","
                + "\"total_trip_price_to_be_paid_by_customer_driver_payment\":"
                + result.total_trip_price_to_be_paid_by_customer_driver_payment
                + "}";


        System.out.println("\n===== POST BODY TO API 3 =====");
        System.out.println(postBody);

        HttpEntity<String> postEntity =
                new HttpEntity<>(postBody, authHeaders());

        rest.exchange(POST_URL, HttpMethod.POST, postEntity, String.class);

        return result;
    }
}
