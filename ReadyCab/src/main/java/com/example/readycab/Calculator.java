package com.example.readycab;

public class Calculator {

    float base_fare, perkm, ac_price, surcharge_muti;
    float infra, insurance_fee, city_fee, adminGST, driverGST;
    boolean applySurcharge, applyAc;
	float base_km;

    public Calculator(
            float base_fare,
            float base_km,
            float perkm,
            float ac_price,
            float surcharge_muti,
            boolean applySurcharge,
            boolean applyAc,
            float infra,
            float insurance_fee,
            float city_fee,
            float adminGST,
            float driverGST) {

        this.base_fare = base_fare;
        this.base_km = base_km;
        this.perkm = perkm;
        this.ac_price = ac_price;
        this.surcharge_muti = surcharge_muti;
        this.applySurcharge = applySurcharge;
        this.applyAc = applyAc;
        this.infra = infra;
        this.insurance_fee = insurance_fee;
        this.city_fee = city_fee;
        this.adminGST = adminGST;
        this.driverGST = driverGST;
    }

    public FareResult calculate(float distance) {

        // ✅ HARD RULE: if distance = 0 → everything must be ZERO
        if (distance <= 0) {
            FareResult zero = new FareResult();

            zero.base_fare = 0;
            zero.base_fee = 0;
            zero.base_km = 0;
            zero.charges_per_km = 0;
            zero.surcharge = 0;
            zero.platform_fee = 0;
            zero.infrastructure_fee = 0;
            zero.insurance_fee = 0;
            zero.city_fee = 0;
            zero.gst_for_app = 0;
            zero.gst_for_driver = 0;
            zero.basic_trip_amount = 0;
            zero.basic_app_earning = 0;
            zero.basic_driver_earning = 0;
            zero.driver_payment = 0;
            zero.app_payment_infrafee = 0;
            zero.airport_pickup_charges = 0;
            zero.insurance_fee_app_payment = 0;
            zero.total_trip_price_to_be_paid_by_customer = 0;
            zero.total_trip_price_to_be_paid_by_customer_app_payment = 0;
            zero.total_trip_price_to_be_paid_by_customer_driver_payment = 0;

            System.out.println("\n===== ZERO DISTANCE → ZERO BILL =====");
            return zero;
        }

        // ---- your existing logic starts BELOW this line ----


    	float base_fee;
    	float charges_per_km = (distance - base_km) * perkm;
    	float ac_charges = distance * ac_price; 
    	
    	

    	if (applyAc) {
    	    base_fee = base_fare + charges_per_km + ac_charges;
    	} else {
    	    base_fee = base_fare + charges_per_km;
    	}
    	

        float surcharge = 0;
        if (applySurcharge) {
            surcharge = (base_fee * surcharge_muti) - base_fee;
        }

        float total = base_fee + surcharge;

        float platform = total * 0.01f;   // 1%

        float insurance = insurance_fee * distance;

        float basic_appCommission = (total * 0.10f) + platform + infra;

        float GST_admin =
                Math.round((basic_appCommission * adminGST / 100f) * 100f) / 100f;

        float basic_driverEarning = total - (total * 0.10f);

        float GST_driver =
                Math.round((basic_driverEarning * driverGST / 100f) * 100f) / 100f;

        float basic_trip_amount = base_fee + surcharge + platform + infra;

        float total_app_earning =
                basic_appCommission + insurance + city_fee + GST_admin;

        float total_driver_earning =
                basic_driverEarning + GST_driver;

        float final_total =
                basic_trip_amount + insurance + city_fee + GST_admin + GST_driver;
        



        FareResult result = new FareResult();

        result.base_fare = base_fare;
        result.base_fee = base_fee;
        result.charges_per_km = charges_per_km;
        result.ac_price = ac_charges;
        result.surcharge = surcharge;
        result.platform_fee = platform;
        result.infrastructure_fee = infra;
        result.insurance_fee = insurance;
        result.city_fee = city_fee;
        result.gst_for_app = GST_admin;
        result.gst_for_driver = GST_driver;
        result.basic_trip_amount = basic_trip_amount;
        result.basic_app_earning = total_app_earning - GST_admin ;
        result.basic_driver_earning = basic_driverEarning;

        result.driver_payment = total_driver_earning;
        result.app_payment_infrafee = platform + infra;
        result.insurance_fee_app_payment = insurance;

        result.total_trip_price_to_be_paid_by_customer = final_total;
        result.total_trip_price_to_be_paid_by_customer_app_payment = total_app_earning;
        result.total_trip_price_to_be_paid_by_customer_driver_payment = total_driver_earning;

        System.out.println("\n===== FINAL BILL =====");
        System.out.println("Base Fee: " + base_fee);
        System.out.println("Charges per Km: " + charges_per_km);
        System.out.println("Ac Charges: " + ac_charges);
        System.out.println("Surcharge: " + surcharge);
        System.out.println("Platform Fee: " + platform);
        System.out.println("Infrastructure Fee: " + infra);
        System.out.println("Insurance: " + insurance);
        System.out.println("City Fee: " + city_fee);
        System.out.println("GST Admin: " + GST_admin);
        System.out.println("GST Driver: " + GST_driver);
        System.out.println("--------------------------------");
        System.out.println("App Earning: " + total_app_earning);
        System.out.println("Driver Earning: " + total_driver_earning);
        System.out.println("Customer Pays: " + final_total);

        return result;
    }
}
