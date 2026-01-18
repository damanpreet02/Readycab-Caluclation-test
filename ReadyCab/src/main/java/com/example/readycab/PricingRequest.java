package com.example.readycab;

public class PricingRequest {

    private float distance;
    private boolean applySurcharge;
    private String vehicle_id;
	private boolean applyAc;

    public float getDistance() { return distance; }
    public boolean isApplySurcharge() { return applySurcharge; }
    public boolean isApplyAc() { return applyAc; }
    public String getVehicle_id() { return vehicle_id; }

    public void setDistance(float distance) { this.distance = distance; }
    public void setApplySurcharge(boolean applySurcharge) { this.applySurcharge = applySurcharge; }
    public void setapplyAc(boolean applyAc) { this.applyAc = applyAc; }
    public void setVehicle_id(String vehicle_id) { this.vehicle_id = vehicle_id; }
}
