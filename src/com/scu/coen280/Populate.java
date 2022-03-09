package com.scu.coen280;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;


public final class Populate {
    private static final String ROOT_PATH = "data/";
    private static final String BUSINESS = "yelp_business.json";
    private static final String REVIEW = "yelp_review.json";
    private static final String CHECKIN = "yelp_checkin.json";
    private static final String USER = "yelp_user.json";

    private static final String[] MAIN_CATEGORIES = {
            "Active Life", "Arts & Entertainment", "Automotive", "Car Rental", "Cafes", "Beauty & Spas",
            "Convenience Stores", "Dentists", "Doctors", "Drugstores", "Department Stores", "Education",
            "Event Planning & Services", "Flowers & Gifts", "Food", "Health & Medical", "Home Services",
            "Home & Garden", "Hospitals", "Hotels & Travel", "Hardware Stores", "Grocery", "Medical Centers",
            "Nurseries & Gardening", "Nightlife", "Restaurants", "Shopping", "Transportation"
    };

    public static void populateBusiness(Connection connection) throws IOException, ParseException, SQLException {
        readyLog(BUSINESS);
        String path = ROOT_PATH + BUSINESS;

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        HashSet<String> mainCategories = new HashSet<>(Arrays.asList(MAIN_CATEGORIES));

        String main_sql = "INSERT INTO YELP_DATA.Business VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String business_category_sql = "INSERT INTO YELP_DATA.Business_Category_Relation VALUES (?, ?)";
        String business_subcategory_sql = "INSERT INTO YELP_DATA.Business_Subcategory_Relation VALUES (?, ?)";

        String params = String.join(", ", new String[38]).replace("null", "?");
        String business_attributes_statement = "INSERT INTO YELP_DATA.Business_Attributes_Relation VALUES (%s)";
        String business_attributes_sql = String.format(business_attributes_statement, params);

        PreparedStatement insertBusiness = connection.prepareStatement(main_sql);
        PreparedStatement insertBusinessCategory = connection.prepareStatement(business_category_sql);
        PreparedStatement insertBusinessSubcategory = connection.prepareStatement(business_subcategory_sql);
        PreparedStatement insertBusinessAttributes = connection.prepareStatement(business_attributes_sql);

        while ((line = bufferedReader.readLine()) != null) {
            // Insert into Business
            JSONObject obj = (JSONObject) new JSONParser().parse(line);
            String bid = (String) obj.get("business_id");

            String address = (String) obj.get("full_address");
            String city = (String) obj.get("city");
            long reviewCount = (long) obj.get("review_count");
            String bName = (String) obj.get("name");
            double latitude = (double) obj.get("latitude");
            double longitude = (double) obj.get("longitude");
            String state = (String) obj.get("state");
            double stars = (double) obj.get("stars");
            boolean isOpen = (boolean) obj.get("open");

            insertBusiness.setString(1, bid);
            insertBusiness.setString(2, address);
            insertBusiness.setString(3, city);
            insertBusiness.setLong(4, reviewCount);
            insertBusiness.setString(5, bName);
            insertBusiness.setDouble(6, latitude);
            insertBusiness.setDouble(7, longitude);
            insertBusiness.setString(8, state);
            insertBusiness.setDouble(9, stars);
            insertBusiness.setBoolean(10, isOpen);

            insertBusiness.executeUpdate();

            // Insert into Business_Category_Relation and Business_Subcategory_Relation
            JSONArray categories = (JSONArray) obj.get("categories");
            for (Object element: categories) {
                String name = (String) element;
                if (mainCategories.contains(name)) {
                    insertBusinessCategory.setString(1, bid);
                    insertBusinessCategory.setString(2, name);
                    insertBusinessCategory.executeUpdate();
                } else {
                    insertBusinessSubcategory.setString(1, bid);
                    insertBusinessSubcategory.setString(2, name);
                    insertBusinessSubcategory.executeUpdate();
                }
            }

            // Insert Business_Attribute_Relation
            JSONObject attributes = (JSONObject) obj.get("attributes");
            Object hasTV = attributes.get("Has TV");
            Object coatCheck = attributes.get("Coat Check");
            Object open24Hour = attributes.get("Open 24 Hours");
            Object acceptInsurance = attributes.get("Accepts Insurance");
            String alcohol = (String) attributes.get("Alcohol");
            Object dogsAllowed = attributes.get("Dogs Allowed");
            Object caters = attributes.get("Caters");
            Object priceRange = attributes.get("Price Range");
            Object happyHour = attributes.get("Happy Hour");
            Object goodForKids = attributes.get("Good for Kids");
            if (goodForKids == null) {
                goodForKids = attributes.get("Good For Kids");
            }
            Object goodForDancing = attributes.get("Good For Dancing");
            Object outdoorSeating = attributes.get("Outdoor Seating");
            Object takeReservations = attributes.get("Takes Reservations");
            Object waiterService = attributes.get("Waiter Service");
            String wifi = (String) attributes.get("Wi-Fi");
            JSONObject goodFor = (JSONObject) attributes.get("Good For");
            JSONObject parking = (JSONObject) attributes.get("Parking");
            JSONObject hairSpecialized = (JSONObject) attributes.get("Hair Types Specialized In");
            Object driveThru = attributes.get("Drive-Thru");
            Object orderAtCounter = attributes.get("Order at Counter");
            Object acceptCreditCard = attributes.get("Accepts Credit Cards");
            String BYOBOrCorkage = (String) attributes.get("BYOB/Corkage");
            Object goodForGroups = attributes.get("Good For Groups");
            String noiseLevel = (String) attributes.get("Noise Level");
            Object byAppointmentOnly = attributes.get("By Appointment Only");
            Object takeOut = attributes.get("Take-out");
            Object wheelchairAccessible = attributes.get("Wheelchair Accessible");
            Object BYOB = attributes.get("BYOB");
            JSONObject music = (JSONObject) attributes.get("Music");
            String attire = (String) attributes.get("Attire");
            JSONObject paymentTypes = (JSONObject) attributes.get("Payment Types");
            Object delivery = attributes.get("Delivery");
            JSONObject ambience = (JSONObject) attributes.get("Ambience");
            JSONObject dietaryRestriction = (JSONObject) attributes.get("Dietary Restrictions");
            Object corkage = attributes.get("Corkage");
            String ageAllowed = (String) attributes.get("Ages Allowed");
            String smoking = (String) attributes.get("Smoking");


            insertBusinessAttributes.setString(1, bid);
            prepareBoolean(insertBusinessAttributes, 2, hasTV);
            prepareBoolean(insertBusinessAttributes, 3, coatCheck);
            prepareBoolean(insertBusinessAttributes, 4, open24Hour);
            prepareBoolean(insertBusinessAttributes, 5, acceptInsurance);
            insertBusinessAttributes.setString(6, alcohol);
            prepareBoolean(insertBusinessAttributes, 7, dogsAllowed);
            prepareBoolean(insertBusinessAttributes, 8, caters);
            prepareLong(insertBusinessAttributes, 9, priceRange);
            prepareBoolean(insertBusinessAttributes, 10, happyHour);
            prepareBoolean(insertBusinessAttributes, 11, goodForKids);
            prepareBoolean(insertBusinessAttributes, 12, goodForDancing);
            prepareBoolean(insertBusinessAttributes, 13, outdoorSeating);
            prepareBoolean(insertBusinessAttributes, 14, takeReservations);
            prepareBoolean(insertBusinessAttributes, 15, waiterService);
            insertBusinessAttributes.setString(16, wifi);
            prepareJSONObject(insertBusinessAttributes, 17, goodFor);
            prepareJSONObject(insertBusinessAttributes, 18, parking);
            prepareJSONObject(insertBusinessAttributes, 19, hairSpecialized);
            prepareBoolean(insertBusinessAttributes, 20, driveThru);
            prepareBoolean(insertBusinessAttributes, 21, orderAtCounter);
            prepareBoolean(insertBusinessAttributes, 22, acceptCreditCard);
            insertBusinessAttributes.setString(23, BYOBOrCorkage);
            prepareBoolean(insertBusinessAttributes, 24, goodForGroups);
            insertBusinessAttributes.setString(25, noiseLevel);
            prepareBoolean(insertBusinessAttributes, 26, byAppointmentOnly);
            prepareBoolean(insertBusinessAttributes, 27, takeOut);
            prepareBoolean(insertBusinessAttributes, 28, wheelchairAccessible);
            prepareBoolean(insertBusinessAttributes, 29, BYOB);
            prepareJSONObject(insertBusinessAttributes, 30, music);
            insertBusinessAttributes.setString(31, attire);
            prepareJSONObject(insertBusinessAttributes, 32, paymentTypes);
            prepareBoolean(insertBusinessAttributes, 33, delivery);
            prepareJSONObject(insertBusinessAttributes, 34, ambience);
            prepareJSONObject(insertBusinessAttributes, 35, dietaryRestriction);
            prepareBoolean(insertBusinessAttributes, 36, corkage);
            insertBusinessAttributes.setString(37, ageAllowed);
            insertBusinessAttributes.setString(38, smoking);

            insertBusinessAttributes.executeUpdate();
        }

        insertBusiness.close();
        insertBusinessCategory.close();
        insertBusinessSubcategory.close();
        insertBusinessAttributes.close();
        bufferedReader.close();
        finishLog(BUSINESS);
    }

    public static void populateReview(Connection connection) throws IOException, ParseException, SQLException {
        readyLog(REVIEW);
        String path = ROOT_PATH + REVIEW;

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String sql = "INSERT INTO YELP_DATA.Reviews_Buffer VALUES (?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d'), ?, ?, ?, ?, ?)";
        PreparedStatement insertReviews = connection.prepareStatement(sql);
        String line;

        while ((line = bufferedReader.readLine()) != null){
            JSONObject obj = (JSONObject) new JSONParser().parse(line);
            String rid = (String) obj.get("review_id");
            String uid = (String) obj.get("user_id");
            String bid = (String) obj.get("business_id");
            String review_date = (String) obj.get("date");
            long stars = (long) obj.get("stars");
            String review_text = (String) obj.get("text");
            JSONObject votes = (JSONObject) obj.get("votes");
            long useful_votes = (long) votes.get("useful");
            long cool_votes = (long) votes.get("cool");
            long funny_votes = (long) votes.get("funny");

            insertReviews.setString(1, rid);
            insertReviews.setString(2, uid);
            insertReviews.setString(3, bid);
            insertReviews.setString(4, review_date);
            insertReviews.setLong(5, stars);
            insertReviews.setString(6, review_text);
            insertReviews.setLong(7, useful_votes);
            insertReviews.setLong(8, cool_votes);
            insertReviews.setLong(9, funny_votes);

            insertReviews.executeUpdate();
        }

        String fillName =
                "INSERT INTO YELP_DATA.Review (rid, uid, bid, review_date, stars, review_text, business_name," +
                " useful_votes, cool_votes, funny_votes, total_votes) " +
                "SELECT r.rid, r.uid, r.bid, r.review_date, r.stars, r.review_text, b.business_name, r.useful_votes, " +
                "r.cool_votes, r.funny_votes, r.useful_votes + r.cool_votes + r.funny_votes FROM YELP_DATA.Business b " +
                "JOIN YELP_DATA.Reviews_Buffer r ON b.bid = r.bid";

        PreparedStatement fillBusinessName = connection.prepareStatement(fillName);
        fillBusinessName.executeUpdate();

        insertReviews.close();
        fillBusinessName.close();
        bufferedReader.close();
        finishLog(REVIEW);
    }

    public static void populateCheckin(Connection connection) throws IOException, ParseException, SQLException{
        readyLog(CHECKIN);
        String path = ROOT_PATH + CHECKIN;

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String sql = "INSERT INTO YELP_DATA.Checkin VALUES (?, ?)";
        PreparedStatement insertCheckin = connection.prepareStatement(sql);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            JSONObject obj = (JSONObject) new JSONParser().parse(line);
            String bid = (String) obj.get("business_id");
            JSONObject info = (JSONObject) obj.get("checkin_info");

            insertCheckin.setString(1, bid);
            insertCheckin.setString(2, info.toJSONString());
            insertCheckin.executeUpdate();
        }

        insertCheckin.close();
        bufferedReader.close();
        finishLog(CHECKIN);
    }

    public static void populateUser(Connection connection) throws IOException, ParseException, SQLException {
        readyLog(USER);
        String path = ROOT_PATH + USER;

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        String sql = "INSERT INTO YELP_DATA.Users VALUES (?, ?, STR_TO_DATE(?, '%Y-%m-%d'), ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement userInsertion = connection.prepareStatement(sql);

        while ((line = bufferedReader.readLine()) != null){
            JSONObject obj = (JSONObject) new JSONParser().parse(line);
            String uid = (String) obj.get("user_id");
            String yelpSince = obj.get("yelping_since") + "-01";
            JSONObject votes = (JSONObject) obj.get("votes");
            long funny = (long) votes.get("funny");
            long useful = (long) votes.get("useful");
            long cool = (long) votes.get("cool");
            long totalVotes = funny + useful + cool;
            long reviewCount = (long) obj.get("review_count");
            String name = (String) obj.get("name");
            JSONArray friends = (JSONArray) obj.get("friends");
            long numOfFriends = friends.size();
            double avgStars = (double) obj.get("average_stars");

            userInsertion.setString(1, uid);
            userInsertion.setString(2, name);
            userInsertion.setString(3, yelpSince);
            userInsertion.setLong(4, reviewCount);
            userInsertion.setLong(5, numOfFriends);
            userInsertion.setDouble(6, avgStars);
            userInsertion.setLong(7, funny);
            userInsertion.setLong(8, useful);
            userInsertion.setLong(9, cool);
            userInsertion.setLong(10, totalVotes);

            userInsertion.executeUpdate();
        }

        userInsertion.close();
        bufferedReader.close();
        finishLog(USER);
    }

    private static void readyLog(String fileName){
        System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
        System.out.printf("Populating %s ...%n", fileName);
        System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
    }

    private static void finishLog(String fileName){
        System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
        System.out.printf("Populated %s successfully %n", fileName);
        System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
    }

    private static void prepareBoolean (PreparedStatement stmt, int idx, Object obj) throws SQLException{
        if (obj == null) {
            stmt.setNull(idx, Types.NULL);
        } else {
            try {
                stmt.setBoolean(idx, (boolean) obj);
            } catch (ClassCastException e) {
                stmt.setNull(idx, Types.NULL);
            }
        }
    }

    private static void prepareLong (PreparedStatement stmt, int idx, Object obj) throws SQLException{
        if (obj == null) {
            stmt.setNull(idx, Types.NULL);
        } else {
            stmt.setLong(idx, (long) obj);
        }
    }

    private static void prepareJSONObject (PreparedStatement stmt, int idx, JSONObject obj) throws SQLException{
        if (obj == null) {
            stmt.setNull(idx, Types.NULL);
        } else {
            ArrayList<String> buffer = new ArrayList<>();

            for (Object k: obj.keySet()){
                String key = (String) k;

                if ((boolean) obj.get(key)) {
                    buffer.add(key);
                }
            }
            stmt.setString(idx, String.join(",", buffer));
        }
    }
}



