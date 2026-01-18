package com.example.readycab;

public class JsonUtil {

    public static float getFloat(String json, String key) {

        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;

        start = start + search.length();

        // skip opening quote if present
        if (json.charAt(start) == '"') {
            start++;
        }

        int end = json.indexOf(",", start);
        if (end == -1) {
            end = json.indexOf("}", start);
        }

        String value = json.substring(start, end)
                .replace("\"", "")
                .trim();

        return Float.parseFloat(value);
    }

    public static String extractTaxBlock(String gstJson) {

        int start = gstJson.indexOf("\"tax\"");
        start = gstJson.indexOf("{", start);
        int end = gstJson.indexOf("}", start);

        return gstJson.substring(start, end + 1);
    }
}
