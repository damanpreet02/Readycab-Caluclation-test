package com.example.readycab;
public class VehicleConfig {

    private float commission_percentage;
    private float distance_price;
    private float base_fare;
    private float surcharge_price;
    private float city_fees;
    private float infrastructure_fees;
    private float insurance_fees;
    private float ac_extra_per_km;
    private String city_fees_unit;

    
    // ---- Getters & Setters (important for Jackson) ----

    public float getBase_fare() { return base_fare; }
    public float getDistance_price() { return distance_price; }
    public float getAc_extra_per_km() { return ac_extra_per_km; }
    public float getSurcharge_price() { return surcharge_price; }
    public float getCity_fees() { return city_fees; }
    public String getCity_fees_unit() { return city_fees_unit; }
    public float getInfrastructure_fees() { return infrastructure_fees; }
    public float getInsurance_fees() { return insurance_fees; }
    public float getCommission_percentage() { return commission_percentage; }
}
