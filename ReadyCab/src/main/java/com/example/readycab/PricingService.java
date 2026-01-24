package com.example.readycab;

import org.springframework.http.MediaType;
import java.util.List;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;


import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class PricingService {

    private static final String VEHICLE_BASE_URL =
        "https://dev.readycab.api.henceforthsolutions.com/vehicle";

    private static final String GST_URL =
        "https://dev.readycab.api.henceforthsolutions.com/configuration";

//    private static final String POST_URL =
//        "https://dev.readycab.api.henceforthsolutions.com/booking/admin/calculate/pricing";

    private final RestTemplate rest = new RestTemplate();

    /** 
     * Create headers using the token coming from React
     */
    private HttpHeaders authHeaders(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    
    
//    System.out.println("FRONTEND VEHICLE ID = " + vehicleId);

    // ===== UPDATED SIGNATURE: now receives token =====
    @SuppressWarnings("deprecation")
	public FareResult getPricing(
    		String vehicleId,
            float distance,
            boolean applySurcharge,
            boolean applyAc,
            String authHeader   // <--- JWT from React
    ) {

        // ---------- API 1 : VEHICLE ----------
    	String vehicleUrl = VEHICLE_BASE_URL + "?id=" + vehicleId;
//    	System.out.println("===============================");
//    	System.out.println(vehicleUrl);

    	ResponseEntity<String> vehicleRes =
    	        rest.exchange(
    	                vehicleUrl,
    	                HttpMethod.GET,
    	                new HttpEntity<>(authHeaders(authHeader)),
    	                String.class
    	        );
    	
    	System.out.println("========"+vehicleId);
    	
    	
    	
    	String vehicleJson = vehicleRes.getBody();

    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode root = mapper.readTree(vehicleJson);

    	// array is inside "data"
    	JsonNode dataArray = root.get("data");

    	JsonNode matched = null;

    	for (JsonNode node : dataArray) {

    	    String id = node.get("_id").asText();   // pricing _id

    	    if (vehicleId.equals(id)) {
    	        matched = node;
    	        break;
    	    }
    	}

    	if (matched == null) {
    	    throw new RuntimeException("Vehicle not found for id: " + vehicleId);
    	}
    	
    	
    	
    	float base_fare = (float) matched.path("base_fare").asDouble(0);
    	float base_km = (float) matched.path("base_km").asDouble(0);
    	float perKm = (float) matched.path("distance_price").asDouble(0);
    	float acExtra = (float) matched.path("ac_extra_per_km").asDouble(0);
    	float surchargeMulti = (float) matched.path("surcharge_price").asDouble(0);
    	float infraFee = (float) matched.path("infrastructure_fees").asDouble(0);
    	float insuranceFee = (float) matched.path("insurance_fees").asDouble(0);
    	float cityFee = (float) matched.path("city_fees").asDouble(0);
    	float airportPickup = (float) matched.path("airport_pickup_charges").asDouble(0);

    	
    	
    	
//    	float base_fare = JsonUtil.getFloat(vehicleJson, "base_fare");
//    	float base_km = JsonUtil.getFloat(vehicleJson, "base_km");
//    	float perKm = JsonUtil.getFloat(vehicleJson, "distance_price");
//    	float acExtra = JsonUtil.getFloat(vehicleJson, "ac_extra_per_km");
//    	float surchargeMulti = JsonUtil.getFloat(vehicleJson, "surcharge_price");
//    	float infraFee = JsonUtil.getFloat(vehicleJson, "infrastructure_fees");
//    	float insuranceFee = JsonUtil.getFloat(vehicleJson, "insurance_fees");
//    	float cityFee = JsonUtil.getFloat(vehicleJson, "city_fees");
//    	float airportPickup = JsonUtil.getFloat(vehicleJson, "airport_pickup_charges");
    	
    	
    	
    	
    	
    	
    	
//    	ObjectMapper mapper = new ObjectMapper();
//
//    	JsonNode root = mapper.readTree(vehicleJson);
//
//    	JsonNode dataArray = root.get("data");
//
//    	JsonNode matched = null;
//    	
//    	for (JsonNode node : dataArray) {
//    		System.out.println("========"+node);
//
//    	    String id = node
//    	            .get("_id")
////    	            .get("vehicle_id")
//    	            .asText();
//
//    	    if (vehicleId.equals(id)) {
//    	        matched = node;
//    	        break;
//    	    }
//    	}
//
//    	if (matched == null) {
//    	    throw new RuntimeException("Vehicle not found for id: " + vehicleId);
//    	}



//        System.out.println("Vehicle Response: " + vehicleJson);

        // ---------- API 2 : GST ----------
        ResponseEntity<String> gstRes =
                rest.exchange(
                        GST_URL,
                        HttpMethod.GET,
                        new HttpEntity<>(authHeaders(authHeader)),
                        String.class
                );

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
                applySurcharge,
                applyAc,
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
                + "\"vehicle_id\":\"" + vehicleId + "\","
                + "\"distance_in_km\":" + distance + ","

                + "\"apply_surcharge\":" + applySurcharge + ","
                + "\"apply_ac\":" + applyAc + ","

                + "\"base\":" + Math.round(result.base_fare) + ","
                + "\"base_fee\":" + Math.round(result.base_fee) + ","

                + "\"charges_per_km\":" + Math.round(result.charges_per_km) + ","

                + "\"airport_pickup_charges\":" + Math.round(result.airport_pickup_charges) + ","

                + "\"city_fee\":" + Math.round(result.city_fee) + ","

                + "\"comission_fee\":" + Math.round(result.platform_fee) + ","

                + "\"driver_payment\":" + Math.round(result.basic_driver_earning) + ","

                + "\"gst_for_app\":" + Math.round(result.gst_for_app) + ","
                + "\"gst_for_driver\":" + Math.round(result.gst_for_driver) + ","

                + "\"infrastructure_fee\":" + Math.round(result.infrastructure_fee) + ","
                + "\"insurance_fee\":" + Math.round(result.insurance_fee) + ","
                + "\"insurance_fee_app_payment\":" + Math.round(result.insurance_fee_app_payment) + ","

                + "\"surcharge\":" + Math.round(result.surcharge) + ","

                + "\"basic_trip_amount\":" + Math.round(result.basic_trip_amount) + ","

                + "\"total_trip_price_to_be_paid_by_customer\":"
                + Math.round(result.total_trip_price_to_be_paid_by_customer) + ","

                + "\"total_trip_price_to_be_paid_by_customer_app_payment\":"
                + Math.round(result.total_trip_price_to_be_paid_by_customer_app_payment) + ","

                + "\"total_trip_price_to_be_paid_by_customer_driver_payment\":"
                + Math.round(result.total_trip_price_to_be_paid_by_customer_driver_payment)

                + "}";



//        System.out.println("\n===== POST BODY TO API 3 =====");
//        System.out.println("+++++++"+postBody);
//        System.out.println("---------"+vehicleJson);

//        HttpEntity<String> postEntity =
//                new HttpEntity<>(postBody, authHeaders(authHeader));
//
//        ResponseEntity<String> postResponse =
//                rest.exchange(POST_URL, HttpMethod.POST, postEntity, String.class);
//
//        System.out.println("API3 RESPONSE:");
//        System.out.println(postResponse.getBody());


        return result;
    }
}
