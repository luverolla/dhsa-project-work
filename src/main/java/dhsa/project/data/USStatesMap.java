package dhsa.project.data;

import java.util.HashMap;

public final class USStatesMap extends HashMap<String, String> {
    private final static USStatesMap instance = new USStatesMap();

    private USStatesMap() {
        super();
        super.put("Alabama", "AL");
        super.put("Alaska", "AK");
        super.put("Arizona", "AZ");
        super.put("Arkansas", "AR");
        super.put("California", "CA");
        super.put("Colorado", "CO");
        super.put("Connecticut", "CT");
        super.put("Delaware", "DE");
        super.put("District of Columbia", "DC");
        super.put("Florida", "FL");
        super.put("Georgia", "GA");
        super.put("Hawaii", "HI");
        super.put("Idaho", "ID");
        super.put("Illinois", "IL");
        super.put("Indiana", "IN");
        super.put("Iowa", "IA");
        super.put("Kansas", "KS");
        super.put("Kentucky", "KY");
        super.put("Louisiana", "LA");
        super.put("Maine", "ME");
        super.put("Maryland", "MD");
        super.put("Massachusetts", "MA");
        super.put("Michigan", "MI");
        super.put("Minnesota", "MN");
        super.put("Mississippi", "MS");
        super.put("Missouri", "MO");
        super.put("Montana", "MT");
        super.put("Nebraska", "NE");
        super.put("Nevada", "NV");
        super.put("New Hampshire", "NH");
        super.put("New Jersey", "NJ");
        super.put("New Mexico", "NM");
        super.put("New York", "NY");
        super.put("North Carolina", "NC");
        super.put("North Dakota", "ND");
        super.put("Ohio", "OH");
        super.put("Oklahoma", "OK");
        super.put("Oregon", "OR");
        super.put("Pennsylvania", "PA");
        super.put("Rhode Island", "RI");
        super.put("South Carolina", "SC");
        super.put("South Dakota", "SD");
        super.put("Tennessee", "TN");
        super.put("Texas", "TX");
        super.put("Utah", "UT");
        super.put("Vermont", "VT");
        super.put("Virginia", "VA");
        super.put("Washington", "WA");
        super.put("West Virginia", "WV");
        super.put("Wisconsin", "WI");
        super.put("Wyoming", "WY");
    }

    public static USStatesMap get() {
        return instance;
    }
}
